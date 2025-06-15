package ch.fhnw.ticket_system.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.fhnw.ticket_system.data.domain.Ticket;
import ch.fhnw.ticket_system.data.domain.TicketStatus;
import ch.fhnw.ticket_system.data.domain.User;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
        select t from Ticket t
        join t.latestStatusChange s
        where t.createdBy = :createdBy
          and s.statusForCreator = :status
        order by t.priority asc, t.createdAt asc, t.title asc
        """)
    List<Ticket> findByCreatedByAndStatusOrderByPriorityAscCreatedAtAscTitleAsc(
        @Param("createdBy") User createdBy, 
        @Param("status") TicketStatus status);

    @Query("""
        select t from Ticket t
        join t.latestStatusChange s
        where t.assignedTo = :assignedTo
          and s.statusForAssigned = :status
        order by t.priority asc, t.createdAt asc, t.title asc
        """)
    List<Ticket> findByAssignedToAndStatusOrderByPriorityAscCreatedAtAscTitleAsc(
        @Param("assignedTo") User assignedTo, 
        @Param("status") TicketStatus status);

    long countByAssignedTo(User assignedTo);

    List<Ticket> findByAssignedTo(User assignedTo);
}
