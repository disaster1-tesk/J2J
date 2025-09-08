# J2J Reference Guide

This comprehensive reference guide provides detailed information about all aspects of the J2J library.

## Table of Contents

- [API Reference](#api-reference)
- [Transformation Specifications](#transformation-specifications)
- [Command-Line Interface](#command-line-interface)
- [Web Interface](#web-interface)
- [Configuration Options](#configuration-options)
- [Error Codes](#error-codes)

## API Reference

### Core Classes

#### Shiftr
Repositions key-value pairs in JSON data according to a specification.

```java
public class Shiftr implements Transform {
    public Shiftr(Object spec)
    public Object transform(Object input)
}
```

**Methods:**
- `Shiftr(Object spec)`: Constructor that takes a specification
- `transform(Object input)`: Applies the shift transformation to the input

#### Defaultr
Applies default values to JSON data where values are missing.

```java
public class Defaultr implements Transform {
    public Defaultr(Object spec)
    public Object transform(Object input)
}
```

**Methods:**
- `Defaultr(Object spec)`: Constructor that takes a specification
- `transform(Object input)`: Applies the default transformation to the input

#### Removr
Removes key-value pairs from JSON data based on patterns.

```java
public class Removr implements Transform {
    public Removr(Object spec)
    public Object transform(Object input)
}
```

**Methods:**
- `Removr(Object spec)`: Constructor that takes a specification
- `transform(Object input)`: Applies the remove transformation to the input

#### Sortr
Sorts keys in JSON data alphabetically.

```java
public class Sortr implements Transform {
    public Sortr()
    public Object transform(Object input)
}
```

**Methods:**
- `Sortr()`: Default constructor
- `transform(Object input)`: Applies the sort transformation to the input

#### Chainr
Chains multiple transformations together for complex operations.

```java
public class Chainr implements Transform {
    public static Chainr fromSpec(Object spec)
    public Object transform(Object input)
}
```

**Methods:**
- `fromSpec(Object spec)`: Static factory method to create a Chainr from a specification
- `transform(Object input)`: Applies all chained transformations to the input

#### Modifier
Applies custom functions to JSON data values.

```java
public class Modifier implements Transform {
    public Modifier(Object spec)
    public Object transform(Object input)
}
```

**Methods:**
- `Modifier(Object spec)`: Constructor that takes a specification
- `transform(Object input)`: Applies the modifier transformation to the input

### Utility Classes

#### JsonUtils
Provides utility methods for working with JSON data.

```java
public class JsonUtils {
    public static Object jsonToObject(String json)
    public static String toJsonString(Object obj)
    public static String toPrettyJsonString(Object obj)
}
```

**Methods:**
- `jsonToObject(String json)`: Converts a JSON string to an Object
- `toJsonString(Object obj)`: Converts an Object to a compact JSON string
- `toPrettyJsonString(Object obj)`: Converts an Object to a formatted JSON string

### Exception Classes

#### TransformException
Thrown when a transformation operation fails.

```java
public class TransformException extends JoltException {
    public TransformException(String message)
    public TransformException(String message, Throwable cause)
}
```

#### SpecException
Thrown when a specification is invalid.

```java
public class SpecException extends JoltException {
    public SpecException(String message)
    public SpecException(String message, Throwable cause)
}
```

#### JoltException
Base exception class for all JOLT-related exceptions.

```java
public class JoltException extends RuntimeException {
    public JoltException(String message)
    public JoltException(String message, Throwable cause)
}
```

## Transformation Specifications

### Shiftr Specification

Shiftr specifications define how to reposition data in JSON documents.

#### Basic Syntax

```json
{
  "sourceKey": "destinationKey"
}
```

#### Wildcard Patterns

- `*`: Matches any single key
- `**`: Matches any hierarchy of keys
- `&`: References the matched key
- `$`: References the matched key's value

#### Examples

##### Simple Shift
```json
{
  "user": {
    "name": "personName"
  }
}
```

##### Array Handling
```json
{
  "users": {
    "*": {
      "name": "names[]"
    }
  }
}
```

##### Wildcard Usage
```json
{
  "data": {
    "*": "items.&"
  }
}
```

### Defaultr Specification

Defaultr specifications define default values to apply to JSON data.

#### Basic Syntax

```json
{
  "key": "defaultValue"
}
```

#### Examples

##### Simple Defaults
```json
{
  "user": {
    "name": "Anonymous",
    "active": true
  }
}
```

### Removr Specification

Removr specifications define patterns for removing key-value pairs.

#### Basic Syntax

```json
{
  "keyToRemove": ""
}
```

#### Examples

##### Simple Removal
```json
{
  "temp": ""
}
```

### Modifier Specification

Modifier specifications define functions to apply to values.

#### Basic Syntax

```json
{
  "key": "=functionName"
}
```

#### Built-in Functions

##### String Functions
- `=toUpper`: Converts to uppercase
- `=toLower`: Converts to lowercase
- `=stringLength`: Gets string length
- `=charAt`: Gets character at position

##### Math Functions
- `=add`: Adds numbers
- `=subtract`: Subtracts numbers
- `=multiply`: Multiplies numbers
- `=divide`: Divides numbers

##### Array Functions
- `=size`: Gets array size
- `=firstElement`: Gets first element
- `=lastElement`: Gets last element

#### Examples

##### String Transformation
```json
{
  "text": "=toUpper"
}
```

##### Math Operations
```json
{
  "values": {
    "a": "=add(@(1,b), @(1,c))"
  }
}
```

## Command-Line Interface

### Usage

```bash
java -jar j2j-cli.jar [command] [options]
```

### Commands

#### transform
Performs JSON transformation using a specification.

```bash
java -jar j2j-cli.jar transform -t [type] -i [input] -s [spec] -o [output]
```

**Options:**
- `-t, --type`: Transformation type (shift, default, remove, sort, chain)
- `-i, --input`: Input JSON file
- `-s, --spec`: Specification file
- `-o, --output`: Output file (optional, prints to stdout if not specified)

#### sort
Sorts JSON keys alphabetically.

```bash
java -jar j2j-cli.jar sort -i [input] -o [output]
```

**Options:**
- `-i, --input`: Input JSON file
- `-o, --output`: Output file (optional, prints to stdout if not specified)

#### validate
Validates JSON syntax.

```bash
java -jar j2j-cli.jar validate -i [input]
```

**Options:**
- `-i, --input`: Input JSON file to validate

### Examples

##### Transform with Specification
```bash
java -jar j2j-cli.jar transform -t shift -i input.json -s spec.json -o output.json
```

##### Sort JSON Keys
```bash
java -jar j2j-cli.jar sort -i unsorted.json -o sorted.json
```

##### Validate JSON
```bash
java -jar j2j-cli.jar validate -i data.json
```

## Web Interface

### REST API Endpoints

#### Transformation Endpoints

##### POST /api/transform
Performs JSON transformation.

**Request Body:**
```json
{
  "input": "string",
  "spec": "string",
  "operation": "string"
}
```

**Response:**
```json
{
  "result": "string",
  "success": "boolean",
  "error": "string",
  "executionTime": "number"
}
```

##### POST /api/transform/validate/json
Validates JSON format.

**Request Body:**
```json
"JSON string to validate"
```

**Response:**
```json
{
  "valid": "boolean",
  "message": "string",
  "details": "string"
}
```

##### POST /api/transform/validate/spec
Validates transformation specification.

**Request Body:**
```json
{
  "spec": "string",
  "operation": "string"
}
```

**Response:**
```json
{
  "valid": "boolean",
  "message": "string",
  "details": "string"
}
```

### Web Interface Features

#### Interactive Editors
- Syntax-highlighted JSON editors
- Real-time transformation results
- Multiple transformation types

#### Example Library
- Pre-built examples for common patterns
- Different transformation types
- Complex use cases

#### Validation
- Real-time JSON validation
- Specification validation
- Detailed error messages

## Configuration Options

### Application Properties

#### Server Configuration
- `server.port`: Port to run the web server on (default: 8080)
- `server.servlet.context-path`: Context path for the application

#### Logging Configuration
- `logging.level.*`: Logging levels for different packages
- `logging.pattern.console`: Console logging pattern

#### Spring Boot Configuration
- `spring.thymeleaf.cache`: Whether to cache Thymeleaf templates
- `spring.devtools.restart.enabled`: Enable hot reload
- `spring.devtools.livereload.enabled`: Enable live reload

### Environment Variables

#### JVM Options
- `JAVA_OPTS`: JVM options for the application
- `SERVER_PORT`: Port to run the application on

#### Docker Configuration
- `SERVER_PORT`: Port mapping for Docker containers

## Error Codes

### Transformation Errors

| Code | Description | Solution |
|------|-------------|----------|
| TRANSFORM_001 | Invalid input JSON | Check JSON syntax |
| TRANSFORM_002 | Invalid specification | Check specification format |
| TRANSFORM_003 | Unsupported operation | Use supported operations |
| TRANSFORM_004 | Transformation failed | Check input and specification |

### Validation Errors

| Code | Description | Solution |
|------|-------------|----------|
| VALIDATE_001 | Invalid JSON syntax | Fix JSON syntax errors |
| VALIDATE_002 | Invalid specification format | Check specification structure |
| VALIDATE_003 | Missing required fields | Provide required fields |

### System Errors

| Code | Description | Solution |
|------|-------------|----------|
| SYSTEM_001 | Out of memory | Increase heap size |
| SYSTEM_002 | File not found | Check file paths |
| SYSTEM_003 | Permission denied | Check file permissions |

### HTTP Status Codes

| Code | Description | When It Occurs |
|------|-------------|----------------|
| 200 | OK | Successful request |
| 400 | Bad Request | Invalid input or specification |
| 404 | Not Found | Endpoint not found |
| 500 | Internal Server Error | Unexpected server error |

## Performance Metrics

### Execution Time
- Measured in milliseconds
- Includes parsing, transformation, and serialization time

### Memory Usage
- Monitored during transformation
- Reported in transformation results

### Complexity Analysis
- Low: Simple transformations
- Medium: Moderate complexity transformations
- High: Complex nested transformations

## Security Considerations

### Input Validation
- All JSON input is validated
- Specifications are validated before use
- Size limits prevent resource exhaustion

### Output Sanitization
- Transformation results are properly serialized
- No code injection vulnerabilities

### Access Control
- Web interface has no authentication by default
- Should be protected in production environments

## Troubleshooting

### Common Issues and Solutions

#### Performance Issues
**Problem**: Slow transformations with large JSON documents
**Solution**: 
1. Break complex transformations into smaller steps
2. Reuse transformation instances
3. Consider streaming for very large documents

#### Memory Issues
**Problem**: Out of memory errors
**Solution**:
1. Increase heap size with `-Xmx` parameter
2. Process large documents in chunks
3. Use streaming when available

#### Specification Errors
**Problem**: Invalid specification format
**Solution**:
1. Validate specifications using the web interface
2. Check JSON syntax
3. Refer to specification documentation

### Debugging Tips

1. **Enable Debug Logging**: Set logging level to DEBUG for detailed information
2. **Use Simple Specifications**: Start with simple specs and gradually increase complexity
3. **Test with Sample Data**: Use representative sample data for testing
4. **Check Web Interface**: Use the web interface for interactive debugging
5. **Review Examples**: Refer to provided examples for guidance

### Log Analysis

Key log entries to look for:
- Transformation start and end times
- Error messages and stack traces
- Performance metrics
- Resource usage information