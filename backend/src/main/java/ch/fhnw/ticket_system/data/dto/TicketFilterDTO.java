package ch.fhnw.ticket_system.data.dto;

import java.time.LocalDateTime;

public class TicketFilterDTO {
    private Long ticketId;
    private String title;
    private String priority; // "High", "Medium", "Low"
    private LocalDateTime createdAt;

    public TicketFilterDTO() {}

    public TicketFilterDTO(Long ticketId, String title, String priority, LocalDateTime createdAt) {
        this.ticketId = ticketId;
        this.title = title;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getTitle() {
        return title;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
