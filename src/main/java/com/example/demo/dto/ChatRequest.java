package com.example.demo.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Developer Comment:
 * DTO representing the Chat request payload.
 * It is mapped to the incoming JSON request and also sent to Ollama's /api/chat endpoint.
 */
public class ChatRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String model;

    private List<Message> messages;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean stream = false;

    // Default constructor for Jackson JSON deserialization
    public ChatRequest() {}

    /**
     * Constructor for convenience.
     */
    public ChatRequest(String model, List<Message> messages, Boolean stream) {
        this.model = model;
        this.messages = messages;
        this.stream = stream;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "model='" + model + '\'' +
                ", messages=" + messages +
                ", stream=" + stream +
                '}';
    }
}
