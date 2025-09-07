#!/bin/bash

# Script to stop the J2J Web Validator application

# Check if the application is running
if ! pgrep -f "j2j-web" > /dev/null; then
    echo "J2J Web Validator is not running"
    exit 1
fi

# Stop the application
echo "Stopping J2J Web Validator..."
pkill -f "j2j-web"

# Wait a moment for the application to stop
sleep 2

# Check if the application stopped successfully
if ! pgrep -f "j2j-web" > /dev/null; then
    echo "J2J Web Validator stopped successfully!"
else
    echo "Failed to stop J2J Web Validator"
    exit 1
fi