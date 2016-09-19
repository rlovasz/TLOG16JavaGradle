package timelogger.exceptions;

public class NoMonthsException extends RuntimeException {

    public NoMonthsException() {
    }
    
    public NoMonthsException(String message) {
        super(message);
    }
    
}
