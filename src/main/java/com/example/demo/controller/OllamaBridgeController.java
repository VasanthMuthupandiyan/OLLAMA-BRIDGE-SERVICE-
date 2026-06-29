package com.example.demo.controller;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.dto.EmbeddingRequest;
import com.example.demo.dto.EmbeddingResponse;
import com.example.demo.service.OllamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.http.MediaType;

/**
 * Developer Comment:
 * RestController exposing the bridge endpoints.
 * These match the exact paths of the local Ollama API endpoints: /api/chat and /api/embeddings.
 * The endpoints require mandatory X-API-KEY authentication, enforced by Spring Security.
 */
@RestController
@RequestMapping("/api")
public class OllamaBridgeController {

    private static final Logger log = LoggerFactory.getLogger(OllamaBridgeController.class);

    private final OllamaService ollamaService;

    /**
     * Developer Comment:
     * Constructor injection for OllamaService.
     * @param ollamaService The service logic containing Ollama integration
     */
    public OllamaBridgeController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    /**
     * Developer Comment:
     * Exposes POST /api/chat. Bridges client chat requests to local Ollama chat API.
     * Enforces non-streaming (stream=false) chat completion.
     * 
     * Example Client Curl Request:
     * curl -H "X-API-KEY: ollama-bridge-secret-key-12345" -H "Content-Type: application/json" \
     * -d '{"messages":[{"role":"user","content":"what is gemma 2 2b mode ?"}]}' http://localhost:7483/api/chat
     * 
     * @param request Incoming chat options and conversation history
     * @return ChatResponse containing the structured response from Ollama
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request at bridge API.");
        
        // Call Ollama through the service layer
        ChatResponse response = ollamaService.chat(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Developer Comment:
     * Exposes POST /api/embeddings. Bridges client embedding request to local Ollama embeddings API.
     * 
     * Example Client Curl Request:
     * curl -H "X-API-KEY: ollama-bridge-secret-key-12345" -H "Content-Type: application/json" \
     * -d '{"content":"What is Spring Boot?"}' http://localhost:7483/api/embeddings
     * 
     * @param request Incoming model and prompt description to embed
     * @return EmbeddingResponse containing the generated vector array
     */
    @PostMapping("/embeddings")
    public ResponseEntity<EmbeddingResponse> embed(@RequestBody EmbeddingRequest request) {
        log.info("Received embedding request at bridge API.");
        
        // Call Ollama through the service layer
        EmbeddingResponse response = ollamaService.embed(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Exception handler to propagate errors from downstream Ollama APIs (e.g. 404 model not found)
     * back to the client with the same HTTP status code and response body.
     */
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<String> handleHttpStatusCodeException(HttpStatusCodeException e) {
        return ResponseEntity.status(e.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getResponseBodyAsString());
    }
}
