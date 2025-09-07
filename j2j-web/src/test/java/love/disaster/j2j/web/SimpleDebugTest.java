package love.disaster.j2j.web;

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
public class SimpleDebugTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    // Simple test to debug the transformation
    @Test
    public void testSimpleTransformation() {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"name\":\"John\"}");
        
        // Simple chain spec that should work
        String chainSpec = "[{\"operation\":\"shift\",\"spec\":{\"name\":\"fullName\"}}]";
        request.setChainSpec(chainSpec);

        System.out.println("Sending request with input: " + request.getInput());
        System.out.println("Sending request with chainSpec: " + chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        
        if (!transformResponse.isSuccess()) {
            System.out.println("Error: " + transformResponse.getError());
        }
        
        assertTrue(transformResponse.isSuccess());
    }

    // Simple test to debug JSON validation
    @Test
    public void testJsonValidation() {
        String validJson = "{\"test\": \"value\"}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(validJson, headers);

        ResponseEntity<ValidationResult> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform/validate/json",
                entity,
                ValidationResult.class);

        System.out.println("Validation response status: " + response.getStatusCode());
        System.out.println("Validation response body: " + response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ValidationResult result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isValid());
    }
}