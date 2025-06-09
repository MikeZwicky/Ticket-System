package ch.fhnw.ticket_system.data.dto;

import java.time.LocalDateTime;

import ch.fhnw.ticket_system.data.domain.Priority;
import ch.fhnw.ticket_system.data.domain.TicketStatus;

public class TicketInfoDTO {
    private Long ticketId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Priority priority;
    private TicketStatus statusForCreator;
    private TicketStatus statusForAssigned;
    private Long createdById;
    private Long assignedToId;

    public TicketInfoDTO() {}

    public TicketInfoDTO(Long ticketId, String title, String description, LocalDateTime createdAt,
                         Priority priority, TicketStatus statusForCreator, TicketStatus statusForAssigned,
                         Long createdById, Long assignedToId) {
        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.priority = priority;
        this.statusForCreator = statusForCreator;
        this.statusForAssigned = statusForAssigned;
        this.createdById = createdById;
        this.assignedToId = assignedToId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
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

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }
}
