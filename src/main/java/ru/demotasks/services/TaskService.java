package ru.demotasks.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.demotasks.jdbc.FriendsDAO;
import ru.demotasks.jdbc.TaskDAO;
import ru.demotasks.model.Task;
import ru.demotasks.util.ServletUtil;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Utility class  for  {@link ru.demotasks.servlets.TaskServlet}
 * to interact with {@link TaskDAO}
 */
public class TaskService {
    final TaskDAO taskDAO = new TaskDAO();

    public String getAllTAsksJson() throws JsonProcessingException {
        List<Task> tasks = taskDAO.readAll();
        return new ObjectMapper().writeValueAsString(tasks);
    }

    public String getAllUserTasksJson(int user_id) throws JsonProcessingException {
        List<Task> tasks = taskDAO.readAllById(user_id, "SELECT * FROM tasks WHERE user_id=(?)");
        return new ObjectMapper().writeValueAsString(tasks);
    }

    public String getAllTasksByGoalIdJson(int goal_id) throws JsonProcessingException {
        List<Task> tasks = taskDAO.readAllById(goal_id, "SELECT * FROM tasks WHERE goal_id=(?)");
        return new ObjectMapper().writeValueAsString(tasks);
    }

    public String getTaskByIdJson(int task_id) throws JsonProcessingException {
        Task task = taskDAO.readById(task_id);
        return new ObjectMapper().writeValueAsString(task);
    }

    public void deleteTask(int task_id) {
        taskDAO.delete(task_id);
    }

    public int createTask(HttpServletRequest request, int user_id) throws IOException {
        int task_id = 0;
        request.setCharacterEncoding("UTF-8");
        final Task task = new ObjectMapper().readValue(request.getReader(), Task.class);
        task.setUser_id(user_id);
        if (ServletUtil.isTaskValid(task)) {
            task_id = taskDAO.create(task);
        }
        return task_id;
    }

    public boolean giveTaskToFriend(int user_id, int friend_id, int task_id) {
        boolean result = false;
        final FriendsDAO friendsDAO = new FriendsDAO();
        if (friendsDAO.isFriend(user_id, friend_id)) {
            result = taskDAO.giveTask(friend_id, task_id);
        }
        return result;
    }

    public boolean changeTask(HttpServletRequest request, int task_id) throws IOException {
        boolean result = false;
        request.setCharacterEncoding("UTF-8");
        final Task task = new ObjectMapper().readValue(request.getReader(), Task.class);
        if (ServletUtil.isTaskValid(task)) {
            task.setId(task_id);
            result = taskDAO.update(task_id, task);
        }
        return result;
    }
}
