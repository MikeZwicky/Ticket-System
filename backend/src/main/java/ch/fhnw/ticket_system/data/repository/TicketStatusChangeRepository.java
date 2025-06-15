package ch.fhnw.ticket_system.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.ticket_system.data.domain.Message;
import ch.fhnw.ticket_system.data.domain.Rating;
import ch.fhnw.ticket_system.data.domain.TicketStatusChange;

public interface TicketStatusChangeRepository extends JpaRepository<TicketStatusChange, Long> {
    TicketStatusChange findByMessage(Message message);

    TicketStatusChange findByRating (Rating rating);
}