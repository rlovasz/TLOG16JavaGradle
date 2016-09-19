package timelogger.exceptions;

public class NotNewMonthException extends RuntimeException {

    public NotNewMonthException() {
    }
    
    public NotNewMonthException(String message) {
         super(message);
    }
    
}
