# j2j-dependencies

Dependency management module for the J2J project.

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Maven](https://img.shields.io/badge/maven-3.6+-blue.svg)](https://maven.apache.org/)

## Overview

This module centralizes dependency management for all J2J modules. It defines versions and scopes for all third-party libraries used across the project, ensuring consistency and avoiding version conflicts. By using this module as a dependency management source, all J2J modules can share the same versions of libraries, making maintenance easier and reducing the risk of compatibility issues.

## Managed Dependencies

### Core Dependencies
- **Jackson** (2.13.4) - JSON processing library for parsing and generating JSON
- **SLF4J** (1.7.36) - Simple Logging Facade for Java, providing a common interface for various logging frameworks
- **jakarta.inject** (2.0.1) - Standard annotations for dependency injection

### CLI Dependencies
- **argparse4j** (0.4.4) - Command-line argument parser for Java

### Test Dependencies
- **TestNG** (6.8.21) - Testing framework for Java with advanced features
- **Guava** (29.0-jre) - Google's core libraries for Java with utilities and collections
- **Commons Lang3** (3.4) - Apache Commons utilities for working with strings, arrays, and other basic functionality

### Optional Dependencies
- **Beetl** (3.15.0.RELEASE) - Lightweight template engine for Java
- **ANTLR** (4.12.0) - Parser generator for reading, processing, executing, or translating structured text or binary files
- **JSONata4Java** (2.4.1) - Java implementation of JSONata, a query and transformation language for JSON

## Usage

Other J2J modules inherit from this module to ensure consistent dependency versions:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>love.disaster</groupId>
            <artifactId>j2j-dependencies</artifactId>
            <version>${project.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

To use a managed dependency in a module, simply declare it without a version:

```xml
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

## Benefits

- **Centralized version management**: All dependency versions are defined in one place, making it easy to update and maintain
- **Consistent dependency versions**: All modules use the same versions of libraries, preventing compatibility issues
- **Simplified dependency declarations**: Child modules don't need to specify versions, reducing pom.xml complexity
- **Easier maintenance**: Updating a dependency version requires changes in only one place
- **Conflict prevention**: Maven's dependency resolution is more predictable with centralized management

## Version Update Process

To update a dependency version:

1. Locate the dependency in the `<properties>` section of this module's pom.xml
2. Update the version property to the desired version
3. Run `mvn clean install` to verify compatibility
4. Commit and push the changes

Example:
```xml
<properties>
    <!-- Update Jackson version -->
    <jackson.version>2.15.0</jackson.version>
</properties>
```

## Dependency Analysis

This module uses Maven's dependency plugin to analyze and report on dependencies:

```bash
# Analyze dependencies for j2j-dependencies
mvn dependency:analyze -pl j2j-dependencies

# List all dependencies
mvn dependency:list -pl j2j-dependencies

# Generate dependency tree
mvn dependency:tree -pl j2j-dependencies
```

## Security Considerations

The module includes the OWASP dependency check plugin to identify known vulnerabilities in dependencies:

```bash
# Check for known vulnerabilities
mvn dependency-check:check -pl j2j-dependencies
```

## Module Integration

This module is imported by all other J2J modules through the `<dependencyManagement>` section. It does not contain any code itself but serves as a bill of materials (BOM) for the project.

### Parent POM Integration
The j2j-dependencies module is referenced in the parent pom.xml:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>love.disaster</groupId>
            <artifactId>j2j-dependencies</artifactId>
            <version>${project.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Best Practices

1. **Always use managed versions**: Never specify versions for dependencies that are managed by this module
2. **Regular updates**: Periodically review and update dependency versions to get security fixes and new features
3. **Compatibility testing**: Always test the entire project after updating dependencies
4. **Minimal direct dependencies**: Only include dependencies that are actually needed by multiple modules
5. **Documentation**: Keep this README updated when adding or removing managed dependencies

## Troubleshooting

### Version Conflicts
If you encounter version conflicts, check that:
1. The dependency is properly managed in this module
2. The module importing the dependency is not overriding the version
3. There are no transitive dependency conflicts

### Missing Dependencies
If a dependency is not available:
1. Verify it's declared in the `<dependencyManagement>` section
2. Check that the version property is correctly defined
3. Ensure the module is using the correct scope

## Future Considerations

As the project evolves, consider:
1. Adding new dependencies as needed by project features
2. Removing unused dependencies to reduce project complexity
3. Regularly reviewing dependency versions for security updates
4. Evaluating new libraries that could enhance project capabilities

## Dependency Matrix

| Category | Dependency | Version | Scope |
|----------|------------|---------|-------|
| Core | Jackson Databind | 2.13.4 | compile |
| Core | Jackson Core | 2.13.4 | compile |
| Core | SLF4J API | 1.7.36 | compile |
| Core | jakarta.inject | 2.0.1 | compile |
| CLI | argparse4j | 0.4.4 | compile |
| Test | TestNG | 6.8.21 | test |
| Test | Guava | 29.0-jre | test |
| Test | Commons Lang3 | 3.4 | test |
| Optional | Beetl | 3.15.0.RELEASE | optional |
| Optional | ANTLR Runtime | 4.12.0 | optional |
| Optional | JSONata4Java | 2.4.1 | optional |

## Version Compatibility

This module ensures compatibility across all J2J modules by:
1. Pinning specific versions of all dependencies
2. Managing transitive dependency versions
3. Providing a consistent baseline for all modules
4. Enabling easy upgrades through property changes

## Security Scanning

Regular security scanning is performed using:
1. OWASP Dependency Check
2. Maven Enforcer Plugin
3. Manual review of release notes

To run security checks:
```bash
mvn dependency-check:check
mvn enforcer:enforce
```

## Release Management

When preparing a release:
1. Review all dependency versions for stability
2. Check for any deprecated dependencies
3. Update version properties as needed
4. Run full test suite with dependency analysis
5. Document any breaking changes in release notes