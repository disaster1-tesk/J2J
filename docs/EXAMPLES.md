# J2J Examples

This document provides comprehensive examples of how to use J2J transformations for various common scenarios.

## Basic Examples

### Simple Shift
**Input:**
```json
{
  "user": {
    "name": "John Doe",
    "age": 35
  }
}
```

**Specification:**
```json
{
  "user": {
    "name": "person.fullName",
    "age": "person.years"
  }
}
```

**Output:**
```json
{
  "person": {
    "fullName": "John Doe",
    "years": 35
  }
}
```

### Default Values
**Input:**
```json
{
  "user": {
    "name": "Jane Smith"
  }
}
```

**Specification:**
```json
{
  "user": {
    "name": "Anonymous",
    "age": 25,
    "active": true
  }
}
```

**Output:**
```json
{
  "user": {
    "name": "Jane Smith",
    "age": 25,
    "active": true
  }
}
```

### Remove Fields
**Input:**
```json
{
  "user": {
    "name": "John Doe",
    "password": "secret123",
    "tempToken": "abc123"
  }
}
```

**Specification:**
```json
{
  "user": {
    "password": "",
    "tempToken": ""
  }
}
```

**Output:**
```json
{
  "user": {
    "name": "John Doe"
  }
}
```

## Advanced Examples

### Array Transformation
**Input:**
```json
{
  "users": [
    {
      "name": "John",
      "role": "admin"
    },
    {
      "name": "Jane",
      "role": "user"
    }
  ]
}
```

**Specification:**
```json
{
  "users": {
    "*": {
      "name": "people[&1].fullName",
      "role": "people[&1].accessLevel"
    }
  }
}
```

**Output:**
```json
{
  "people": [
    {
      "fullName": "John",
      "accessLevel": "admin"
    },
    {
      "fullName": "Jane",
      "accessLevel": "user"
    }
  ]
}
```

### Complex Nested Structure
**Input:**
```json
{
  "company": {
    "departments": {
      "engineering": {
        "employees": [
          {
            "id": 1,
            "name": "Alice",
            "skills": ["Java", "Python"]
          },
          {
            "id": 2,
            "name": "Bob",
            "skills": ["JavaScript", "HTML"]
          }
        ]
      }
    }
  }
}
```

**Specification:**
```json
{
  "company": {
    "departments": {
      "engineering": {
        "employees": {
          "*": {
            "name": "team.members[&1].name",
            "skills": "team.members[&1].expertise"
          }
        }
      }
    }
  }
}
```

**Output:**
```json
{
  "team": {
    "members": [
      {
        "name": "Alice",
        "expertise": ["Java", "Python"]
      },
      {
        "name": "Bob",
        "expertise": ["JavaScript", "HTML"]
      }
    ]
  }
}
```

### JSONata in Modify Operations
Using JSONata functions within modify transformations to process source JSON data.

**Input:**
```json
{
  "employees": [
    {
      "id": 1,
      "name": "Alice Johnson",
      "department": "Engineering",
      "position": "Senior Developer",
      "salary": 95000,
      "hireDate": "2020-03-15"
    },
    {
      "id": 2,
      "name": "Bob Smith",
      "department": "Marketing",
      "position": "Marketing Manager",
      "salary": 85000,
      "hireDate": "2019-07-22"
    },
    {
      "id": 3,
      "name": "Carol Williams",
      "department": "Engineering",
      "position": "Junior Developer",
      "salary": 70000,
      "hireDate": "2021-01-10"
    },
    {
      "id": 4,
      "name": "David Brown",
      "department": "HR",
      "position": "HR Specialist",
      "salary": 65000,
      "hireDate": "2020-11-05"
    }
  ],
  "companyInfo": {
    "name": "InnovateCorp",
    "founded": 2015,
    "location": "San Francisco"
  }
}
```

**Specification:**
```json
{
  "operation": "modify",
  "spec": {
    "analytics": {
      "totalEmployees": "=size(@(1,employees))",
      "avgSalary": "=jsonata('$avg(employees.salary)', @(1))",
      "maxSalary": "=jsonata('$max(employees.salary)', @(1))",
      "minSalary": "=jsonata('$min(employees.salary)', @(1))",
      "departments": "=jsonata('employees{department: $count}', @(1))",
      "engineeringAvgSalary": "=jsonata('$avg(employees[department=\"Engineering\"].salary)', @(1))",
      "employeeNames": "=jsonata('employees.name', @(1))",
      "highEarners": "=jsonata('employees[salary > 80000].name', @(1))",
      "summary": "=jsonata('\"Company \" & companyInfo.name & \" has \" & $count(employees) & \" employees with average salary of $\" & $round($avg(employees.salary), 2)', @(1))"
    },
    "engineeringTeam": "=jsonata('employees[department=\"Engineering\"]', @(1))",
    "sortedBySalary": "=jsonata('$sort(employees, salary)', @(1))"
  }
}
```

