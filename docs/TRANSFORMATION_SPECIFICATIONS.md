# J2J Transformation Specifications

## Overview

J2J transformations are defined using JSON specifications that describe how to manipulate input JSON data. Each transformation type has its own specification format and capabilities.

## Common Concepts

### Wildcards
- `*` - Matches any key at the current level
- `**` - Matches any key at any level (recursive descent)
- `@(key)` - References the value of a key at the current level
- `&(n)` - References the nth matched wildcard from the left

### Array Handling
- `[index]` - Accesses a specific array element
- `[-1]` - Accesses the last element
- `[*]` - Applies to all array elements
- `[start:end]` - Slices an array (Python-style)

## Shiftr Specification

Shiftr is used to reposition data in a JSON document.

### Basic Syntax
```json
{
  "inputKey": "outputKey"
}
```

### Nested Objects
```json
{
  "user": {
    "name": "person.name",
    "age": "person.age"
  }
}
```

### Array Operations
```json
{
  "users[*]": "people[]"
}
```

### Wildcard Usage
```json
{
  "*": "&"
}
```

### Complex Example
```json
{
  "users": {
    "*": {
      "name": "people[&1].fullName",
      "age": "people[&1].years"
    }
  }
}
```

## Defaultr Specification

Defaultr applies default values to missing or null data.

### Basic Syntax
```json
{
  "key": "defaultValue"
}
```

### Nested Defaults
```json
{
  "user": {
    "name": "Anonymous",
    "active": true
  }
}
```

### Type-Specific Defaults
```json
{
  "numbers": [0, 0, 0],
  "flags": {
    "*": false
  }
}
```

## Removr Specification

Removr removes key-value pairs from the JSON document.

### Basic Syntax
```json
{
  "keyToRemove": ""
}
```

### Pattern Removal
```json
{
  "temp*": "",
  "debug": ""
}
```

### Nested Removal
```json
{
  "user": {
    "password": "",
    "tempToken": ""
  }
}
```

## Sortr Specification

Sortr sorts keys alphabetically. No specification is needed.

## Modifier Specification

Modifier applies functions to data values.

### Function Syntax
```json
{
  "key": "=functionName"
}
```

### Function with Parameters
```json
{
  "key": ["=concat", "prefix-", "@(1,key)"]
}
```

### Built-in Functions
- `=toLower` - Convert to lowercase
- `=toUpper` - Convert to uppercase
- `=concat` - Concatenate strings
- `=join` - Join array elements
- `=split` - Split string into array
- `=truncate` - Truncate string to length
- `=default` - Apply default if null

### Custom Functions
Custom functions can be registered and used in the same way as built-in functions.

## Chainr Specification

Chainr chains multiple transformations together.

### Basic Syntax
```json
[
  {
    "operation": "shift",
    "spec": {
      "input": "output"
    }
  },
  {
    "operation": "default",
    "spec": {
      "output": "defaultValue"
    }
  }
]
```

### Supported Operations
- `shift` - Shiftr transformation
- `default` - Defaultr transformation
- `remove` - Removr transformation
- `sort` - Sortr transformation
- `modify` - Modifier transformation
- Fully qualified class name - Custom Java transformations

### Custom Java Operations
```json
[
  {
    "operation": "com.example.CustomTransform",
    "spec": {
      "custom": "configuration"
    }
  }
]
```

## Advanced Features

### Context-Aware Transformations
Transformations can access contextual data passed at runtime.

### Conditional Logic
Some transformations support conditional application based on data values.

### Recursive Transformations
Transformations can be applied recursively to nested structures.

## Best Practices

### Specification Design
1. Keep specifications simple and focused
2. Use comments to document complex logic
3. Test with various input data
4. Validate specifications before use

### Performance Optimization
1. Minimize wildcard usage when possible
2. Avoid deep nesting when not necessary
3. Use specific keys instead of broad patterns
4. Cache compiled specifications for reuse

### Error Handling
1. Validate input data before transformation
2. Handle transformation exceptions gracefully
3. Provide meaningful error messages
4. Log transformation failures for debugging

## Examples

### Data Migration
```json
[
  {
    "operation": "shift",
    "spec": {
      "oldField": "newField",
      "nested": {
        "oldKey": "nested.newKey"
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "newField": "default_value",
      "status": "active"
    }
  }
]
```

### API Response Formatting
```json
{
  "data": {
    "*": {
      "id": "items[&1].identifier",
      "name": "items[&1].title"
    }
  }
}
```

### Data Cleaning
```json
[
  {
    "operation": "remove",
    "spec": {
      "temp*": "",
      "debug": "",
      "internal*": ""
    }
  },
  {
    "operation": "default",
    "spec": {
      "status": "pending",
      "priority": "normal"
    }
  }
]
```

## Validation

### Specification Validation
Specifications are validated for:
- Correct JSON syntax
- Valid operation types
- Proper structure for each transformation type
- Reference validity (wildcards, etc.)

### Runtime Validation
During transformation, data is validated for:
- Type compatibility
- Structure conformity
- Reference resolution