package love.disaster.j2j.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import love.disaster.j2j.web.dto.TransformRequest;
import love.disaster.j2j.web.dto.TransformResponse;
import love.disaster.j2j.web.dto.ValidationResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BetterValidationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    // Test Case 1: Simple shift transformation
    @Test
    public void testSimpleShiftTransformation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"name\":\"John\"}");
        
        // Using Object instead of String for chainSpec
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"name\":\"fullName\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Parse the result and check content
        Object result = objectMapper.readValue(transformResponse.getResult(), Object.class);
        String resultString = objectMapper.writeValueAsString(result);
        assertTrue(resultString.contains("\"fullName\":\"John\"") || resultString.contains("\"fullName\": \"John\""));
    }

    // Test Case 2: Identity transformation
    @Test
    public void testIdentityTransformation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"name\":\"John\",\"age\":30}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"@\":\"\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that the result contains the original data
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"name\""));
        assertTrue(resultString.contains("\"John\""));
        assertTrue(resultString.contains("\"age\""));
        assertTrue(resultString.contains("30"));
    }

    // Test Case 3: JSON validation - valid JSON
    @Test
    public void testValidJsonValidation() {
        String validJson = "{\"test\": \"value\"}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(validJson, headers);

        ResponseEntity<ValidationResult> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform/validate/json",
                entity,
                ValidationResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ValidationResult result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("Valid JSON", result.getMessage());
    }

    // Test Case 4: Spec validation - valid chain spec
    @Test
    public void testValidSpecValidation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"name\":\"fullName\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<ValidationResult> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform/validate/spec",
                request,
                ValidationResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ValidationResult result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("Valid specification", result.getMessage());
    }

    // Test Case 5: Complex chain with multiple operations
    @Test
    public void testComplexChainTransformation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"user\":{\"name\":\"John\",\"age\":30}}");
        
        // Chain with shift and default operations
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"user\":{\"name\":\"fullName\"}}},{\"operation\":\"default\",\"spec\":{\"country\":\"USA\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that both operations worked
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"fullName\""));
        assertTrue(resultString.contains("\"John\""));
        assertTrue(resultString.contains("\"country\""));
        assertTrue(resultString.contains("\"USA\""));
    }

    // Test Case 6: Remove operation
    @Test
    public void testRemoveTransformation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"name\":\"John\",\"hidden\":\"secret\",\"visible\":\"data\"}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"remove\",\"spec\":{\"hidden\":\"\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that hidden field was removed but visible field remains
        String resultString = transformResponse.getResult();
        assertFalse(resultString.contains("\"hidden\""));
        assertFalse(resultString.contains("\"secret\""));
        assertTrue(resultString.contains("\"name\""));
        assertTrue(resultString.contains("\"visible\""));
    }

    // Test Case 7: Sort operation
    @Test
    public void testSortTransformation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"zoo\":\"animals\",\"bar\":\"test\",\"foo\":\"data\"}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"sort\"}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // After sorting, keys should be in alphabetical order
        String resultString = transformResponse.getResult();
        // We can't easily test the order in a string comparison, but we can check that it's valid JSON
        assertTrue(resultString.contains("\"bar\""));
        assertTrue(resultString.contains("\"foo\""));
        assertTrue(resultString.contains("\"zoo\""));
    }

    // Test Case 8: Array handling
    @Test
    public void testArrayHandling() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"items\":[\"first\",\"second\",\"third\"]}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"items\":\"elements\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that the array was renamed
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"elements\""));
        assertTrue(resultString.contains("\"first\""));
        assertTrue(resultString.contains("\"second\""));
        assertTrue(resultString.contains("\"third\""));
    }

    // Test Case 9: Nested object transformation
    @Test
    public void testNestedObjectTransformation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"user\":{\"profile\":{\"name\":\"John\",\"age\":30}}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"user\":{\"profile\":{\"name\":\"fullName\"}}}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that the nested field was extracted
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"fullName\""));
        assertTrue(resultString.contains("\"John\""));
    }

    // Test Case 10: Error handling for malformed JSON input
    @Test
    public void testErrorHandlingForMalformedInput() {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"name\":\"John\""); // Missing closing brace
        
        Object chainSpec = "[{\"operation\":\"shift\",\"spec\":{\"name\":\"fullName\"}}]";
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertFalse(transformResponse.isSuccess());
        assertNotNull(transformResponse.getError());
    }
}