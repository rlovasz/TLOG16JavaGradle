package timelogger.exceptions;

public class FutureWorkException extends RuntimeException {

    public FutureWorkException() {
    }
    
    public FutureWorkException(String message) {
        super(message);
    }
    
}
