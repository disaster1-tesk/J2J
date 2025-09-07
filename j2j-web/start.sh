#!/bin/bash

# Script to start the J2J Web Validator application

# Check if the application is already running
if pgrep -f "j2j-web" > /dev/null; then
    echo "J2J Web Validator is already running"
    exit 1
fi

# Start the application
echo "Starting J2J Web Validator..."
cd /Users/disaster/IdeaProjects/J2J
mvn spring-boot:run -pl j2j-web &

# Wait a moment for the application to start
sleep 3

# Check if the application started successfully
if pgrep -f "j2j-web" > /dev/null; then
    echo "J2J Web Validator started successfully!"
    echo "Access the application at: http://localhost:8080"
else
    echo "Failed to start J2J Web Validator"
    exit 1
fi