package love.disaster.j2j.web.dto;

/**
 * Response DTO for transformation operations
 */
public class TransformResponse {
    private boolean success;
    private String result;
    private String error;
    private long executionTime;
    private int memoryUsage;
    private String complexity;

    public TransformResponse() {}

    public TransformResponse(boolean success, String result, String error, long executionTime) {
        this.success = success;
        this.result = result;
        this.error = error;
        this.executionTime = executionTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }
}