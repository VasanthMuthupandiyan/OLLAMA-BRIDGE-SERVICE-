package com.example.demo.controller;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.dto.EmbeddingRequest;
import com.example.demo.dto.EmbeddingResponse;
import com.example.demo.dto.Message;
import com.example.demo.service.OllamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Developer Comment:
 * Integration tests for the Ollama Bridge API controller.
 * Mocks the OllamaService layer to allow isolated testing of routing and security authentication filters.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class OllamaBridgeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OllamaService ollamaService;

    // Read the expected valid API key from application properties configured for the test
    @Value("${bridge.api-key}")
    private String validApiKey;

    /**
     * Developer Comment:
     * Test verifying that calling endpoints without any authentication headers results in HTTP 401 Unauthorized.
     */
    @Test
    public void givenNoApiKey_whenChat_thenReturnUnauthorized401() throws Exception {
        ChatRequest request = new ChatRequest(null, java.util.Collections.emptyList(), false);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Missing or invalid 'X-API-KEY' header required to access this service."));
    }

    /**
     * Developer Comment:
     * Test verifying that calling endpoints with an incorrect API key results in HTTP 401 Unauthorized.
     */
    @Test
    public void givenInvalidApiKey_whenChat_thenReturnUnauthorized401() throws Exception {
        ChatRequest request = new ChatRequest(null, java.util.Collections.emptyList(), false);

        mockMvc.perform(post("/api/chat")
                        .header("X-API-KEY", "wrong-secret-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Developer Comment:
     * Test verifying that a valid API key header successfully authenticates and routes chat requests to OllamaService.
     */
    @Test
    public void givenValidApiKey_whenChat_thenReturnSuccess200() throws Exception {
        ChatRequest request = new ChatRequest(null, java.util.Collections.emptyList(), false);
        ChatResponse expectedResponse = new ChatResponse();
        expectedResponse.setModel("gemma2:2b");
        expectedResponse.setMessage(new Message("assistant", "Hello human!"));
        expectedResponse.setDone(true);

        // Define mock behavior for the service
        Mockito.when(ollamaService.chat(Mockito.any(ChatRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/chat")
                        .header("X-API-KEY", validApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("gemma2:2b"))
                .andExpect(jsonPath("$.message.content").value("Hello human!"))
                .andExpect(jsonPath("$.done").value(true));
    }

    /**
     * Developer Comment:
     * Test verifying that a valid API key header successfully authenticates and routes embedding requests to OllamaService.
     */
    @Test
    public void givenValidApiKey_whenEmbedding_thenReturnSuccess200() throws Exception {
        EmbeddingRequest request = new EmbeddingRequest();
        request.setContent("What is Spring Boot?");
        double[] mockEmbedding = {0.1, 0.2, 0.3};
        EmbeddingResponse expectedResponse = new EmbeddingResponse("embeddinggemma", mockEmbedding);

        // Define mock behavior for the service
        Mockito.when(ollamaService.embed(Mockito.any(EmbeddingRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/embeddings")
                        .header("X-API-KEY", validApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("embeddinggemma"))
                .andExpect(jsonPath("$.embedding").isArray())
                .andExpect(jsonPath("$.embedding[0]").value(0.1))
                .andExpect(jsonPath("$.embedding[1]").value(0.2))
                .andExpect(jsonPath("$.embedding[2]").value(0.3));
    }

    /**
     * Developer Comment:
     * Test verifying that when the service throws a 404 Not Found error (such as when the requested
     * model does not exist or has not been pulled yet in Ollama), the controller handles it 
     * and returns a proper 404 response containing the exact downstream error payload.
     */
    @Test
    public void givenModelNotFound_whenChat_thenReturn404NotFound() throws Exception {
        ChatRequest request = new ChatRequest(null, java.util.Collections.emptyList(), false);

        // Mock HttpStatusCodeException
        org.springframework.web.client.HttpClientErrorException.NotFound ex =
                (org.springframework.web.client.HttpClientErrorException.NotFound)
                        org.springframework.web.client.HttpClientErrorException.create(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Not Found",
                                org.springframework.http.HttpHeaders.EMPTY,
                                "{\"error\": \"model 'non-existent-model' not found, try pulling it first\"}".getBytes(),
                                java.nio.charset.StandardCharsets.UTF_8
                        );

        Mockito.when(ollamaService.chat(Mockito.any(ChatRequest.class))).thenThrow(ex);

        mockMvc.perform(post("/api/chat")
                        .header("X-API-KEY", validApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("model 'non-existent-model' not found, try pulling it first"));
    }
}
