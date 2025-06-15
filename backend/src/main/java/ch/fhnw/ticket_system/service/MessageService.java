package ch.fhnw.ticket_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ch.fhnw.ticket_system.data.domain.Message;
import ch.fhnw.ticket_system.data.domain.Ticket;
import ch.fhnw.ticket_system.data.domain.TicketStatus;
import ch.fhnw.ticket_system.data.domain.TicketStatusChange;
import ch.fhnw.ticket_system.data.domain.User;
import ch.fhnw.ticket_system.data.dto.MessageCreationDTO;
import ch.fhnw.ticket_system.data.dto.MessageInfoDTO;
import ch.fhnw.ticket_system.data.repository.MessageRepository;
import ch.fhnw.ticket_system.data.repository.TicketRepository;
import ch.fhnw.ticket_system.data.repository.TicketStatusChangeRepository;
import ch.fhnw.ticket_system.data.repository.UserRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketStatusChangeRepository statusChangeRepository;

    public MessageService(MessageRepository messageRepository,
                          TicketRepository ticketRepository,
                          UserRepository userRepository,
                          TicketStatusChangeRepository statusChangeRepository) {
        this.messageRepository = messageRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.statusChangeRepository = statusChangeRepository;
    }

    /*****************************************************************
     * CRUD operations for tickets
     *****************************************************************/

    /*-----------------------------------------------------------------
     * CRUD: Create
     -----------------------------------------------------------------*/
    public List<MessageInfoDTO> createMessage(MessageCreationDTO dto, String status) {
        Ticket ticket = ticketRepository.findById(dto.getTicketId())
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        TicketStatusChange latestStatus = ticket.getLatestStatusChange();
        TicketStatus currentCreatorStatus = latestStatus.getStatusForCreator();
        TicketStatus currentAssignedStatus = latestStatus.getStatusForAssigned();

        if (!(currentCreatorStatus == TicketStatus.Open || currentCreatorStatus == TicketStatus.Pending))
            throw new RuntimeException("Messages can only be created for open or pending tickets.");

        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isCreator = user.getUserId().equals(ticket.getCreatedBy().getUserId());
        boolean isAssigned = ticket.getAssignedTo() != null &&
                            user.getUserId().equals(ticket.getAssignedTo().getUserId());

        if (!isCreator && !isAssigned)
            throw new RuntimeException("User not allowed to message on this ticket");

        if (!"Keep".equals(status) && !"Switch".equals(status))
            throw new RuntimeException("Status must be either 'Keep' or 'Switch'");

        if (dto.getText() == null || dto.getText().trim().isEmpty())
            throw new RuntimeException("Message cannot be empty");

        TicketStatus newCreatorStatus = currentCreatorStatus;
        TicketStatus newAssignedStatus = currentAssignedStatus;

        if ("Switch".equals(status)) {
            if (isAssigned) {
                if (currentAssignedStatus != TicketStatus.Open) {
                    throw new RuntimeException("Assigned user can only 'Switch' when status is Open.");
                }
                if (currentCreatorStatus == TicketStatus.Pending) {
                    newCreatorStatus = TicketStatus.Open;
                    newAssignedStatus = TicketStatus.Pending;
                }
            } else if (isCreator) {
                if (currentCreatorStatus != TicketStatus.Open) {
                    throw new RuntimeException("Creator can only 'Switch' when status is Open.");
                }
                if (currentAssignedStatus == TicketStatus.Pending) {
                    newAssignedStatus = TicketStatus.Open;
                    newCreatorStatus = TicketStatus.Pending;
                }
            }
        }

        Message message = new Message();
        message.setText(dto.getText());
        message.setUser(user);
        message.setTicket(ticket);
        message.setCreatedAt(LocalDateTime.now());

        messageRepository.save(message);

        TicketStatusChange statusChange = new TicketStatusChange();
        statusChange.setChangedAt(LocalDateTime.now());
        statusChange.setPreviousChange(latestStatus);
        statusChange.setMessage(message);
        statusChange.setTicket(ticket);
        statusChange.setStatusForCreator(newCreatorStatus);
        statusChange.setStatusForAssigned(newAssignedStatus);

        statusChangeRepository.save(statusChange);

        ticket.setLatestStatusChange(statusChange);
        ticketRepository.save(ticket);

        return getMessagesByTicketId(dto.getTicketId());
    }

    /*****************************************************************
     * CRUD: Update
     *****************************************************************/
    public List<MessageInfoDTO> updateMessage(Long messageId, MessageCreationDTO creationDTO) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getTicket().getTicketId().equals(creationDTO.getTicketId()))
            throw new RuntimeException("Ticket ID mismatch");

        if (!message.getUser().getUserId().equals(creationDTO.getUserId()))
            throw new RuntimeException("Only creator can edit this message");

        List<Message> newer = messageRepository.findByTicket_TicketIdAndCreatedAtAfterAndUser_UserIdNot(
            message.getTicket().getTicketId(), message.getCreatedAt(), creationDTO.getUserId());

        if (!newer.isEmpty())
            throw new RuntimeException("Cannot update: newer message from another user exists");

        if (message.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now()))
            throw new RuntimeException("Message can only be updated within 30 minutes");

        if (creationDTO.getText() == null || creationDTO.getText().trim().isEmpty())
            throw new RuntimeException("Message cannot be empty");

        message.setText(creationDTO.getText());
        messageRepository.save(message);

        return getMessagesByTicketId(creationDTO.getTicketId());
    }

    /*****************************************************************
     * CRUD: DELETE
     *****************************************************************/

    public List<MessageInfoDTO> deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getUser().getUserId().equals(userId))
            throw new RuntimeException("Only the creator can delete this message");

        Ticket ticket = message.getTicket();

        if (!(ticket.getLatestStatusChange().getStatusForCreator() == TicketStatus.Open ||
              ticket.getLatestStatusChange().getStatusForCreator() == TicketStatus.Pending))
            throw new RuntimeException("Only messages in open or pending tickets can be deleted");

        List<Message> newer = messageRepository.findByTicket_TicketIdAndCreatedAtAfterAndUser_UserIdNot(
            ticket.getTicketId(), message.getCreatedAt(), userId);

        if (!newer.isEmpty())
            throw new RuntimeException("Cannot delete: newer message from another user exists");

        TicketStatusChange statusChangeToDelete = statusChangeRepository.findByMessage(message);

        ticket.setLatestStatusChange(statusChangeToDelete.getPreviousChange());
        ticketRepository.save(ticket);

        statusChangeRepository.delete(statusChangeToDelete);

        messageRepository.delete(message);

        return getMessagesByTicketId(ticket.getTicketId());
    }

    /*****************************************************************
     * CRUD: READ
     *****************************************************************/

    public MessageInfoDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        return mapToDTO(message);
    }

    public List<MessageInfoDTO> getMessagesByTicketId(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new RuntimeException("Ticket not found");
        }
        return messageRepository.findByTicket_TicketIdOrderByCreatedAtAsc(ticketId)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /*****************************************************************
     * Helper method to map Message to DTO
     *****************************************************************/

    private MessageInfoDTO mapToDTO(Message c) {
        TicketStatusChange statusChange = statusChangeRepository.findByMessage(c);

        TicketStatus statusForAssigned = null;
        TicketStatus statusForCreator = null;

        if (statusChange != null) {
            statusForAssigned = statusChange.getStatusForAssigned();
            statusForCreator = statusChange.getStatusForCreator();
        }

        return new MessageInfoDTO(
            c.getMessageId(),
            c.getTicket().getTicketId(),
            c.getUser().getUserId(),
            c.getText(),
            c.getCreatedAt(),
            statusForAssigned,
            statusForCreator
        );
    }
}
