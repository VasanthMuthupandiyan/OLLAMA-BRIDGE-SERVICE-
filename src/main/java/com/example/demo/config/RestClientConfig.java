package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Developer Comment:
 * Configuration class to define the RestClient bean.
 * The RestClient is Spring's modern, fluent, synchronous HTTP client,
 * introduced in Spring Boot 3.2 to replace RestTemplate.
 */
@Configuration
public class RestClientConfig {

    // Retrieve the base URL of the local Ollama instance from application.properties
    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    /**
     * Developer Comment:
     * Defines a RestClient bean pre-configured with the Ollama base URL
     * and default HTTP headers like Content-Type.
     * 
     * @return Pre-configured RestClient instance
     */
    @Bean
    public RestClient ollamaRestClient() {
        return RestClient.builder()
                .baseUrl(ollamaBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
