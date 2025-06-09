package ch.fhnw.ticket_system.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*****************************************************************
 * This class is used to hash passwords using BCrypt. It is not used for the main application
 * but can be used to generate hashed passwords for user accounts (in the database).
 *****************************************************************/

public class PasswordHasher {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123"; // your plain password here
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println(hashedPassword);
    }
}
