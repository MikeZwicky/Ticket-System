package ch.fhnw.ticket_system.data.dto;

public class TicketCreateDTO {
    private String title;
    private String description;
    private String priority; // Accepts "High", "Medium", "Low"
    private Long createdById;

    public TicketCreateDTO() {}

    public TicketCreateDTO(String title, String description, String priority, Long createdById) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.createdById = createdById;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
}
