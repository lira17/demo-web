package ru.demotasks.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.demotasks.jdbc.DAO;
import ru.demotasks.jdbc.TaskDAO;
import ru.demotasks.model.Task;
import ru.demotasks.util.TaskUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/tasks/*")
public class TaskServlet extends HttpServlet {
    DAO<Task, Integer> taskDao;

    @Override
    public void init() throws ServletException {
        taskDao = new TaskDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) {
            List<Task> tasks = taskDao.readAll();
            final String jsonTask = new ObjectMapper().writeValueAsString(tasks);
            resp.setContentType("application/json; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(jsonTask);
        } else {
            int id = TaskUtil.getTaskIdFromPath(path);
            req.setCharacterEncoding("UTF-8");
            final Task task = taskDao.readById(id);
            final String jsonTask = new ObjectMapper().writeValueAsString(task);
            resp.setContentType("application/json; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(jsonTask);
        }

        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (TaskUtil.taskRequestIsValid(req)) {
            final String title = req.getParameter("title");
            final String description = req.getParameter("description");
            final String dueDate = req.getParameter("due_date");
            final boolean status = Boolean.parseBoolean(req.getParameter("status"));
            final Task task = new Task(0, title, description, status, dueDate);
            taskDao.create(task);
        }
        resp.setStatus(201);

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        int id = TaskUtil.getTaskIdFromPath(path);
        if (id < 0) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().write("No task_id");
            return;
        }

        req.setCharacterEncoding("UTF-8");

        if (TaskUtil.taskRequestIsValid(req)) {
            final String title = req.getParameter("title");
            final String description = req.getParameter("description");
            final String dueDate = req.getParameter("due_date");
            final boolean status = Boolean.parseBoolean(req.getParameter("status"));
            Task task = new Task(id, title, description, status, dueDate);
            taskDao.update(task);
        }
        resp.setStatus(200);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        int id = TaskUtil.getTaskIdFromPath(path);
        if (id < 0) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().write("No task_id");
            return;
        }
        taskDao.delete(id);
        resp.setStatus(202);
    }

}
