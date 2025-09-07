package love.disaster.j2j.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import love.disaster.j2j.core.*;
import love.disaster.j2j.core.exception.SpecException;
import love.disaster.j2j.core.exception.TransformException;
import love.disaster.j2j.utils.JsonUtils;
import love.disaster.j2j.web.dto.TransformRequest;
import love.disaster.j2j.web.dto.TransformResponse;
import love.disaster.j2j.web.dto.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for handling JSON transformations using j2j-core APIs
 * Modified to only support chain operations and basic JSON validation
 */
@Service
public class TransformService {

    private static final Logger logger = LoggerFactory.getLogger(TransformService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Perform a JSON transformation based on the provided request
     *
     * @param request the transformation request
     * @return the transformation response
     */
    public TransformResponse performTransform(TransformRequest request) {
        long startTime = System.currentTimeMillis();
        TransformResponse response = new TransformResponse();

        try {
            // Parse input JSON
            Object input = JsonUtils.jsonToObject(request.getInput());

            // Perform transformation - only support chain operations
            Object result = performChainTransformation(request.getEffectiveSpec(), input);

            // Format result as JSON string
            String resultString = JsonUtils.toPrettyJsonString(result);

            response.setSuccess(true);
            response.setResult(resultString);
            response.setExecutionTime(System.currentTimeMillis() - startTime);
            response.setComplexity(estimateComplexity(request));
            
            logger.info("Transformation completed successfully in {}ms", response.getExecutionTime());
        } catch (TransformException e) {
            logger.error("Transform exception occurred", e);
            response.setSuccess(false);
            response.setError("Transform error: " + e.getMessage());
        } catch (SpecException e) {
            logger.error("Specification exception occurred", e);
            response.setSuccess(false);
            response.setError("Specification error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during transformation", e);
            response.setSuccess(false);
            response.setError("Unexpected error: " + e.getMessage());
        }

        return response;
    }

    /**
     * Perform a chain transformation
     */
    private Object performChainTransformation(Object spec, Object input) {
        Object parsedSpec = parseSpec(spec);
        Chainr chainr = Chainr.fromSpec(parsedSpec);
        return chainr.transform(input);
    }

    /**
     * Parse specification string to object
     */
    private Object parseSpec(Object spec) {
        if (spec instanceof String) {
            return JsonUtils.jsonToObject((String) spec);
        }
        return spec;
    }

    /**
     * Validate a JSON string
     *
     * @param json the JSON string to validate
     * @return validation result
     */
    public ValidationResult validateJson(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return ValidationResult.error("Empty JSON", "JSON input is empty");
            }
            
            Object parsed = JsonUtils.jsonToObject(json);
            if (parsed == null) {
                return ValidationResult.error("Invalid JSON", "JSON could not be parsed");
            }
            
            return ValidationResult.valid("Valid JSON", "JSON is well-formed");
        } catch (Exception e) {
            return ValidationResult.error("Invalid JSON", "Error parsing JSON: " + e.getMessage());
        }
    }

    /**
     * Validate a transformation specification - only validate JSON format
     *
     * @param request the transformation request
     * @return validation result
     */
    public ValidationResult validateSpec(TransformRequest request) {
        try {
            Object spec = request.getEffectiveSpec();

            if (spec == null) {
                return ValidationResult.error("Missing specification", "Specification is required");
            }

            // Only validate JSON format
            validateSpecStructure(spec);

            return ValidationResult.valid("Valid specification", "Specification is valid JSON");
        } catch (Exception e) {
            return ValidationResult.error("Invalid specification", "Error validating specification: " + e.getMessage());
        }
    }

    /**
     * Validate specification structure - only check if it's valid JSON
     */
    private void validateSpecStructure(Object spec) throws SpecException {
        try {
            if (spec instanceof String) {
                Object parsedSpec = JsonUtils.jsonToObject((String) spec);
                if (parsedSpec == null) {
                    throw new SpecException("Specification could not be parsed as valid JSON");
                }
            } else {
                // For non-string specs (like chainSpec), convert to JSON string and validate
                String specString = objectMapper.writeValueAsString(spec);
                Object parsedSpec = JsonUtils.jsonToObject(specString);
                if (parsedSpec == null) {
                    throw new SpecException("Specification could not be parsed as valid JSON");
                }
            }
        } catch (JsonProcessingException e) {
            throw new SpecException("Specification could not be parsed as valid JSON: " + e.getMessage());
        }
    }

    /**
     * Estimate the complexity of a transformation
     *
     * @param request the transformation request
     * @return complexity level (Low, Medium, High)
     */
    public String estimateComplexity(TransformRequest request) {
        try {
            // Check spec complexity
            Object spec = request.getEffectiveSpec();
            String specString = specToString(spec);
            
            if (specString != null) {
                int specLength = specString.length();
                int nestingLevel = countNestingLevels(specString);
                
                if (specLength < 200 && nestingLevel < 3) {
                    return "Low";
                } else if (specLength < 500 && nestingLevel < 5) {
                    return "Medium";
                } else {
                    return "High";
                }
            }
            
            return "Medium";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Convert spec to string representation
     */
    private String specToString(Object spec) {
        try {
            if (spec instanceof String) {
                return (String) spec;
            } else {
                return objectMapper.writeValueAsString(spec);
            }
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Count nesting levels in JSON string
     */
    private int countNestingLevels(String jsonString) {
        int maxDepth = 0;
        int currentDepth = 0;
        
        for (char c : jsonString.toCharArray()) {
            if (c == '{' || c == '[') {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
            } else if (c == '}' || c == ']') {
                currentDepth--;
            }
        }
        
        return maxDepth;
    }

    /**
     * Get list of supported operations - only chain
     *
     * @return list of supported operations
     */
    public List<String> getSupportedOperations() {
        return Arrays.asList("chain");
    }
}