package ch.fhnw.ticket_system.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.fhnw.ticket_system.data.domain.User;
import ch.fhnw.ticket_system.data.domain.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

    List<User> findByRole(UserRole role);
}
