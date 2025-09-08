# J2J Examples

This directory will contain practical examples of using J2J transformations. Currently, examples are located in other parts of the project.

## 📚 Current Examples

### Core Module Examples
Examples can be found in the core module test resources:
- [j2j-core/src/test/resources](../../j2j-core/src/test/resources) - Unit test examples

### Web Interface Examples
Examples available in the web interface:
- [j2j-web/src/main/resources/static/examples](../../j2j-web/src/main/resources/static/examples) - Web UI examples

## 📖 Example Categories

The examples typically cover:

### Basic Transformations
- Simple key repositioning
- Applying default values
- Removing fields
- Sorting keys

### Advanced Transformations
- Array manipulation
- Nested structure transformation
- Wildcard usage
- Context-aware transformations

### Chained Operations
- Multi-step transformation pipelines
- Data migration scenarios
- API response formatting

### Modifier Functions
- String operations
- Mathematical calculations
- Custom function usage

### Real-World Scenarios
- E-commerce data processing
- User profile normalization
- Log data structuring

## 🚀 Using Examples

### Try Online
The easiest way to experiment with examples is using the [online web interface](http://www.disaster.love/j2j/).

### Run Locally
```bash
# Start the web interface
mvn spring-boot:run -pl j2j-web

# Navigate to http://localhost:8080
# Examples are available in the dropdown menu
```

### Command Line
```bash
# Use examples with the CLI
java -jar j2j-cli.jar transform -t shift -i input.json -s spec.json -o output.json
```

## 📚 Related Documentation

- [Examples in User Guide](../EXAMPLES.md) - Practical use cases
- [First Transformation Tutorial](../tutorials/first-transformation.md) - Getting started
- [Web Interface Documentation](../../j2j-web/) - Interactive examples

## 🤝 Contributing Examples

We welcome contributions of new examples! To contribute:

1. Fork the repository
2. Add your example files
3. Update documentation
4. Submit a pull request

Each example should include:
- Clear input data
- Transformation specification
- Expected output
- Explanation of the transformation logic