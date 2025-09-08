# J2J - Java to JSON Transformation Library

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java 8+](https://img.shields.io/badge/java-17+-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Maven](https://img.shields.io/badge/maven-3.6+-blue.svg)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/docker-latest-blue.svg)](https://www.docker.com/)

J2J is a powerful Java-based JSON transformation library designed to provide flexible and extensible tools for manipulating JSON data. Built on the principles of the popular JOLT library, J2J offers enhanced capabilities for data integration and transformation pipelines.

> Try the [online web interface](http://www.disaster.love/j2j/) to experiment with JSON transformations without installing anything!

## 🚀 Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>love.disaster</groupId>
    <artifactId>j2j-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Simple Usage

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

## 📚 Documentation

- [Getting Started Guide](docs/GETTING_STARTED.md) - Installation and quick start
- [User Guide](docs/USER_GUIDE.md) - Comprehensive usage guide
- [Tutorials](docs/tutorials/) - Step-by-step learning guides
- [API Reference](docs/REFERENCE.md) - Complete technical reference
- [Examples](docs/EXAMPLES.md) - Practical use cases

## 🛠️ Key Features

- **Multiple Transformation Operations**: Shift, Default, Remove, Sort, and Chain operations
- **Declarative Specifications**: Define transformations using JSON specifications
- **Command-Line Interface**: Use transformations directly from the terminal
- **Web Interface**: Interactive web-based transformation tool at [http://www.disaster.love/j2j/](http://www.disaster.love/j2j/)
- **Extensible Architecture**: Create custom transformations and functions
- **Template Engine Integration**: Support for Beetl and JSONata expressions
- **Comprehensive Validation**: Built-in JSON and specification validation
- **Performance Optimized**: Efficient processing for large JSON documents
- **Docker Support**: Containerized deployment for easy distribution

## 📦 Modules

| Module | Description |
|--------|-------------|
| [j2j-core](j2j-core/) | Core transformation logic and utilities |
| [j2j-cli](j2j-cli/) | Command-line interface for JSON transformations |
| [j2j-complete](j2j-complete/) | Factory classes for transformation chains |
| [j2j-dependencies](j2j-dependencies/) | Centralized dependency management |
| [j2j-web](j2j-web/) | Web interface for interactive JSON transformations |

## 🚀 Try It Online

Experiment with JSON transformations without installing anything:

👉 **[http://www.disaster.love/j2j/](http://www.disaster.love/j2j/)**

## 🐳 Docker Deployment

```bash
# Build the project
mvn clean package -DskipTests

# Build the Docker image
docker build -t j2j-web:latest .

# Run the container
docker run -p 8080:8080 j2j-web:latest
```

## 📖 Learning Resources

- [First Transformation Tutorial](docs/tutorials/first-transformation.md)
- [Shiftr Tutorial](docs/tutorials/shiftr.md)
- [Modifier Tutorial](docs/tutorials/modifier.md)
- [Chainr Tutorial](docs/tutorials/chainr.md)

## 🏗️ Building from Source

```bash
# Clone the repository
git clone https://github.com/your-username/j2j.git
cd j2j

# Build the entire project
mvn clean install

# Run tests
mvn test

# Package for distribution
mvn package
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a pull request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by the [JOLT](https://github.com/bazaarvoice/jolt) library
- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- JSON processing powered by [Jackson](https://github.com/FasterXML/jackson)