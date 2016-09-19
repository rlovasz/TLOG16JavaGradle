package timelogger.exceptions;

public class NoTaskIdException extends RuntimeException {

    public NoTaskIdException() {
    }
    
    public NoTaskIdException(String message) {
        super(message);
    }
    
}
