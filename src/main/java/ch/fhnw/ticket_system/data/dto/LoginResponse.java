package ch.fhnw.ticket_system.data.dto;

public class LoginResponse {
    private String token;
    private String redirectUrl;

    public LoginResponse(String token, String redirectUrl) {
        this.token = token;
        this.redirectUrl = redirectUrl;
    }

    public String getToken() {
        return token;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}

