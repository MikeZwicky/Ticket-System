package ch.fhnw.ticket_system.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ch.fhnw.ticket_system.data.domain.Rating;
import ch.fhnw.ticket_system.data.domain.Ticket;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRatedUserUserId(Long ratedUserId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.ratedUser.userId = :ratedUserId")
    Double findAverageRatingForSupport(Long ratedUserId);

    boolean existsByTicket(Ticket ticket);

    Optional<Rating> findByTicket(Ticket ticket);
}
