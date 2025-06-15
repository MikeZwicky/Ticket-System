package ch.fhnw.ticket_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.ticket_system.data.domain.TicketStatus;
import ch.fhnw.ticket_system.data.dto.TicketCreateDTO;
import ch.fhnw.ticket_system.data.dto.TicketFilterDTO;
import ch.fhnw.ticket_system.data.dto.TicketInfoDTO;
import ch.fhnw.ticket_system.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "TicketController", description = "Manages CRUD operations for tickets, including fetching tickets based on user roles and status filters.")
@SecurityRequirement(name = "bearerAuth") 
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /*****************************************************************
     * CRUD operations for tickets
     *****************************************************************/

    /*-----------------------------------------------------------------
     * CRUD: Create
     -----------------------------------------------------------------*/
    @PostMapping
    @PreAuthorize("hasAnyRole('User', 'Admin')")
    @Operation(
        summary = "Create a new ticket",
        description = """
            Creates a new ticket with the provided details.
            Tickets can only be created by users, and must include either a title or description. If title is not provided, it defaults to Username of creator and date.
            Priority can be "Low", "Medium" or "High". If not or wrongly provided it defaults to 'Low'.
            The tickets are automatically assigned to the support with the lowest workload based on the business logic.
            Business logic for assigning tickets: Workload is calculated based on the number of open or pending tickets assigned to each support, weighted by their priority (High = 3, Medium = 2, Low = 1).
            The ticket status for the assigned user is set to 'Open' and the ticket status for the creator is set to 'Pending' upon creation.
        """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                name = "Create Ticket Example",
                value = """
                    {
                    "title": "Website doesn't load",
                    "description": "Our website is blocked by the firewall!",
                    "priority": "Medium",
                    "createdById": 4
                    }
                    """
            )
        )
    )
    public TicketInfoDTO createTicket(@RequestBody TicketCreateDTO createDTO) {
        return ticketService.createTicket(createDTO);
    }

    /*-----------------------------------------------------------------
     * CRUD: Read (single ticket)
     -----------------------------------------------------------------*/
    @GetMapping("/{id}")
    @Operation(
        summary = "Get a single ticket by its ID.",
        description = "Retrieves detailed information about a specific ticket using its ID."
    )
    public TicketInfoDTO getTicketById(
        @Parameter(
            description = "ID of the ticket to retrieve",
            required = true,
            example = "1",
            in = ParameterIn.PATH
        )
        @PathVariable Long id) {
        return ticketService.getTicketById(id);
    }


    /*-----------------------------------------------------------------
     * CRUD: Read (all tickets)
     -----------------------------------------------------------------*/
    @GetMapping(path = "/all", produces = "application/json")
    @Operation(
        summary = "Get all tickets",
        description = "Retrieves a list of all tickets with their details."
    )


    public List<TicketInfoDTO> getAllTickets() {
        return ticketService.getAllTicketsInfo();
    }

    /*-----------------------------------------------------------------
     * CRUD: Read (filtered tickets by user role and status)
     -----------------------------------------------------------------*/
    @GetMapping("/filter/{status}")
    @Operation(
        summary = "Get tickets filtered by user role and status",
        description = """
            Lists all tickets from a specified user with the filter 'Open', 'Pending', 'Closed'.
            Users will see their created tickets; supports will see assigned tickets.
        """
    )
    public List<TicketFilterDTO> getTickets(
        @Parameter(
            description = "ID of the user requesting tickets",
            required = true,
            example = "6",
            in = ParameterIn.QUERY
        )
        @RequestParam long userID,

        @Parameter(
            description = "Status to filter tickets by",
            required = true,
            example = "Open",
            in = ParameterIn.PATH
        )
        @PathVariable String status) {

        TicketStatus filter;
        switch (status.toLowerCase()) {
            case "open" -> filter = TicketStatus.Open;
            case "pending" -> filter = TicketStatus.Pending;
            case "closed" -> filter = TicketStatus.Closed;
            default -> throw new IllegalArgumentException("Invalid status value. Accepted values are 'Open', 'Pending', 'Closed'.");
        }
        return ticketService.getTicketsByRoleAndFilter(userID, filter);
    }

    /*-----------------------------------------------------------------
     * CRUD: Update
     -----------------------------------------------------------------*/
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('User', 'Admin')")
    @Operation(
        summary = "Update an existing ticket by ID",
        description = """
            Updates the details (title, description, priority) of an existing ticket.
            Only the creator can update it, and only within 30 minutes of creation.
            Empty or null fields will retain their existing values.
            Priority can be "Low", "Medium" or "High". If wrongly provided it will default to "Low".
        """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Update Ticket Example",
                value = """
                    {
                    "title": "",
                    "description": "Our website is blocked by the firewall. It is also blocked for some of our customers!",
                    "priority": "High",
                    "createdById": 4
                    }
                    """
            )
        )
    )
    public TicketInfoDTO updateTicket(
            @Parameter(
                description = "ID of the ticket to update",
                example = "21"
            )
            @PathVariable Long id,
            @RequestBody TicketCreateDTO createDTO) {
        return ticketService.updateTicket(id, createDTO);
    }


    /*-----------------------------------------------------------------
     * CRUD: Delete
     -----------------------------------------------------------------*/
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('User', 'Admin')")
    @Operation(
        summary = "Delete a ticket by ID",
        description = """
            Deletes a ticket by ID using the userID as validation.
            Only Open or Pending tickets can be deleted by their creator
            if no message or rating was created for the ticket.
        """
    )
    public void deleteTicket(
        @Parameter(example = "21", description = "ID of the ticket to delete")
        @PathVariable Long id,

        @Parameter(example = "4", description = "ID of the user attempting to delete the ticket")
        @RequestParam Long userId
    ) {
        ticketService.deleteTicket(id, userId);
    }
}
