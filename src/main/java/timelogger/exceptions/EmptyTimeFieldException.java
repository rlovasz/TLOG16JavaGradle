package timelogger.exceptions;

public class EmptyTimeFieldException extends RuntimeException {

    public EmptyTimeFieldException() {
    } 
    
    public EmptyTimeFieldException(String message) {
        super(message);
    }
    
}
