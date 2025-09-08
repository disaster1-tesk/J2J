# j2j-core

The core module of the J2J library containing all the fundamental JSON transformation logic and utilities.

[![Maven Central](https://img.shields.io/maven-central/v/love.disaster/j2j-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22love.disaster%22%20AND%20a:%22j2j-core%22)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/java-17+-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk17-downloads.html)

## Overview

This module provides the core transformation capabilities including:

- **[Shiftr](#shiftr)**: Reposition key-value pairs in JSON data according to a specification
- **[Defaultr](#defaultr)**: Apply default values to JSON data where values are missing
- **[Removr](#removr)**: Remove key-value pairs from JSON data based on patterns
- **[Sortr](#sortr)**: Sort keys in JSON data alphabetically
- **[Chainr](#chainr)**: Chain multiple transformations together for complex operations
- **[Modifier](#modifier)**: Apply custom functions to JSON data values

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

## 🔧 Core Transformations

### Shiftr
Main class for shifting operations. It allows you to move data from one location in a JSON document to another.

```java
Object spec = JsonUtils.jsonToObject("{ \"foo\": \"bar\" }");
Shiftr shiftr = new Shiftr(spec);
Object transformed = shiftr.transform(input);
```

### Defaultr
Main class for applying default values to JSON data when values are missing or null.

```java
Object spec = JsonUtils.jsonToObject("{ \"foo\": \"defaultBar\" }");
Defaultr defaultr = new Defaultr(spec);
Object transformed = defaultr.transform(input);
```

### Removr
Main class for removing key-value pairs from JSON data based on matching patterns.

```java
Object spec = JsonUtils.jsonToObject("{ \"foo\": \"\" }");
Removr removr = new Removr(spec);
Object transformed = removr.transform(input);
```

### Sortr
Main class for sorting keys in JSON data alphabetically.

```java
Sortr sortr = new Sortr();
Object transformed = sortr.transform(input);
```

### Chainr
Main class for chaining transformations together. This allows complex transformations by applying multiple operations in sequence.

```java
Object spec = JsonUtils.jsonToObject("[{ \"operation\": \"shift\", \"spec\": { \"*\": \"&\" } }]");
Chainr chainr = Chainr.fromSpec(spec);
Object transformed = chainr.transform(input);
```

### Modifier
Main class for applying custom functions to JSON data values. Supports various built-in functions and allows custom implementations.

```java
Object spec = JsonUtils.jsonToObject("{ \"foo\": \"=toUpper\" }");
Modifier modifier = new Modifier(spec);
Object transformed = modifier.transform(input);
```

## 📚 Documentation

- [User Guide](../docs/USER_GUIDE.md) - Comprehensive usage guide
- [API Reference](../docs/REFERENCE.md) - Complete technical reference
- [Examples](../docs/EXAMPLES.md) - Practical use cases
- [Tutorials](../docs/tutorials/) - Step-by-step learning guides

## 📦 Dependencies

- **Jackson** (2.13.4) - JSON processing
- **SLF4J** (1.7.36) - Logging API
- **jakarta.inject** (2.0.1) - Dependency injection annotations
- **Optional script engines**:
  - Beetl (3.15.0.RELEASE) - Template engine
  - ANTLR (4.12.0) - Parser generator
  - JSONata4Java (2.4.1) - JSON query and transformation language

## 🧪 Testing

The module includes comprehensive unit tests to ensure reliability and correctness of transformations.

```bash
# Run core tests
mvn test -pl j2j-core

# Run specific test class
mvn test -pl j2j-core -Dtest=ShiftrTest
```

## 🏗️ Building

```bash
# Build the module
mvn clean install -pl j2j-core

# Package the module
mvn clean package -pl j2j-core
```

## 🤝 Contributing

See the [main contributing guide](../CONTRIBUTING.md) for details on how to contribute to this module.

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](../LICENSE) file for details.