package timelogger.beans;

import timelogger.exceptions.NotNewMonthException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class TimeLogger {

    @Getter
    private final List<WorkMonth> months = new ArrayList();

    /**
     * This method adds a new month to the TimeLogger, if it is in the same
     * year, as the earlier ones
     *
     * @param workMonth the month to add
     * @throws timelogger.exceptions.NotNewMonthException
     */
    public void addMonth(WorkMonth workMonth) throws NotNewMonthException {
        if (isNewMonth(workMonth)) {
            months.add(workMonth);
        } else {
            throw new NotNewMonthException("This month is already exists.");
        }

    }

    /**
     * This method decides if the work month is in the list of the months
     * already
     *
     * @param workMonth, the parameter about to decide
     * @return true, if it is new, false, if it is already exists
     */
    private boolean isNewMonth(WorkMonth workMonth) {
        boolean isNewMonth = true;
        for (WorkMonth wm : months) {
            if (wm.getDate().equals(workMonth.getDate())) {
                isNewMonth = false;
                break;
            }
        }
        return isNewMonth;
    }

}
