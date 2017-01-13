package timelogger.beans;

import timelogger.exceptions.NotTheSameMonthException;
import timelogger.exceptions.NotNewDateException;
import timelogger.exceptions.NotNewMonthException;
import timelogger.exceptions.WeekendNotEnabledException;
import org.junit.Test;
import static org.junit.Assert.*;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.FutureWorkException;
import timelogger.exceptions.InvalidTaskIdException;
import timelogger.exceptions.NoTaskIdException;
import timelogger.exceptions.NotExpectedTimeOrderException;
import timelogger.exceptions.NotMultipleQuarterHourException;
import timelogger.exceptions.NotSeparatedTaskTimesException;

public class TimeLoggerTest {

    private TimeLogger getTimeLogger() {
        return new TimeLogger();
    }

    private Task getTask() throws InvalidTaskIdException, NoTaskIdException, NotMultipleQuarterHourException, EmptyTimeFieldException, NotExpectedTimeOrderException {
        return new Task("4654", "", 7, 30, 10, 30);
    }

    @Test
    public void testAddMonthNormal() throws FutureWorkException, NotSeparatedTaskTimesException, NotMultipleQuarterHourException, NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NotNewDateException, NotTheSameMonthException, WeekendNotEnabledException, NotNewMonthException {
        WorkDay workDay = new WorkDay(2016, 4, 14);
        WorkMonth workMonth = new WorkMonth(2016, 4);
        Task task = getTask();
        workDay.addTask(task);
        workMonth.addWorkDay(workDay);
        TimeLogger timeLogger = getTimeLogger();
        timeLogger.addMonth(workMonth);
        assertEquals(task.getMinPerTask(), timeLogger.getMonths().get(0).getSumPerMonth());
    }

    @Test(expected = NotNewMonthException.class)
    public void testAddMonthNotNewMonth() throws NotNewMonthException {
        TimeLogger timeLogger = getTimeLogger();
        WorkMonth workMonth1 = new WorkMonth(2016, 4);
        WorkMonth workMonth2 = new WorkMonth(2016, 4);
        timeLogger.addMonth(workMonth1);
        timeLogger.addMonth(workMonth2);
    }

}