**Output:**
```json
{
  "employees": [
    {
      "id": 1,
      "name": "Alice Johnson",
      "department": "Engineering",
      "position": "Senior Developer",
      "salary": 95000,
      "hireDate": "2020-03-15"
    },
    {
      "id": 2,
      "name": "Bob Smith",
      "department": "Marketing",
      "position": "Marketing Manager",
      "salary": 85000,
      "hireDate": "2019-07-22"
    },
    {
      "id": 3,
      "name": "Carol Williams",
      "department": "Engineering",
      "position": "Junior Developer",
      "salary": 70000,
      "hireDate": "2021-01-10"
    },
    {
      "id": 4,
      "name": "David Brown",
      "department": "HR",
      "position": "HR Specialist",
      "salary": 65000,
      "hireDate": "2020-11-05"
    }
  ],
  "companyInfo": {
    "name": "InnovateCorp",
    "founded": 2015,
    "location": "San Francisco"
  },
  "analytics": {
    "totalEmployees": 4,
    "avgSalary": 78750,
    "maxSalary": 95000,
    "minSalary": 65000,
    "departments": {
      "Engineering": 2,
      "Marketing": 1,
      "HR": 1
    },
    "engineeringAvgSalary": 82500,
    "employeeNames": [
      "Alice Johnson",
      "Bob Smith",
      "Carol Williams",
      "David Brown"
    ],
    "highEarners": [
      "Alice Johnson",
      "Bob Smith"
    ],
    "summary": "Company InnovateCorp has 4 employees with average salary of $78750"
  },
  "engineeringTeam": [
    {
      "id": 1,
      "name": "Alice Johnson",
      "department": "Engineering",
      "position": "Senior Developer",
      "salary": 95000,
      "hireDate": "2020-03-15"
    },
    {
      "id": 3,
      "name": "Carol Williams",
      "department": "Engineering",
      "position": "Junior Developer",
      "salary": 70000,
      "hireDate": "2021-01-10"
    }
  ],
  "sortedBySalary": [
    {
      "id": 4,
      "name": "David Brown",
      "department": "HR",
      "position": "HR Specialist",
      "salary": 65000,
      "hireDate": "2020-11-05"
    },
    {
      "id": 3,
      "name": "Carol Williams",
      "department": "Engineering",
      "position": "Junior Developer",
      "salary": 70000,
      "hireDate": "2021-01-10"
    },
    {
      "id": 2,
      "name": "Bob Smith",
      "department": "Marketing",
      "position": "Marketing Manager",
      "salary": 85000,
      "hireDate": "2019-07-22"
    },
    {
      "id": 1,
      "name": "Alice Johnson",
      "department": "Engineering",
      "position": "Senior Developer",
      "salary": 95000,
      "hireDate": "2020-03-15"
    }
  ]
}
```

This example demonstrates how to use JSONata functions within modify operations:
- `=jsonata('$avg(employees.salary)', @(1))` calculates the average salary
- `=jsonata('employees{department: $count}', @(1))` groups employees by department and counts them
- `=jsonata('employees[salary > 80000].name', @(1))` filters high-earning employees
- `=jsonata('$sort(employees, salary)', @(1))` sorts employees by salary

The key is using `@(1)` to pass the entire source JSON as a parameter to the JSONata function.

## Chained Transformations

### Data Migration Pipeline
**Input:**
```json
{
  "old_format": {
    "user_name": "john_doe",
    "user_age": 30,
    "temp_field": "remove_me"
  }
}
```

**Specification:**
```json
[
  {
    "operation": "shift",
    "spec": {
      "old_format": {
        "user_name": "user.name",
        "user_age": "user.age"
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "user": {
        "active": true,
        "role": "user"
      }
    }
  },
  {
    "operation": "remove",
    "spec": {
      "temp*": ""
    }
  }
]
```

**Output:**
```json
{
  "user": {
    "name": "john_doe",
    "age": 30,
    "active": true,
    "role": "user"
  }
}
```

## Modifier Examples

### String Operations
**Input:**
```json
{
  "user": {
    "firstName": "john",
    "lastName": "doe",
    "email": "JOHN.DOE@EXAMPLE.COM"
  }
}
```

**Specification:**
```json
{
  "user": {
    "firstName": "=toUpper",
    "lastName": "=toUpper",
    "email": "=toLower"
  }
}
```

**Output:**
```json
{
  "user": {
    "firstName": "JOHN",
    "lastName": "DOE",
    "email": "john.doe@example.com"
  }
}
```

