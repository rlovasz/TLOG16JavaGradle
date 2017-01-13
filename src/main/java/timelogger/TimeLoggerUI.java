package timelogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import lombok.extern.java.Log;

@Log
public class TimeLoggerUI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int input, month, day;
        String answer;
        TimeLogger timeLogger = new TimeLogger();
        do {
            input = -1;
            printTheMainMenu();
            answer = scanner.nextLine();
            if (answer.equals("")) {
                answer = scanner.nextLine();
            }
            if (isValidInput(answer, 0, 10)) {
                input = Integer.parseInt(answer);
                switch (input) {
                    case 0:
                        break;
                    case 1:
                        printListOfMonths(timeLogger);
                        break;
                    case 2:
                        month = chooseAMonth(timeLogger, scanner);
                        if (month != -1) {
                            printListOfDays(month, timeLogger);
                        }
                        break;
                    case 3:
                        month = chooseAMonth(timeLogger, scanner);
                        if (month != -1) {
                            day = chooseADay(month, timeLogger, scanner);
                            if (day != -1) {
                                printListOfTasks(month, day, timeLogger);
                            }
                        }
                        break;
                    case 4:
                        addNewMonth(scanner, timeLogger);
                        break;
                    case 5:
                        addNewWorkDay(timeLogger, scanner);
                        break;
                    case 6:
                        startNewTask(timeLogger, scanner);
                        break;
                    case 7:
                        finishTask(timeLogger, scanner);
                        break;
                    case 8:
                        deleteTask(timeLogger, scanner);
                        break;
                    case 9:
                        modifyExistingTask(timeLogger, scanner);
                        break;
                    case 10:
                        askForStatistics(timeLogger, scanner);
                        break;
                    default:
                        System.out.println("Not valid input, please type in a number between 0-10!");
                        break;
                }
            }
        } while (input != 0);
    }

    private static boolean isValidInput(String answer, int lowLimit, int highLimit) {
        boolean valid = false;
        try {
            int input = Integer.parseInt(answer);
            if (input <= highLimit && input >= lowLimit) {
                valid = true;
            } else {
                System.out.format("Not valid input, please type in a number between %d-%d!\n", lowLimit, highLimit);
            }
        } catch (NumberFormatException e) {
            System.out.format("Not valid input, please type in a number between %d-%d!\n", lowLimit, highLimit);
        }
        return valid;
    }

    private static boolean isValidTimeFormat(String answer) {
        boolean valid = false;
        if (answer.matches("\\d{2}:\\d{2}")) {
            int hour = Integer.parseInt(answer.split(":")[0]);
            int minutes = Integer.parseInt(answer.split(":")[1]);
            if (0 <= hour && hour < 24 && 0 <= minutes && minutes < 60) {
                valid = true;
            } else {
                System.out.format("Not valid input, please type in a time in the following format: HH:MM!\n");
            }
        } else {
            System.out.format("Not valid input, please type in a time in the following format: HH:MM!\n");
        }
        return valid;
    }

    private static void askForStatistics(TimeLogger timeLogger, Scanner scanner) {
        int month;
        WorkMonth actualMonth;
        month = chooseAMonth(timeLogger, scanner);
        if (month != -1) {
            actualMonth = timeLogger.getMonths().get(month - 1);
            try {
                printTheMonthStatistics(actualMonth);
                printDaysStatisticsOfMonth(actualMonth);
            } catch (EmptyTimeFieldException | NotExpectedTimeOrderException ex) {
                Logger.getLogger(TimeLoggerUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void modifyExistingTask(TimeLogger timeLogger, Scanner scanner) {
        int taskNumber;
        int month = chooseAMonth(timeLogger, scanner);
        if (month != -1) {
            int day = chooseADay(month, timeLogger, scanner);
            if (day != -1) {
                WorkMonth chosenMonth = timeLogger.getMonths().get(month - 1);
                WorkDay chosenDay = chosenMonth.getDays().get(day - 1);
                List<Task> tasks = chosenDay.getTasks();
                Task chosenTask;
                {
                    try {
                        taskNumber = chooseATask(month, day, timeLogger, scanner);
                        if (taskNumber != -1) {
                            chosenTask = tasks.get(taskNumber - 1);
                            List<Task> testTasks = new ArrayList<>();
                            Task testTask = new Task(chosenTask.getTaskId(), chosenTask.getComment(), chosenTask.getStartTime().toString(), chosenTask.getEndTime().toString());
                            int matchIndex = tasks.indexOf(chosenTask);
                            for (int i = 0; i < tasks.size(); i++) {
                                if (i != matchIndex) {
                                    testTasks.add(tasks.get(i));
                                }
                            }
                            modifyAnExistingTask(testTask, scanner);
                            chosenTask.setTaskId(testTask.getTaskId());
                            chosenTask.setComment(testTask.getComment());
                            if (!Util.isSeparatedTime(testTasks, testTask)) {
                                throw new NotSeparatedTaskTimesException("");
                            } else {
                                chosenTask.setStartTime(testTask.getStartTime());
                                chosenTask.setEndTime(testTask.getEndTime());
                            }
                        }
                    } catch (NoTaskIdException | InvalidTaskIdException | EmptyTimeFieldException | NotExpectedTimeOrderException | NotSeparatedTaskTimesException | NotMultipleQuarterHourException ex) {
                        Logger.getLogger(TimeLoggerUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private static void deleteTask(TimeLogger timeLogger, Scanner scanner) {
        int taskNumber;
        String answer;
        int month = chooseAMonth(timeLogger, scanner);
        if (month != -1) {
            int day = chooseADay(month, timeLogger, scanner);
            if (day != -1) {
                WorkMonth chosenMonth = timeLogger.getMonths().get(month - 1);
                WorkDay chosenDay = chosenMonth.getDays().get(day - 1);
                {
                    try {
                        taskNumber = chooseATask(month, day, timeLogger, scanner);
                        if (taskNumber != -1) {
                            System.out.println("Are you sure, you want to delete the choosen task? (y/n)");
                            answer = scanner.next();
                            if (answer.equals("y")) {
                                chosenDay.getTasks().remove(taskNumber - 1);
                            }
                        }
                    } catch (NoTaskIdException ex) {
                        Logger.getLogger(TimeLoggerUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private static void finishTask(TimeLogger timeLogger, Scanner scanner) {
        int taskNumber;
        String answer;
        int month = chooseAMonth(timeLogger, scanner);
        if (month != -1) {
            int day = chooseADay(month, timeLogger, scanner);
            if (day != -1) {
                WorkMonth chosenMonth = timeLogger.getMonths().get(month - 1);
                WorkDay chosenDay = chosenMonth.getDays().get(day - 1);
                List<Task> tasks = chosenDay.getTasks();
                {
                    try {
                        List<Task> unfinishedTasks = getUnfinishedTasks(tasks);
                        taskNumber = chooseAnUnfinishedTask(month, day, timeLogger, scanner);
                        if (taskNumber != -1) {
                            Task chosenTask = unfinishedTasks.get(taskNumber - 1);
                            do {
                                System.out.println("Please type in the finishing time of the task HH:MM");
                                answer = scanner.nextLine();
                            } while (!isValidTimeFormat(answer));
                            List< Task> testTasks = new ArrayList<>();
                            Task testTask = new Task(chosenTask.getTaskId(), chosenTask.getComment(), chosenTask.getStartTime().toString(), chosenTask.getEndTime().toString());
                            int matchIndex = tasks.indexOf(chosenTask);
                            for (int i = 0; i < tasks.size(); i++) {
                                if (i != matchIndex) {
                                    testTasks.add(tasks.get(i));
                                }
                            }
                            testTask.setEndTime(answer);
                            if (!Util.isSeparatedTime(testTasks, testTask)) {
                                throw new NotSeparatedTaskTimesException("");
                            } else {
                                chosenTask.setEndTime(testTask.getEndTime());
                            }
                        }
                    } catch (NoTaskIdException | EmptyTimeFieldException | NotExpectedTimeOrderException | NotSeparatedTaskTimesException ex) {
                        Logger.getLogger(TimeLoggerUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private static void startNewTask(TimeLogger timeLogger, Scanner scanner) {
        int month = chooseAMonth(timeLogger, scanner);
        if (month != -1) {
            int day = chooseADay(month, timeLogger, scanner);
            if (day != -1) {
                WorkMonth chosenMonth = timeLogger.getMonths().get(month - 1);
                WorkDay chosenDay = chosenMonth.getDays().get(day - 1);
                Task task;
                try {
                    task = startANewTask(scanner, chosenDay);
                    if (task != null) {
                        chosenDay.addTask(task);
                    }
                } catch (EmptyTimeFieldException | NotSeparatedTaskTimesException | InvalidTaskIdException | NoTaskIdException | NotExpectedTimeOrderException | NotMultipleQuarterHourException ex) {
                    Logger.getLogger(TimeLogger.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static void addNewWorkDay(TimeLogger timeLogger, Scanner scanner) throws NumberFormatException {
        String answer;
        int month = chooseAMonth(timeLogger, scanner);
        WorkMonth workMonth = timeLogger.getMonths().get(month-1);
        int day;
        if (month != -1) {
            do {
            System.out.println("day:");
            answer = scanner.nextLine();
            if (answer.equals("")) {
                answer = scanner.nextLine();
            }
        } while (!isValidInput(answer, 1, workMonth.getDate().lengthOfMonth()));
        day = Integer.parseInt(answer);
            System.out.println("How many required working hour do you have this day? (default = 7.5)");
            System.out.println("");
            answer = scanner.nextLine();
            Double requiredHour;
            if (answer.length() == 0) {
                requiredHour = 7.5;
            } else {
                requiredHour = Double.parseDouble(answer);
            }
            WorkMonth chosenMonth = timeLogger.getMonths().get(month - 1);
            try {
                chosenMonth.addWorkDay(new WorkDay((long) (requiredHour * 60), chosenMonth.getDate().getYear(), chosenMonth.getDate().getMonthValue(), day));
            } catch (NegativeMinutesOfWorkException | FutureWorkException | WeekendNotEnabledException | NotNewDateException | NotTheSameMonthException ex) {
                System.err.println(ex.getMessage());
                Logger.getLogger(TimeLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void addNewMonth(Scanner scanner, TimeLogger timeLogger) {
        int year;
        int month;
        String answer;
         do {
            System.out.println("year:");
            answer = scanner.nextLine();
            if (answer.equals("")) {
                answer = scanner.nextLine();
            }
        } while (!isValidInput(answer, 2000, LocalDate.now().plusYears(15).getYear()));
        year = Integer.parseInt(answer);
        do {
            System.out.println("month:");
            answer = scanner.nextLine();
            if (answer.equals("")) {
                answer = scanner.nextLine();
            }
        } while (!isValidInput(answer, 1, 12));
        month = Integer.parseInt(answer);
        System.out.println("");
        try {
            timeLogger.addMonth(new WorkMonth(year, month));
        } catch (NotNewMonthException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(TimeLoggerUI.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private static void printDaysStatisticsOfMonth(WorkMonth actualMonth) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        for (WorkDay workDay : actualMonth.getDays()) {
            System.out.println(workDay.getActualDay().toString()
                    + ": Sum of the worked minutes this day: "
                    + workDay.getSumPerDay()
                    + " Required working minutes this day: "
                    + workDay.getRequiredMinPerDay()
                    + " Extra worked minutes this day: "
                    + workDay.getExtraMinPerDay());
        }
    }

    private static void printTheMonthStatistics(WorkMonth actualMonth) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        System.out.println(actualMonth.getDate().toString() + " Sum of the worked minutes this month: "
                + actualMonth.getSumPerMonth()
                + " Required working minutes this month: "
                + actualMonth.getRequiredMinPerMonth()
                + " Extra worked minutes this month: "
                + actualMonth.getExtraMinPerMonth());
    }

    private static void modifyAnExistingTask(Task task, Scanner scanner) throws InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException, NotExpectedTimeOrderException, NotMultipleQuarterHourException {
        String answer;
        System.out.println("Please type in a vaild task Id! (default: " + task.getTaskId() + ")");
        answer = scanner.nextLine();
        if (answer.length() != 0) {
            task.setTaskId(answer);
        }
        System.out.println("Please type in a detailed description about this task! (default: " + task.getComment() + ")");
        answer = scanner.nextLine();
        if (answer.length() != 0) {
            task.setComment(answer);
        }
        do {
            System.out.println("Please type in the beginning time of this task HH:MM! (default: " + task.getStartTime().toString() + ")");
            answer = scanner.nextLine();
            if (answer.length() == 0) {
                answer = task.getStartTime().toString();
            }
        } while (!isValidTimeFormat(answer));
        task.setStartTime(answer);
        do {
            System.out.println("Please type in the finishing time of this task HH:MM! (default: " + task.getEndTime().toString() + ")");
            answer = scanner.nextLine();
            if (answer.length() == 0) {
                answer = task.getStartTime().toString();
            }
        } while (!isValidTimeFormat(answer));
        task.setEndTime(answer);
    }

    private static int chooseATask(int month, int day, TimeLogger timeLogger, Scanner scanner) throws NoTaskIdException {
        if (printListOfTasks(month, day, timeLogger)) {
            String answer;
            do {
                System.out.println("Choose a task!");
                answer = scanner.nextLine();
            } while (!isValidInput(answer, 1, timeLogger.getMonths().get(month - 1).getDays().get(day - 1).getTasks().size()));
            return Integer.parseInt(answer);
        } else {
            return -1;
        }
    }

    private static int chooseAnUnfinishedTask(int month, int day, TimeLogger timeLogger, Scanner scanner) throws NoTaskIdException, EmptyTimeFieldException, NotExpectedTimeOrderException {
        if (printListOfUnfinishedTasks(month, day, timeLogger)) {
            String answer;
            do {
                System.out.println("Choose a task!");
                answer = scanner.nextLine();
            } while (!isValidInput(answer, 1, getUnfinishedTasks(timeLogger.getMonths().get(month - 1).getDays().get(day - 1).getTasks()).size()));
            return Integer.parseInt(answer);
        } else {
            return -1;
        }
    }

    private static Task startANewTask(Scanner scanner, WorkDay workDay) throws InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException, NotExpectedTimeOrderException, NotMultipleQuarterHourException {
        String answer;
        Task task;
        System.out.println("Please type in a valid task id!");
        answer = scanner.nextLine();
        try {
            task = new Task(answer);
            System.out.println("Please type in what did you exactly do!");
            answer = scanner.nextLine();
            task.setComment(answer);
            do {
                if (workDay.endTimeOfTheLastTask() != null) {
                    System.out.println("Please type in the beginning time of the task! HH:MM (default: " + workDay.endTimeOfTheLastTask().toString() + ")");
                } else {
                    System.out.println("Please type in the beginning time of the task! HH:MM ");
                }
                answer = scanner.nextLine();
                if (answer.length() == 0) {
                    answer = workDay.endTimeOfTheLastTask().toString();
                }
            } while (!isValidTimeFormat(answer));
            task.setStartTime(answer);
            task.setEndTime(answer);
            return task;
        } catch (InvalidTaskIdException | NoTaskIdException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(TimeLogger.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static int chooseADay(int month, TimeLogger timeLogger, Scanner scanner) {
        if (printListOfDays(month, timeLogger)) {
            String answer;
            do {
                System.out.println("Choose a day!");
                answer = scanner.nextLine();
            } while (!isValidInput(answer, 1, timeLogger.getMonths().get(month - 1).getDays().size()));
            return Integer.parseInt(answer);
        } else {
            return -1;
        }
    }

    private static int chooseAMonth(TimeLogger timeLogger, Scanner scanner) {
        if (printListOfMonths(timeLogger)) {
            String answer;
            do {
                System.out.println("Choose a month!");
                answer = scanner.nextLine();
            } while (!isValidInput(answer, 1, timeLogger.getMonths().size()));
            return Integer.parseInt(answer);
        } else {
            return -1;
        }
    }

    private static boolean printListOfTasks(int month, int day, TimeLogger months) {
        boolean notEmpty = true;
        List<Task> tasks = months.getMonths().get(month - 1).getDays().get(day - 1).getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).toString());
        }
        if (tasks.isEmpty()) {
            System.err.println("No existing tasks yet");
            notEmpty = false;
        }
        return notEmpty;
    }

    private static boolean printListOfUnfinishedTasks(int month, int day, TimeLogger months) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        boolean notEmpty = true;
        List<Task> tasks = months.getMonths().get(month - 1).getDays().get(day - 1).getTasks();
        List<Task> unfinishedTasks = getUnfinishedTasks(tasks);
        if (tasks.isEmpty()) {
            System.err.println("No existing tasks yet");
        }
        for (int i = 0; i < unfinishedTasks.size(); i++) {
            System.out.println((i + 1) + ". " + unfinishedTasks.get(i).toString());
        }
        if (unfinishedTasks.isEmpty()) {
            System.err.println("No unfinished tasks at this moment");
            notEmpty = false;
        }
        return notEmpty;
    }

    private static boolean printListOfDays(int month, TimeLogger months) {
        boolean notEmpty = true;
        List<WorkDay> days = months.getMonths().get(month - 1).getDays();
        for (int i = 0; i < days.size(); i++) {
            System.out.println((i + 1) + ". " + days.get(i).getActualDay().toString());
        }
        if (days.isEmpty()) {
            System.err.println("No existing days yet");
            notEmpty = false;
        }
        return notEmpty;
    }

    private static boolean printListOfMonths(TimeLogger timeLogger) {
        boolean notEmpty = true;
        List<WorkMonth> months = timeLogger.getMonths();
        for (int i = 0; i < months.size(); i++) {
            System.out.println((i + 1) + ". " + months.get(i).getDate().toString());
        }
        if (months.isEmpty()) {
            System.err.println("No existing months yet");
            notEmpty = false;
        }
        return notEmpty;
    }

    private static List<Task> getUnfinishedTasks(List<Task> tasks) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        List<Task> unfinishedTasks = new ArrayList();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getMinPerTask() == 0) {
                unfinishedTasks.add(task);
            }
        }
        return unfinishedTasks;
    }
}
