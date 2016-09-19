package timelogger.exceptions;

public class NotExpectedTimeOrderException extends RuntimeException {

    public NotExpectedTimeOrderException() {
    }
    
    public NotExpectedTimeOrderException(String message) {
        super(message);
    }
    
}
