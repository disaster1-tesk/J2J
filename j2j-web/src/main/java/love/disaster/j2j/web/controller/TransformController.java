package love.disaster.j2j.web.controller;

import love.disaster.j2j.web.dto.TransformRequest;
import love.disaster.j2j.web.dto.TransformResponse;
import love.disaster.j2j.web.dto.ValidationResult;
import love.disaster.j2j.web.service.TransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling JSON transformation requests
 */
@RestController
@RequestMapping("/api/transform")
@CrossOrigin(origins = "*")
public class TransformController {

    private final TransformService transformService;

    @Autowired
    public TransformController(TransformService transformService) {
        this.transformService = transformService;
    }

    /**
     * Perform a JSON transformation based on the provided operation and specification
     *
     * @param request the transformation request containing operation type, spec, and input
     * @return the transformation result
     */
    @PostMapping
    public ResponseEntity<TransformResponse> transform(@RequestBody TransformRequest request) {
        try {
            TransformResponse response = transformService.performTransform(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TransformResponse errorResponse = new TransformResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Transformation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Validate a JSON input string
     *
     * @param json the JSON string to validate
     * @return validation result
     */
    @PostMapping("/validate/json")
    public ResponseEntity<ValidationResult> validateJson(@RequestBody String json) {
        try {
            ValidationResult result = transformService.validateJson(json);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ValidationResult errorResult = ValidationResult.error(
                "Validation failed", 
                "Error validating JSON: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    /**
     * Validate a transformation specification
     *
     * @param request the transformation request containing operation type and spec
     * @return validation result
     */
    @PostMapping("/validate/spec")
    public ResponseEntity<ValidationResult> validateSpec(@RequestBody TransformRequest request) {
        try {
            ValidationResult result = transformService.validateSpec(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ValidationResult errorResult = ValidationResult.error(
                "Validation failed", 
                "Error validating specification: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    /**
     * Get a list of supported transformation operations
     *
     * @return list of supported operations
     */
    @GetMapping("/operations")
    public ResponseEntity<List<String>> getSupportedOperations() {
        List<String> operations = transformService.getSupportedOperations();
        return ResponseEntity.ok(operations);
    }
}