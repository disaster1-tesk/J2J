# j2j-web

Web interface for interactive JSON transformations using the J2J library.

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/java-17+-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring--boot-2.7.18-blue.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/docker-latest-blue.svg)](https://www.docker.com/)

## Overview

This module provides a web-based user interface for experimenting with JSON transformations. It allows users to:
- Input JSON data
- Define transformation specifications
- See real-time transformation results
- Access various examples and documentation
- Validate JSON syntax
- Test complex transformation chains

The web interface is particularly useful for developers who want to experiment with JSON transformations without writing code, or for teams that need to collaborate on transformation specifications.

You can try out the J2J web interface online at [http://www.disaster.love/j2j/](http://www.disaster.love/j2j/).

## Features

- **Interactive JSON Editor**: Syntax-highlighted editors for both input data and transformation specifications
- **Real-time Transformation**: See transformation results as you type
- **Multiple Transformation Types**: Support for shift, default, remove, sort, and chain operations
- **JSON Validation**: Real-time validation of JSON syntax with detailed error messages
- **Example Library**: Pre-built examples for common transformation patterns
- **Performance Metrics**: Transformation timing and complexity analysis
- **Responsive Design**: Works on desktop and mobile devices with light/dark themes
- **Integrated Tools**: Access to additional tools like Beetl Online and JSONata Exerciser
- **Template Engine Support**: Integration with Beetl and JSONata template engines

## Technology Stack

- **Backend**: 
  - Spring Boot 2.7.18
  - j2j-core for transformation logic
  - Jackson for JSON processing
- **Frontend**: 
  - HTML5, CSS3, JavaScript
  - Bootstrap 5 for responsive design
  - CodeMirror for code editing
  - jQuery for DOM manipulation
- **Build Tool**: Maven

## REST API Endpoints

### Transformation Endpoints
- `POST /api/transform` - Perform JSON transformation
  - Request body: TransformRequest DTO with input, spec, and operation type
  - Response: TransformResponse DTO with result and timing information

- `POST /api/transform/validate/json` - Validate JSON format
  - Request body: JSON string to validate
  - Response: ValidationResult DTO with isValid flag and error message

- `POST /api/transform/validate/spec` - Validate transformation specification
  - Request body: Specification string to validate
  - Response: ValidationResult DTO with isValid flag and error message

### Web Interface Endpoints
- `GET /` - Main web interface page
- `GET /tools` - Additional tools page
- `GET /docs` - Documentation page

## Usage

### Running the Application

```bash
# Run the web application in development mode
mvn spring-boot:run -pl j2j-web

# Run with a specific port
mvn spring-boot:run -pl j2j-web -Dspring-boot.run.arguments='--server.port=8081'

# Run with custom configuration
mvn spring-boot:run -pl j2j-web -Dspring-boot.run.arguments='--server.port=8081 --logging.level.root=DEBUG'
```

### Building for Production

```bash
# Build the application
mvn clean package -pl j2j-web

# Run the built JAR
java -jar target/j2j-web-1.0-SNAPSHOT.jar

# Run with custom port
java -jar target/j2j-web-1.0-SNAPSHOT.jar --server.port=8081
```

### Docker Deployment

```bash
# Build the project
mvn clean package -DskipTests

# Build the Docker image
docker build -t j2j-web:latest .

# Run the container
docker run -p 8080:8080 j2j-web:latest

# Or use Docker Compose from project root
cd .. && docker-compose up -d
```

### Using the Web Interface

1. **Open the Application**: Navigate to `http://localhost:8080` in your browser or try the online version at [http://www.disaster.love/j2j/](http://www.disaster.love/j2j/)
2. **Enter JSON Data**: Type or paste your input JSON in the left editor
3. **Define Specification**: Create your transformation specification in the middle editor
4. **View Results**: See the transformation result in the right editor
5. **Select Operation**: Choose the transformation type from the dropdown
6. **Try Examples**: Use the example dropdown to load pre-built examples

### Using the REST API

#### Transform JSON
```bash
curl -X POST http://localhost:8080/api/transform \
  -H "Content-Type: application/json" \
  -d '{
    "input": "{\"user\":{\"name\":\"John\",\"age\":30}}",
    "spec": "{\"user\":{\"name\":\"personName\"}}",
    "operation": "shift"
  }'
```

#### Validate JSON
```bash
curl -X POST http://localhost:8080/api/transform/validate/json \
  -H "Content-Type: application/json" \
  -d '{"user":{"name":"John","age":30}}'
```

## Scripts

- `start.sh` - Start the web application in the background
- `stop.sh` - Stop the web application

### Start Script Usage
```bash
# Make the script executable
chmod +x start.sh

# Start the application
./start.sh

# Start with custom port
PORT=8081 ./start.sh
```

### Stop Script Usage
```bash
# Make the script executable
chmod +x stop.sh

# Stop the application
./stop.sh
```

## Dependencies

- **j2j-core** - Core transformation logic
- **Spring Boot Starter Web** - Web framework for REST APIs
- **Spring Boot Starter Thymeleaf** - Template engine for server-side rendering
- **Spring Boot DevTools** - Development tools for hot reload (runtime scope)
- **Jackson** - JSON processing
- **Optional script engines**:
  - Beetl - Template engine
  - ANTLR - Parser generator
  - JSONata4Java - JSON query and transformation language

## Project Structure

### Backend (Java)
- `love.disaster.j2j.web.J2JWebApplication` - Main application class
- `love.disaster.j2j.web.controller.TransformController` - REST API controller
- `love.disaster.j2j.web.service.TransformService` - Business logic service
- `love.disaster.j2j.web.dto` - Data transfer objects for API communication

### Frontend (Resources)
- `templates/` - Thymeleaf HTML templates
- `static/css/` - CSS stylesheets
- `static/js/` - JavaScript files
- `static/examples/` - JSON transformation examples

## Development

### Hot Reload
The application uses Spring Boot's hot reload feature for development, allowing changes to be reflected without restarting the server. DevTools is included in the runtime scope for this purpose.

### Frontend Development
- JavaScript files are located in `src/main/resources/static/js/`
- CSS files are located in `src/main/resources/static/css/`
- HTML templates are located in `src/main/resources/templates/`

### Backend Development
- Controllers are located in `src/main/java/love/disaster/j2j/web/controller/`
- Services are located in `src/main/java/love/disaster/j2j/web/service/`
- DTOs are located in `src/main/java/love/disaster/j2j/web/dto/`

### Testing
```bash
# Run web module tests
mvn test -pl j2j-web

# Run integration tests
mvn failsafe:integration-test -pl j2j-web
```

## Configuration

The application can be configured through:
1. **Application properties**: `src/main/resources/application.properties`
2. **Command line arguments**: When running with `java -jar`
3. **Environment variables**: System environment variables

### Key Configuration Options
- `server.port` - Port to run the web server on (default: 8080)
- `logging.level.*` - Logging levels for different packages
- `spring.thymeleaf.cache` - Whether to cache Thymeleaf templates (false in development)

## Security Considerations

- The web interface should not be exposed to untrusted networks without proper security measures
- Input validation is performed on all API endpoints
- JSON parsing uses Jackson's secure parsing features
- No sensitive data is stored or logged by the application

## Performance Optimization

- **Caching**: Transformation results can be cached for repeated operations
- **Connection Pooling**: Database connections (if used) are pooled for efficiency
- **Async Processing**: Long-running transformations can be processed asynchronously
- **Compression**: HTTP responses can be compressed to reduce bandwidth

## Troubleshooting

### Common Issues

1. **Port already in use**: Change the port using `--server.port=<port>`
2. **Memory issues**: Increase heap size with `-Xmx` JVM options
3. **Slow transformations**: Complex specifications may take time to process
4. **Editor issues**: Clear browser cache if CodeMirror behaves unexpectedly

### Logs
Logs are written to the console by default. For file logging, configure logback settings in `src/main/resources/logback-spring.xml`.

### Debugging
Enable debug logging with:
```bash
java -jar target/j2j-web-1.0-SNAPSHOT.jar --logging.level.love.disaster.j2j=DEBUG
```

## Extending the Web Interface

### Adding New Transformation Types
1. Add the transformation type to the TransformService
2. Update the frontend JavaScript to support the new type
3. Add the operation to the dropdown in the HTML template

### Adding New API Endpoints
1. Create a new controller method in TransformController
2. Implement the business logic in TransformService
3. Add appropriate DTOs for request/response data

### Customizing the UI
1. Modify HTML templates in `src/main/resources/templates/`
2. Update CSS in `src/main/resources/static/css/`
3. Enhance JavaScript in `src/main/resources/static/js/`

## Integration with Other Tools

The web interface can be integrated with:
- CI/CD pipelines for automated testing
- Monitoring tools for performance tracking
- Authentication systems for access control
- External APIs for enhanced functionality

## API Documentation

### TransformRequest DTO
```java
public class TransformRequest {
    private String input;       // Input JSON string
    private String spec;        // Transformation specification
    private String operation;   // Operation type (shift, default, etc.)
}
```

### TransformResponse DTO
```java
public class TransformResponse {
    private String result;      // Transformation result
    private boolean success;    // Success flag
    private String error;       // Error message if any
    private long executionTime; // Execution time in milliseconds
}
```

### ValidationResult DTO
```java
public class ValidationResult {
    private boolean valid;      // Validation result
    private String message;     // Validation message
    private String details;     // Detailed error information
}
```

## Example Transformations

### Shift Operation
Input:
```json
{
  "user": {
    "name": "John Doe",
    "age": 35
  }
}
```

Specification:
```json
{
  "user": {
    "name": "person.name",
    "age": "person.age"
  }
}
```

Result:
```json
{
  "person": {
    "name": "John Doe",
    "age": 35
  }
}
```

### JSONata in Modify Operations
The web interface includes an example demonstrating how to use JSONata functions within modify transformations. This example shows how to:
- Calculate statistics from employee data
- Filter and group information using JSONata expressions
- Transform complex nested structures

To access this example:
1. Open the web interface at [http://www.disaster.love/j2j/](http://www.disaster.love/j2j/)
2. Select "JSONata in Modify Transformation" from the examples dropdown
3. Review the input data, specification, and expected output

The example demonstrates advanced JSONata functions like:
- `$avg()`, `$max()`, `$min()` for statistical calculations
- Filtering with expressions like `employees[salary > 80000]`
- Grouping with expressions like `employees{department: $count}`
- String operations and complex transformations

### Default Operation
Input:
```json
{
  "user": {
    "name": "John Doe"
  }
}
```

Specification:
```json
{
  "user": {
    "name": "Anonymous",
    "active": true
  }
}
```

Result:
```json
{
  "user": {
    "name": "John Doe",
    "active": true
  }
}
```

## Template Engine Integration

The web interface supports advanced template engines:

### Beetl Templates
```javascript
// Using Beetl template functions
var result = beetl(template, data);
```

### JSONata Expressions
```javascript
// Using JSONata query language
var result = jsonata(expression, data);
```

## Performance Monitoring

The application includes built-in performance monitoring:
- Execution time tracking for transformations
- Memory usage monitoring
- Request/response logging
- Error rate tracking

To enable detailed performance logging:
```bash
java -jar target/j2j-web-1.0-SNAPSHOT.jar --logging.level.love.disaster.j2j.web=DEBUG
```

## Deployment

### Docker Deployment
Create a Dockerfile:
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/j2j-web-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
docker build -t j2j-web .
docker run -p 8080:8080 j2j-web
```

### Cloud Deployment
The application can be deployed to:
- AWS Elastic Beanstalk
- Google Cloud Run
- Azure App Service
- Heroku
- Kubernetes clusters

## Monitoring and Logging

### Health Checks
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Log Configuration
Customize logging in `src/main/resources/logback-spring.xml`:
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/j2j-web.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

## Contributing to the Web Interface

To contribute to the web interface:

1. Fork the repository
2. Create a feature branch
3. Make your changes to the frontend or backend
4. Test thoroughly
5. Submit a pull request

### Frontend Development Guidelines
- Follow Bootstrap 5 conventions
- Use responsive design principles
- Ensure cross-browser compatibility
- Optimize for performance
- Maintain consistent styling

### Backend Development Guidelines
- Follow REST API best practices
- Implement proper error handling
- Write comprehensive unit tests
- Document API endpoints
- Maintain security best practices