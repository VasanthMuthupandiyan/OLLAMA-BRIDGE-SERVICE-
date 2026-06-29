package com.example.demo.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Developer Comment:
 * Intercepts incoming HTTP requests to check for the mandatory "X-API-KEY" header.
 * Validates the value against the pre-configured value in application.properties.
 * If valid, places authentication in Spring's SecurityContextHolder.
 * If invalid or missing, immediately aborts the chain and returns a 401 Unauthorized JSON error response.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-API-KEY";

    @Value("${bridge.api-key}")
    private String configuredApiKey;

    @PostConstruct
    public void validateConfiguredApiKey() {
        if (configuredApiKey == null || configuredApiKey.trim().isEmpty()) {
            throw new IllegalStateException("CRITICAL SECURITY ERROR: The 'bridge.api-key' (BRIDGE_API_KEY) must be configured in .env and cannot be empty!");
        }
        if ("ollama-bridge-secret-key-12345".equals(configuredApiKey)) {
            logger.warn("SECURITY WARNING: Using the default placeholder BRIDGE_API_KEY. Please set a secure key in your .env file!");
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the X-API-KEY header from the request
        String requestApiKey = request.getHeader(HEADER_NAME);

        // Check if the API key is provided and matches the configured value
        if (requestApiKey != null && requestApiKey.equals(configuredApiKey)) {
            // Authentication succeeded: create token and register it in the SecurityContext
            ApiKeyAuthenticationToken auth = new ApiKeyAuthenticationToken(requestApiKey, true);
            SecurityContextHolder.getContext().setAuthentication(auth);
            
            // Proceed to the next filter in the security chain
            filterChain.doFilter(request, response);
        } else {
            // Authentication failed: Return HTTP 401 Unauthorized with JSON error details
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\": \"Unauthorized\", \"message\": \"Missing or invalid '" + HEADER_NAME + "' header required to access this service.\"}"
            );
        }
    }

    /**
     * Developer Comment:
     * Exclude specific actuator or health check endpoints from API key verification if needed.
     * In our current simple setup, we check API Key for all paths.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Allow public accessing of basic error paths and Swagger UI / OpenAPI docs
        return "/error".equals(path) 
                || path.startsWith("/swagger-ui") 
                || path.startsWith("/v3/api-docs");
    }
}
