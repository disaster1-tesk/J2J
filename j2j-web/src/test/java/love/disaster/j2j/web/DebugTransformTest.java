package love.disaster.j2j.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import love.disaster.j2j.web.dto.TransformRequest;
import love.disaster.j2j.web.dto.TransformResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DebugTransformTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void debugSimpleTransform() throws Exception {
        TransformRequest request = new TransformRequest();
        request.setOperation("chain");
        request.setInput("{\"name\":\"John\"}");
        
        Object chainSpec = objectMapper.readValue("[{\"operation\":\"shift\",\"spec\":{\"name\":\"fullName\"}}]", Object.class);
        request.setChainSpec(chainSpec);

        ResponseEntity<TransformResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/transform", 
                request, 
                TransformResponse.class);

        System.out.println("Status Code: " + response.getStatusCode());
        TransformResponse transformResponse = response.getBody();
        if (transformResponse != null) {
            System.out.println("Success: " + transformResponse.isSuccess());
            System.out.println("Result: " + transformResponse.getResult());
            System.out.println("Error: " + transformResponse.getError());
        }
    }
}