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

    @io.swagger.v3.oas.annotations.media.Schema(description = "Optional model parameters such as temperature, num_ctx, etc.")
    private java.util.Map<String, Object> options;

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

    public java.util.Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(java.util.Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "EmbeddingRequest{" +
                "model='" + model + '\'' +
                ", input=" + input +
                ", options=" + options +
                '}';
    }
}