### Concatenation
**Input:**
```json
{
  "user": {
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

**Specification:**
```json
{
  "user": {
    "fullName": ["=concat", "@(1,firstName)", " ", "@(1,lastName)"]
  }
}
```

**Output:**
```json
{
  "user": {
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe"
  }
}
```

## Real-World Scenarios

### E-commerce Order Transformation
**Input:**
```json
{
  "order_id": "12345",
  "customer_info": {
    "first_name": "John",
    "last_name": "Smith",
    "email": "john@example.com"
  },
  "items": [
    {
      "product_id": "P001",
      "quantity": 2,
      "price": 29.99
    },
    {
      "product_id": "P002",
      "quantity": 1,
      "price": 49.99
    }
  ],
  "shipping_address": {
    "street": "123 Main St",
    "city": "Anytown",
    "state": "CA",
    "zip": "12345"
  }
}
```

**Specification:**
```json
[
  {
    "operation": "shift",
    "spec": {
      "order_id": "order.id",
      "customer_info": {
        "first_name": "order.customer.firstName",
        "last_name": "order.customer.lastName",
        "email": "order.customer.contact.email"
      },
      "items": {
        "*": {
          "product_id": "order.items[&1].productId",
          "quantity": "order.items[&1].quantity",
          "price": "order.items[&1].unitPrice"
        }
      },
      "shipping_address": "order.shipping.address"
    }
  },
  {
    "operation": "default",
    "spec": {
      "order": {
        "status": "pending",
        "currency": "USD"
      }
    }
  }
]
```

**Output:**
```json
{
  "order": {
    "id": "12345",
    "customer": {
      "firstName": "John",
      "lastName": "Smith",
      "contact": {
        "email": "john@example.com"
      }
    },
    "items": [
      {
        "productId": "P001",
        "quantity": 2,
        "unitPrice": 29.99
      },
      {
        "productId": "P002",
        "quantity": 1,
        "unitPrice": 49.99
      }
    ],
    "shipping": {
      "address": {
        "street": "123 Main St",
        "city": "Anytown",
        "state": "CA",
        "zip": "12345"
      }
    },
    "status": "pending",
    "currency": "USD"
  }
}
```

### API Response Normalization
**Input:**
```json
{
  "data": {
    "users": [
      {
        "user_id": 1,
        "user_name": "john_doe",
        "created_at": "2023-01-15T10:30:00Z",
        "is_active": true
      },
      {
        "user_id": 2,
        "user_name": "jane_smith",
        "created_at": "2023-02-20T14:45:00Z",
        "is_active": false
      }
    ]
  }
}
```

**Specification:**
```json
[
  {
    "operation": "shift",
    "spec": {
      "data": {
        "users": {
          "*": {
            "user_id": "users[&1].id",
            "user_name": "users[&1].username",
            "created_at": "users[&1].createdAt",
            "is_active": "users[&1].active"
          }
        }
      }
    }
  },
  {
    "operation": "modify",
    "spec": {
      "users": {
        "*": {
          "fullName": ["=concat", "@(1,username)", " (ID: ", "@(1,id)", ")"]
        }
      }
    }
  }
]
```

**Output:**
```json
{
  "users": [
    {
      "id": 1,
      "username": "john_doe",
      "createdAt": "2023-01-15T10:30:00Z",
      "active": true,
      "fullName": "john_doe (ID: 1)"
    },
    {
      "id": 2,
      "username": "jane_smith",
      "createdAt": "2023-02-20T14:45:00Z",
      "active": false,
      "fullName": "jane_smith (ID: 2)"
    }
  ]
}
```

## Performance Examples

### Large Dataset Processing
For processing large datasets efficiently, consider breaking transformations into smaller steps:

**Step 1 - Basic restructuring:**
```json
{
  "*": {
    "id": "records[&1].identifier",
    "data": "records[&1].content"
  }
}
```

**Step 2 - Apply defaults:**
```json
{
  "records": {
    "*": {
      "status": "processed",
      "timestamp": "=now"
    }
  }
}
```

## Error Handling Examples

### Graceful Degradation
When dealing with potentially inconsistent data, use defaults to ensure consistent output:

**Input:**
```json
{
  "user": {
    "name": "John"
    }
  }
}
```

**Specification:**
```json
[
  {
    "operation": "shift",
    "spec": {
      "user": {
        "name": "person.name",
        "age": "person.age"
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "person": {
        "age": 0,
        "status": "unknown"
      }
    }
  }
]
```

**Output:**
```json
{
  "person": {
    "name": "John",
    "age": 0,
    "status": "unknown"
  }
}
```

These examples demonstrate the flexibility and power of J2J transformations for handling various JSON manipulation tasks. By combining different transformation types in chains, you can accomplish complex data restructuring with simple, declarative specifications.