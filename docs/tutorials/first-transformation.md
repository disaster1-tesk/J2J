# Your First J2J Transformation

This tutorial will guide you through creating your first JSON transformation using J2J.

## Prerequisites

Before starting this tutorial, you should have:
- Java 8 or higher installed
- Maven 3.6 or higher installed
- Basic understanding of JSON syntax
- Text editor or IDE for writing code

## Learning Objectives

By the end of this tutorial, you will be able to:
- Set up a J2J project
- Create a simple JSON transformation
- Understand basic J2J specification syntax
- Run transformations programmatically

## Setting Up the Project

### 1. Create a New Maven Project

Create a new directory for your project and initialize a Maven project:

```bash
mkdir j2j-tutorial
cd j2j-tutorial
mvn archetype:generate -DgroupId=com.example \
    -DartifactId=j2j-first-transformation \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DinteractiveMode=false
```

### 2. Add J2J Dependencies

Edit the `pom.xml` file to include J2J dependencies:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>j2j-first-transformation</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>love.disaster</groupId>
            <artifactId>j2j-core</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        
        <dependency>
            <groupId>love.disaster</groupId>
            <artifactId>j2j-complete</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

## Creating Sample Data

### 1. Create Input JSON

Create a file named `input.json` in `src/main/resources`:

```json
{
  "user": {
    "firstName": "John",
    "lastName": "Doe",
    "age": 30,
    "email": "john.doe@example.com"
  },
  "order": {
    "id": "12345",
    "items": [
      {
        "name": "Product A",
        "price": 29.99,
        "quantity": 2
      },
      {
        "name": "Product B",
        "price": 49.99,
        "quantity": 1
      }
    ]
  }
}
```

## Writing Your First Transformation

### 1. Understanding Shiftr Specification

The Shiftr transformation allows you to reposition data in a JSON document. Let's create a specification that:
- Moves user information to a `person` object
- Renames `firstName` to `name`
- Combines order information into a flatter structure

Create a file named `FirstTransformation.java` in `src/main/java/com/example`:

```java
package com.example;

import love.disaster.j2j.core.Chainr;
import love.disaster.j2j.utils.JsonUtils;

import java.io.InputStream;

public class FirstTransformation {
    
    public static void main(String[] args) throws Exception {
        // Load input JSON
        InputStream input = FirstTransformation.class
            .getClassLoader()
            .getResourceAsStream("input.json");
        Object inputData = JsonUtils.jsonToObject(input);
        
        // Define transformation specification
        String specJson = "{" +
            "  \"user\": {" +
            "    \"firstName\": \"person.name\"," +
            "    \"lastName\": \"person.surname\"," +
            "    \"age\": \"person.age\"," +
            "    \"email\": \"person.contact.email\"" +
            "  }," +
            "  \"order\": {" +
            "    \"id\": \"orderDetails.id\"," +
            "    \"items\": \"orderDetails.products\"" +
            "  }" +
            "}";
        
        Object spec = JsonUtils.jsonToObject(specJson);
        
        // Create and execute transformation
        Chainr chainr = Chainr.fromSpec(spec);
        Object transformed = chainr.transform(inputData);
        
        // Output result
        String result = JsonUtils.toJsonString(transformed);
        System.out.println(result);
    }
}
```

### 2. Running the Transformation

Build and run your application:

```bash
mvn compile exec:java -Dexec.mainClass="com.example.FirstTransformation"
```

You should see output similar to:

```json
{
  "person" : {
    "name" : "John",
    "surname" : "Doe",
    "age" : 30,
    "contact" : {
      "email" : "john.doe@example.com"
    }
  },
  "orderDetails" : {
    "id" : "12345",
    "products" : [ {
      "name" : "Product A",
      "price" : 29.99,
      "quantity" : 2
    }, {
      "name" : "Product B",
      "price" : 49.99,
      "quantity" : 1
    } ]
  }
}
```

## Understanding the Specification

Let's break down the specification:

```json
{
  "user": {
    "firstName": "person.name",
    "lastName": "person.surname",
    "age": "person.age",
    "email": "person.contact.email"
  }
}
```

