# J2J User Guide

This comprehensive guide covers all aspects of using the J2J library for JSON transformations.

## Table of Contents

- [Core Concepts](#core-concepts)
- [Transformation Types](#transformation-types)
- [Specifications](#specifications)
- [Advanced Features](#advanced-features)
- [Integration](#integration)
- [Best Practices](#best-practices)

## Core Concepts

J2J provides several core transformation operations that can be used individually or chained together:

### Shiftr
Repositions key-value pairs in JSON data according to a specification.

### Defaultr
Applies default values to JSON data where values are missing.

### Removr
Removes key-value pairs from JSON data based on patterns.

### Sortr
Sorts keys in JSON data alphabetically.

### Chainr
Chains multiple transformations together for complex operations.

### Modifier
Applies custom functions to JSON data values.

## Transformation Types

### Shift Transformation

The shift transformation allows you to move data from one location in a JSON document to another:

```java
String inputJson = "{ \"user\": { \"name\": \"John\", \"age\": 30 } }";
String specJson = "{ \"user\": { \"name\": \"personName\" } }";

Object input = JsonUtils.jsonToObject(inputJson);
Object spec = JsonUtils.jsonToObject(specJson);

Shiftr shiftr = new Shiftr(spec);
Object transformed = shiftr.transform(input);
```

### Default Transformation

The default transformation applies default values to JSON data when values are missing or null:

```java
String inputJson = "{ \"user\": { \"name\": \"John\" } }";
String specJson = "{ \"user\": { \"name\": \"Anonymous\", \"active\": true } }";

Object input = JsonUtils.jsonToObject(inputJson);
Object spec = JsonUtils.jsonToObject(specJson);

Defaultr defaultr = new Defaultr(spec);
Object transformed = defaultr.transform(input);
```

### Remove Transformation

The remove transformation removes key-value pairs from JSON data based on matching patterns:

```java
String inputJson = "{ \"user\": { \"name\": \"John\", \"temp\": \"temporary\" } }";
String specJson = "{ \"user\": { \"temp\": \"\" } }";

Object input = JsonUtils.jsonToObject(inputJson);
Object spec = JsonUtils.jsonToObject(specJson);

Removr removr = new Removr(spec);
Object transformed = removr.transform(input);
```

### Sort Transformation

The sort transformation sorts keys in JSON data alphabetically:

```java
String inputJson = "{ \"zoo\": \"animal\", \"apple\": \"fruit\" }";

Object input = JsonUtils.jsonToObject(inputJson);

Sortr sortr = new Sortr();
Object transformed = sortr.transform(input);
```

### Chain Transformation

The chain transformation allows complex transformations by applying multiple operations in sequence:

```java
String specJson = "[" +
    "{ \"operation\": \"shift\", \"spec\": { \"user\": { \"name\": \"personName\" } } }," +
    "{ \"operation\": \"default\", \"spec\": { \"personName\": \"Anonymous\" } }" +
    "]";

Object spec = JsonUtils.jsonToObject(specJson);
Chainr chainr = Chainr.fromSpec(spec);
Object transformed = chainr.transform(input);
```

### Modifier Transformation

The modifier transformation applies custom functions to JSON data values:

```java
String inputJson = "{ \"text\": \"hello world\" }";
String specJson = "{ \"text\": \"=toUpper\" }";

Object input = JsonUtils.jsonToObject(inputJson);
Object spec = JsonUtils.jsonToObject(specJson);

Modifier modifier = new Modifier(spec);
Object transformed = modifier.transform(input);
```

## Specifications

J2J uses JSON-based specifications to define transformations. These specifications follow specific syntax rules depending on the transformation type.

### Specification Structure

Each transformation type has its own specification structure:

- **Shiftr**: Defines how to reposition data
- **Defaultr**: Defines default values to apply
- **Removr**: Defines patterns for removal
- **Modifier**: Defines functions to apply to values

### Wildcards

J2J supports several wildcard patterns for flexible matching:

- `*`: Matches any single key
- `**`: Matches any hierarchy of keys
- `&`: References the matched key
- `$`: References the matched key's value

## Advanced Features

### Template Engine Integration

J2J supports integration with template engines:

#### Beetl Templates
```java
// Using Beetl template functions
var result = beetl(template, data);
```

#### JSONata Expressions
```java
// Using JSONata query language
var result = jsonata(expression, data);
```

### Custom Functions

You can create custom modifier functions by extending the Modifier class:

```java
public class CustomFunction extends Function {
    @Override
    public Object apply(Object... args) {
        // Custom implementation
        return processedValue;
    }
}
```

## Integration

### Java Integration

Using J2J programmatically in Java applications:

```java
// Import required classes
import love.disaster.j2j.core.*;
import love.disaster.j2j.core.utils.JsonUtils;

// Use transformations as needed
Chainr chainr = Chainr.fromSpec(specification);
Object result = chainr.transform(inputData);
```

### Spring Boot Integration

Integrating J2J with Spring Boot applications:

```java
@Configuration
public class J2JConfig {
    
    @Bean
    public Chainr chainr() {
        return Chainr.fromSpec(specification);
    }
}
```

### Web Interface

The web interface provides an interactive way to experiment with transformations:

1. Navigate to `http://www.disaster.love/j2j/` or run locally
2. Enter your JSON data
3. Define your transformation specification
4. View the results in real-time

## Best Practices

### Performance Optimization

1. **Reuse instances**: Transformation instances are thread-safe and should be reused when possible
2. **Validate specifications**: Always validate transformation specifications before use
3. **Handle exceptions**: Properly handle transformation exceptions in production code
4. **Use Chainr for complex operations**: For multiple transformations, use Chainr instead of chaining manually

### Security Considerations

1. **Input validation**: Always validate input JSON data
2. **Specification validation**: Validate transformation specifications
3. **Resource limits**: Set appropriate limits for processing large documents
4. **Sandboxing**: When using template engines, consider sandboxing for security

### Error Handling

Proper error handling is crucial for production applications:

```java
try {
    Object result = chainr.transform(input);
} catch (TransformException e) {
    // Handle transformation errors
    logger.error("Transformation failed", e);
} catch (SpecException e) {
    // Handle specification errors
    logger.error("Invalid specification", e);
}
```

## Troubleshooting

### Common Issues

1. **Invalid specification format**: Ensure your specification is valid JSON and follows the expected structure
2. **ClassCastException**: Check that input data matches expected types
3. **Performance issues**: For large documents, consider breaking transformations into smaller steps
4. **Memory usage**: Reuse transformation instances to minimize memory allocation

### Debugging Tips

1. Enable debug logging to see detailed transformation steps
2. Use simple specifications during development and gradually increase complexity
3. Test transformations with various input data to ensure robustness
4. Use the web interface for interactive testing and debugging