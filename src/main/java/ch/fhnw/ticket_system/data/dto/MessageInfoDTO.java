package ch.fhnw.ticket_system.data.dto;

import java.time.LocalDateTime;

import ch.fhnw.ticket_system.data.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed information about a message with status changes")
public class MessageInfoDTO {

    // existing fields
    @Schema(description = "Unique ID of the message", required = true)
    private final Long messageId;

    @Schema(description = "ID of the ticket the message belongs to", required = true)
    private final Long ticketId;

    @Schema(description = "ID of the user who created the message", required = true)
    private final Long userId;

    @Schema(description = "Text content of the message", required = true)
    private final String text;

    @Schema(description = "Timestamp when the message was created", required = true)
    private final LocalDateTime createdAt;

    @Schema(description = "Status for the assigned user", required = true)
    private final TicketStatus statusForAssigned;

    @Schema(description = "Status for the creator", required = true)
    private final TicketStatus statusForCreator;

    public MessageInfoDTO(Long messageId, Long ticketId, Long userId, String text, LocalDateTime createdAt,
                          TicketStatus statusForAssigned, TicketStatus statusForCreator) {
        this.messageId = messageId;
        this.ticketId = ticketId;
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
        this.statusForAssigned = statusForAssigned;
        this.statusForCreator = statusForCreator;
    }

    // existing getters
    public Long getMessageId() { return messageId; }
    public Long getTicketId() { return ticketId; }
    public Long getUserId() { return userId; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TicketStatus getStatusForAssigned() { return statusForAssigned; }
    public TicketStatus getStatusForCreator() { return statusForCreator; }
}
