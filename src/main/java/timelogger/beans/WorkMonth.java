package timelogger.beans;

import java.time.DayOfWeek;
import timelogger.exceptions.NotNewDateException;
import timelogger.exceptions.NotTheSameMonthException;
import timelogger.exceptions.WeekendNotEnabledException;
import java.time.YearMonth;
import java.util.*;
import lombok.Getter;
import timelogger.exceptions.EmptyTimeFieldException;

/**
 * With the instantiation of this class we can create work months. We can ask
 for the days in this date and we can change it. We can ask for the sum of
 the working minutes in this work date, and the extra minutes.
 *
 * @author precognox
 */
public class WorkMonth implements Comparable<WorkMonth>{

    private static final List<DayOfWeek> WEEKDAYS = Arrays.asList(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
    );
    @Getter
    private final List<WorkDay> days;
    @Getter
    private final YearMonth date;
    private long sumPerMonth = 0;
    private long requiredMinPerMonth = 0;

    /**
     * 
     * @param year This is the year of this date in YYYY format
     * @param month This is the date's value with a simple integer
     */
    public WorkMonth(int year, int month) {
        this.days = new ArrayList<>();
        this.date = YearMonth.of(year, month);
    }
    
    /**
     * This method calculates all the minutes in this date while the employee worked
     *
     * @return with a positive value of worked minutes
     * @throws EmptyTimeFieldException
     */
    public long getSumPerMonth() throws EmptyTimeFieldException { 
        if(sumPerMonth == 0) {
            for(WorkDay workDay: days) {
                sumPerMonth += workDay.getSumPerDay();
            }
        }
        return sumPerMonth;
    }

    /**
     * This method calculates all the extra worked minutes in this date
     *
     * @return with the signed value of extra minutes. If it is positive the
     * employee worked more, if it is negative the employee worked less, then
     * the required.
     * @throws EmptyTimeFieldException
     */
    public long getExtraMinPerMonth() throws EmptyTimeFieldException {
        if (requiredMinPerMonth == 0) {
            requiredMinPerMonth = getRequiredMinPerMonth();
        }
        return getSumPerMonth() - requiredMinPerMonth;
    }

    /**
     * This method calculates how many minutes should the employee work this date.
     *
     * @return with the integer value of minutes.
     */
    public long getRequiredMinPerMonth() {
        days.stream().forEach((wd) -> {
            requiredMinPerMonth += wd.getRequiredMinPerDay();
        });
        return requiredMinPerMonth;
    }

    /**
     * This method is an overloaded method of addWorkDay(WorkDay,boolean) with
     * the default false value: addWorkDay(WorkDay,false)
     *
     * @param workDay This is a WorkDay parameter, which will be added.
     * @throws timelogger.exceptions.NotNewDateException
     * @throws timelogger.exceptions.NotTheSameMonthException
     * @throws timelogger.exceptions.WeekendNotEnabledException
     */
    public void addWorkDay(WorkDay workDay) throws NotNewDateException, NotTheSameMonthException, WeekendNotEnabledException {
        addWorkDay(workDay, false);
    }

    /**
     * This method adds a work day to this date, if the work day is a weekday.
     * But if it is on weekend we have to enable to work on weekend.
     *
     * @param workDay This is a WorkDay parameter, which will be added.
     * @param isWeekendEnabled This is a boolean parameter, if it is false, we
     * cannot work on weekend, but if it is true, we can add a day of weekend to
     * this date.
     * @throws timelogger.exceptions.NotNewDateException
     * @throws timelogger.exceptions.NotTheSameMonthException
     * @throws timelogger.exceptions.WeekendNotEnabledException
     */
    public void addWorkDay(WorkDay workDay, boolean isWeekendEnabled) throws NotNewDateException, NotTheSameMonthException, WeekendNotEnabledException {
        if (isNewDate(workDay) && (isWeekendEnabled || isWeekday(workDay)) && isSameMonth(workDay)) {
            days.add(workDay);
            sumPerMonth = 0;
            requiredMinPerMonth = 0;
        } else if (!isNewDate(workDay)) {
            throw new NotNewDateException("You have already added this day. You should choose an other day!");
        } else if (!isSameMonth(workDay)) {
            throw new NotTheSameMonthException("You have changed the month, so you should add this to an other month!");
        } else {
            throw new WeekendNotEnabledException("You cannot add this day, because it is on weekend and it is not enabled.");
        }
    }

    /**
     * This method decides if the date of workDay already exist in the list of
     * days
     *
     * @param workDay the day we check
     * @return true, if it is a new date, false if it isn't new.
     */
    private boolean isNewDate(WorkDay workDay) {
        return days.stream().noneMatch((wd) -> (!days.isEmpty() && wd.getActualDay().equals(workDay.getActualDay())));
        
    }

    /**
     * This method decides, if the parameter has the same date value, like the days
     *
     * @param workDay parameter about to decide
     * @return true if it is the same date, false, if it is not
     */
    private boolean isSameMonth(WorkDay workDay) {
        return !(hasDifferentMonthValue(workDay) || hasDifferentYearValue(workDay));
    }

    /**
     * Decides if the WorkDay parameter is in a different year as this month
     * @param workDay
     * @return true, if they are in the same year, false, if they are not
     */
    private boolean hasDifferentYearValue(WorkDay workDay) {
        return workDay.getActualDay().getYear() != date.getYear();
    }

    /**
     * Decides if the WorkDay parameter is in a different month
     * @param workDay
     * @return true, if it is in this month, false, if it is not
     */
    private boolean hasDifferentMonthValue(WorkDay workDay) {
        return workDay.getActualDay().getMonthValue() != date.getMonthValue();
    }
    
       /**
     * This method decides if this work day is a weekday or notexpResult
     *
     * @return true if it is a weekday, false if it is on weekend
     */
    private boolean isWeekday(WorkDay workDay) {
        return WEEKDAYS.contains(DayOfWeek.from(workDay.getActualDay()));
    }

    @Override
    public int compareTo(WorkMonth otherMonth) {
        return this.date.compareTo(otherMonth.date);
    }

}