- `"user"`: Matches the `user` key in the input
- `"firstName": "person.name"`: Takes the value of `firstName` and places it at `person.name` in the output
- `"email": "person.contact.email"`: Creates a nested structure `person.contact.email`

## Adding Default Values

Let's enhance our transformation by adding default values using a chained transformation:

```java
public class EnhancedTransformation {
    
    public static void main(String[] args) throws Exception {
        // Load input JSON
        InputStream input = EnhancedTransformation.class
            .getClassLoader()
            .getResourceAsStream("input.json");
        Object inputData = JsonUtils.jsonToObject(input);
        
        // Define chained transformation specification
        String chainSpecJson = "[" +
            "  {" +
            "    \"operation\": \"shift\"," +
            "    \"spec\": {" +
            "      \"user\": {" +
            "        \"firstName\": \"person.name\"," +
            "        \"lastName\": \"person.surname\"," +
            "        \"age\": \"person.age\"," +
            "        \"email\": \"person.contact.email\"" +
            "      }," +
            "      \"order\": {" +
            "        \"id\": \"orderDetails.id\"," +
            "        \"items\": \"orderDetails.products\"" +
            "      }" +
            "    }" +
            "  }," +
            "  {" +
            "    \"operation\": \"default\"," +
            "    \"spec\": {" +
            "      \"person\": {" +
            "        \"status\": \"active\"," +
            "        \"role\": \"customer\"" +
            "      }," +
            "      \"orderDetails\": {" +
            "        \"currency\": \"USD\"," +
            "        \"status\": \"pending\"" +
            "      }" +
            "    }" +
            "  }" +
            "]";
        
        Object chainSpec = JsonUtils.jsonToObject(chainSpecJson);
        
        // Create and execute chained transformation
        Chainr chainr = Chainr.fromSpec(chainSpec);
        Object transformed = chainr.transform(inputData);
        
        // Output result
        String result = JsonUtils.toJsonString(transformed);
        System.out.println(result);
    }
}
```

Run the enhanced transformation:

```bash
mvn compile exec:java -Dexec.mainClass="com.example.EnhancedTransformation"
```

## Using the CLI

You can also perform the same transformation using the J2J CLI:

### 1. Create Specification File

Create a file named `spec.json`:

```json
[
  {
    "operation": "shift",
    "spec": {
      "user": {
        "firstName": "person.name",
        "lastName": "person.surname",
        "age": "person.age",
        "email": "person.contact.email"
      },
      "order": {
        "id": "orderDetails.id",
        "items": "orderDetails.products"
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "person": {
        "status": "active",
        "role": "customer"
      },
      "orderDetails": {
        "currency": "USD",
        "status": "pending"
      }
    }
  }
]
```

### 2. Run CLI Transformation

```bash
java -jar j2j-cli/target/j2j-cli-1.0-SNAPSHOT.jar transform \
  -i src/main/resources/input.json \
  -s spec.json \
  -o output.json \
  --pretty
```

## Summary

In this tutorial, you've learned:

1. How to set up a J2J project with Maven
2. The basics of J2J specification syntax
3. How to perform shift transformations
4. How to chain multiple transformations
5. How to add default values to JSON data
6. How to use both programmatic and CLI approaches

## Next Steps

To continue learning J2J:

1. Try more complex transformations with arrays
2. Experiment with wildcard patterns
3. Explore the Modifier transformation for function application
4. Learn about the web interface for interactive transformations
5. Practice with real-world data transformation scenarios

## Troubleshooting

### Common Issues

1. **ClassNotFoundException**: Ensure all J2J dependencies are properly included
2. **Invalid JSON**: Validate your JSON syntax using online tools
3. **Specification Errors**: Check that your specification matches the expected format
4. **File Not Found**: Verify file paths and resource locations

### Getting Help

If you encounter issues:
1. Check the J2J documentation
2. Review the examples in the repository
3. Search existing issues on the project's GitHub page
4. Ask questions in relevant community forums