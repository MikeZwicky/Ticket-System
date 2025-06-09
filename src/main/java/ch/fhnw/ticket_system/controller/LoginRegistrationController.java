package ch.fhnw.ticket_system.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.ticket_system.data.domain.User;
import ch.fhnw.ticket_system.data.domain.UserRole;
import ch.fhnw.ticket_system.data.dto.LoginRequest;
import ch.fhnw.ticket_system.data.dto.RegisterRequest;
import ch.fhnw.ticket_system.data.repository.UserRepository;
import ch.fhnw.ticket_system.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "LoginAndRegistrationController", description = "Register, login and authenticate users")
public class LoginRegistrationController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginRegistrationController(UserRepository userRepository,
                                       JwtTokenProvider jwtTokenProvider,
                                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /*****************************************************************
     * Authentication operations
     *****************************************************************/

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = """
            Authenticates a user using either email or username and password.
            Returns JWT token on success.
            Returns HTTP 401 if credentials are invalid.
        """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                name = "Login Example",
                value = """
                    {
                    "login": "admin1",
                    "password": "admin123"
                    }
                    """
            )
        )
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmailOrUsername(
                request.getLogin(), request.getLogin());

        if (userOpt.isEmpty() ||
            !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        User user = userOpt.get();
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(token);
    }

    /*****************************************************************
     * Registration operation
     *****************************************************************/

    @PostMapping("/register")
    @Operation(
        summary = "User registration",
        description = "Registers a new user with a username, email, and password."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Register Example",
                value = """
                    {
                      "username": "user7",
                      "email": "user7@example.com",
                      "password": "pass123"
                    }
                    """
            )
        )
    )
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        boolean usernameExists = userRepository.findByUsername(request.getUsername()).isPresent();
        boolean emailExists = userRepository.findByEmail(request.getEmail()).isPresent();

        if (usernameExists && emailExists) {
            return ResponseEntity.badRequest().body("Username and email are already taken");
        } else if (usernameExists) {
            return ResponseEntity.badRequest().body("Username is already taken");
        } else if (emailExists) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        // Create new user and encode password
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(UserRole.User);
        newUser.setCreatedAt(LocalDateTime.now());

        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

}
