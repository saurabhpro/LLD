package worktech.parkinglot.exception;

/**
 * @author saurabhk
 */
public enum ErrorCode {
    NOT_FOUND("Not Found"),
    INVALID_FILE("Invalid File"),
    INVALID_REQUEST("Invalid Request"),
    INVALID_VALUE("{variable} value is incorrect"),
    PARKING_ALREADY_EXIST("Sorry Parking Already Created, It CAN NOT be again recreated."),
    PARKING_NOT_EXIST_ERROR("Sorry, Car Parking Does not Exist"),
    PROCESSING_ERROR("Processing Error ");

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
