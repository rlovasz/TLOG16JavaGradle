package timelogger.beans;

import timelogger.exceptions.NotTheSameMonthException;
import timelogger.exceptions.NotNewDateException;
import timelogger.exceptions.NotNewMonthException;
import timelogger.exceptions.NoMonthsException;
import timelogger.exceptions.WeekendNotEnabledException;
import org.junit.Test;
import static org.junit.Assert.*;

public class TimeLoggerTest {

    
    private TimeLogger getTimeLogger()
    {
    return new TimeLogger();
    }
    
    private Task getTask()
    {
    Task task = new Task("4654", "", 7, 30, 10, 30);
    return task;
    }
    
    @Test
    public void testAddMonthNormal() {
        WorkDay workDay = new WorkDay(2016, 4, 14);
        WorkMonth workMonth = new WorkMonth(2016, 4);
        workDay.addTask(getTask());
        workMonth.addWorkDay(workDay);
        TimeLogger timeLogger = getTimeLogger();
        timeLogger.addMonth(workMonth);
        assertEquals(getTask().getMinPerTask(),timeLogger.getMonths().get(0).getSumPerMonth());
    }

    @Test(expected = NotNewMonthException.class)
    public void testAddMonthNotNewMonth(){
        TimeLogger timeLogger = getTimeLogger();
        WorkMonth workMonth1 = new WorkMonth(2016, 4);
        WorkMonth workMonth2 = new WorkMonth(2016, 4);
        timeLogger.addMonth(workMonth1);
        timeLogger.addMonth(workMonth2);
    }

    @Test
    public void testIsNewMonthTrue() {
        TimeLogger timeLogger = getTimeLogger();
        WorkMonth workMonth1 = new WorkMonth(2016, 4);
        WorkMonth workMonth2 = new WorkMonth(2016, 9);
        boolean expResult = true;
        timeLogger.addMonth(workMonth1);
        boolean result = timeLogger.isNewMonth(workMonth2);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsNewMonthFalse() {
        TimeLogger timeLogger = getTimeLogger();
        WorkMonth workMonth1 = new WorkMonth(2016, 4);
        WorkMonth workMonth2 = new WorkMonth(2016, 4);
        boolean expResult = false;
        timeLogger.addMonth(workMonth1);
        boolean result = timeLogger.isNewMonth(workMonth2);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetFirstMonthOfTimeLoggerNormal() {
        TimeLogger timeLogger = getTimeLogger();
        WorkMonth workMonth1 = new WorkMonth(2016, 4);
        WorkMonth workMonth2 = new WorkMonth(2016, 8);
        WorkMonth workMonth3 = new WorkMonth(2016, 9);
        timeLogger.addMonth(workMonth1);
        timeLogger.addMonth(workMonth2);
        timeLogger.addMonth(workMonth3);
        assertEquals(workMonth1, timeLogger.getFirstMonthOfTimeLogger());
    }

    @Test(expected = NoMonthsException.class)
    public void testGetFirstMonthOfTimeLoggerNoMonths() {
        TimeLogger timeLogger = getTimeLogger();
        timeLogger.getFirstMonthOfTimeLogger();

    }

}
