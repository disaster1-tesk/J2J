#!/bin/bash

# Script to run the J2J CLI tool
# Usage: ./run-cli.sh [arguments]
# Example: ./run-cli.sh transform -i input.json -s spec.json -o output.json

echo "Running J2J CLI Tool..."
echo "Usage examples:"
echo "  ./run-cli.sh transform -i input.json -s spec.json -o output.json"
echo "  ./run-cli.sh sort -i input.json -o output.json"
echo "  ./run-cli.sh diff -a file1.json -b file2.json"
echo ""

# Run the CLI tool with provided arguments
java -jar j2j-cli/target/j2j-cli-1.0-SNAPSHOT.jar "$@"