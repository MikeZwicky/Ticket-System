// MessageCreationDTO.java
package ch.fhnw.ticket_system.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data required to create a new message")
public class MessageCreationDTO {

    @Schema(description = "The text content of the message", required = true)
    private String text;

    @Schema(description = "ID of the user creating the message", required = true)
    private Long userId;

    @Schema(description = "ID of the ticket the message belongs to", required = true)
    private Long ticketId;

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
}