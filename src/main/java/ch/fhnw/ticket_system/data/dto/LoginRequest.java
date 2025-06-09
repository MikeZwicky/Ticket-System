package ch.fhnw.ticket_system.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request object with username and email")
public class LoginRequest {

    @Schema(description = "The user's username", example = "user1", required = true)
    private String login;
    
    @Schema(description = "The user's password", example = "pass123", required = true)
    private String password;


    // Getters and setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
