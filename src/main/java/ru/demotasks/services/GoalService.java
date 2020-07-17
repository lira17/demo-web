package ru.demotasks.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.demotasks.jdbc.GoalDAO;
import ru.demotasks.model.Goal;
import ru.demotasks.util.ServletUtil;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Utility class  for  {@link ru.demotasks.servlets.GoalServlet}
 * to interact with {@link GoalDAO}
 */

public class GoalService {
    final GoalDAO goalDAO = new GoalDAO();

    public String getAllGoalsJson() throws JsonProcessingException {
        List<Goal> goals = goalDAO.readAll();
        return new ObjectMapper().writeValueAsString(goals);
    }

    public String getAllUserGoalsJson(int user_id) throws JsonProcessingException {
        final String sql = "SELECT * FROM goals WHERE user_id=(?)";
        List<Goal> goals = goalDAO.readAllById(user_id, sql);
        return new ObjectMapper().writeValueAsString(goals);
    }

    public String getGoalJson(int goal_id) throws JsonProcessingException {
        final Goal goal = goalDAO.readById(goal_id);
        return new ObjectMapper().writeValueAsString(goal);
    }

    public String getSubgoals(int goal_id) throws JsonProcessingException {
        final String sql = "SELECT * FROM goals WHERE parentgoal_id=(?)";
        List<Goal> goals = goalDAO.readAllById(goal_id, sql);
        return new ObjectMapper().writeValueAsString(goals);
    }

    public int createGoal(HttpServletRequest req, int user_id) throws IOException {
        int goal_id = 0;
        req.setCharacterEncoding("UTF-8");
        final Goal goal = new ObjectMapper().readValue(req.getReader(), Goal.class);
        if (ServletUtil.isGoalValid(goal)) {
            goal.setUser_id(user_id);
            goal_id = goalDAO.create(goal);
        }
        return goal_id;
    }

    public void deleteGoal(int goal_id) {
         goalDAO.delete(goal_id);
    }

    public boolean updateGoal(HttpServletRequest req, int goal_id) throws IOException {
        req.setCharacterEncoding("UTF-8");
        final Goal goal = new ObjectMapper().readValue(req.getReader(), Goal.class);
        return goalDAO.update(goal_id, goal);
    }

    public void addParentgoal(int goal_id, int parentgoal_id) {
         goalDAO.updateParentGoal(goal_id, parentgoal_id);
    }
}
