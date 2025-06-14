package ch.fhnw.ticket_system.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ch.fhnw.ticket_system.data.domain.Rating;
import ch.fhnw.ticket_system.data.domain.Ticket;
import ch.fhnw.ticket_system.data.domain.TicketStatus;
import ch.fhnw.ticket_system.data.domain.TicketStatusChange;
import ch.fhnw.ticket_system.data.domain.User;
import ch.fhnw.ticket_system.data.domain.UserRole;
import ch.fhnw.ticket_system.data.dto.RatingCreationDTO;
import ch.fhnw.ticket_system.data.dto.RatingInfoDTO;
import ch.fhnw.ticket_system.data.repository.RatingRepository;
import ch.fhnw.ticket_system.data.repository.TicketRepository;
import ch.fhnw.ticket_system.data.repository.TicketStatusChangeRepository;
import ch.fhnw.ticket_system.data.repository.UserRepository;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketStatusChangeRepository statusChangeRepository;

    public RatingService(RatingRepository ratingRepository,
                         TicketRepository ticketRepository,
                         UserRepository userRepository,
                         TicketStatusChangeRepository statusChangeRepository) {
        this.ratingRepository = ratingRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.statusChangeRepository = statusChangeRepository;
    }

    // CREATE
    public RatingInfoDTO createRating(RatingCreationDTO dto) {
        Ticket ticket = ticketRepository.findById(dto.getTicketId())
        .orElseThrow(() -> new IllegalArgumentException("Ticket not found."));

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        if (!ticket.getCreatedBy().getUserId().equals(dto.getCreatedById())) {
            throw new SecurityException("Only the creator of the ticket can submit a rating.");
        }

        User creator = ticket.getCreatedBy();
        User rated = ticket.getAssignedTo();

        String text = dto.getText();
        if (text == null || text.trim().isEmpty()) {
            text = "Ticket was rated and automatically closed by the user.";
        }

        Rating rating = new Rating();
        rating.setTicket(ticket);
        rating.setText(text);
        rating.setRating(dto.getRating());
        rating.setCreatedBy(creator);
        rating.setRatedUser(rated);
        rating.setCreatedAt(LocalDateTime.now());

        // Create a new status change
        TicketStatusChange statusChange = new TicketStatusChange();
        statusChange.setTicket(ticket);
        statusChange.setChangedAt(LocalDateTime.now());
        statusChange.setStatusForCreator(TicketStatus.Closed);
        statusChange.setStatusForAssigned(TicketStatus.Closed);
        statusChange.setRating(rating);
        statusChange.setPreviousChange(ticket.getLatestStatusChange());

        // Persist entities
        ratingRepository.save(rating);
        statusChangeRepository.save(statusChange);

        ticket.setLatestStatusChange(statusChange);
        ticketRepository.save(ticket);

        return toDTO(rating);
    }

    // CRUD: READ (Rating for a specific ticket)
    public RatingInfoDTO getRatingForTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found."));

        Rating rating = ratingRepository.findByTicket(ticket)
                .orElseThrow(() -> new IllegalArgumentException("No rating found for this ticket."));

        return toDTO(rating);
    }

    // CRUD: READ (Ratings for Support)
    public List<RatingInfoDTO> getRatingsForSupport(Long supportId) {
        User user = userRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (user.getRole() != UserRole.Support) {
            throw new SecurityException("User is not an support.");
        }

        return ratingRepository.findByRatedUserUserId(supportId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // CRUD: READ (Average Rating for Support)
    public Double getAverageRatingForSupport(Long supportId) {
        User user = userRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!user.getRole().equals(UserRole.Support)) {
            throw new SecurityException("User is not an support.");
        }

        return ratingRepository.findAverageRatingForSupport(supportId);
    }

    // CRUD: UPDATE
    public RatingInfoDTO updateRating(Long ratingId, RatingCreationDTO dto) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found."));

        if (!rating.getTicket().getTicketId().equals(dto.getTicketId())) {
            throw new IllegalArgumentException("Rating does not belong to the specified ticket.");
        }

        if (!rating.getCreatedBy().getUserId().equals(dto.getCreatedById())) {
            throw new SecurityException("Only the creator can update the rating.");
        }

        if (Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toMinutes() > 30) {
            throw new IllegalStateException("Rating can only be updated within 30 minutes.");
        }

        if (dto.getText() != null && !dto.getText().trim().isEmpty()) {
            rating.setText(dto.getText());
        }

        if (dto.getRating() != null) {
            double newRating = dto.getRating();
            if (newRating >= 1 && newRating <= 5) {
                rating.setRating(newRating);
            } else {
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
            }
        }

        return toDTO(ratingRepository.save(rating));
    }

    // DELETE
    public void deleteRating(Long ratingId, Long userId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found."));

        if (!rating.getCreatedBy().getUserId().equals(userId)) {
            throw new SecurityException("Only the creator can delete the rating.");
        }

        if (Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toMinutes() > 30) {
            throw new IllegalStateException("Rating can only be deleted within 30 minutes.");
        }

        Ticket ticket = rating.getTicket();
        TicketStatusChange statusChangeToDelete = statusChangeRepository.findByRating(rating);

        ticket.setLatestStatusChange(statusChangeToDelete.getPreviousChange());
        ticketRepository.save(ticket);

        statusChangeRepository.delete(statusChangeToDelete);

        ratingRepository.delete(rating);
    }

    // Helper
    private RatingInfoDTO toDTO(Rating rating) {
        RatingInfoDTO dto = new RatingInfoDTO();
        dto.setRatingId(rating.getRatingId());
        dto.setText(rating.getText());
        dto.setRating(rating.getRating());
        dto.setTicketId(rating.getTicket().getTicketId());
        dto.setCreatedById(rating.getCreatedBy().getUserId());
        dto.setRatedUserId(rating.getRatedUser().getUserId());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
}
