# Using JSONata in Modify Operations - Web Interface Example

This guide explains how to use the JSONata example in the J2J web interface.

## Accessing the Example

1. Start the J2J web application:
   ```bash
   java -jar j2j-web/target/j2j-web-1.0-SNAPSHOT.war
   ```

2. Open your browser and navigate to `http://localhost:8080`

3. In the examples dropdown, select "JSONata in Modify Transformation"

## Understanding the Example

This example demonstrates how to use JSONata functions within modify transformations to process complex data operations directly in your transformation specifications.

### Input Data
The example uses employee data with the following structure:
- Employee information including ID, name, department, position, salary, and hire date
- Company information including name, founded year, and location

### Transformation Specification
The specification uses the modify operation with JSONata functions:
- `=jsonata('$avg(employees.salary)', @(1))` - Calculates average salary
- `=jsonata('employees{department: $count}', @(1))` - Groups employees by department
- `=jsonata('employees[salary > 80000].name', @(1))` - Filters high-earning employees
- `=jsonata('$sort(employees, salary)', @(1))` - Sorts employees by salary

### Key Concept: Source JSON Parameter
The `@(1)` reference passes the entire source JSON as a parameter to the JSONata function, allowing complex queries across the entire document.

## Running the Transformation

1. After loading the example, click the "Transform" button
2. Observe the results in the output panel
3. The transformation generates:
   - Analytics data including statistics and summaries
   - Engineering team members filtered from the main list
   - Employees sorted by salary

## Modifying the Example

You can modify this example to experiment with different JSONata expressions:

1. Change the input data to match your use case
2. Modify the JSONata queries to perform different operations
3. Add new fields that use JSONata functions

### Example Modifications

1. **Calculate total payroll**:
   ```json
   "totalPayroll": "=jsonata('$sum(employees.salary)', @(1))"
   ```

2. **Find employees hired in the last year**:
   ```json
   "recentHires": "=jsonata('employees[$millis() - $toMillis(hireDate) < 31536000000].name', @(1))"
   ```

3. **Group employees by salary ranges**:
   ```json
   "salaryRanges": "=jsonata('employees{$floor(salary/10000): name}', @(1))"
   ```

## Learning More

For more information about JSONata functions and syntax, visit:
- [JSONata Exerciser](https://try.jsonata.org/)
- [JSONata Documentation](https://docs.jsonata.org/)

For more information about J2J modifier functions, see the [Modifier Tutorial](modifier.md) and [Template Engine Integration](templates.md).