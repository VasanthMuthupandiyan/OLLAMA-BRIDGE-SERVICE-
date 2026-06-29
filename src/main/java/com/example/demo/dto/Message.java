package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Developer Comment:
 * Message representation for the Ollama Chat API.
 * Maps directly to individual message objects in the chat history.
 */
public class Message {

    @Schema(example = "user", description = "The role of the message sender (e.g., 'user', 'assistant', 'system')")
    private String role;

    @Schema(example = "Hello, how are you?", description = "The text content of the message")
    private String content;

    // Default constructor for Jackson JSON deserialization
    public Message() {}

    /**
     * Developer Comment:
     * Constructor to build a Message with role and content.
     * @param role The role of the message sender (e.g., "user", "assistant", "system")
     * @param content The text content of the message
     */
    @JsonCreator
    public Message(@JsonProperty("role") String role, @JsonProperty("content") String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
