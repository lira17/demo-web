package ru.demotasks.util;

import ru.demotasks.model.Goal;
import ru.demotasks.model.Task;
import ru.demotasks.model.User;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Class with utils methods
 * @author Victoria Veselova
 * */
public class ServletUtil {
    /**
     * @return -1 if there is no valid ID in the path; 0 - if there is no ID at all; int ID - if ID is valid
     */
    public static int getIdFromPath(HttpServletRequest request) {
        final String path = request.getPathInfo();
        int result = 0;
        if (path == null || "/".equals(path)) {
            return result;
        }
        String[] parts = path.split("/");
        if (parts[1].matches("\\d*")){
            result = Integer.parseInt(parts[1]);
        } else {
            result = - 1;
        }
        return result;
    }
    /**
     * @return true if there is "subgoals" in the path
     */
    public static boolean isSubgoalValid(HttpServletRequest req) {
        final String path = req.getPathInfo();
        if (path == null || "/".equals(path)) {
            return false;
        }
        String[] parts = path.split("/");
        if (parts.length < 3) {
            return false;
        }
        return "subgoals".equals(parts[2]);
    }
    /**
     * Check parameter by name
     * @return true if parameters value is number
     * @throws UnsupportedEncodingException
     */
    public static boolean parameterIsValid(HttpServletRequest req, String paramName) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");
        final String id = req.getParameter(paramName);
        return id != null && id.matches("[0-9]+");
    }
    /**
     * Check new user before insert into database
     * @return true if all fields are NOT NULL and email address is valid
     */
    public static boolean isUserValid(User user) {
        final String emailRegexp = "^(?!.*@.*@.*$)(?!.*@.*--.*\\..*$)(?!.*@.*-\\..*$)(?!.*@.*-$)(.*@.+(\\..{1,11})?)$";
        final String login = user.getLogin();
        final String password = user.getPassword();
        final String email = user.getEmail();
        return (login != null && password != null && email != null && email.matches(emailRegexp));
    }
    /**
     * Check new goal before insert into database
     * @return true, if all obligatory fields NOT NULL
     */
    public static boolean isGoalValid(Goal goal) {
        final String title = goal.getTitle();
        return title != null;
    }
    /**
     * Check new task before insert into database
     * @return true, if all obligatory fields NOT NULL
     */
    public static boolean isTaskValid(Task task) {
        final String title = task.getTitle();
        final String description = task.getDescription();
        final LocalDate dueDate = task.getDueDate();
        return title != null && description != null && dueDate != null;
    }

    /**
     *convert sqkDate from Postgres db to LocalDate
     * @return valid LocalDate
     */
    public static LocalDate convertToLocalDate(Date sqlDate) {
        return new java.sql.Date(sqlDate.getTime()).toLocalDate();
    }
}
