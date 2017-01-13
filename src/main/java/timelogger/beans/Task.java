package timelogger.beans;

import timelogger.exceptions.NotExpectedTimeOrderException;
import timelogger.exceptions.NoTaskIdException;
import timelogger.exceptions.InvalidTaskIdException;
import timelogger.exceptions.EmptyTimeFieldException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
import timelogger.Util;

/**
 * With the instantiation of this class we can create tasks. We can set a tasks
 * Id, the time of its start, the time of its end, we can add a comment to
 * detail. We can check if a task Id is valid and we can ask for the duration of
 * the task.
 *
 * @author precognox
 */
@Getter
public class Task {

    private String taskId;
    private LocalTime startTime;
    private LocalTime endTime;
    @Setter
    private String comment = "";

    /**
     * @param taskId This is the Id of the task. Redmine project: 4 digits, LT
     * project: LT-(4 digits)
     * @param comment In this parameter you can add some detail about what did
     * you do exactly.
     * @param startHour, the hour part of the beginning time
     * @param startMin, the mint part of the beginning time
     * @param endHour, the hour part of the finishing time
     * @param endMin , the min part of the finishing time
     * @throws InvalidTaskIdException
     * @throws timelogger.exceptions.NoTaskIdException
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public Task(String taskId, String comment, int startHour, int startMin, int endHour, int endMin) throws InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException, NotExpectedTimeOrderException {
        this.taskId = taskId;
        this.startTime = LocalTime.of(startHour, startMin);
        this.endTime = LocalTime.of(endHour, endMin);
        this.comment = comment;
        checkValidity();
    }

    /**
     *
     * @param taskId This is the Id of the task. Redmine project: 4 digits, LT
     * project: LT-(4 digits)
     * @throws InvalidTaskIdException
     * @throws timelogger.exceptions.NoTaskIdException
     */
    public Task(String taskId) throws InvalidTaskIdException, NoTaskIdException {
        this.taskId = taskId;
        if (!isValidTaskID()) {
            throw new InvalidTaskIdException("It is not a valid task Id. Valid id's: 4 digits or LT-4 digits");
        }
    }

    /**
     *
     * @param taskId This is the Id of the task. Redmine project: 4 digits, LT
     * project: LT-(4 digits)
     * @param comment In this parameter you can add some detail about what did
     * you do exactly.
     * @param startTimeString the beginning time of task with string in format
     * HH:MM
     * @param endTimeString the finishing time of task with string in format
     * HH:MM
     * @throws InvalidTaskIdException
     * @throws timelogger.exceptions.NoTaskIdException
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public Task(String taskId, String comment, String startTimeString, String endTimeString) throws InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException, NotExpectedTimeOrderException {
        if ("".equals(startTimeString)) {
            this.startTime = null;
        } else {
            this.startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ISO_TIME);
        }
        if ("".equals(endTimeString)) {
            this.endTime = null;
        } else {
            this.endTime = LocalTime.parse(endTimeString, DateTimeFormatter.ISO_TIME);
        }
        this.taskId = taskId;
        this.comment = comment;
        checkValidity();
    }

    /**
     *
     * @param hour the value of hour with integer
     * @param min the value of minutes with integer
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setStartTime(int hour, int min) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        setStartTime(LocalTime.of(hour, min));
    }

    /**
     *
     * @param hour the value of hour with integer
     * @param min the value of minutes with integer
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setEndTime(int hour, int min) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        setEndTime(LocalTime.of(hour, min));
    }

    /**
     *
     * @param time The String value of time in format HH:MM
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setStartTime(String time) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        setStartTime(LocalTime.parse(time, DateTimeFormatter.ISO_TIME));
    }

    /**
     *
     * @param time The String value of time in format HH:MM
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setEndTime(String time) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        setEndTime(LocalTime.parse(time, DateTimeFormatter.ISO_TIME));
    }

    /**
     *
     * @param time The LocalTime value of time
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setStartTime(LocalTime time) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        this.startTime = time;
        checkMultipleQuarterHour();
    }

    /**
     *
     * @param time The LocalTime value of time
     * @throws timelogger.exceptions.NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setEndTime(LocalTime time) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        this.endTime = time;
        checkMultipleQuarterHour();
    }

    /**
     * @param taskId The parameter to set
     * @throws InvalidTaskIdException
     * @throws timelogger.exceptions.NoTaskIdException
     */
    public void setTaskId(String taskId) throws InvalidTaskIdException, NoTaskIdException {
        this.taskId = taskId;
        if (!isValidTaskID()) {
            throw new InvalidTaskIdException("It is not a valid task Id. Valid id's: 4 digits or LT-4 digits");
        }
    }

    /**
     * @return with the value of comment, but if it is not set, it returns with
     * an empty String
     */
    public String getComment() {
        if (comment == null) {
            comment = "";
        }
        return comment;
    }

    /**
     * This method is a getter for the minPerTask field.
     *
     * @return with the time interval between startTime and endTime in minutes
     * @throws EmptyTimeFieldException
     */
    public long getMinPerTask() throws EmptyTimeFieldException {
        if (startTime == null || endTime == null) {
            throw new EmptyTimeFieldException("You leaved out a time argument, you should set it.");
        } else {
            return Duration.between(startTime, endTime).toMinutes();
        }

    }

    /**
     * This method checks if the Id of the task is a valid redmine task Id.
     *
     * @return true, if it is valid, false if it isn't valid.
     */
    private boolean isValidRedmineTaskId() {
        return taskId.matches("\\d{4}");
    }

    /**
     * This method checks if the Id of the task is a valid LT task Id.
     *
     * @return true, if it is valid, false if it isn't valid.
     */
    private boolean isValidLTTaskId() {
        return taskId.matches("LT-\\d{4}");
    }

    /**
     * This method checks if the Id of the task is a valid task Id (redmine or
     * LT project task id).
     *
     * @return true, if it is valid, false if it isn't valid.
     * @throws timelogger.exceptions.NoTaskIdException
     */
    private boolean isValidTaskID() throws NoTaskIdException {
        if (taskId == null) {
            throw new NoTaskIdException("There is no task Id, please set a valid Id!");
        } else {
            return isValidLTTaskId() || isValidRedmineTaskId();
        }
    }

    /**
     * Checks if the duration of the task is multiple of quarter hour and rounds
     * it if not
     *
     * @param time only checks if this time is not null
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException
     */
    private void checkMultipleQuarterHour() throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        if (!Util.isMultipleQuarterHour(this.startTime, this.endTime)) {
            this.endTime = Util.roundToMultipleQuarterHour(this.startTime, this.endTime);
        }
    }

    /**
     * Checks if the given values are valid for the task
     *
     * @throws NoTaskIdException
     * @throws InvalidTaskIdException
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException
     */
    private void checkValidity() throws NoTaskIdException, InvalidTaskIdException, EmptyTimeFieldException, NotExpectedTimeOrderException {
        if (!Util.isMultipleQuarterHour(startTime, endTime)) {
            this.endTime = Util.roundToMultipleQuarterHour(startTime, endTime);
        }
        if (!isValidTaskID()) {
            throw new InvalidTaskIdException("It is not a valid task Id. Valid id's: 4 digits or LT-4 digits");
        }
    }

    @Override
    public String toString() {
        return "Task Id: " + taskId + ", Start time: " + startTime + ", End Time: " + endTime + ", Comment: " + comment;
    }
}
