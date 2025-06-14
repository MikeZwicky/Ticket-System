package ch.fhnw.ticket_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                    "/api/login",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/swagger-resources/configuration/ui",
                    "/swagger-resources/configuration/security",
                    "/webjars/**",
                    "/",
                    "/index.html",
                    "/h2-console/**"
                ).permitAll()

                // Protected endpoints
                .requestMatchers("/api/register").anonymous()
                .requestMatchers("/api/ratings/{ratingId}").hasAnyRole("User", "Admin")
                .requestMatchers("/api/ratings").hasAnyRole("User", "Admin")
                .requestMatchers("/api/ratings//ticket/{ticketId}").hasAnyRole("User", "Support", "Admin")
                .requestMatchers("/api/ratings/support/{supportId}").hasAnyRole("Support", "Admin")
                .requestMatchers("/api/ratings/support/{supportId}/average").hasAnyRole("Support", "Admin")

                .requestMatchers("/api/tickets/filter/{status}").hasAnyRole("User", "Support", "Admin")
                .requestMatchers("/api/tickets/all").hasAnyRole("Admin")
                .requestMatchers("/api/tickets/{id}").hasAnyRole("User", "Support", "Admin")
                .requestMatchers("/api/tickets/").hasAnyRole("User", "Admin")

                .requestMatchers("/api/messages/**").hasAnyRole("User", "Support", "Admin")

                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(jwtTokenProvider.getSecretKey()).build();
    }
}
