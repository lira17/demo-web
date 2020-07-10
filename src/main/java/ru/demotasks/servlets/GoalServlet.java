package ru.demotasks.servlets;

import ru.demotasks.services.GoalService;
import ru.demotasks.util.ServletUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/goals/*")
public class GoalServlet extends HttpServlet {

    private GoalService goalService;

    @Override
    public void init() throws ServletException {
        this.goalService = new GoalService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final int goal_id = ServletUtil.getIdFromPath(req);
        final boolean isSubgoalsValid = ServletUtil.isSubgoalValid(req);
        final boolean userParameterIsValid = ServletUtil.parameterIsValid(req, "user_id");
        String jsonGoal = "";
        if (goal_id < 0) {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid goal_id");
            resp.setStatus(404);
            return;
        }
        if (goal_id == 0 && !userParameterIsValid) {
            jsonGoal = goalService.getAllGoalsJson();
        }
        if (goal_id == 0 && userParameterIsValid) {
            final int user_id = Integer.parseInt(req.getParameter("user_id"));
            jsonGoal = goalService.getAllUserGoalsJson(user_id);
        }
        if (goal_id > 0 && !isSubgoalsValid) {
            jsonGoal = goalService.getGoalJson(goal_id);
        }
        if (goal_id > 0 && isSubgoalsValid) {
            jsonGoal = goalService.getSubgoals(goal_id);
        }
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write(jsonGoal);
        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (ServletUtil.parameterIsValid(req, "user_id")) {
            final int user_id = Integer.parseInt(req.getParameter("user_id"));
            final int goal_id = goalService.createGoal(req, user_id);
            if (goal_id > 0) {
                resp.addHeader("Location", "/goals/" + goal_id);
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
        final int goal_id = ServletUtil.getIdFromPath(req);
        final boolean parentgoalIdIsValid = ServletUtil.parameterIsValid(req, "parentgoal_id");
        resp.setContentType("text/plain; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        if (goal_id < 0) {
            writer.write("Invalid goal_id");
            resp.setStatus(404);
            return;
        }
        if (parentgoalIdIsValid) {
            final int parentgoal_id = Integer.parseInt(req.getParameter("parentgoal_id"));
            goalService.addParentgoal(goal_id, parentgoal_id);
        }
        if (!parentgoalIdIsValid) {
            final boolean result = goalService.updateGoal(req, goal_id);
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
        final int goal_id = ServletUtil.getIdFromPath(req);
        if (goal_id > 0) {
            goalService.deleteGoal(goal_id);
            resp.setStatus(204);
        } else {
            resp.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Invalid goal_id");
            resp.setStatus(404);
        }
    }
}
