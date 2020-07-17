package ru.demotasks.servlets;

import ru.demotasks.services.FriendsService;
import ru.demotasks.util.ServletUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/friends")
public class FriendsServlet extends HttpServlet {
    private FriendsService friendsService;

    @Override
    public void init() throws ServletException {
        friendsService = new FriendsService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (ServletUtil.parameterIsValid(req, "user_id")) {
            final int user_id = Integer.parseInt(req.getParameter("user_id"));
            final String jsonFriends = friendsService.getAllFriendsJson(user_id);
            resp.setContentType("application/json; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(jsonFriends);
            resp.setStatus(200);
        } else {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid user_id");
            resp.setStatus(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (ServletUtil.parameterIsValid(req, "user_id") && ServletUtil.parameterIsValid(req, "friend_id")) {
            final int user_id = Integer.parseInt(req.getParameter("user_id"));
            final int friend_id = Integer.parseInt(req.getParameter("friend_id"));
            friendsService.addFriend(user_id, friend_id);
            resp.setStatus(201);
        } else {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid user_id/friend_id");
            resp.setStatus(404);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (ServletUtil.parameterIsValid(req, "user_id") && ServletUtil.parameterIsValid(req, "friend_id")) {
            final int user_id = Integer.parseInt(req.getParameter("user_id"));
            final int friend_id = Integer.parseInt(req.getParameter("friend_id"));
            friendsService.acceptFriend(user_id, friend_id);
            resp.setStatus(204);
        } else {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid user_id/friend_id");
            resp.setStatus(404);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (ServletUtil.parameterIsValid(req, "user_id") && ServletUtil.parameterIsValid(req, "friend_id")) {
            final int user_id = Integer.parseInt(req.getParameter("user_id"));
            final int friend_id = Integer.parseInt(req.getParameter("friend_id"));
            friendsService.deleteFriend(user_id, friend_id);
            resp.setStatus(204);
        } else {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid user_id/friend_id");
            resp.setStatus(404);
        }
    }
}
