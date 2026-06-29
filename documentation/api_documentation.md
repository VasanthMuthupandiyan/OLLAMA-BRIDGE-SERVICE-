# Ollama Bridge Service API Documentation

This guide describes the API endpoints exposed by the Ollama Bridge Service. 

The bridge simplifies requests by:
1.  **Enforcing Default Models**: Unconditionally using the default models configured in your `.env` file (any `model` parameter supplied in the incoming request is ignored).
2.  **Streaming Settings**: Forcing `stream: false` automatically (any `stream` parameter supplied in the incoming request is ignored).

---

## Authentication

All API requests must include the following header:
```http
X-API-KEY: <your-configured-bridge-api-key>
```
*(Default testing key: `ollama-bridge-secret-key-12345`)*

---

## Endpoints

### 1. Chat Completion (`POST /api/chat`)
Sends a single prompt or a multi-turn conversation history. You can pass multiple messages with different roles (`user`, `assistant`, `system`).

#### Request Schema
*   `messages` (Array of objects, required):
    *   `role` (String): Who is sending the message (`system`, `user`, or `assistant`).
    *   `content` (String): The message text.

#### Curl Example (Multi-Role Conversation History)
```bash
curl -X POST http://localhost:7483/api/chat \
  -H "X-API-KEY: ollama-bridge-secret-key-12345" \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      { "role": "system", "content": "You are a helpful tutor." },
      { "role": "user", "content": "What is 2 + 2?" },
      { "role": "assistant", "content": "2 + 2 is 4." },
      { "role": "user", "content": "Multiply that by 3." }
    ]
  }'
```

#### Example Response
```json
{
  "model": "gemma2:2b",
  "message": {
    "role": "assistant",
    "content": "4 multiplied by 3 is 12."
  },
  "done": true
}
```

---

### 2. Generate Embeddings (`POST /api/embeddings`)
Generates vector embeddings for a given input text or array of texts (batching) using the default embedding model.

#### Request Schema
*   `input` (String or Array of Strings, required): The text(s) to run the embedding on. *(Note: For backward compatibility, `content` or `prompt` is also accepted).*

#### Curl Example (Single Request)
```bash
curl -X POST http://localhost:7483/api/embeddings \
  -H "X-API-KEY: ollama-bridge-secret-key-12345" \
  -H "Content-Type: application/json" \
  -d '{
    "input": "What is Spring Boot?"
  }'
```

#### Curl Example (Batch Request)
```bash
curl -X POST http://localhost:7483/api/embeddings \
  -H "X-API-KEY: ollama-bridge-secret-key-12345" \
  -H "Content-Type: application/json" \
  -d '{
    "input": ["What is Spring Boot?", "What is Gemma?"]
  }'
```

#### Example Response
Returns a 2D array (`embeddings`), which supports multiple vectors for batching.
```json
{
  "model": "embeddinggemma",
  "embeddings": [
    [
      0.012354,
      -0.084723,
      0.204593
    ],
    [
      0.034567,
      -0.012345,
      0.987654
    ]
  ]
}
```

---

## Interactive API Testing (Swagger UI)

You can view and test the API directly in your browser:
*   **URL**: `http://localhost:7483/swagger-ui/index.html`
*   **Instructions**:
    1. Click the **Authorize** button on the top-right.
    2. Input your `X-API-KEY` value and click **Authorize**.
    3. You can now use "Try it out" to send requests.
