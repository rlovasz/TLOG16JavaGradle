package timelogger.exceptions;

/**
 * This is a type of Exceptions which is thrown if the new task has a common
 * time interval with the existing ones
 *
 * @author rlovasz
 */
public class NotSeparatedTaskTimesException extends Exception {

    /**
     *
     * @param message sets the message of the exception
     */
    public NotSeparatedTaskTimesException(String message) {
        super(message);
    }
}
