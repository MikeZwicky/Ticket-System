package ch.fhnw.ticket_system.data.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

/**
 * Entity representing a change in the status of a ticket.
 */
@Entity
public class TicketStatusChange {

    /*****************************************************************
     * Keys and Relationships
     *****************************************************************/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_change_id")
    private TicketStatusChange previousChange;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus statusForCreator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus statusForAssigned;

    private LocalDateTime changedAt;

    /*****************************************************************
     * Getters and Setters
     *****************************************************************/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public TicketStatusChange getPreviousChange() {
        return previousChange;
    }

    public void setPreviousChange(TicketStatusChange previousChange) {
        this.previousChange = previousChange;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public TicketStatus getStatusForCreator() {
        return statusForCreator;
    }

    public void setStatusForCreator(TicketStatus statusForCreator) {
        this.statusForCreator = statusForCreator;
    }

    public TicketStatus getStatusForAssigned() {
        return statusForAssigned;
    }

    public void setStatusForAssigned(TicketStatus statusForAssigned) {
        this.statusForAssigned = statusForAssigned;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
