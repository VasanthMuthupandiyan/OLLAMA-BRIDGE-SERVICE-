package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Developer Comment:
 * DTO representing the Embedding request payload.
 * Updated to support Ollama's /api/embed which accepts 'input' (String or List).
 */
public class EmbeddingRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String model;

    // The new Ollama /api/embed takes "input", which can be a String or Array of Strings.
    @Schema(example = "[\"Text 1\", \"Text 2\"]", description = "The text or list of texts to embed")
    private Object input;

    // Legacy fields for backward compatibility
    private String prompt;
    private Object content;

    public EmbeddingRequest() {}

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EmbeddingRequest{" +
                "model='" + model + '\'' +
                ", input=" + input +
                ", prompt='" + prompt + '\'' +
                ", content=" + content +
                '}';
    }
}
