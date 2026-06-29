#!/bin/bash

# Exit on error
set -e

# Load environment variables from .env file
if [ -f .env ]; then
    echo "Loading environment variables from .env..."
    # Read non-empty lines, skip comments, and export
    export $(grep -v '^#' .env | xargs)
else
    echo "ERROR: .env file not found!"
    exit 1
fi

# Check if application is already running
if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null; then
        echo "Application is already running with PID $PID."
        exit 0
    else
        echo "Stale app.pid file found, cleaning up..."
        rm app.pid
    fi
fi

# Always clean and package the application to ensure changes are captured
echo "Cleaning and packaging the application..."
if command -v mvn >/dev/null 2>&1; then
    mvn clean package -DskipTests
else
    echo "ERROR: Global 'mvn' command was not found! Cannot package the application."
    exit 1
fi
JAR_FILE=$(find target -maxdepth 1 -name "*.jar" -not -name "*sources*" | head -n 1)

echo "Starting Ollama Bridge Service in the background..."
# Run the application in the background, redirect logs to app.log
nohup java -jar "$JAR_FILE" > app.log 2>&1 &
PID=$!
disown $PID

# Save the process ID of the started java process
echo $PID > app.pid

echo "Application started with PID $PID."
echo "You can follow logs using: tail -f app.log"
