package timelogger;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import timelogger.beans.Task;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.NotExpectedTimeOrderException;

public class Util {

    /**
     * This method rounds the given duration to be multiple of quarter hour
     * @param startTime the beginning of the time interval
     * @param endTime the end of the time interval
     * @return returns with the new endTime for the interval
     */
    public static LocalTime roundToMultipleQuarterHour(LocalTime startTime, LocalTime endTime) {
        long taskLengthInMinutes = Math.round((float)Duration.between(startTime, endTime).toMinutes()/15)*15;
        return startTime.plusMinutes(taskLengthInMinutes);
    }
    /**
     * This method checks if the minutes are the multiple of quarter hour
     *
     * @param startTime
     * @param endTime
     * @return true, if it is multiple, but false if it isn't.
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public static boolean isMultipleQuarterHour(LocalTime startTime, LocalTime endTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        if (startTime == null || endTime == null) {
            throw new EmptyTimeFieldException("You leaved out a time argument, you should set it.");
        } else if (startTime.isBefore(endTime) || startTime.equals(endTime)) {
            return Duration.between(startTime, endTime).toMinutes()%15 == 0;
        } else {
            throw new NotExpectedTimeOrderException("Something went wrong. You should begin"
                    + " your task before you finish it.");
        }

    }

    /**
     * This method decides, if the task parameter has common time interval with
     * one of the existing tasks
     *
     * @param tasks
     * @param task the parameter to check
     * @return true, if there is no common time interval, false, if there is a
     * common time interval
     */
    public static boolean isSeparatedTime(List<Task> tasks, Task task) {
        boolean isSeparated = true;
        for (Task t : tasks) {
            boolean existingBeginsEarlier = t.getStartTime().isBefore(task.getStartTime()) && task.getStartTime().isBefore(t.getEndTime());
            boolean newBeginsEarlier = t.getStartTime().isAfter(task.getStartTime()) && t.getStartTime().isBefore(task.getEndTime());
            boolean endsOrBeginsTogether = (t.getEndTime().equals(task.getEndTime()) && (!t.getEndTime().equals(t.getStartTime()) && !task.getEndTime().equals(task.getStartTime()))) || t.getStartTime().equals(task.getStartTime());
            isSeparated = !(existingBeginsEarlier || newBeginsEarlier || endsOrBeginsTogether);
            if (isSeparated == false) {
                break;
            }
        }
        return isSeparated;
    }

}
