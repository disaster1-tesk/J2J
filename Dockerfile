# Use OpenJDK 8 JRE Alpine as base image
FROM openjdk:8-jre-alpine

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN addgroup -g 1001 -S j2j &&\
    adduser -u 1001 -S j2j -G j2j

# Copy the JAR file to the container
COPY j2j-web/target/j2j-web-1.0-SNAPSHOT.jar app.jar

# Change ownership to the non-root user
RUN chown -R j2j:j2j /app

# Switch to non-root user
USER j2j

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]