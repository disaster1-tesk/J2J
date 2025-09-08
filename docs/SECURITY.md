# J2J Security Guide

This document outlines the security considerations and best practices for using J2J in your applications.

## Security Model

J2J follows a secure-by-default approach with the following principles:

1. **No Arbitrary Code Execution**: Core transformations do not execute arbitrary code
2. **Input Validation**: Built-in validation for JSON syntax and structure
3. **Resource Limits**: Protection against resource exhaustion
4. **Safe Parsing**: Uses Jackson's secure JSON parsing features

## Potential Security Risks

### 1. Denial of Service (DoS)

#### Large Input Data
Processing extremely large JSON documents can consume excessive memory and CPU resources.

**Mitigation:**
- Implement input size limits
- Use streaming for large documents when possible
- Monitor resource usage
- Set appropriate JVM heap limits

```java
// Example: Limiting input size
public class SecureTransformService {
    private static final int MAX_INPUT_SIZE = 10 * 1024 * 1024; // 10MB
    
    public Object transform(String jsonInput, Object spec) {
        if (jsonInput.length() > MAX_INPUT_SIZE) {
            throw new IllegalArgumentException("Input too large");
        }
        // Proceed with transformation
    }
}
```

#### Deeply Nested Structures
Excessively deep JSON structures can cause stack overflow or excessive processing time.

**Mitigation:**
- Validate JSON structure depth
- Implement recursion limits
- Use iterative algorithms where possible

### 2. Injection Attacks

#### Template Engine Injection
When using template engines like Beetl or JSONata, user-provided templates could potentially execute unintended logic.

**Mitigation:**
- Sanitize template inputs
- Use sandboxed template execution
- Limit template complexity
- Validate template syntax

```java
// Example: Template validation
public class SecureTemplateService {
    public String executeTemplate(String template, Object data) {
        // Validate template syntax
        if (!isValidTemplate(template)) {
            throw new SecurityException("Invalid template syntax");
        }
        
        // Execute in sandboxed environment
        return executeInSandbox(template, data);
    }
}
```

### 3. Information Disclosure

#### Error Messages
Detailed error messages might reveal internal system information.

**Mitigation:**
- Sanitize error messages in production
- Log detailed errors internally
- Provide generic error messages to clients

```java
// Example: Secure error handling
public class SecureTransformController {
    private static final Logger logger = LoggerFactory.getLogger(SecureTransformController.class);
    
    @PostMapping("/transform")
    public ResponseEntity<TransformResponse> transform(@RequestBody TransformRequest request) {
        try {
            TransformResponse response = transformService.performTransform(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log detailed error internally
            logger.error("Transformation failed", e);
            
            // Return generic error to client
            TransformResponse errorResponse = new TransformResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Transformation failed. Please check your input and specification.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
```

## Secure Configuration

### JVM Security Settings

Configure JVM security properties:

```bash
# Disable external entity processing
java -Djavax.xml.accessExternalDTD=none \
     -Djavax.xml.accessExternalSchema=none \
     -jar j2j-application.jar
```

### Jackson Security Configuration

Configure Jackson for secure parsing:

```java
public class SecureJsonUtils {
    private static final ObjectMapper SECURE_MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(JsonParser.Feature.ALLOW_COMMENTS, false)
        .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, false);
        
    public static Object jsonToObject(String json) {
        try {
            return SECURE_MAPPER.readValue(json, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
```

## Input Validation

### JSON Syntax Validation

Always validate JSON syntax before processing:

```java
public class JsonValidator {
    public static boolean isValidJson(String json) {
        try {
            JsonUtils.jsonToObject(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### Specification Validation

Validate transformation specifications:

```java
public class SpecValidator {
    public static ValidationResult validateSpec(Object spec, String operationType) {
        try {
            // Attempt to create transformation instance
            switch (operationType.toLowerCase()) {
                case "shift":
                    new Shiftr(spec);
                    break;
                case "default":
                    new Defaultr(spec);
                    break;
                // ... other cases
            }
            return ValidationResult.valid();
        } catch (Exception e) {
            return ValidationResult.invalid(e.getMessage());
        }
    }
}
```

## Secure Deployment

### Web Application Security

When deploying the web interface:

1. **Use HTTPS**: Always deploy with TLS encryption
2. **Input Sanitization**: Sanitize all user inputs
3. **CORS Configuration**: Restrict cross-origin requests
4. **Authentication**: Implement authentication for sensitive operations
5. **Rate Limiting**: Prevent abuse through rate limiting

```java
@RestController
@RequestMapping("/api/transform")
@CrossOrigin(origins = "${app.allowed.origins:https://yourdomain.com}")
public class SecureTransformController {
    
