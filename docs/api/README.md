# J2J API Documentation

This directory contains detailed API documentation for the J2J library.

## 📚 Core API

### Transformation Interfaces

#### [Transform Interface](../../j2j-core/src/main/java/love/disaster/j2j/core/Transform.java)
The base interface implemented by all transformation classes.

```java
public interface Transform {
    Object transform(Object input);
}
```

### Core Transformation Classes

#### [Shiftr Class](../../j2j-core/src/main/java/love/disaster/j2j/core/Shiftr.java)
Repositions key-value pairs in JSON data according to a specification.

#### [Defaultr Class](../../j2j-core/src/main/java/love/disaster/j2j/core/Defaultr.java)
Applies default values to JSON data where values are missing.

#### [Removr Class](../../j2j-core/src/main/java/love/disaster/j2j/core/Removr.java)
Removes key-value pairs from JSON data based on patterns.

#### [Sortr Class](../../j2j-core/src/main/java/love/disaster/j2j/core/Sortr.java)
Sorts keys in JSON data alphabetically.

#### [Modifier Class](../../j2j-core/src/main/java/love/disaster/j2j/core/Modifier.java)
Applies custom functions to JSON data values.

#### [Chainr Class](../../j2j-core/src/main/java/love/disaster/j2j/core/Chainr.java)
Chains multiple transformations together for complex operations.

### Utility Classes

#### [JsonUtils Class](../../j2j-core/src/main/java/love/disaster/j2j/core/utils/JsonUtils.java)
Utility methods for JSON processing.

Common methods:
- `jsonToObject(String json)` - Converts JSON string to Object
- `toJsonString(Object obj)` - Converts Object to JSON string
- `toPrettyJsonString(Object obj)` - Converts Object to formatted JSON string

### Factory Classes

#### [ChainrFactory Class](../../j2j-complete/src/main/java/love/disaster/j2j/complete/ChainrFactory.java)
Factory methods for creating Chainr instances.

## 🌐 Web API

### Controllers

#### [TransformController](../../j2j-web/src/main/java/love/disaster/j2j/web/controller/TransformController.java)
REST controller for web-based transformations.

**Endpoints:**
- `POST /api/transform` - Perform JSON transformation
- `POST /api/transform/validate/json` - Validate JSON format
- `POST /api/transform/validate/spec` - Validate transformation specification

### Services

#### [TransformService](../../j2j-web/src/main/java/love/disaster/j2j/web/service/TransformService.java)
Business logic for transformations.

### Data Transfer Objects

#### [TransformRequest](../../j2j-web/src/main/java/love/disaster/j2j/web/dto/TransformRequest.java)
Request object for transformation operations.

#### [TransformResponse](../../j2j-web/src/main/java/love/disaster/j2j/web/dto/TransformResponse.java)
Response object for transformation operations.

#### [ValidationResult](../../j2j-web/src/main/java/love/disaster/j2j/web/dto/ValidationResult.java)
Result object for validation operations.

## ⚠️ Exception Classes

#### [JoltException](../../j2j-core/src/main/java/love/disaster/j2j/core/exception/JoltException.java)
Base exception for JOLT-related errors.

#### [SpecException](../../j2j-core/src/main/java/love/disaster/j2j/core/exception/SpecException.java)
Exception for specification-related errors.

#### [TransformException](../../j2j-core/src/main/java/love/disaster/j2j/core/exception/TransformException.java)
Exception for transformation-related errors.

## 📖 Usage Examples

### Core API Usage

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

### Web API Usage

```bash
# Transform JSON via REST API
curl -X POST http://localhost:8080/api/transform \
  -H "Content-Type: application/json" \
  -d '{
    "input": "{\"user\":{\"name\":\"John\",\"age\":30}}",
    "spec": "{\"user\":{\"name\":\"personName\"}}",
    "operation": "shift"
  }'
```

## 📚 Related Documentation

- [User Guide](../USER_GUIDE.md) - Comprehensive usage guide
- [Reference Guide](../REFERENCE.md) - Complete technical reference
- [Examples](../EXAMPLES.md) - Practical use cases