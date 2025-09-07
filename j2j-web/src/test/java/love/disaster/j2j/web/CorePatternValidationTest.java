package love.disaster.j2j.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import love.disaster.j2j.web.dto.TransformRequest;
import love.disaster.j2j.web.dto.TransformResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CorePatternValidationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    // Test Case 1: Wildcard matching pattern (based on core shiftr tests)
    @Test
    public void testWildcardMatchingPattern() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"data\":{\"item1\":\"value1\",\"item2\":\"value2\"}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"data\":{\"*\":\"items.&\"}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that both items were processed
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"item1\" : \"value1\""));
        assertTrue(resultString.contains("\"item2\" : \"value2\""));
    }

    // Test Case 2: Attribute reference pattern (based on core shiftr tests)
    @Test
    public void testAttributeReferencePattern() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"rating\":{\"primary\":{\"value\":3,\"max\":5}}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"rating\":{\"primary\":{\"value\":\"Rating\"}}}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that the value was extracted correctly
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"Rating\" : 3"));
    }

    // Test Case 3: Complex nested structure transformation
    @Test
    public void testComplexNestedStructure() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"level1\":{\"level2\":{\"level3\":{\"value\":\"deep\"}}}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"level1\":{\"level2\":{\"level3\":{\"value\":\"deepValue\"}}}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that the deep value was extracted
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"deepValue\" : \"deep\""));
    }

    // Test Case 4: Array handling with indexing
    @Test
    public void testArrayHandlingWithIndexing() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"users\":[{\"name\":\"John\"},{\"name\":\"Jane\"}]}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"users\":{\"*\":{\"name\":\"names[]\"}}}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that names were collected into an array
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"names\" : ["));
        assertTrue(resultString.contains("\"John\""));
        assertTrue(resultString.contains("\"Jane\""));
    }

    // Test Case 5: Default value application
    @Test
    public void testDefaultValueApplication() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"Rating\":3}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"default\",\"spec\":{\"RatingRange\":5}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that both original and default values are present
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"Rating\" : 3"));
        assertTrue(resultString.contains("\"RatingRange\" : 5"));
    }

    // Test Case 6: Null value handling
    @Test
    public void testNullValueHandling() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"key\":null}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"key\":\"value\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that null value is preserved
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"value\" : null"));
    }

    // Test Case 7: Boolean value handling
    @Test
    public void testBooleanValueHandling() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"flag\":true,\"status\":false}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"flag\":\"flagResult\",\"status\":\"statusResult\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that boolean values are preserved
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"flagResult\" : true"));
        assertTrue(resultString.contains("\"statusResult\" : false"));
    }

    // Test Case 8: Number value handling
    @Test
    public void testNumberValueHandling() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"integer\":42,\"float\":3.14,\"negative\":-10}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"integer\":\"intResult\",\"float\":\"floatResult\",\"negative\":\"negResult\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that number values are preserved
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"intResult\" : 42"));
        assertTrue(resultString.contains("\"floatResult\" : 3.14"));
        assertTrue(resultString.contains("\"negResult\" : -10"));
    }

    // Test Case 9: Empty object handling
    @Test
    public void testEmptyObjectHandling() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{}");
        
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
        
        // Check that empty object is preserved
        String resultString = transformResponse.getResult().trim();
        assertEquals("{ }", resultString);
    }

    // Test Case 10: Empty array handling
    @Test
    public void testEmptyArrayHandling() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"emptyArray\":[]}");
        
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
        
        // Check that empty array is preserved
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"emptyArray\" : [ ]"));
    }

    // Test Case 11: Special characters in keys
    @Test
    public void testSpecialCharactersInKeys() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"key-with-dash\":\"value1\",\"key_with_underscore\":\"value2\"}");
        
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
        
        // Check that special characters in keys are preserved
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"key-with-dash\" : \"value1\""));
        assertTrue(resultString.contains("\"key_with_underscore\" : \"value2\""));
    }

    // Test Case 12: Escaping characters
    @Test
    public void testEscapingCharacters() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"data\":{\"key1\":\"value1\"}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"data\":{\"key1\":\"result\"}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that escaped characters work correctly
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"result\" : \"value1\""));
    }

    // Test Case 13: Complex multi-operation chain
    @Test
    public void testComplexMultiOperationChain() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"rating\":{\"primary\":{\"value\":3},\"quality\":{\"value\":4}}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"rating\":{\"primary\":{\"value\":\"primaryRating\"}}}},{\"operation\":\"default\",\"spec\":{\"primaryRatingMax\":5}},{\"operation\":\"remove\",\"spec\":{\"rating\":{\"quality\":\"\"}}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        // Check that all operations worked correctly
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"primaryRating\" : 3"));
        assertTrue(resultString.contains("\"primaryRatingMax\" : 5"));
        assertFalse(resultString.contains("\"quality\""));
    }

    // Test Case 14: Sort operation verification
    @Test
    public void testSortOperationVerification() throws Exception {
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
        
        // Check that it's valid JSON (we can't easily test order in string comparison)
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"bar\" : \"test\""));
        assertTrue(resultString.contains("\"foo\" : \"data\""));
        assertTrue(resultString.contains("\"zoo\" : \"animals\""));
    }

    // Test Case 15: Error handling for malformed chain spec
    @Test
    public void testErrorHandlingForMalformedChainSpec() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"name\":\"John\"}");
        
        // Malformed chain spec (missing closing bracket)
        String malformedSpec = "[{\"operation\":\"shift\",\"spec\":{\"name\":\"fullName\"}}";
        try {
            Object chainSpec = objectMapper.readValue(malformedSpec, Object.class);
            request.setChainSpec(chainSpec);
            
            ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/api/transform", 
                    request, 
                    TransformResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            TransformResponse transformResponse = response.getBody();
            assertNotNull(transformResponse);
            // This should fail because the spec is malformed
            assertFalse(transformResponse.isSuccess());
        } catch (Exception e) {
            // If parsing fails, that's also a valid test result
            assertTrue(e instanceof com.fasterxml.jackson.core.JsonParseException);
        }
    }
}