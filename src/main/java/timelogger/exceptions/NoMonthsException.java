package timelogger.exceptions;

/**
 * This exception is thrown if there is no month in the timelogger 
 * when the first month is queried
 *
 * @author rlovasz
 */
public class NoMonthsException extends Exception {

    /**
     *
     * @param message sets the message of the exception
     */
    public NoMonthsException(String message) {
        super(message);
    }
    
}
