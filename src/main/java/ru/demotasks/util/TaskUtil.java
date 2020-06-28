package ru.demotasks.util;

import javax.servlet.http.HttpServletRequest;

public class TaskUtil {

    public static boolean taskRequestIsValid(HttpServletRequest request) {
        final String title = request.getParameter("title");
        final String description = request.getParameter("description");
        final String dueDate = request.getParameter("due_date");
        final String isDone = request.getParameter("is_done");

        return title != null && title.length() > 0 &&
                description != null && description.length() > 0 &&
                dueDate.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$") &&
                (isDone.equals("false") || isDone.equals("true"));
    }

    public static int getTaskIdFromPath(String path) {
        if (path == null || path.length() < 2) {
            return -1;
        }
        String[] parts = path.split("/");
        String id = parts[1];
        return Integer.parseInt(id);
    }
}
