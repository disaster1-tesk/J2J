# j2j-core

The core module of the J2J library containing all the fundamental JSON transformation logic and utilities.

## Overview

This module provides the core transformation capabilities including:

- **Shiftr**: Reposition key-value pairs in JSON data according to a specification
- **Defaultr**: Apply default values to JSON data where values are missing
- **Removr**: Remove key-value pairs from JSON data based on patterns
- **Sortr**: Sort keys in JSON data alphabetically
- **Chainr**: Chain multiple transformations together for complex operations
- **Modifier**: Apply custom functions to JSON data values

## Key Classes

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

## Dependencies

- **Jackson** (2.13.4) - JSON processing
- **SLF4J** (1.7.36) - Logging API
- **javax.inject** (1) - Dependency injection annotations
- **Optional script engines**:
  - Beetl (3.15.0.RELEASE) - Template engine
  - ANTLR (4.12.0) - Parser generator
  - JSONata4Java (2.4.1) - JSON query and transformation language

## Usage Examples

### Simple Transformation
```java
// Example of using Shiftr transformation
String inputJson = "{ \"user\": { \"name\": \"John\", \"age\": 30 } }";
String specJson = "{ \"user\": { \"name\": \"personName\" } }";

Object input = JsonUtils.jsonToObject(inputJson);
Object spec = JsonUtils.jsonToObject(specJson);

Shiftr shiftr = new Shiftr(spec);
Object transformed = shiftr.transform(input);
String result = JsonUtils.toJsonString(transformed);
// Result: { "personName": "John" }
```

### Chained Transformations
```java
// Example of chaining multiple transformations
String specJson = "[" +
    "{ \"operation\": \"shift\", \"spec\": { \"user\": { \"name\": \"personName\" } } }," +
    "{ \"operation\": \"default\", \"spec\": { \"personName\": \"Anonymous\" } }" +
    "]";

Object spec = JsonUtils.jsonToObject(specJson);
Chainr chainr = Chainr.fromSpec(spec);
Object transformed = chainr.transform(input);
```

## Package Structure

- `love.disaster.j2j.core` - Main transformation classes (Shiftr, Defaultr, etc.)
- `love.disaster.j2j.core.cardinality` - Cardinality transformation utilities
- `love.disaster.j2j.core.chainr` - Chainr implementation details
- `love.disaster.j2j.core.common` - Common utilities and base classes
- `love.disaster.j2j.core.defaultr` - Defaultr implementation details
- `love.disaster.j2j.core.exception` - Custom exceptions
- `love.disaster.j2j.core.modifier` - Modifier implementation details
- `love.disaster.j2j.core.shiftr` - Shiftr implementation details
- `love.disaster.j2j.core.spec` - Specification handling
- `love.disaster.j2j.core.traversr` - Tree traversal utilities
- `love.disaster.j2j.core.utils` - Core utilities

## Testing

The module includes comprehensive unit tests to ensure reliability and correctness of transformations.

```bash
# Run core tests
mvn test -pl j2j-core

# Run specific test class
mvn test -pl j2j-core -Dtest=ShiftrTest
```

## Performance Considerations

- Reuse transformation instances when processing multiple documents with the same specification
- Chainr instances are thread-safe and can be reused across threads
- For high-performance scenarios, consider using the streaming API when available

## Extending Functionality

The library is designed to be extensible:

1. Custom transformations can implement the `Transform` interface
2. Custom modifier functions can extend the `Modifier` class
3. New operations can be added to Chainr through custom operation definitions

## Integration with Other Modules

- **j2j-cli** uses j2j-core for all transformation operations
- **j2j-complete** provides factory classes for easier j2j-core usage
- **j2j-web** uses j2j-core as the backend transformation engine