package ch.fhnw.ticket_system.controller;

// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden // Hide this controller from the Swagger UI
public class WelcomeController {

    @GetMapping(value="/")
    public String getWelcomeString() {
        
        return "Hello, welcome to our Pizzeria!";
    }
}
