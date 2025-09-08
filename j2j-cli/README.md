# j2j-cli

Command-line interface for J2J JSON transformation library.

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/java-17+-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

## Overview

This module provides a command-line interface to use the J2J transformation capabilities without needing to write Java code. It allows users to perform JSON transformations directly from the terminal, making it ideal for automation scripts and batch processing.

## Features

- Transform JSON files using various transformation types (shift, default, remove, sort, chain)
- Support for all core J2J transformation operations
- JSON syntax validation
- JSON comparison (Diffy) to identify differences between JSON files
- Sort JSON keys alphabetically
- Process multiple files in batch mode
- Configurable output formatting

## Installation

### Prerequisites
- Java 8 or higher
- Maven 3.6 or higher (for building from source)

### Building from Source
```bash
# Clone the repository
git clone https://github.com/your-username/j2j.git
cd j2j

# Build the CLI module
mvn clean package -pl j2j-cli

# The executable JAR will be in the target directory
ls -la target/j2j-cli-*.jar
```

### Download Pre-built JAR
Download the latest release from the [Releases page](https://github.com/your-username/j2j/releases).

## Usage

### Basic Transformation
```bash
# Basic transformation with a specification file
java -jar j2j-cli.jar transform -i input.json -s spec.json -o output.json

# Transform with explicit transformation type
java -jar j2j-cli.jar transform -t shift -i input.json -s spec.json -o output.json
```

### Sorting JSON
```bash
# Sort JSON keys alphabetically
java -jar j2j-cli.jar sort -i input.json -o output.json
```

### Comparing JSON Files
```bash
# Compare two JSON files
java -jar j2j-cli.jar diff -a file1.json -b file2.json
```

### Validation
```bash
# Validate JSON syntax
java -jar j2j-cli.jar validate -i input.json
```

## Command Line Options

### Transform Command
- `-i, --input <file>` - Input JSON file (required)
- `-s, --spec <file>` - Transformation specification file (required for transform)
- `-o, --output <file>` - Output file (optional, prints to stdout if not specified)
- `-t, --transform-type <type>` - Type of transformation (shift, default, remove, sort, chain)
- `--pretty` - Pretty print output JSON

### Sort Command
- `-i, --input <file>` - Input JSON file (required)
- `-o, --output <file>` - Output file (optional, prints to stdout if not specified)
- `--pretty` - Pretty print output JSON

### Diff Command
- `-a, --file-a <file>` - First JSON file to compare (required)
- `-b, --file-b <file>` - Second JSON file to compare (required)
- `--pretty` - Pretty print output

### Validate Command
- `-i, --input <file>` - JSON file to validate (required)

## Transformation Types

### Shift (`shift`)
Repositions key-value pairs in the input JSON according to the specification.

Example spec.json:
```json
{
  "user": {
    "name": "personName",
    "age": "personAge"
  }
}
```

### Default (`default`)
Applies default values to the input JSON where values are missing.

Example spec.json:
```json
{
  "user": {
    "name": "Anonymous",
    "active": true
  }
}
```

### Remove (`remove`)
Removes key-value pairs from the input JSON based on patterns.

Example spec.json:
```json
{
  "temp": "",
  "debug": ""
}
```

### Sort (`sort`)
Sorts keys in the JSON alphabetically. No specification file needed.

### Chain (`chain`)
Chains multiple transformations together. The specification should be an array of operations.

Example spec.json:
```json
[
  {
    "operation": "shift",
    "spec": {
      "user": {
        "name": "personName"
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "personName": "Anonymous"
    }
  }
]
```

## Dependencies

- **j2j-complete** (which includes j2j-core) - Core transformation logic
- **argparse4j** (0.4.4) - Command-line argument parsing
- **Jackson** - JSON processing

## Examples

### Example 1: Simple Shift Transformation
Input file (input.json):
```json
{
  "user": {
    "name": "John Doe",
    "age": 35
  }
}
```

Specification file (spec.json):
```json
{
  "user": {
    "name": "person.name",
    "age": "person.age"
  }
}
```

Command:
```bash
java -jar j2j-cli.jar transform -t shift -i input.json -s spec.json -o output.json
```

Output file (output.json):
```json
{
  "person": {
    "name": "John Doe",
    "age": 35
  }
}
```

### Example 2: Chained Transformations
Specification file (chain-spec.json):
```json
[
  {
    "operation": "shift",
    "spec": {
      "user": {
        "name": "person.name"
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "person": {
        "active": true
      }
    }
  }
]
```

Command:
```bash
java -jar j2j-cli.jar transform -t chain -i input.json -s chain-spec.json -o output.json --pretty
```

### Example 3: Comparing JSON Files
Command:
```bash
java -jar j2j-cli.jar diff -a file1.json -b file2.json
```

## Integration with Other Tools

The CLI tool can be easily integrated into:
- Shell scripts for automated processing
- CI/CD pipelines for data validation
- Data migration workflows
- Configuration file processing

## Error Handling

The CLI tool provides clear error messages for:
- Invalid JSON syntax in input files
- Invalid transformation specifications
- File not found errors
- Permission errors

All errors are printed to stderr, while normal output goes to stdout, making it easy to redirect outputs in scripts.

## Performance

For large files or batch processing:
- Use the `--pretty` flag only when human-readable output is needed
- Consider using streaming processing for very large files
- Reuse specification files rather than creating new ones for similar transformations

## Advanced Usage

### Batch Processing
Process multiple files in a directory:
```bash
for file in *.json; do
  java -jar j2j-cli.jar transform -t shift -i "$file" -s spec.json -o "${file%.json}_output.json"
done
```

### Using in Shell Scripts
```bash
#!/bin/bash
INPUT_FILE="data.json"
SPEC_FILE="transform-spec.json"
OUTPUT_FILE="result.json"

if java -jar j2j-cli.jar transform -i "$INPUT_FILE" -s "$SPEC_FILE" -o "$OUTPUT_FILE"; then
  echo "Transformation successful"
else
  echo "Transformation failed"
  exit 1
fi
```

## Configuration

The CLI tool can be configured through:
1. Command-line arguments (as shown above)
2. Environment variables for common settings
3. Configuration files for complex setups

## Troubleshooting

### Common Issues

1. **ClassNotFoundException**: Ensure all dependencies are in the classpath
2. **File not found**: Check file paths and permissions
3. **Invalid JSON**: Validate JSON syntax before processing
4. **Memory issues**: For large files, increase JVM heap size with `-Xmx` parameter

### Debugging

Enable verbose output for debugging:
```bash
java -Dlogging.level.love.disaster.j2j=DEBUG -jar j2j-cli.jar transform -i input.json -s spec.json -o output.json
```

## Extending the CLI

To add new commands or functionality:
1. Extend the `JoltCliProcessor` class
2. Register the new processor in `JoltCli`
3. Add command-line argument parsing with argparse4j

## Best Practices

1. **Always validate input**: Use the validate command before transformation
2. **Use version control**: Keep transformation specifications in version control
3. **Test with sample data**: Test transformations with various input data
4. **Document specifications**: Add comments to complex transformation specifications
5. **Monitor performance**: For batch processing, monitor execution time and resource usage