package ru.demotasks.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.demotasks.jdbc.FriendsDAO;
import ru.demotasks.jdbc.UserDAO;
import ru.demotasks.model.User;
import ru.demotasks.util.ServletUtil;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Utility class  for  {@link ru.demotasks.servlets.UserServlet}
 * to interact with {@link UserDAO}
 */
public class UserService {
    final UserDAO userDAO = new UserDAO();

    public String getAllUsersJson() throws JsonProcessingException {
        List<User> users = userDAO.readAll();
        return new ObjectMapper().writeValueAsString(users);
    }

    public String getUserJson(Integer user_id) throws JsonProcessingException {
        User user = userDAO.readById(user_id);
        return new ObjectMapper().writeValueAsString(user);
    }

    public int createUser(HttpServletRequest req) throws IOException {
        int user_id = 0;
        req.setCharacterEncoding("UTF-8");
        final User user = new ObjectMapper().readValue(req.getReader(), User.class);
        if (ServletUtil.isUserValid(user)) {
            user_id = userDAO.create(user);
        }
        return user_id;
    }

    public void  deleteUser(Integer user_id) {
         userDAO.delete(user_id);
    }

    public boolean updateUser(HttpServletRequest req, int user_id) throws IOException {
        boolean result = false;
        req.setCharacterEncoding("UTF-8");
        final User user = new ObjectMapper().readValue(req.getReader(), User.class);
        if (ServletUtil.isUserValid(user)) {
            result = userDAO.update(user_id, user);
        }
        return result;
    }
}
