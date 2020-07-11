package ru.demotasks.servlets;


import ru.demotasks.services.UserService;
import ru.demotasks.util.ServletUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/users/*")
public class UserServlet extends HttpServlet {

    UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final int id = ServletUtil.getIdFromPath(req);
        String jsonUser;
        if (id < 0) {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid user_id");
            resp.setStatus(404);
            return;
        }
        if (id == 0) {
            jsonUser = userService.getAllUsersJson();
        } else {
            jsonUser = userService.getUserJson(id);
        }

        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write(jsonUser);
        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        final int user_id = userService.createUser(req);
        if (user_id > 0) {
            resp.addHeader("Location", "/users/" + user_id);
            resp.setStatus(201);
        } else {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Bad JSON");
            resp.setStatus(415);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final int id = ServletUtil.getIdFromPath(req);
        resp.setContentType("text/plain; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        if (id <= 0) {
            writer.write("Invalid user_id");
            resp.setStatus(404);
        } else {
            final boolean result = userService.updateUser(req, id);
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
        final int id = ServletUtil.getIdFromPath(req);
        if (id > 0) {
            userService.deleteUser(id);
            resp.setStatus(204);
        } else {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid user_id");
            resp.setStatus(404);
        }
    }
}

