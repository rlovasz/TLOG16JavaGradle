package timelogger.exceptions;

public class WeekendNotEnabledException extends RuntimeException {

    public WeekendNotEnabledException() {
    }
    
    public WeekendNotEnabledException(String message) {
        super(message);
    }
    
}
