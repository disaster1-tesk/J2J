# J2J Web Application Deployment Guide

This guide explains how to deploy and run the packaged J2J web application.

## Prerequisites

- Java 8 or higher installed
- Maven 3.6 or higher (for building from source)
- Docker (for containerized deployment)

## Building the Application

To build the entire J2J project and package it into executable JAR/WAR files:

```bash
# Build the project (skipping tests to avoid issues)
mvn clean package -DskipTests
```

This will create the following artifacts:
- `j2j-core/target/j2j-core-1.0-SNAPSHOT.jar` - Core transformation library
- `j2j-cli/target/j2j-cli-1.0-SNAPSHOT.jar` - Command-line interface
- `j2j-complete/target/j2j-complete-1.0-SNAPSHOT.jar` - Factory classes
- `j2j-web/target/j2j-web-1.0-SNAPSHOT.jar` - Web application (Spring Boot executable JAR)

## Running the Web Application

### Method 1: Using the provided script

```bash
# Make the script executable (if not already done)
chmod +x run-web.sh

# Run on default port 8080
./run-web.sh

# Run on a specific port
./run-web.sh 9090
```

### Method 2: Direct execution

```bash
# Run on default port 8080
java -jar j2j-web/target/j2j-web-1.0-SNAPSHOT.jar

# Run on a specific port
java -jar j2j-web/target/j2j-web-1.0-SNAPSHOT.jar --server.port=9090
```

### Method 3: Using environment variables

```bash
# Set port via environment variable
export SERVER_PORT=9090
java -jar j2j-web/target/j2j-web-1.0-SNAPSHOT.jar
```

## Accessing the Application

Once the application is running, you can access it at:
- `http://localhost:8080` (default port)
- `http://localhost:<port>` (if you specified a different port)

You can also try the online version at [http://www.disaster.love/j2j/](http://www.disaster.love/j2j/).

## Docker Deployment

### Building the Docker Image

First, ensure you have built the application:

```bash
mvn clean package -DskipTests
```

Then build the Docker image:

```bash
docker build -t j2j-web:latest .
```

### Running with Docker

```bash
# Run the container on port 8080
docker run -p 8080:8080 j2j-web:latest

# Run the container on a different port
docker run -p 9090:8080 j2j-web:latest

# Run with environment variables
docker run -p 8080:8080 -e SERVER_PORT=8080 j2j-web:latest

# Run with volume mounting for logs
docker run -p 8080:8080 -v $(pwd)/logs:/app/logs j2j-web:latest
```

### Running with Docker Compose

```bash
# Build and run the services
docker-compose up --build

# Run in detached mode
docker-compose up -d

# Stop the services
docker-compose down
```

## Docker Image Details

The Docker image is based on `openjdk:8-jre-alpine` for a small footprint. It:
- Uses a non-root user for security
- Exposes port 8080
- Copies the JAR file into the container
- Sets appropriate entrypoint

## Troubleshooting

### Common Issues

1. **Port already in use**: Change the port using `--server.port=<port>`
2. **Memory issues**: Increase heap size with `-Xmx` parameter:
   ```bash
   java -Xmx2g -jar j2j-web/target/j2j-web-1.0-SNAPSHOT.jar
   ```
3. **Missing dependencies**: Ensure all JAR files are in the correct locations
4. **Docker daemon not running**: Start Docker service on your system

### Logs

The application logs will be displayed in the console. For file-based logging, you can configure logback settings or use volume mounting with Docker.

## Stopping the Application

To stop the application, press `Ctrl+C` in the terminal where it's running.