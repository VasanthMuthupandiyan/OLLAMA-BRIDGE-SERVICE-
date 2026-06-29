package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Developer Comment:
 * DTO representing the response from Ollama's Chat API endpoint.
 * Jackson property mappings are used to deserialize snake_case fields from Ollama into camelCase fields.
 */
public class ChatResponse {

    private String model;

    private Message message;

    private Boolean done;

    @JsonProperty("total_duration")
    private Long totalDuration;

    @JsonProperty("load_duration")
    private Long loadDuration;

    @JsonProperty("prompt_eval_count")
    private Integer promptEvalCount;

    @JsonProperty("prompt_eval_duration")
    private Long promptEvalDuration;

    @JsonProperty("eval_count")
    private Integer evalCount;

    @JsonProperty("eval_duration")
    private Long evalDuration;

    // Default constructor for Jackson JSON deserialization
    public ChatResponse() {}

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(Long loadDuration) {
        this.loadDuration = loadDuration;
    }

    public Integer getPromptEvalCount() {
        return promptEvalCount;
    }

    public void setPromptEvalCount(Integer promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }

    public Long getPromptEvalDuration() {
        return promptEvalDuration;
    }

    public void setPromptEvalDuration(Long promptEvalDuration) {
        this.promptEvalDuration = promptEvalDuration;
    }

    public Integer getEvalCount() {
        return evalCount;
    }

    public void setEvalCount(Integer evalCount) {
        this.evalCount = evalCount;
    }

    public Long getEvalDuration() {
        return evalDuration;
    }

    public void setEvalDuration(Long evalDuration) {
        this.evalDuration = evalDuration;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "model='" + model + '\'' +
                ", message=" + message +
                ", done=" + done +
                '}';
    }
}
