package ch.fhnw.ticket_system.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.ticket_system.data.domain.Message;
import ch.fhnw.ticket_system.data.domain.Ticket;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find messages for a given ticket, ordered by creation date ascending
    List<Message> findByTicket_TicketIdOrderByCreatedAtAsc(Long ticketId);

    // Find messages for a given ticket and user, created after a specific date
    List<Message> findByTicket_TicketIdAndCreatedAtAfterAndUser_UserIdNot(Long ticketId, java.time.LocalDateTime after, Long userId);

    // Find the latest message for a given ticket
    Message findTopByTicket_TicketIdOrderByCreatedAtDesc(Long ticketId);

    // Check if a message exists for a given ticket
    boolean existsByTicket(Ticket ticket);
}