# J2J - Java to JSON Transformation Library

J2J is a Java-based JSON transformation library designed to provide flexible and extensible tools for manipulating JSON data. It is particularly useful in data integration and transformation pipelines, offering both programmatic APIs and user-friendly interfaces.

## Project Modules

This project is organized into several Maven modules, each serving a specific purpose:

1. **[j2j-core](j2j-core/README.md)** - Core transformation logic and utilities
2. **[j2j-cli](j2j-cli/README.md)** - Command-line interface for JSON transformations
3. **[j2j-complete](j2j-complete/README.md)** - Factory classes for transformation chains
4. **[j2j-dependencies](j2j-dependencies/README.md)** - Dependency management module
5. **[j2j-web](j2j-web/README.md)** - Web interface for interactive JSON transformations

## Features

- JSON transformation using declarative specifications
- JSON comparison (Diffy)
- JSON sorting
- JSON key removal, shifting, and default value setting
- Support for complex transformation chains
- Template engine integration (Beetl and JSONata4Java)
- Real-time validation and transformation in web interface
- Command-line tools for automation

## Quick Start

```bash
# Clone the repository
git clone <repository-url>

# Build the entire project
mvn clean install

# Run the web interface
mvn spring-boot:run -pl j2j-web

# Or run the CLI tool
java -jar j2j-cli/target/j2j-cli-1.0-SNAPSHOT.jar --help
```

## Technology Stack

- **Language**: Java 8
- **Build Tool**: Maven
- **Core Libraries**: Custom JSON transformation utilities based on JOLT concepts
- **Web Framework**: Spring Boot 2.7.18
- **Frontend Libraries**: CodeMirror, Bootstrap
- **JSON Processing**: Jackson 2.13.4

## Module Overview

### j2j-core
The foundation of the entire library, containing all core transformation classes like Shiftr, Defaultr, Removr, Sortr, and Chainr. This module implements the actual JSON transformation logic.

### j2j-cli
A command-line interface that allows users to perform JSON transformations directly from the terminal without writing Java code.

### j2j-complete
Provides factory classes that make it easier to create and configure transformation chains, serving as a bridge between core logic and other modules.

### j2j-dependencies
Centralizes dependency management for all J2J modules, ensuring consistent versions and avoiding conflicts.

### j2j-web
A web-based interface for interactive JSON transformations, featuring real-time validation and a user-friendly editor.

## Usage Examples

### Using the Core API
```java
// Create a transformation specification
Object spec = JsonUtils.jsonToObject("{ \"*\": \"&\" }");

// Create a Chainr instance
Chainr chainr = Chainr.fromSpec(spec);

// Transform JSON data
Object input = JsonUtils.jsonToObject("{ \"foo\": \"bar\" }");
Object output = chainr.transform(input);

// Convert back to JSON string
String result = JsonUtils.toJsonString(output);
```

### Using the CLI
```bash
# Transform JSON using a specification file
java -jar j2j-cli.jar transform -t chain -i input.json -s spec.json -o output.json

# Sort JSON keys
java -jar j2j-cli.jar sort -i input.json -o output.json
```

## Development

### Building the Project
```bash
# Build all modules
mvn clean install

# Build a specific module
mvn clean install -pl j2j-core
```

### Running Tests
```bash
# Run tests for all modules
mvn test

# Run tests for a specific module
mvn test -pl j2j-core
```

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.