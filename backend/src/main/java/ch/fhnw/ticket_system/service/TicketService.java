package ch.fhnw.ticket_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.fhnw.ticket_system.data.domain.Priority;
import ch.fhnw.ticket_system.data.domain.Ticket;
import ch.fhnw.ticket_system.data.domain.TicketStatus;
import ch.fhnw.ticket_system.data.domain.TicketStatusChange;
import ch.fhnw.ticket_system.data.domain.User;
import ch.fhnw.ticket_system.data.domain.UserRole;
import ch.fhnw.ticket_system.data.dto.TicketCreateDTO;
import ch.fhnw.ticket_system.data.dto.TicketFilterDTO;
import ch.fhnw.ticket_system.data.dto.TicketInfoDTO;
import ch.fhnw.ticket_system.data.repository.MessageRepository;
import ch.fhnw.ticket_system.data.repository.RatingRepository;
import ch.fhnw.ticket_system.data.repository.TicketRepository;
import ch.fhnw.ticket_system.data.repository.TicketStatusChangeRepository;
import ch.fhnw.ticket_system.data.repository.UserRepository;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private TicketStatusChangeRepository ticketStatusChangeRepository;

    
    /*****************************************************************
     * CRUD operations for tickets
     *****************************************************************/

    /*-----------------------------------------------------------------
     * CRUD: Create
     -----------------------------------------------------------------*/
    public TicketInfoDTO createTicket(TicketCreateDTO ticketCreateDTO) {
        Ticket ticket = new Ticket();

        String title = ticketCreateDTO.getTitle();
        String description = ticketCreateDTO.getDescription();

        if ((title == null || title.trim().isEmpty()) && (description == null || description.trim().isEmpty())) {
            throw new IllegalArgumentException("Title and description must not both be empty.");
        }

        if (title == null || title.trim().isEmpty()) {
            User creator = userRepository.findById(ticketCreateDTO.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("Creator not found"));
            String creatorName = creator.getUsername();
            String date = java.time.LocalDate.now().toString();
            title = creatorName + " " + date;
        }

        ticket.setTitle(title);
        ticket.setDescription(description);

        String inputPriority = ticketCreateDTO.getPriority();
        Priority priority;

        if (inputPriority != null) {
            switch (inputPriority.trim().toLowerCase()) {
                case "High" -> priority = Priority.High;
                case "Medium" -> priority = Priority.Medium;
                case "Low" -> priority = Priority.Low;
                default -> priority = Priority.Low;
            }
        } else {
            priority = Priority.Low;
        }

        ticket.setPriority(priority);

        ticket.setCreatedAt(LocalDateTime.now());

        User creator = userRepository.findById(ticketCreateDTO.getCreatedById())
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        if (creator.getRole() == UserRole.Support) {
            throw new RuntimeException("Supports are not allowed to create tickets.");
        }
        ticket.setCreatedBy(creator);

        ticket.setAssignedTo(findSupportWithLowestWorkload() != null
                ? userRepository.findById(findSupportWithLowestWorkload()).orElse(null)
                : null);
        ticketRepository.save(ticket);
        
        TicketStatusChange initialStatus = new TicketStatusChange();
        initialStatus.setTicket(ticket);
        initialStatus.setChangedAt(LocalDateTime.now());
        initialStatus.setStatusForCreator(TicketStatus.Pending);
        initialStatus.setStatusForAssigned(TicketStatus.Open);

        ticketStatusChangeRepository.save(initialStatus);

        ticket.setLatestStatusChange(initialStatus);

        ticketRepository.save(ticket);
        return mapToTicketInfoDTO(ticket);
    }

    /*-----------------------------------------------------------------
     * CRUD: Read (single Ticket)
     -----------------------------------------------------------------*/
    public TicketInfoDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return mapToTicketInfoDTO(ticket);
    }

    /*-----------------------------------------------------------------
     * CRUD: Read (all Tickets)
     -----------------------------------------------------------------*/
    public List<TicketInfoDTO> getAllTicketsInfo() {
        return ticketRepository.findAll().stream()
                .map(this::mapToTicketInfoDTO)
                .collect(Collectors.toList());
    }

    /*-----------------------------------------------------------------
     * CRUD: Read (Tickets by role and filter)
     -----------------------------------------------------------------*/
    public List<TicketFilterDTO> getTicketsByRoleAndFilter(long userID, TicketStatus filter) {
        List<Ticket> tickets = getFilteredTickets(userID, filter);

        return tickets.stream()
                .map(ticket -> new TicketFilterDTO(
                        ticket.getTicketId(),
                        ticket.getTitle(),
                        ticket.getPriority().name(),
                        ticket.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /*-----------------------------------------------------------------
     * CRUD: Update
     -----------------------------------------------------------------*/
    public TicketInfoDTO updateTicket(Long id, TicketCreateDTO ticketCreateDTO) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getCreatedAt() == null ||
            ticket.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("The ticket cannot be updated more than 30 minutes after it has been created.");
        }

        if (!ticket.getCreatedBy().getUserId().equals(ticketCreateDTO.getCreatedById())) {
            throw new RuntimeException("The ticket can only be updated by its creator.");
        }

        String newTitle = ticketCreateDTO.getTitle();
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            ticket.setTitle(newTitle);
        }

        String newDescription = ticketCreateDTO.getDescription();
        if (newDescription != null && !newDescription.trim().isEmpty()) {
            ticket.setDescription(newDescription);
        }

    String inputPriority = ticketCreateDTO.getPriority();
    Priority priority;

    if (inputPriority != null) {
        switch (inputPriority.trim().toLowerCase()) {
            case "High" -> priority = Priority.High;
            case "Medium" -> priority = Priority.Medium;
            case "Low" -> priority = Priority.Low;
            default -> priority = Priority.Low;
        }
    } else {
        priority = ticket.getPriority();
    }

    ticket.setPriority(priority);

        Ticket updated = ticketRepository.save(ticket);
        return mapToTicketInfoDTO(updated);
    }

    /*-----------------------------------------------------------------
     * CRUD: Delete
     -----------------------------------------------------------------*/
    public void deleteTicket(Long id, Long userId) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!ticket.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("Only the creator can delete this ticket.");
        }

        TicketStatus status = getCurrentStatus(ticket, user);
        if (!(status == TicketStatus.Open || status == TicketStatus.Pending)) {
            throw new RuntimeException("Ticket can only be deleted if it is Open or Pending.");
        }

        if (messageRepository.existsByTicket(ticket)) {
            throw new RuntimeException("Cannot delete ticket with existing messages.");
        }

        if (ratingRepository.existsByTicket(ticket)) {
            throw new RuntimeException("Cannot delete ticket with existing ratings.");
        }

        ticketRepository.deleteById(id);
    }


    /*****************************************************************
     * Helper methods
     *****************************************************************/
    private TicketInfoDTO mapToTicketInfoDTO(Ticket ticket) {
        TicketStatus statusForCreator = null;
        TicketStatus statusForAssigned = null;

        TicketStatusChange latestChange = ticket.getLatestStatusChange();
        if (latestChange != null) {
            statusForCreator = latestChange.getStatusForCreator();
            statusForAssigned = latestChange.getStatusForAssigned();
        }

        return new TicketInfoDTO(
                ticket.getTicketId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCreatedAt(),
                ticket.getPriority(),
                statusForCreator,
                statusForAssigned,
                ticket.getCreatedBy() != null ? ticket.getCreatedBy().getUserId() : null,
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUserId() : null
        );
    }

    /*-----------------------------------------------------------------
     * Helper method to get the current status of a ticket for a user
     -----------------------------------------------------------------*/
    private TicketStatus getCurrentStatus(Ticket ticket, User user) {
        TicketStatusChange change = ticket.getLatestStatusChange();
        if (change == null) return null;

        if (user.equals(ticket.getCreatedBy())) {
            return change.getStatusForCreator();
        } else if (user.equals(ticket.getAssignedTo())) {
            return change.getStatusForAssigned();
        } else {
            throw new RuntimeException("User is neither creator nor assignee of the ticket.");
        }
    }

    /*-----------------------------------------------------------------
    * Business logic for finding the support with the lowest workload
    -----------------------------------------------------------------*/
    // Method to find the support with the lowest workload (least open or pending tickets weighted by priority)
    public Long findSupportWithLowestWorkload() {
        List<User> supports = userRepository.findByRole(UserRole.Support);
        for (User support : supports) {
            long workload = calculateWeightedTicketLoad(support);
            System.out.println("Support: " + support.getUsername() + " (ID: " + support.getUserId() + ") has workload: " + workload);
        }
        return supports.stream()
            .min((u1, u2) -> Long.compare(
                calculateWeightedTicketLoad(u1),
                calculateWeightedTicketLoad(u2)))
            .map(User::getUserId)
            .orElse(null);
    }

    private long calculateWeightedTicketLoad(User support) {
        return ticketRepository.findByAssignedTo(support).stream()
            .filter(ticket -> {
                TicketStatus status = TicketStatus.Open;
                TicketStatusChange latestChange = ticket.getLatestStatusChange();
                if (latestChange != null) {
                    status = latestChange.getStatusForAssigned();
                }
                return status == TicketStatus.Open || status == TicketStatus.Pending;
            })
            .mapToLong(ticket -> switch (ticket.getPriority()) {
                case High -> 3;
                case Medium -> 2;
                case Low -> 1;
            }).sum();
    }

    /*-----------------------------------------------------------------
     * Ticket filtering by role and status
     -----------------------------------------------------------------*/
    private List<Ticket> getFilteredTickets(long userID, TicketStatus filter) {
        User user = userRepository.findById(userID)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Ticket> relevantTickets = switch (user.getRole()) {
            case User -> ticketRepository.findAll().stream()
                .filter(t -> t.getCreatedBy() != null && t.getCreatedBy().equals(user))
                .toList();

            case Support -> ticketRepository.findAll().stream()
                .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().equals(user))
                .toList();

            default -> throw new RuntimeException("Unsupported user role");
        };

        return relevantTickets.stream()
            .filter(t -> {
                TicketStatus status = getCurrentStatus(t, user);
                return status != null && status == filter;
            })
            .sorted((t1, t2) -> {
                int cmp = Integer.compare(
                        t1.getPriority().ordinal(), t2.getPriority().ordinal());
                if (cmp == 0) cmp = t1.getCreatedAt().compareTo(t2.getCreatedAt());
                if (cmp == 0) cmp = t1.getTitle().compareTo(t2.getTitle());
                return cmp;
            }).toList();
    }
}
