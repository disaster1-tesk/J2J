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
public class AdditionalPatternValidationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    // Test Case 1: Array to object transformation
    @Test
    public void testArrayToObjectTransformation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("[{\"id\":1,\"name\":\"John\"},{\"id\":2,\"name\":\"Jane\"}]");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"*\":{\"id\":\"users[&1].id\",\"name\":\"users[&1].name\"}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"users\" : ["));
        assertTrue(resultString.contains("\"id\" : 1"));
        assertTrue(resultString.contains("\"name\" : \"John\""));
    }

    // Test Case 2: Complex wildcard with array indexing
    @Test
    public void testComplexWildcardWithArrayIndexing() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"data\":[{\"value\":\"a\"},{\"value\":\"b\"},{\"value\":\"c\"}]}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"data\":{\"*\":{\"value\":\"result.&1\"}}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"result\" : {"));
        assertTrue(resultString.contains("\"0\" : \"a\""));
        assertTrue(resultString.contains("\"1\" : \"b\""));
    }

    // Test Case 3: Nested array handling
    @Test
    public void testNestedArrayHandling() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"departments\":[{\"name\":\"IT\",\"employees\":[{\"name\":\"John\"},{\"name\":\"Jane\"}]},{\"name\":\"HR\",\"employees\":[{\"name\":\"Bob\"}]}]}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"departments\":{\"*\":{\"name\":\"depts[&1].name\",\"employees\":{\"*\":{\"name\":\"depts[&3].employees[&1].name\"}}}}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"depts\" : ["));
        assertTrue(resultString.contains("\"IT\""));
        assertTrue(resultString.contains("\"John\""));
        assertTrue(resultString.contains("\"HR\""));
        assertTrue(resultString.contains("\"Bob\""));
    }

    // Test Case 4: Attribute reference with complex nesting
    @Test
    public void testAttributeReferenceWithComplexNesting() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"user\":{\"profile\":{\"personal\":{\"firstName\":\"John\",\"lastName\":\"Doe\"}}}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"user\":{\"profile\":{\"personal\":{\"firstName\":\"@(1,lastName)\"}}}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"Doe\" : \"John\""));
    }

    // Test Case 5: Multiple operations in chain
    @Test
    public void testMultipleOperationsInChain() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"product\":{\"id\":123,\"name\":\"Laptop\",\"price\":999.99}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"product\":{\"id\":\"item.id\",\"name\":\"item.name\"}}},{\"operation\":\"default\",\"spec\":{\"item\":{\"category\":\"Electronics\"}}},{\"operation\":\"remove\",\"spec\":{\"item\":{\"price\":\"\"}}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"item\" : {"));
        assertTrue(resultString.contains("\"id\" : 123"));
        assertTrue(resultString.contains("\"name\" : \"Laptop\""));
        assertTrue(resultString.contains("\"category\" : \"Electronics\""));
        assertFalse(resultString.contains("\"price\""));
    }

    // Test Case 6: Complex object restructuring
    @Test
    public void testComplexObjectRestructuring() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"order\":{\"id\":1001,\"customer\":{\"name\":\"John Doe\",\"email\":\"john@example.com\"},\"items\":[{\"product\":\"Laptop\",\"quantity\":1},{\"product\":\"Mouse\",\"quantity\":2}]}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"order\":{\"id\":\"orderNumber\",\"customer\":{\"name\":\"customerName\",\"email\":\"customerEmail\"},\"items\":{\"*\":{\"product\":\"products[&1].name\",\"quantity\":\"products[&1].qty\"}}}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"orderNumber\" : 1001"));
        assertTrue(resultString.contains("\"customerName\" : \"John Doe\""));
        assertTrue(resultString.contains("\"products\" : ["));
        assertTrue(resultString.contains("\"Laptop\""));
        assertTrue(resultString.contains("\"qty\" : 1"));
    }

    // Test Case 7: Identity transformation
    @Test
    public void testIdentityTransformation() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"message\":\"Hello World\",\"count\":42}");
        
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
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"message\" : \"Hello World\""));
        assertTrue(resultString.contains("\"count\" : 42"));
    }

    // Test Case 8: Complex default value application
    @Test
    public void testComplexDefaultValueApplication() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"user\":{\"name\":\"John\"}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"default\",\"spec\":{\"user\":{\"age\":25,\"active\":true,\"role\":\"user\"}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"name\" : \"John\""));
        assertTrue(resultString.contains("\"age\" : 25"));
        assertTrue(resultString.contains("\"active\" : true"));
        assertTrue(resultString.contains("\"role\" : \"user\""));
    }

    // Test Case 9: Array element filtering
    @Test
    public void testArrayElementFiltering() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"numbers\":[1,2,3,4,5,6,7,8,9,10]}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"numbers\":{\"2\":\"third\",\"5\":\"sixth\",\"8\":\"ninth\"}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"third\" : 3"));
        assertTrue(resultString.contains("\"sixth\" : 6"));
        assertTrue(resultString.contains("\"ninth\" : 9"));
    }

    // Test Case 10: Deep nested structure with multiple levels
    @Test
    public void testDeepNestedStructureWithMultipleLevels() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"level1\":{\"level2\":{\"level3\":{\"level4\":{\"value\":\"deeplyNested\"}}}}}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"level1\":{\"level2\":{\"level3\":{\"level4\":{\"value\":\"result\"}}}}}}]}", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        assertNotNull(transformResponse);
        assertTrue(transformResponse.isSuccess());
        
        String resultString = transformResponse.getResult();
        assertTrue(resultString.contains("\"result\" : \"deeplyNested\""));
    }
}