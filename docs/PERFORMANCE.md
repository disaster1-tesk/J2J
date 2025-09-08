# J2J Performance Guide

This document provides guidance on optimizing J2J transformations for better performance, especially when dealing with large datasets or high-throughput scenarios.

## Performance Characteristics

### Time Complexity
- **Shiftr**: O(n) where n is the number of keys in the input
- **Defaultr**: O(m) where m is the number of default specifications
- **Removr**: O(k) where k is the number of removal patterns
- **Sortr**: O(n log n) where n is the number of keys
- **Modifier**: Varies based on function complexity

### Space Complexity
- Transformations generally operate in-place when possible
- Memory usage is proportional to the size of input data
- Specification parsing creates temporary objects that are eligible for garbage collection

## Optimization Strategies

### 1. Reuse Transformation Instances

Transformation instances are designed to be thread-safe and reusable. Create them once and reuse across multiple transformations:

```java
// Good: Reuse the same Chainr instance
Chainr chainr = Chainr.fromSpec(specification);
for (Object inputData : largeDataset) {
    Object result = chainr.transform(inputData);
    // Process result
}

// Avoid: Creating new instances for each transformation
for (Object inputData : largeDataset) {
    Chainr chainr = Chainr.fromSpec(specification); // Inefficient
    Object result = chainr.transform(inputData);
    // Process result
}
```

### 2. Optimize Specification Design

#### Minimize Wildcard Usage
Wildcards are powerful but can impact performance. Use specific keys when possible:

```json
// Less efficient with many wildcards
{
  "*": {
    "*": {
      "*": "&2.&1.&0"
    }
  }
}

// More efficient with specific keys
{
  "user": {
    "name": "user.name",
    "email": "user.email"
  },
  "order": {
    "id": "order.id",
    "total": "order.total"
  }
}
```

#### Avoid Deep Nesting
Deeply nested specifications can be harder to process efficiently:

```json
// Potentially inefficient
{
  "level1": {
    "level2": {
      "level3": {
        "level4": {
          "level5": {
            "value": "target"
          }
        }
      }
    }
  }
}

// More efficient approach
{
  "level1.level2.level3.level4.level5.value": "target"
}
```

### 3. Batch Processing

For large datasets, consider batch processing to reduce overhead:

```java
public class BatchProcessor {
    private final Chainr chainr;
    
    public BatchProcessor(Object spec) {
        this.chainr = Chainr.fromSpec(spec);
    }
    
    public List<Object> processBatch(List<Object> inputs) {
        return inputs.stream()
                .map(chainr::transform)
                .collect(Collectors.toList());
    }
}
```

### 4. Parallel Processing

For CPU-intensive transformations, leverage parallel processing:

```java
public class ParallelProcessor {
    private final Chainr chainr;
    
    public ParallelProcessor(Object spec) {
        this.chainr = Chainr.fromSpec(spec);
    }
    
    public List<Object> processParallel(List<Object> inputs) {
        return inputs.parallelStream()
                .map(chainr::transform)
                .collect(Collectors.toList());
    }
}
```

## Memory Management

### Object Reuse
J2J is designed to minimize object creation. However, for extremely high-throughput scenarios, consider:

1. Using object pools for frequently created objects
2. Reusing Jackson JsonNode instances when possible
3. Minimizing intermediate object creation in custom transformations

### Garbage Collection
Monitor garbage collection patterns:
- Large JSON documents may create many temporary objects
- Complex specifications may increase memory pressure
- Consider JVM tuning for high-throughput scenarios

## Profiling and Monitoring

### Built-in Metrics
J2J provides execution time metrics for transformations:

```java
long startTime = System.currentTimeMillis();
Object result = chainr.transform(input);
long executionTime = System.currentTimeMillis() - startTime;
System.out.println("Transformation took: " + executionTime + "ms");
```

### Custom Monitoring
Implement custom monitoring for production systems:

```java
public class MonitoredChainr {
    private final Chainr chainr;
    private final MeterRegistry meterRegistry;
    
    public Object transform(Object input) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Object result = chainr.transform(input);
            sample.stop(Timer.builder("j2j.transform")
                    .register(meterRegistry));
            return result;
        } catch (Exception e) {
            sample.stop(Timer.builder("j2j.transform.error")
                    .register(meterRegistry));
            throw e;
        }
    }
}
```

