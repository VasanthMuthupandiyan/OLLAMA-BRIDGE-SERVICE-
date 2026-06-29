package com.example.demo.dto;

/**
 * Developer Comment:
 * DTO representing the response from Ollama's /api/embed endpoint.
 * Contains the generated vector embedding values in a 2D array (for batching).
 */
public class EmbeddingResponse {

    private String model;
    
    // /api/embed returns a 2D array (list of embeddings)
    private double[][] embeddings;
    
    // Legacy /api/embeddings returned a single 1D array
    private double[] embedding;

    public EmbeddingResponse() {}

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double[][] getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(double[][] embeddings) {
        this.embeddings = embeddings;
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
                ", embeddingsCount=" + (embeddings != null ? embeddings.length : 0) +
                '}';
    }
}
