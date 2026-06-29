package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Developer Comment:
 * Configures HTTP security parameters for the application.
 * Disables session creation and CSRF since this is a stateless REST API bridge.
 * Inserts the custom ApiKeyAuthFilter before UsernamePasswordAuthenticationFilter.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApiKeyAuthFilter apiKeyAuthFilter;

    /**
     * Developer Comment:
     * Constructor injection for our custom API key filter.
     * @param apiKeyAuthFilter Custom request filter for checking X-API-KEY
     */
    public SecurityConfig(ApiKeyAuthFilter apiKeyAuthFilter) {
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    /**
     * Developer Comment:
     * Configures the security filter chain to enforce API Key auth on all api endpoints.
     * Permits access to /error path (to allow boot fallback error rendering to respond properly).
     * 
     * @param http HttpSecurity configuration object
     * @return Built SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection because APIs are stateless and don't use cookies
            .csrf(AbstractHttpConfigurer::disable)
            // Configure stateless session management (no HTTP session is created or used)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Define URL access authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to access the basic Spring boot error path
                .requestMatchers("/error").permitAll()
                // Allow public access to Swagger UI and OpenAPI documentation
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            // Add custom API key check filter before the default username-password checker
            .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
