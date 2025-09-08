# Getting Started with J2J

J2J is a powerful Java-based JSON transformation library designed to provide flexible and extensible tools for manipulating JSON data. This guide will help you quickly get up and running with J2J.

## Prerequisites

- Java 8 or higher
- Maven 3.6 or higher (for building from source)

## Installation

### Maven Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>love.disaster</groupId>
    <artifactId>j2j-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Gradle Dependency

If you're using Gradle, add this to your `build.gradle`:

```gradle
implementation 'love.disaster:j2j-core:1.0-SNAPSHOT'
```

## Quick Start

### Programmatic Usage

Here's a simple example to get you started with J2J transformations:

```java
import love.disaster.j2j.core.chainr.Chainr;
import love.disaster.j2j.core.utils.JsonUtils;

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

### Command-Line Usage

J2J also provides a command-line interface for transformations:

```bash
# Transform JSON using a specification file
java -jar j2j-cli.jar transform -t chain -i input.json -s spec.json -o output.json

# Sort JSON keys
java -jar j2j-cli.jar sort -i input.json -o output.json
```

### Web Interface

You can try out the J2J web interface online at [http://www.disaster.love/j2j/](http://www.disaster.love/j2j/) or run it locally:

```bash
# Run the web interface
mvn spring-boot:run -pl j2j-web

# Or run the built JAR
java -jar j2j-web/target/j2j-web-1.0-SNAPSHOT.jar
```

Then navigate to `http://localhost:8080` in your browser.

## Next Steps

- Explore the [Tutorials](tutorials/) to learn more about specific transformation types
- Check out the [Examples](EXAMPLES.md) for practical use cases
- Review the [API Documentation](api/) for detailed class information
- Learn about [Deployment Options](DEPLOYMENT.md) for production use

## Getting Help

If you need help with J2J:

1. Check the [Examples](EXAMPLES.md) for similar use cases
2. Review the [API Documentation](api/) for detailed class information
3. Try the online web interface at [http://www.disaster.love/j2j/](http://www.disaster.love/j2j/) to experiment with transformations
4. Search existing issues on the GitHub repository
5. Create a new issue if you can't find an answer