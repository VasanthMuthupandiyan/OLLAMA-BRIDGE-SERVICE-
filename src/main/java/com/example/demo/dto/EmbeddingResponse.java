package com.example.demo.dto;

import java.util.Arrays;

/**
 * Developer Comment:
 * DTO representing the response from Ollama's /api/embeddings endpoint.
 * Contains the generated vector embedding values.
 */
public class EmbeddingResponse {

    private String model;
    private double[] embedding;

    // Default constructor for Jackson JSON deserialization
    public EmbeddingResponse() {}

    /**
     * Developer Comment:
     * Constructor for convenience.
     * @param model Model name
     * @param embedding Array of double values representing the vector embedding
     */
    public EmbeddingResponse(String model, double[] embedding) {
        this.model = model;
        this.embedding = embedding;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(double[] embedding) {
        this.embedding = embedding;
    }

    @Override
    public String toString() {
        return "EmbeddingResponse{" +
                "model='" + model + '\'' +
                ", embeddingLength=" + (embedding != null ? embedding.length : 0) +
                '}';
    }
}
