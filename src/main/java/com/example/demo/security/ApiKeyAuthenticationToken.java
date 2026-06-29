package com.example.demo.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Developer Comment:
 * Custom Spring Security Authentication object representing an API Key authentication.
 * Extends AbstractAuthenticationToken so that Spring Security can track the auth state in SecurityContext.
 */
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;

    /**
     * Developer Comment:
     * Constructor for unauthenticated token request.
     * @param apiKey The API key received in the request headers
     */
    public ApiKeyAuthenticationToken(String apiKey) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.apiKey = apiKey;
        setAuthenticated(false);
    }

    /**
     * Developer Comment:
     * Constructor for successfully authenticated token.
     * Sets authenticated status to true and grants ROLE_API user role.
     * @param apiKey The validated API Key
     */
    public ApiKeyAuthenticationToken(String apiKey, boolean authenticated) {
        super(AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_API"));
        this.apiKey = apiKey;
        setAuthenticated(authenticated);
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return "api-client";
    }
}
