package timelogger.beans;

import timelogger.exceptions.NegativeMinutesOfWorkException;
import timelogger.exceptions.FutureWorkException;
import timelogger.exceptions.NotSeparatedTaskTimesException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import timelogger.Util;
import timelogger.exceptions.EmptyTimeFieldException;

/**
 * With the instantiation of this class we can create work days. We can set the
 * date of the work day, and the required minutes we should work today. We can
 * check if the work day is weekday, and we can add tasks for the day. We can
 * ask for the sum of the working minutes on this work day, and the extra
 * minutes.
 *
 * @author precognox
 */
@Getter
public class WorkDay {

    private static final int DEFAULT_REQUIRED_MIN_PER_DAY = 450;
    private final List<Task> tasks = new ArrayList<>();
    private long requiredMinPerDay;
    private LocalDate actualDay;
    private long sumPerDay = 0;

    /**
     * @param requiredMinPerDay In this parameter you can set the minutes you
     * should work today.
     * @param year
     * @param month
     * @param day
     * @throws timelogger.exceptions.FutureWorkException
     * @throws timelogger.exceptions.NegativeMinutesOfWorkException
     */
    public WorkDay(long requiredMinPerDay, int year, int month, int day) throws FutureWorkException, NegativeMinutesOfWorkException {
        LocalDate currentDay = LocalDate.of(year, month, day);
        if (requiredMinPerDay <= 0) {
            throw new NegativeMinutesOfWorkException("You set a negative value for required minutes, you should set a non-negative value!");
        }
        if (currentDay.isAfter(LocalDate.now())) {
            throw new FutureWorkException("You cannot work later than today, you should set an other day!");
        }
        this.requiredMinPerDay = requiredMinPerDay;
        this.actualDay = currentDay;
    }

    /**
     * The default actual day will be today (server time).
     *
     * @param requiredMinPerDay In this parameter you can set the minutes you
     * should work today.
     * @throws timelogger.exceptions.FutureWorkException
     * @throws timelogger.exceptions.NegativeMinutesOfWorkException
     */
    public WorkDay(long requiredMinPerDay) throws FutureWorkException, NegativeMinutesOfWorkException {
        this(requiredMinPerDay, LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
    }

    /**
     * The default required minutes will be 450 min=7.5 h
     *
     * @param year, the year value of the date in YYYY format
     * @param month, the month value of the date with simple integer value
     * @param day, the day value of the date with simple integer value
     * @throws timelogger.exceptions.FutureWorkException
     * @throws timelogger.exceptions.NegativeMinutesOfWorkException
     */
    public WorkDay(int year, int month, int day) throws FutureWorkException, NegativeMinutesOfWorkException {
        this(DEFAULT_REQUIRED_MIN_PER_DAY, year, month, day);
    }

    /**
     * The default actual day will be today (server time), the default required
     * minutes will be 450 min = 7.5 h
     *
     * @throws timelogger.exceptions.FutureWorkException
     * @throws timelogger.exceptions.NegativeMinutesOfWorkException
     */
    public WorkDay() throws FutureWorkException, NegativeMinutesOfWorkException {
        this(DEFAULT_REQUIRED_MIN_PER_DAY, LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
    }

    /**
     * We can set the amount of the minutes the employee should work this day.
     *
     * @param requiredMinPerDay the value which will be set
     */
    public void setRequiredMinPerDay(long requiredMinPerDay) throws NegativeMinutesOfWorkException {
        if (requiredMinPerDay <= 0) {
            throw new NegativeMinutesOfWorkException("You set a negative value for required minutes, you should set a non-negative value!");
        }
        this.requiredMinPerDay = requiredMinPerDay;
    }

    /**
     * We can set the date of the actual day.
     *
     * @param year, the year value of the date in YYYY format
     * @param month, the month value of the date with simple integer value
     * @param day, the day value of the date with simple integer value
     * @throws timelogger.exceptions.FutureWorkException
     */
    public void setActualDay(int year, int month, int day) throws FutureWorkException {
        LocalDate currentDay = LocalDate.of(year, month, day);
        if (currentDay.isAfter(LocalDate.now())) {
            throw new FutureWorkException("You cannot work later than today, you should set an other day!");
        }
        this.actualDay = currentDay;
    }

    /**
     * This method calculates the difference between the minutes while the
     * employee worked and the minutes while the employee should have worked
     *
     * @return with the signed value of the extra minutes on this work day. If
     * it is positive the employee worked more, if it is negative the employee
     * worked less, then the required.
     * @throws EmptyTimeFieldException
     */
    public long getExtraMinPerDay() throws EmptyTimeFieldException {
        return getSumPerDay() - requiredMinPerDay;
    }

    /**
     * This methods calculates the sum of the minutes of the tasks of this work
     * day.
     *
     * @return with the minutes while the employee worked on this work day
     * @throws EmptyTimeFieldException
     */
    public long getSumPerDay() throws EmptyTimeFieldException {
        if (sumPerDay == 0) {
            for (Task task : tasks) {
                sumPerDay += task.getMinPerTask();
            }
        }
        return sumPerDay;
    }

    /**
     * This method adds a new task to the List named tasks, after it checks if
     * the minutes of the task are the multiple of a quarter hour. If it would
     * be false, this method throws
     *
     * @param task It is a Task type parameter, which will be added
     * @throws timelogger.exceptions.NotSeparatedTaskTimesException
     */
    public void addTask(Task task) throws NotSeparatedTaskTimesException {
        if (Util.isSeparatedTime(tasks, task)) {
            tasks.add(task);
            sumPerDay = 0;
        } else {
            throw new NotSeparatedTaskTimesException("You should separate the time intervals of your tasks!");
        }
    }

    /**
     * This method finds the latest endTime
     *
     * @return with the latest endTime, if exists, but if there are no tasks, it
     * returns with null
     */
    public LocalTime endTimeOfTheLastTask() {
        LocalTime endTimeOfTheLastTask = null;
        if (!tasks.isEmpty()) {
            List<LocalTime> endTimes = tasks.stream().map(Task::getEndTime).collect(Collectors.toList());
            List<LocalTime> startTimes = tasks.stream().map(Task::getStartTime).collect(Collectors.toList());
            for (int i = 0; i < endTimes.size(); i++) {
                if (endTimes.get(i).equals(startTimes.get(i))) {
                    endTimes.remove(i);
                    startTimes.remove(i);
                    i--;
                }
            }
            if (!endTimes.isEmpty()) {
                endTimeOfTheLastTask = Collections.max(endTimes);
            }
        }
        return endTimeOfTheLastTask;
    }
}
