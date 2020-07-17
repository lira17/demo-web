package ru.demotasks.servlets;

import ru.demotasks.services.TaskService;
import ru.demotasks.util.ServletUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/tasks/*")
public class TaskServlet extends HttpServlet {
    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        taskService = new TaskService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final int task_id = ServletUtil.getIdFromPath(req);
        final boolean userParameterIsValid = ServletUtil.parameterIsValid(req, "user_id");
        final boolean goalParameterIsValid = ServletUtil.parameterIsValid(req, "goal_id");

        String jsonTasks = "";

        if (task_id < 0) {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid task_id");
            resp.setStatus(404);
            return;
        }
        if (task_id > 0) {
            jsonTasks = taskService.getTaskByIdJson(task_id);
        } else {
            if (!userParameterIsValid && !goalParameterIsValid) {
                jsonTasks = taskService.getAllTAsksJson();
            }
            if (userParameterIsValid) {
                final int user_id = Integer.parseInt(req.getParameter("user_id"));
                jsonTasks = taskService.getAllUserTasksJson(user_id);
            }
            if (goalParameterIsValid) {
                final int goal_id = Integer.parseInt(req.getParameter("goal_id"));
                jsonTasks = taskService.getAllTasksByGoalIdJson(goal_id);
            }
        }
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write(jsonTasks);
        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (ServletUtil.parameterIsValid(req, "user_id")) {
            final int user_id = Integer.parseInt(req.getParameter("user_id"));
            final int task_id = taskService.createTask(req, user_id);
            if (task_id > 0) {
                resp.addHeader("Location", "/tasks/" + task_id);
                resp.setStatus(201);
            } else {
                resp.setContentType("text/plain; charset=UTF-8");
                PrintWriter writer = resp.getWriter();
                writer.write("Bad JSON");
                resp.setStatus(415);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final int task_id = ServletUtil.getIdFromPath(req);
        resp.setContentType("text/plain; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        if (task_id < 0) {
            writer.write("Invalid task_id");
            resp.setStatus(404);
            return;
        }
        if (ServletUtil.parameterIsValid(req, "friend_id") &&
                ServletUtil.parameterIsValid(req, "user_id")) {
            final int user_id = Integer.parseInt(req.getParameter("user_id"));
            final int friend_id = Integer.parseInt(req.getParameter("friend_id"));
            final boolean result = taskService.giveTaskToFriend(user_id, friend_id, task_id);
            if (result) {
                resp.setStatus(204);
            } else {
                writer.write("Users are not friends");
                resp.setStatus(403);
            }
        } else {
            final boolean result = taskService.changeTask(req, task_id);
            if (result) {
                resp.setStatus(204);
            } else {
                writer.write("Bad JSON");
                resp.setStatus(415);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final int task_id = ServletUtil.getIdFromPath(req);
        if (task_id > 0) {
            taskService.deleteTask(task_id);
            resp.setStatus(204);
        } else {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid task_id");
            resp.setStatus(404);
        }
    }
}
