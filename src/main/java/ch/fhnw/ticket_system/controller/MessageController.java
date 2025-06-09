package ch.fhnw.ticket_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.ticket_system.data.dto.MessageCreationDTO;
import ch.fhnw.ticket_system.data.dto.MessageInfoDTO;
import ch.fhnw.ticket_system.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "MessageController", description = "Handles CRUD operations for ticket messages including validation rules, status updates, and filtering by ticket.")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /*****************************************************************
     * CRUD operations for messages
     *****************************************************************/

    /*-----------------------------------------------------------------
     * CRUD: Create
     -----------------------------------------------------------------*/
    @PostMapping
    @Operation(
        summary = "Create a new message",
        description = """
            Creates a message under a ticket if the sender is the creator or assigned support.
            The message text must not be empty.
            It eigther switches the ticket status for the assigned support and ticket creator or keeps them the same. It uses the keywords "keep" or "switch" to determine the action.
            Returns the full list of messages for the given ticket after creation.
        """
    )
    public List<MessageInfoDTO> createMessage(
            @RequestBody MessageCreationDTO dto,
            @RequestParam(required = false) String status) {
        return messageService.createMessage(dto, status);
    }

    /*-----------------------------------------------------------------
     * CRUD: Read (single message)
     -----------------------------------------------------------------*/
    @GetMapping("/{id}")
    @Operation(
        summary = "Get message by ID",
        description = "Retrieves a single message based on its unique message ID."
    )
    public MessageInfoDTO getMessage(@PathVariable Long id) {
        return messageService.getMessageById(id);
    }

    /*-----------------------------------------------------------------
     * CRUD: Read (all messages for a ticket)
     -----------------------------------------------------------------*/
    @GetMapping("/ticket/{ticketId}")
    @Operation(
        summary = "Get all messages of a ticket",
        description = "Retrieves all messages associated with a specific ticket, ordered by creation time."
    )
    public List<MessageInfoDTO> getMessagesByTicket(@PathVariable Long ticketId) {
        return messageService.getMessagesByTicketId(ticketId);
    }

    /*-----------------------------------------------------------------
     * CRUD: Update
     -----------------------------------------------------------------*/
    @PutMapping("/{messageId}")
    @Operation(
        summary = "Update a message",
        description = """
            Updates a message's text if the following conditions are met:
            - The user is the original message creator.
            - The message was created within the last 30 minutes.
            - No newer messages exist from other users.
            - The message is not empty.
            Returns the full list of messages for the ticket after update.
        """
    )
    public List<MessageInfoDTO> updateMessage(@PathVariable Long messageId, @RequestBody MessageCreationDTO dto) {
        return messageService.updateMessage(messageId, dto);
    }

    /*-----------------------------------------------------------------
     * CRUD: Delete
     -----------------------------------------------------------------*/
    @DeleteMapping("/{messageId}")
    @Operation(
        summary = "Delete a message",
        description = """
            Deletes a message if the following conditions are met:
            - No newer messages exist by different users.
            - The associated ticket is in 'Open' or 'Pending' status.
            - The requesting user is the original creator.
            Rolls back the ticket status to its previous state if it was changed by the deleted message.
            Returns the full list of messages for the ticket after deletion.
        """
    )
    public List<MessageInfoDTO> deleteMessage(@PathVariable Long messageId,
                                              @RequestParam Long userId) {
        return messageService.deleteMessage(messageId, userId);
    }
}
