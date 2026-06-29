# Docker Deployment Guide

This guide details how to build, run, and manage the Ollama Bridge Service container using **Host Network Mode**.

## Why Host Network Mode?
On Linux, Host Network Mode (`--network host`) is the most efficient deployment option. It allows the container to share the host's network namespace directly. This offers two key benefits:
1.  **Direct Localhost Resolution**: The container can communicate with the host's Ollama instance directly via `localhost` (e.g. `http://localhost:11435`), matching your local configuration.
2.  **Native Performance**: Bypasses the virtual network bridge, ensuring maximum throughput and minimal latency.

---

## 1. Prerequisites
Ensure you have Docker installed and running on your Linux host system.

## 2. Build the Docker Image
Navigate to the root directory of the project (where the `Dockerfile` is located) and build the image:

```bash
docker build -t ollama-bridge-service:latest .
```

---

## 3. Run the Container
Start the container using the host network stack and feed it your local `.env` configuration file:

```bash
docker run -d \
  --network host \
  --env-file .env \
  --name ollama-bridge \
  ollama-bridge-service:latest
```

### Explanation of flags:
*   `-d`: Runs the container in the background (detached mode).
*   `--network host`: Shares the host's network stack directly. The container will automatically bind to the port defined in your `.env` file (default `7483`) on your host machine. Port mapping `-p` is not required.
*   `--env-file .env`: Feeds all environment variable definitions from your local `.env` file directly into the container during startup.
*   `--name ollama-bridge`: Names the container `ollama-bridge` for easier management.

---

## 4. Useful Operations

### View Container Logs
Check application logs to ensure it started successfully:
```bash
docker logs -f ollama-bridge
```

### Stop the Container
```bash
docker stop ollama-bridge
```

### Remove the Container
```bash
docker rm ollama-bridge
```
