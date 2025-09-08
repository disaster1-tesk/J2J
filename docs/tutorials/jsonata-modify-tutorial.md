# Using JSONata Functions in Modify Transformations

This tutorial explains how to use JSONata functions within J2J modify transformations to process source JSON data.

## Overview

J2J provides integration with JSONata, a powerful query and transformation language for JSON. The `jsonata` function can be used in modify operations to perform complex data processing directly within your transformation specifications.

## JSONata Function Syntax

The JSONata function in J2J follows this syntax:

```json
"=jsonata(query, jsonData)"
```

Where:
- `query`: A JSONata query string
- `jsonData`: The JSON data to query (typically the source JSON using `@(1)` reference)

## Basic Example

Here's a simple example that extracts a value using JSONata:

```json
{
  "input": {
    "user": {
      "name": "John Doe",
      "age": 30
    }
  },
  "spec": {
    "operation": "modify",
    "spec": {
      "userName": "=jsonata('user.name', @(1))"
    }
  }
}
```

## Advanced Examples

### 1. Aggregation Functions

Calculate statistics from arrays:

```json
{
  "input": {
    "products": [
      {"name": "Laptop", "price": 1200},
      {"name": "Phone", "price": 800},
      {"name": "Tablet", "price": 600}
    ]
  },
  "spec": {
    "operation": "modify",
    "spec": {
      "stats": {
        "totalProducts": "=jsonata('$count(products)', @(1))",
        "avgPrice": "=jsonata('$average(products.price)', @(1))",
        "maxPrice": "=jsonata('$max(products.price)', @(1))",
        "minPrice": "=jsonata('$min(products.price)', @(1))"
      }
    }
  }
}
```

### 2. Filtering and Transformation

Filter arrays and transform data:

```json
{
  "input": {
    "employees": [
      {"name": "Alice", "department": "Engineering", "salary": 90000},
      {"name": "Bob", "department": "Marketing", "salary": 75000},
      {"name": "Carol", "department": "Engineering", "salary": 80000}
    ]
  },
  "spec": {
    "operation": "modify",
    "spec": {
      "engineeringTeam": "=jsonata('employees[department=\"Engineering\"]', @(1))",
      "highEarners": "=jsonata('employees[salary > 80000].name', @(1))",
      "departmentSummary": "=jsonata('employees{department: $count}', @(1))"
    }
  }
}
```

### 3. String Operations and Complex Expressions

Perform string operations and complex transformations:

```json
{
  "input": {
    "users": [
      {"firstName": "John", "lastName": "Doe", "email": "john.doe@example.com"},
      {"firstName": "Jane", "lastName": "Smith", "email": "jane.smith@example.com"}
    ],
    "company": {
      "name": "TechCorp"
    }
  },
  "spec": {
    "operation": "modify",
    "spec": {
      "userDetails": "=jsonata('users.{\"fullName\": $join([firstName, lastName], \" \"), \"domain\": $substringAfter(email, \"@\")}', @(1))",
      "welcomeMessage": "=jsonata('\"Welcome to \" & company.name & \"! We have \" & $count(users) & \" users.\"', @(1))"
    }
  }
}
```

## Using Source JSON as Parameter

The key to using the source JSON as a parameter is the `@(1)` reference, which refers to the current level of the input JSON. This allows JSONata to access the entire source document:

```json
{
  "input": {
    "orders": [
      {"id": 1, "customer": "Alice", "amount": 150},
      {"id": 2, "customer": "Bob", "amount": 200},
      {"id": 3, "customer": "Alice", "amount": 75}
    ],
    "store": {
      "name": "SuperStore"
    }
  },
  "spec": {
    "operation": "modify",
    "spec": {
      "report": {
        "totalRevenue": "=jsonata('$sum(orders.amount)', @(1))",
        "customerSpending": "=jsonata('orders{customer: $sum(amount)}', @(1))",
        "topCustomer": "=jsonata('$max(orders{customer: $sum(amount)})', @(1))",
        "summary": "=jsonata('store.name & \" Report: \" & $count(orders) & \" orders, $\" & $sum(orders.amount) & \" total\"', @(1))"
      }
    }
  }
}
```

## Best Practices

1. **Error Handling**: JSONata queries that return null or invalid results will result in no operation in the modify transformation.

2. **Performance**: Complex JSONata queries can impact performance. For large datasets, consider preprocessing the data.

3. **Testing**: Always test your JSONata expressions with sample data to ensure they produce the expected results.

4. **Documentation**: Comment complex JSONata expressions to make your transformations more maintainable.

## Common JSONata Functions

Here are some commonly used JSONata functions in J2J:

- `$count(array)` - Count elements in an array
- `$sum(array)` - Sum numeric values in an array
- `$average(array)` - Calculate average of numeric values
- `$max(array)` / `$min(array)` - Find maximum/minimum values
- `$sort(array, key)` - Sort array by key
- `$join(array, separator)` - Join array elements with separator
- `$split(string, separator)` - Split string into array
- `$uppercase(string)` / `$lowercase(string)` - Change case

## Running the Examples

You can test these examples in the J2J web interface or using the CLI:

```bash
# Using the CLI
java -jar j2j-cli.jar transform -t chain -i input.json -s spec.json -o output.json
```

Or in the web interface by loading the example from the examples dropdown.

## Conclusion

The JSONata integration in J2J's modify transformations provides powerful capabilities for complex data processing directly within your transformation specifications. By using the source JSON as a parameter with `@(1)`, you can perform sophisticated queries and transformations without needing custom Java code.