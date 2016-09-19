package timelogger.exceptions;

public class NotSeparatedTaskTimesException extends RuntimeException {

    public NotSeparatedTaskTimesException() {
    }
    
    public NotSeparatedTaskTimesException(String message) {
        super(message);
    }
}
