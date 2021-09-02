package zdha.exceptions;

/**
 * @author saurabhk
 */
public enum ErrorCode {
    NOT_FOUND("Not Found");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    /**
     * @return String
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
