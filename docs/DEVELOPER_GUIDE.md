# J2J Developer Guide

This guide is for developers who want to contribute to J2J or extend its functionality.

## Table of Contents

- [Project Structure](#project-structure)
- [Development Setup](#development-setup)
- [Building the Project](#building-the-project)
- [Testing](#testing)
- [Code Style](#code-style)
- [Contributing](#contributing)
- [Extending J2J](#extending-j2j)

## Project Structure

J2J follows a modular architecture with the following modules:

```
j2j/
├── j2j-core/          # Core transformation logic
├── j2j-cli/           # Command-line interface
├── j2j-web/           # Web interface
├── j2j-complete/      # Factory classes
├── j2j-dependencies/  # Dependency management
└── docs/              # Documentation
```

### Module Details

#### j2j-core
Contains all the fundamental JSON transformation logic and utilities.

Key packages:
- `love.disaster.j2j.core` - Main transformation classes
- `love.disaster.j2j.core.modifier` - Modifier implementation
- `love.disaster.j2j.core.shiftr` - Shiftr implementation
- `love.disaster.j2j.core.utils` - Core utilities

#### j2j-cli
Command-line interface for JSON transformations.

#### j2j-web
Web interface for interactive JSON transformations.

#### j2j-complete
Factory classes for easier usage of core functionality.

#### j2j-dependencies
Centralized dependency management.

## Development Setup

### Prerequisites

- JDK 8 or higher
- Maven 3.6 or higher
- Git

### Clone the Repository

```bash
git clone https://github.com/your-username/j2j.git
cd j2j
```

### Import into IDE

The project uses Maven, so you can import it into any IDE that supports Maven projects:
- IntelliJ IDEA: File → Open → Select pom.xml
- Eclipse: File → Import → Maven → Existing Maven Projects
- VS Code: Open folder containing the project

## Building the Project

### Build All Modules

```bash
# Build the entire project
mvn clean install

# Build without running tests (faster)
mvn clean install -DskipTests
```

### Build Specific Modules

```bash
# Build only the core module
mvn clean install -pl j2j-core

# Build core and CLI modules
mvn clean install -pl j2j-core,j2j-cli
```

### Package for Distribution

```bash
# Package all modules
mvn clean package

# Package without running tests
mvn clean package -DskipTests
```

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl j2j-core

# Run a specific test class
mvn test -pl j2j-core -Dtest=ShiftrTest
```

### Writing Tests

Tests should follow these guidelines:

1. **Use descriptive test method names**
2. **Follow the Arrange-Act-Assert pattern**
3. **Test edge cases and error conditions**
4. **Keep tests independent and isolated**
5. **Use appropriate assertions**

Example test:

```java
@Test
public void shouldShiftSimpleKeyValue() {
    // Arrange
    String inputJson = "{\"name\":\"John\"}";
    String specJson = "{\"name\":\"personName\"}";
    
    Object input = JsonUtils.jsonToObject(inputJson);
    Object spec = JsonUtils.jsonToObject(specJson);
    Shiftr shiftr = new Shiftr(spec);
    
    // Act
    Object result = shiftr.transform(input);
    
    // Assert
    String resultJson = JsonUtils.toJsonString(result);
    assertEquals("{\"personName\":\"John\"}", resultJson);
}
```

## Code Style

### Java Code Conventions

J2J follows standard Java coding conventions:

1. **Naming**: Use camelCase for variables and methods, PascalCase for classes
2. **Formatting**: Use 4 spaces for indentation (no tabs)
3. **Documentation**: All public methods should have JavaDoc comments
4. **Line length**: Keep lines under 120 characters
5. **Braces**: Use braces for all control structures

### Example

```java
/**
 * Performs a shift transformation on the input data.
 */
public class Shiftr implements Transform {
    
    private final Object spec;
    
    /**
     * Creates a new Shiftr instance with the given specification.
     * 
     * @param spec the transformation specification
     */
    public Shiftr(Object spec) {
        this.spec = spec;
    }
    
    /**
     * Transforms the input data according to the specification.
     * 
     * @param input the input data to transform
     * @return the transformed data
     */
    @Override
    public Object transform(Object input) {
        // Implementation here
        return transformedData;
    }
}
```

## Contributing

We welcome contributions to J2J! Here's how you can contribute:

### Reporting Issues

1. Check existing issues to avoid duplicates
2. Provide a clear, descriptive title
3. Include steps to reproduce the issue
4. Specify your environment (Java version, OS, etc.)
5. Include any relevant error messages or logs

### Submitting Pull Requests

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Make your changes
4. Add tests for your changes
5. Update documentation if needed
6. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
7. Push to the branch (`git push origin feature/AmazingFeature`)
8. Open a pull request

### Code Review Process

All pull requests are reviewed by maintainers. The review process focuses on:

1. **Correctness**: Does the code work as intended?
2. **Clarity**: Is the code easy to understand?
3. **Efficiency**: Is the implementation efficient?
4. **Test coverage**: Are there adequate tests?
5. **Documentation**: Is the code properly documented?

## Extending J2J

### Custom Transformations

To create a custom transformation, implement the `Transform` interface:

```java
public class CustomTransform implements Transform {
    
    private final Object spec;
    
    public CustomTransform(Object spec) {
        this.spec = spec;
    }
    
    @Override
    public Object transform(Object input) {
        // Your custom transformation logic here
        return transformedInput;
    }
}
```

### Custom Modifier Functions

To add custom modifier functions, extend the `Function` class:

```java
public class CustomFunction extends Function {
    
    @Override
    public Object apply(Object... args) {
        // Your custom function logic here
        return result;
    }
}
```

### Adding New Operations to Chainr

To add new operations to Chainr, you need to:

1. Create your transformation class
2. Register it with the Chainr factory
3. Update the specification parsing logic

## Release Process

Releases are managed by project maintainers. The process includes:

1. **Version bump**: Update version numbers in pom.xml files
2. **Changelog**: Update CHANGELOG.md with release notes
3. **Tag**: Create a Git tag for the release
4. **Build**: Build and package the release
5. **Publish**: Publish to Maven Central (if applicable)

## Community

### Communication Channels

- GitHub Issues: For bug reports and feature requests
- Pull Requests: For code contributions
- Discussions: For general questions and community interaction

### Code of Conduct

All contributors are expected to follow our [Code of Conduct](../CODE_OF_CONDUCT.md).

## Getting Help

If you need help with development:

1. Check the existing documentation
2. Look at existing code for examples
3. Search existing issues and pull requests
4. Create a new issue if you can't find an answer