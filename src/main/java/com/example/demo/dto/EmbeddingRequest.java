package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Developer Comment:
 * DTO representing the Embedding request payload.
 * Used to capture client requests and forwards to Ollama's /api/embeddings.
 */
public class EmbeddingRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String model;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String prompt;

    @Schema(example = "What is Spring Boot?", description = "The text content to generate embeddings for")
    private String content;

    // Default constructor for Jackson JSON deserialization
    public EmbeddingRequest() {}

    /**
     * Constructor for convenience.
     */
    public EmbeddingRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EmbeddingRequest{" +
                "model='" + model + '\'' +
                ", prompt='" + prompt + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
