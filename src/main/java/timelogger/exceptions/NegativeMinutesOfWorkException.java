package timelogger.exceptions;

public class NegativeMinutesOfWorkException extends RuntimeException {

    public NegativeMinutesOfWorkException() {
    }
    
    public NegativeMinutesOfWorkException(String message) {
        super(message);
    }
}
