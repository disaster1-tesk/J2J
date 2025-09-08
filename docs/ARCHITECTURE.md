# J2J Architecture

## Overview

J2J follows a modular monolithic architecture with a focus on extensibility and reusability. The project is composed of multiple Maven modules that encapsulate different functionalities while maintaining loose coupling between them.

## Module Architecture

```
j2j (parent)
├── j2j-dependencies  # Dependency management
├── j2j-core          # Core transformation logic
├── j2j-complete      # Factory classes and utilities
├── j2j-cli           # Command-line interface
└── j2j-web           # Web interface
```

### j2j-dependencies
This module centralizes dependency management for all other modules. It defines versions and scopes for all third-party libraries, ensuring consistency and avoiding version conflicts across the project.

### j2j-core
The foundation of the entire library, containing all core transformation classes:
- **Shiftr**: Repositions key-value pairs in JSON data
- **Defaultr**: Applies default values to missing data
- **Removr**: Removes key-value pairs based on patterns
- **Sortr**: Sorts keys alphabetically
- **Chainr**: Chains multiple transformations together
- **Modifier**: Applies custom functions to data values

### j2j-complete
Provides factory classes that make it easier to create and configure transformation chains. This module serves as a bridge between the core logic and other modules.

### j2j-cli
A command-line interface that allows users to perform JSON transformations directly from the terminal without writing Java code.

### j2j-web
A web-based interface for interactive JSON transformations, featuring real-time validation and a user-friendly editor.

## Design Patterns

### Chain of Responsibility
Used in transformation pipelines (e.g., [Chainr](../j2j-core/src/main/java/love/disaster/j2j/core/Chainr.java)) to process transformations sequentially.

### Builder Pattern
Used for constructing transformation specifications (e.g., [ShiftrSpecBuilder](../j2j-core/src/main/java/love/disaster/j2j/core/shiftr/ShiftrSpecBuilder.java)).

### Strategy Pattern
Used for different transformation operations (e.g., [Transform](../j2j-core/src/main/java/love/disaster/j2j/core/Transform.java), [Modifier](../j2j-core/src/main/java/love/disaster/j2j/core/Modifier.java)).

### Composite Pattern
Used in handling complex transformation specs (e.g., [CardinalityCompositeSpec](../j2j-core/src/main/java/love/disaster/j2j/core/cardinality/CardinalityCompositeSpec.java)).

## Data Flow

### Core Transformation Flow
1. Input JSON is parsed into Jackson Object/Array nodes
2. Transformation specification is parsed and validated
3. Transformation engine applies the specification to the input
4. Output is generated as Jackson Object/Array nodes
5. Result can be serialized back to JSON string

### Chainr Flow
1. Chainr specification is parsed as an array of operations
2. Each operation is instantiated with its specific specification
3. Input flows through each transformation in sequence
4. Output of each transformation becomes input to the next
5. Final output is returned

## Extensibility

### Custom Transformations
New transformations can be implemented by:
1. Implementing the [Transform](../j2j-core/src/main/java/love/disaster/j2j/core/Transform.java) interface
2. Optionally implementing the [SpecDriven](../j2j-core/src/main/java/love/disaster/j2j/core/SpecDriven.java) interface
3. Registering the transformation with Chainr

### Custom Modifier Functions
New modifier functions can be added by:
1. Extending the [Modifier](../j2j-core/src/main/java/love/disaster/j2j/core/Modifier.java) class
2. Implementing custom function logic
3. Registering functions with the modifier registry

## Performance Considerations

### Caching
- Transformation instances are designed to be reusable
- Specification parsing is done once during instantiation
- Intermediate results can be cached for repeated operations

### Memory Management
- Jackson's tree model is used for efficient JSON processing
- Object reuse patterns minimize garbage collection pressure
- Streaming capabilities are available for large documents

### Concurrency
- Core transformation classes are thread-safe
- Chainr instances can be shared across threads
- Stateless design enables horizontal scaling

## Security

### Input Validation
- JSON syntax validation before processing
- Specification validation to prevent malformed operations
- Size limits to prevent resource exhaustion

### Safe Execution
- No arbitrary code execution in core transformations
- Controlled access to system resources
- Secure parsing with Jackson's built-in protections

## Error Handling

### Exception Hierarchy
```
JoltException
├── SpecException
└── TransformException
```

### Error Propagation
- Specific exceptions for different error types
- Detailed error messages for debugging
- Context preservation in exception stacks

## Testing Strategy

### Unit Testing
- Comprehensive coverage of core transformation logic
- Edge case testing for each transformation type
- Performance benchmarks for regression detection

### Integration Testing
- End-to-end transformation workflows
- CLI command execution verification
- Web API integration testing

### Test Data Management
- Realistic JSON samples for various domains
- Edge case data for boundary testing
- Performance test datasets for benchmarking

## Deployment Architecture

### Library Usage
- Maven dependency integration
- Minimal transitive dependencies
- Clear API contracts

### CLI Deployment
- Standalone JAR distribution
- Cross-platform compatibility
- Simple execution model

### Web Deployment
- Spring Boot embedded server
- Container-ready packaging
- Configurable runtime properties