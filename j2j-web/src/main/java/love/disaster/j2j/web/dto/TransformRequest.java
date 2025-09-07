package love.disaster.j2j.web.dto;

/**
 * Request DTO for transformation operations
 * Updated to support both single operation specs and chain specs
 */
public class TransformRequest {
    private String operation;
    private String spec;
    private Object chainSpec; // For chain operations
    private String input;

    public TransformRequest() {}

    public TransformRequest(String operation, String spec, String input) {
        this.operation = operation;
        this.spec = spec;
        this.input = input;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Object getChainSpec() {
        return chainSpec;
    }

    public void setChainSpec(Object chainSpec) {
        this.chainSpec = chainSpec;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
    
    /**
     * Get the appropriate spec based on operation type
     * For chain operations, returns chainSpec; for others, returns spec
     */
    public Object getEffectiveSpec() {
        if ("chain".equalsIgnoreCase(operation) && chainSpec != null) {
            return chainSpec;
        }
        return spec;
    }
}