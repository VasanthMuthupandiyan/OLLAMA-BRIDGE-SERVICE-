#!/bin/bash

# Check if PID file exists
if [ -f app.pid ]; then
    PID=$(cat app.pid)
    echo "Stopping Ollama Bridge Service (PID $PID)..."
    
    if ps -p $PID > /dev/null; then
        # Send SIGTERM (graceful stop)
        kill $PID
        
        # Wait up to 10 seconds for the process to exit
        for i in {1..10}; do
            if ! ps -p $PID > /dev/null; then
                break
            fi
            sleep 1
        done
        
        # If still running, force terminate (SIGKILL)
        if ps -p $PID > /dev/null; then
            echo "Process did not stop gracefully. Forcing termination..."
            kill -9 $PID
        fi
        
        echo "Application stopped successfully."
    else
        echo "Process with PID $PID was already stopped."
    fi
    
    # Clean up PID file
    rm app.pid
else
    echo "No app.pid file found. Is the application running?"
fi
