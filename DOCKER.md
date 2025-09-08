# Docker Deployment for J2J Web Application

This guide explains how to build and run the J2J web application using Docker.

## Prerequisites

- Docker installed on your system
- JDK 8 or higher
- Maven 3.x

## Building the Application

First, build the J2J web application JAR file:

```bash
mvn clean package -DskipTests
```

This will create a JAR file at `j2j-web/target/j2j-web-1.0-SNAPSHOT.jar`.

## Building the Docker Image

To build the Docker image:

```bash
docker build -t j2j-web .
```

## Running with Docker

To run the application using Docker:

```bash
docker run -p 8080:8080 j2j-web
```

The application will be accessible at http://localhost:8080

## Running with Docker Compose

Alternatively, you can use Docker Compose:

```bash
docker-compose up
```

## Configuration

The application runs on port 8080 by default. You can customize the port mapping in the docker-compose.yml file.

## Security

The Docker image runs the application as a non-root user for security purposes.