package timelogger;

import timelogger.beans.WorkDay;
import timelogger.beans.Task;
import timelogger.beans.TimeLogger;
import timelogger.beans.WorkMonth;
import timelogger.exceptions.NegativeMinutesOfWorkException;
import timelogger.exceptions.NotExpectedTimeOrderException;
import timelogger.exceptions.NotNewDateException;
import timelogger.exceptions.NoTaskIdException;
import timelogger.exceptions.FutureWorkException;
import timelogger.exceptions.NotNewMonthException;
import timelogger.exceptions.NotTheSameMonthException;
import timelogger.exceptions.NotMultipleQuarterHourException;
import timelogger.exceptions.WeekendNotEnabledException;
import timelogger.exceptions.InvalidTaskIdException;
import timelogger.exceptions.NotSeparatedTaskTimesException;
import timelogger.exceptions.EmptyTimeFieldException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeLoggerUI {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int input, year, month, day, taskNumber;
        String answer;
        TimeLogger timeLogger = new TimeLogger();
        WorkMonth actualMonth;
        WorkDay actualDay;
        do {
            
            printTheMainMenu();
            input = scanner.nextInt();
            
            switch (input) {
                case 1:
                    printListOfMonths(timeLogger);
                    break;
                case 2:
                    month = chooseAMonth(timeLogger, scanner);
                    printListOfDays(month, timeLogger);
                    break;
                case 3:
                    month = chooseAMonth(timeLogger, scanner);
                    day = chooseADay(month, timeLogger, scanner);
                    try {
                        printListOfTasks(month, day, timeLogger);
                    } catch (NoTaskIdException ex) {
                        System.err.println(ex.getMessage());
                        Logger.getLogger(TimeLogger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    break;
                case 4:
                    System.out.println("year:");
                    year = scanner.nextInt();
                    System.out.println("month:");
                    month = scanner.nextInt();
                    try {
                        timeLogger.addMonth(new WorkMonth(year, month));
                    } catch (NotNewMonthException ex) {
                        System.err.println(ex.getMessage());
                        Logger.getLogger(TimeLoggerUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 5:
                    month = chooseAMonth(timeLogger, scanner);
                    System.out.println("day:");
                    day = scanner.nextInt();
                    System.out.println("How many required working hour do you have this day? (default = 7.5)");
                    scanner.nextLine();
                    answer = scanner.nextLine();
                    Double requiredHour;
                    if (answer.length() == 0) {
                        requiredHour = 7.5;
                    } else {
                        requiredHour = Double.parseDouble(answer);
                    }
                    actualMonth = timeLogger.getMonths().get(month - 1);
                    try {
                        actualMonth.addWorkDay(new WorkDay((long) (requiredHour * 60), actualMonth.getDate().getYear(), actualMonth.getDate().getMonthValue(), day));
                    } catch (NegativeMinutesOfWorkException | FutureWorkException | WeekendNotEnabledException | NotNewDateException | NotTheSameMonthException ex) {
                        System.err.println(ex.getMessage());
                        Logger.getLogger(TimeLogger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 6:
                    month = chooseAMonth(timeLogger, scanner);
                    day = chooseADay(month, timeLogger, scanner);
                    actualDay = timeLogger.getMonths().get(month - 1).getDays().get(day - 1);
                    Task task = startANewTask(scanner, actualDay);
                    try {
                        actualDay.addTask(task);
                    } catch (NotMultipleQuarterHourException | NotExpectedTimeOrderException | EmptyTimeFieldException | NotSeparatedTaskTimesException ex) {
                        System.err.println(ex.getMessage());
                        Logger.getLogger(TimeLogger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 7:
                    month = chooseAMonth(timeLogger, scanner);
                    day = chooseADay(month, timeLogger, scanner);
                    actualDay = timeLogger.getMonths().get(month-1).getDays().get(day-1);
                    taskNumber = chooseAnUnfinishedTask(month, day, timeLogger, scanner);
                    task = timeLogger.getMonths().get(month - 1).getDays().get(day - 1).getTasks().get(taskNumber - 1);
                    System.out.println("Please type in the finishing time of the task HH:MM");
                    scanner.nextLine();
                    answer = scanner.nextLine();
                    task.setEndTime(answer);
                    break;
                case 8:
                    month = chooseAMonth(timeLogger, scanner);
                    day = chooseADay(month, timeLogger, scanner);
                    actualDay = timeLogger.getMonths().get(month-1).getDays().get(day-1);
                    taskNumber = chooseATask(month, day, timeLogger, scanner);
                    System.out.println("Are you sure, you want to delete the choosen task? (y/n)");
                    answer = scanner.next();
                    if (answer.equals("y")) {
                        timeLogger.getMonths().get(month - 1).getDays().get(day - 1).getTasks().remove(taskNumber - 1);
                    }
                    break;
                case 9:
                    month = chooseAMonth(timeLogger, scanner);
                    day = chooseADay(month, timeLogger, scanner);
                    actualDay = timeLogger.getMonths().get(month-1).getDays().get(day-1);
                    taskNumber = chooseATask(month, day, timeLogger, scanner);
                    task = timeLogger.getMonths().get(month - 1).getDays().get(day - 1).getTasks().get(taskNumber - 1);
                    modifyAnExistingTask(task, scanner);
                    break;
                case 10:
                    month = chooseAMonth(timeLogger, scanner);
                    actualMonth = timeLogger.getMonths().get(month-1);
                    printTheMonthStatistics(actualMonth);
                    printDaysStatisticsOfMonth(actualMonth);
                    break;
                default:
                    break;
                
            }
        } while (input != 0);
    }

    private static void printTheMainMenu() {
        System.out.println("0. Exit");
        System.out.println("1. List months");
        System.out.println("2. List days of a specific month");
        System.out.println("3. List tasks of a specific day");
        System.out.println("4. Add new month");
        System.out.println("5. Add day to a specific month");
        System.out.println("6. Start a task for a specific day");
        System.out.println("7. Finish a specific task");
        System.out.println("8. Delete a task");
        System.out.println("9. Modify a task");
        System.out.println("10. Ask for statistics");
    }

    private static void printDaysStatisticsOfMonth(WorkMonth actualMonth) {
        for(WorkDay workDay : actualMonth.getDays())
        {
            System.out.println(workDay.getActualDay().toString() +
                    ": Sum of the worked minutes this day: " +
                    workDay.getSumPerDay() +
                    " Required working minutes this day: " +
                    workDay.getRequiredMinPerDay() +
                    " Extra worked minutes this day: " +
                    workDay.getExtraMinPerDay());
        }
    }

    private static void printTheMonthStatistics(WorkMonth actualMonth) {
        System.out.println(actualMonth.getDate().toString() +" Sum of the worked minutes this month: " +
                actualMonth.getSumPerMonth() +
                " Required working minutes this month: "
                + actualMonth.getRequiredMinPerMonth() +
                " Extra worked minutes this month: " +
                actualMonth.getExtraMinPerMonth());
    }

    private static void modifyAnExistingTask(Task task, Scanner scanner) {
        String answer;
        System.out.println("Please type in a vaild task Id! (default: " + task.getTaskId() + ")");
        scanner.nextLine();
        answer = scanner.nextLine();
        if (answer.length() != 0) {
            task.setTaskId(answer);
        }
        System.out.println("Please type in a detailed description about this task! (default: " + task.getComment() + ")");
        answer = scanner.nextLine();
        if (answer.length() != 0) {
            task.setComment(answer);
        }
        System.out.println("Please type in the beginning time of this task HH:MM! (default: " + task.getStartTime().toString() + ")");
        answer = scanner.nextLine();
        if (answer.length() != 0) {
            task.setStartTime(answer);
        }
        System.out.println("Please type in the finishing time of this task HH:MM! (default: " + task.getEndTime().toString() + ")");
        answer = scanner.nextLine();
        if (answer.length() != 0) {
            task.setEndTime(answer);
        }
    }
    
    private static int chooseATask(int month, int day, TimeLogger timeLogger, Scanner scanner) throws NoTaskIdException {
        printListOfTasks(month, day, timeLogger);
        System.out.println("Choose a task!");
        return scanner.nextInt();
    }
    
    private static int chooseAnUnfinishedTask(int month, int day, TimeLogger timeLogger, Scanner scanner) throws NoTaskIdException {
        printListOfUnfinishedTasks(month, day, timeLogger);
        System.out.println("Choose a task!");
        return scanner.nextInt();
    }
    
    private static Task startANewTask(Scanner scanner, WorkDay workDay) {
        String answer;
        System.out.println("Please type in a valid task id!");
        scanner.nextLine();
        answer = scanner.nextLine();
        Task task = new Task(answer);
        try {
            task.setTaskId(answer);
        } catch (InvalidTaskIdException | NoTaskIdException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(TimeLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Please type in what did you exactly do!");
        answer = scanner.nextLine();
        task.setComment(answer);
        if(workDay.endTimeOfTheLastTask() !=null )
        {
        System.out.println("Please type in the beginning time of the task! HH:MM (default: " + workDay.endTimeOfTheLastTask().toString() + ")");
        }
        else{
            System.out.println("Please type in the beginning time of the task! HH:MM ");}
        answer = scanner.nextLine();
        if (answer.length() == 0) {
            answer = workDay.endTimeOfTheLastTask().toString();
        }        
        task.setStartTime(answer);
        task.setEndTime(answer);
        return task;
    }
    
    private static int chooseADay(int month, TimeLogger timeLogger, Scanner scanner) {
        printListOfDays(month, timeLogger);
        System.out.println("Choose a day!");
        return scanner.nextInt();
    }
    
    private static int chooseAMonth(TimeLogger timeLogger, Scanner scanner) {
        printListOfMonths(timeLogger);
        System.out.println("Choose a month!");
        return scanner.nextInt();
    }
    
    private static void printListOfTasks(int month, int day, TimeLogger months) {
        for (int i = 0; i < months.getMonths().get(month - 1).getDays().get(day - 1).getTasks().size(); i++) {
            System.out.println((i + 1) + ". " + months.getMonths().get(month - 1).getDays().get(day - 1).getTasks().get(i).toString());
        }
    }
    
    private static void printListOfUnfinishedTasks(int month, int day, TimeLogger months) {
        for (int i = 0; i < months.getMonths().get(month - 1).getDays().get(day - 1).getTasks().size(); i++) {
            if (months.getMonths().get(month - 1).getDays().get(day - 1).getTasks().get(i).getMinPerTask() == 0) {
                System.out.println((i + 1) + ". " + months.getMonths().get(month - 1).getDays().get(day - 1).getTasks().get(i).toString());
            }
        }
    }
    
    private static void printListOfDays(int month, TimeLogger months) {
        for (int i = 0; i < months.getMonths().get(month - 1).getDays().size(); i++) {
            System.out.println((i + 1) + ". " + months.getMonths().get(month - 1).getDays().get(i).getActualDay().toString());
        }
    }
    
    private static void printListOfMonths(TimeLogger months) {
        for (int i = 0; i < months.getMonths().size(); i++) {
            System.out.println((i + 1) + ". " + months.getMonths().get(i).getDate().toString());
        }
    }
}
