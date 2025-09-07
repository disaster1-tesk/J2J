package love.disaster.j2j.web.dto;

/**
 * DTO for validation results with detailed error information
 */
public class ValidationResult {
    
    public enum Level {
        VALID, WARNING, ERROR
    }
    
    private boolean valid;
    private Level level;
    private String message;
    private String details;
    private int line;
    private int column;

    public ValidationResult() {}

    public ValidationResult(boolean valid, Level level, String message, String details) {
        this.valid = valid;
        this.level = level;
        this.message = message;
        this.details = details;
    }

    public static ValidationResult valid(String message, String details) {
        return new ValidationResult(true, Level.VALID, message, details);
    }

    public static ValidationResult warning(String message, String details) {
        return new ValidationResult(true, Level.WARNING, message, details);
    }

    public static ValidationResult error(String message, String details) {
        return new ValidationResult(false, Level.ERROR, message, details);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}