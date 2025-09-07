# j2j-complete

Factory classes for creating transformation chains in the J2J library.

## Overview

This module provides factory classes that make it easier to create and configure transformation chains using the j2j-core library. It serves as a bridge between the core transformation logic and other modules that need to use these transformations, simplifying the process of creating complex transformation workflows.

## Key Classes

### ChainrFactory
The main factory class for creating Chainr instances with predefined configurations. This factory simplifies the creation of transformation chains and provides utility methods for common transformation patterns.

```java
// Example of using the factory to create a transformation chain
Object spec = JsonUtils.jsonToObject("[{ \"operation\": \"shift\", \"spec\": { \"*\": \"&\" } }]");
Chainr chainr = ChainrFactory.createChainr(spec);
Object transformed = chainr.transform(input);
```

## Usage

### Creating Transformation Chains
```java
// Create a chain from a specification
Object chainSpec = JsonUtils.jsonToObject("[" +
    "{ \"operation\": \"shift\", \"spec\": { \"user\": { \"name\": \"userName\" } } }," +
    "{ \"operation\": \"default\", \"spec\": { \"userName\": \"Anonymous\" } }" +
    "]");
    
Chainr chainr = ChainrFactory.createChainr(chainSpec);
Object result = chainr.transform(inputData);
```

### Using Predefined Transformation Patterns
```java
// Create a chain for common data cleaning operations
Chainr cleaningChain = ChainrFactory.createDataCleaningChain();
Object cleanedData = cleaningChain.transform(rawData);
```

## Dependencies

- **j2j-core** (1.0-SNAPSHOT) - Core transformation logic
- **TestNG** - Testing framework (test scope)

## Integration

This module is primarily used by:
- **j2j-cli** - Command-line interface for JSON transformations
- **j2j-web** - Web interface for interactive JSON transformations

The factory classes in this module abstract away the complexity of creating and configuring transformation chains, making it easier for other modules to leverage the full power of the j2j-core library.

## Factory Methods

### createChainr(Object spec)
Creates a Chainr instance from a specification object. The specification can be a single transformation or an array of transformations.

### createChainr(String specJson)
Creates a Chainr instance from a JSON string specification.

### createDefaultChainr()
Creates a Chainr instance with default transformation settings.

## Example Use Cases

### Data Migration
When migrating data from one format to another, the ChainrFactory can create complex transformation chains that handle field mapping, default values, and data cleaning.

### API Response Formatting
Web services can use the factory to create transformation chains that format internal data structures into the JSON format expected by API clients.

### Data Pipeline Processing
In data processing pipelines, the factory can create reusable transformation chains that process data as it flows through different stages.

## Extending the Factory

New factory methods can be added to support common transformation patterns:

```java
public class ChainrFactory {
    // Add a new factory method for a common use case
    public static Chainr createUserNormalizationChain() {
        // Implementation for user data normalization
    }
}
```

## Testing

The module includes tests to ensure the factory methods work correctly:

```bash
# Run complete module tests
mvn test -pl j2j-complete
```

## Best Practices

1. **Reuse Chainr instances**: Once created, Chainr instances are thread-safe and can be reused across multiple transformations.

2. **Cache complex chains**: For frequently used transformation chains, consider caching the Chainr instances to avoid repeated parsing of specifications.

3. **Handle exceptions appropriately**: When using the factory, be prepared to handle exceptions that may occur during chain creation or transformation.

4. **Validate specifications**: Before creating a chain, validate that the specification is well-formed to catch errors early.

## Performance Considerations

- Factory methods perform specification parsing, so reuse Chainr instances when processing multiple documents with the same specification
- Complex specifications may take time to parse, so consider caching for frequently used transformations
- The created Chainr instances are optimized for performance and can be safely used in multi-threaded environments