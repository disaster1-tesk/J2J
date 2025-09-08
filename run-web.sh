#!/bin/bash

# Script to run the J2J web application
# Usage: ./run-web.sh [port]
# If no port is specified, it will use the default port 8080

PORT=${1:-8080}

echo "Starting J2J Web Application on port $PORT..."
echo "Access the application at http://localhost:$PORT"
echo "You can also try the online version at http://www.disaster.love/j2j/"

# Run the web application
java -jar j2j-web/target/j2j-web-1.0-SNAPSHOT.jar --server.port=$PORT