## Benchmarking

### JMH Benchmarks
J2J includes JMH (Java Microbenchmark Harness) benchmarks:

```bash
# Run benchmarks
mvn clean install -pl j2j-core
java -jar j2j-core/target/benchmarks.jar
```

### Custom Benchmarks
Create custom benchmarks for your specific use cases:

```java
@Benchmark
public Object benchmarkSimpleShift() {
    return simpleShiftChainr.transform(simpleInput);
}

@Benchmark
public Object benchmarkComplexChain() {
    return complexChainr.transform(complexInput);
}
```

## Performance Testing

### Test Data Generation
Create representative test datasets:

```java
public class TestDataGenerator {
    public static Object generateLargeJson(int size) {
        Map<String, Object> root = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i);
            item.put("name", "Item " + i);
            item.put("value", Math.random());
            root.put("item" + i, item);
        }
        return root;
    }
}
```

### Load Testing
Perform load testing to understand performance under stress:

```java
public class LoadTest {
    @Test
    public void testHighThroughput() {
        Chainr chainr = Chainr.fromSpec(spec);
        Object input = generateTestData();
        
        long startTime = System.currentTimeMillis();
        int iterations = 10000;
        
        for (int i = 0; i < iterations; i++) {
            chainr.transform(input);
        }
        
        long endTime = System.currentTimeMillis();
        double throughput = iterations / ((endTime - startTime) / 1000.0);
        System.out.println("Throughput: " + throughput + " transformations/second");
    }
}
```

## JVM Tuning

### Heap Size
For large JSON documents, consider increasing heap size:

```bash
java -Xmx4g -jar j2j-application.jar
```

### Garbage Collector
Choose an appropriate garbage collector:

```bash
# G1 Garbage Collector for large heaps
java -XX:+UseG1GC -jar j2j-application.jar

# Parallel GC for throughput-focused applications
java -XX:+UseParallelGC -jar j2j-application.jar
```

## CLI Performance

### Batch Processing with CLI
For processing multiple files:

```bash
# Process multiple files efficiently
for file in *.json; do
    java -jar j2j-cli.jar transform -i "$file" -s spec.json -o "${file%.json}_out.json"
done
```

### Memory Settings for CLI
Adjust JVM settings for CLI operations:

```bash
java -Xmx2g -jar j2j-cli.jar transform -i large-input.json -s spec.json -o output.json
```

## Web Interface Performance

### Caching Strategies
Implement caching for frequently used transformations:

```java
@Service
public class CachedTransformService {
    private final Cache<String, Chainr> chainrCache = 
        Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
                
    public Object transform(String specKey, Object input) {
        Chainr chainr = chainrCache.get(specKey, this::createChainr);
        return chainr.transform(input);
    }
}
```

### Asynchronous Processing
For long-running transformations, use asynchronous processing:

```java
@RestController
public class AsyncTransformController {
    
    @PostMapping("/transform/async")
    public CompletableFuture<TransformResponse> transformAsync(
            @RequestBody TransformRequest request) {
        return CompletableFuture.supplyAsync(() -> 
            transformService.performTransform(request));
    }
}
```

## Troubleshooting Performance Issues

### Common Performance Bottlenecks

1. **Complex Specifications**: Simplify specifications where possible
2. **Large Input Data**: Consider streaming or chunking approaches
3. **Memory Pressure**: Monitor heap usage and GC activity
4. **Thread Contention**: Ensure thread-safe usage patterns

### Diagnostic Tools

1. **JVM Profilers**: Use tools like VisualVM or JProfiler
2. **Logging**: Enable debug logging to trace execution
3. **Metrics**: Implement application metrics for monitoring

### Performance Checklist

- [ ] Reuse transformation instances
- [ ] Optimize specification complexity
- [ ] Monitor memory usage
- [ ] Profile with representative data
- [ ] Tune JVM settings
- [ ] Implement appropriate caching
- [ ] Consider parallel processing
- [ ] Monitor garbage collection

By following these guidelines, you can ensure that your J2J transformations perform efficiently even under demanding conditions.