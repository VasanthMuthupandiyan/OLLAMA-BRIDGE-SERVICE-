package com.example.demo.service;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.dto.EmbeddingRequest;
import com.example.demo.dto.EmbeddingResponse;
import com.example.demo.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Developer Comment:
 * Service class responsible for interfacing with the local Ollama API.
 * Uses RestClient to delegate chat and embedding requests from the bridge to Ollama.
 */
@Service
public class OllamaService {

    private static final Logger log = LoggerFactory.getLogger(OllamaService.class);

    private final RestClient restClient;

    // Default chat model to use if none is provided in the client request
    @Value("${ollama.default.chat-model}")
    private String defaultChatModel;

    // Default embedding model to use if none is provided in the client request
    @Value("${ollama.default.embedding-model}")
    private String defaultEmbeddingModel;

    /**
     * Developer Comment:
     * Constructor injection for RestClient dependency.
     * 
     * @param restClient The pre-configured RestClient bean to make HTTP calls
     */
    public OllamaService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Developer Comment:
     * Calls Ollama's local chat API (/api/chat).
     * If the client does not specify a model, the defaultChatModel (gemma2:2b) is applied.
     * 
     * @param request The ChatRequest containing model, messages, and stream config
     * @return ChatResponse mapping the Ollama response
     */
    public ChatResponse chat(ChatRequest request) {
        // Unconditionally use the configured default chat model
        request.setModel(defaultChatModel);

        // Force stream to false as per client requirements
        request.setStream(false);

        log.info("Forwarding chat request to Ollama. Model: {}, Messages count: {}", 
                request.getModel(), request.getMessages() != null ? request.getMessages().size() : 0);

        try {
            // Perform POST request to Ollama's /api/chat endpoint
            ChatResponse response = restClient.post()
                    .uri("/api/chat")
                    .body(request)
                    .retrieve()
                    .body(ChatResponse.class);

            log.info("Successfully received response from Ollama chat API.");
            return response;
        } catch (HttpStatusCodeException e) {
            log.error("Ollama API returned HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Failed to call Ollama chat API: {}", e.getMessage(), e);
            throw new RuntimeException("Ollama chat service error: " + e.getMessage(), e);
        }
    }

    /**
     * Developer Comment:
     * Calls Ollama's local embeddings API (/api/embeddings).
     * If the client does not specify a model, the defaultEmbeddingModel (embeddinggemma) is applied.
     * 
     * @param request The EmbeddingRequest containing model and prompt
     * @return EmbeddingResponse mapping the Ollama response
     */
    public EmbeddingResponse embed(EmbeddingRequest request) {
        // Unconditionally use the configured default embedding model
        request.setModel(defaultEmbeddingModel);
        
        // Removed legacy mapping logic since we only support 'input' now

        log.info("Forwarding embedding request to Ollama /api/embed. Model: {}", request.getModel());

        try {
            // Perform POST request to Ollama's /api/embed endpoint (supports batching)
            EmbeddingResponse response = restClient.post()
                    .uri("/api/embed")
                    .body(request)
                    .retrieve()
                    .body(EmbeddingResponse.class);

            if (response != null) {
                response.setModel(request.getModel());
            }

            log.info("Successfully received response from Ollama embeddings API.");
            return response;
        } catch (HttpStatusCodeException e) {
            log.error("Ollama API returned HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Failed to call Ollama embeddings API: {}", e.getMessage(), e);
            throw new RuntimeException("Ollama embedding service error: " + e.getMessage(), e);
        }
    }
}
