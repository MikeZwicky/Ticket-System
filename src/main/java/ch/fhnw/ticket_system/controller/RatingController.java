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

import ch.fhnw.ticket_system.data.dto.RatingCreationDTO;
import ch.fhnw.ticket_system.data.dto.RatingInfoDTO;
import ch.fhnw.ticket_system.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ratings")
@Tag(name = "RatingController", description = "Handles CRUD operations for ratings. Ratings close tickets and must be between 1-5.")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /*****************************************************************
     * CRUD operations for ratings
     *****************************************************************/

    /*-----------------------------------------------------------------
     * CRUD: Create
     -----------------------------------------------------------------*/
    @PostMapping
    @Operation(
        summary = "Create a new rating",
        description = """
            Creates a rating for a ticket by its creator.
            The ticket must be in 'Open' or 'Pending' status.
            Rating must be an integer between 1 and 5.
            Creating a rating automatically closes the associated ticket.
        """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                name = "Create Rating Example",
                value = """
                    {
                    "ticketId": 4,
                    "text": "Thank you, it works now!",
                    "rating": 1,
                    "createdById": 4
                    }
                    """
            )
        )
    )
    public RatingInfoDTO createRating(@RequestBody RatingCreationDTO dto) {
        return ratingService.createRating(dto);
    }


    /*-----------------------------------------------------------------
     * CRUD: Read (ratings for an support)
     -----------------------------------------------------------------*/
    @GetMapping("/support/{supportId}")
    @Operation(
        summary = "Get all ratings for a support",
        description = "Retrieves all individual ratings received by a specific support."
    )
    public List<RatingInfoDTO> getRatingsForSupport(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID of the support to retrieve ratings for",
            example = "6"
        )
        @PathVariable Long supportId) {
        return ratingService.getRatingsForSupport(supportId);
    }


    /*-----------------------------------------------------------------
     * CRUD: Read (average rating for an support)
     -----------------------------------------------------------------*/
    @GetMapping("/support/{supportId}/average")
    @Operation(
        summary = "Get average rating for a support",
        description = "Calculates and returns the average rating score for a specific support based on all received ratings."
    )
    public Double getAverageRatingForSupport(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID of the support to calculate average rating for",
            example = "6"
        )
        @PathVariable Long supportId) {
        return ratingService.getAverageRatingForSupport(supportId);
    }

    /*-----------------------------------------------------------------
     * CRUD: Update
     -----------------------------------------------------------------*/
    @PutMapping("/{ratingId}")
    @Operation(
        summary = "Update a rating",
        description = """
            Allows the original creator to update the rating text and/or value within 30 minutes of creation.
            If a field (text or rating) is null or empty, its existing value is retained.
        """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                name = "Update Rating Example",
                value = """
                    {
                    "ticketId": 4,
                    "text": "",
                    "rating": 5,
                    "createdById": 4
                    }
                    """
            )
        )
    )
    public RatingInfoDTO updateRating(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID of the rating to update",
            example = "7"
        )
        @PathVariable Long ratingId,
        @RequestBody RatingCreationDTO dto) {
        return ratingService.updateRating(ratingId, dto);
    }

    /*-----------------------------------------------------------------
     * CRUD: Delete
     -----------------------------------------------------------------*/
    @DeleteMapping("/{ratingId}")
    @Operation(
        summary = "Delete a rating",
        description = """
            Allows the original creator to delete a rating within 30 minutes of its creation.
            Deleting a rating reopens the associated ticket.
        """
    )
    public void deleteRating(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID of the rating to delete",
            example = "7"
        )
        @PathVariable Long ratingId,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID of the user requesting deletion",
            example = "4"
        )
        @RequestParam Long userId) {
        ratingService.deleteRating(ratingId, userId);
    }
}
