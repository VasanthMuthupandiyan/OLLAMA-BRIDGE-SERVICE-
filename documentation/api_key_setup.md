# Generating and Configuring the Bridge API Key

This guide describes how to generate a secure, random API key for the Ollama Bridge Service and configure it in your environment.

---

## 1. Generate a Secure API Key

Run one of the following commands in your terminal to generate a strong, random API key:

### Option A: Using OpenSSL (Recommended)
```bash
openssl rand -hex 32
```

### Option B: Using `/dev/urandom` (If OpenSSL is not available)
```bash
cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1
```

*Example Output:*
`7d1a2f9b8c0e3d4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f`

---

## 2. Configure the API Key

1. Open the `.env` file in the root directory of the project:
   ```bash
   nano .env
   ```

2. Replace the placeholder value of `BRIDGE_API_KEY` with your newly generated key:
   ```env
   BRIDGE_API_KEY=7d1a2f9b8c0e3d4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f
   ```

3. Save and close the file.

---

## 3. Apply the Changes

Restart the service to apply the new API key configuration:

```bash
# Stop the service if it is running
./stop.sh

# Start the service
./start.sh
```

---

## 4. Test the API Key

Verify that requests succeed using the new key, and fail with old or missing keys:

### Successful Request:
```bash
curl -X POST http://localhost:7483/api/chat \
  -H "X-API-KEY: your_newly_generated_key_here" \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"Hi"}]}'
```

### Rejected Request (401 Unauthorized):
```bash
curl -X POST http://localhost:7483/api/chat \
  -H "X-API-KEY: wrong-or-old-key" \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"Hi"}]}'
```