    @PostMapping
    public ResponseEntity<TransformResponse> transform(
            @RequestBody @Valid TransformRequest request,
            HttpServletRequest httpRequest) {
        
        // Rate limiting check
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(429).build(); // Too Many Requests
        }
        
        // ... rest of transformation logic
    }
}
```

### CLI Security

When using the CLI:

1. **File Permissions**: Ensure appropriate file permissions
2. **Input Validation**: Validate file paths and contents
3. **Resource Limits**: Set appropriate resource limits

```bash
# Run CLI with security-conscious settings
java -Xmx1g \
     -Dfile.encoding=UTF-8 \
     -Djava.security.manager \
     -jar j2j-cli.jar transform -i input.json -s spec.json -o output.json
```

## Logging and Monitoring

### Secure Logging

Avoid logging sensitive information:

```java
public class SecureLogger {
    private static final Logger logger = LoggerFactory.getLogger(SecureLogger.class);
    
    public static void logTransformation(String operation, Object input, Object spec) {
        // Log only non-sensitive information
        logger.info("Performing {} transformation", operation);
        
        // Avoid logging full JSON content or specifications
        // Instead, log metadata or hashes
        logger.debug("Input hash: {}", hash(input));
        logger.debug("Spec hash: {}", hash(spec));
    }
    
    private static String hash(Object obj) {
        // Implementation to create hash of object
        return "...";
    }
}
```

### Security Monitoring

Implement security monitoring:

```java
@Component
public class SecurityMonitor {
    private final MeterRegistry meterRegistry;
    
    public void recordSuspiciousActivity(String activityType, String details) {
        Counter.builder("security.suspicious.activity")
                .tag("type", activityType)
                .register(meterRegistry)
                .increment();
                
        // Log for security team review
        logger.warn("Suspicious activity detected: {} - {}", activityType, details);
    }
}
```

## Best Practices

### Development Practices

1. **Code Reviews**: Review all transformation specifications
2. **Security Testing**: Include security tests in your test suite
3. **Dependency Updates**: Regularly update dependencies
4. **Static Analysis**: Use static analysis tools to identify security issues

### Production Practices

1. **Monitoring**: Implement comprehensive monitoring
2. **Alerting**: Set up alerts for suspicious activities
3. **Incident Response**: Have an incident response plan
4. **Regular Audits**: Conduct regular security audits

### Template Engine Security

When using template engines:

1. **Sandboxing**: Execute templates in sandboxed environments
2. **Function Restrictions**: Limit available functions
3. **Input Validation**: Validate template inputs
4. **Output Encoding**: Encode outputs appropriately

## Compliance Considerations

### Data Protection

Ensure compliance with data protection regulations:

1. **GDPR**: Implement data minimization
2. **CCPA**: Provide data deletion capabilities
3. **HIPAA**: Ensure appropriate safeguards for health data

### Audit Trails

Maintain audit trails for security-sensitive operations:

```java
@Aspect
@Component
public class SecurityAuditAspect {
    
    @Around("@annotation(SecurityAudited)")
    public Object auditSecurityOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // Record audit information
        auditService.recordOperation(
            SecurityContext.getCurrentUserId(),
            joinPoint.getSignature().getName(),
            Arrays.toString(joinPoint.getArgs())
        );
        
        return joinPoint.proceed();
    }
}
```

## Incident Response

### Detection

Monitor for:
- Unusual resource consumption
- Failed transformation attempts
- Suspicious specification patterns
- Unauthorized access attempts

### Response

1. **Containment**: Isolate affected systems
2. **Investigation**: Analyze logs and artifacts
3. **Remediation**: Apply fixes and patches
4. **Communication**: Notify stakeholders as appropriate

By following these security guidelines, you can help ensure that your J2J implementations are secure and resilient against common threats.