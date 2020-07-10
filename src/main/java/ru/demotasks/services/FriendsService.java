package ru.demotasks.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.demotasks.jdbc.FriendsDAO;
import ru.demotasks.model.User;
import java.util.List;

/**
 * Utility class  for  {@link ru.demotasks.servlets.FriendsServlet}
 * to interact with {@link FriendsDAO}
 */
public class FriendsService {

    final FriendsDAO friendsDAO = new FriendsDAO();

    public String getAllFriendsJson(int user_id) throws JsonProcessingException {
        List<User> users;
        users = friendsDAO.readAll(user_id);
        return new ObjectMapper().writeValueAsString(users);
    }

    public void addFriend(int user_id, int friendId) {
         friendsDAO.createUserFriend(user_id, friendId);
    }

    public void deleteFriend(int user_id, int friend_id) {
        friendsDAO.deleteFriend(user_id, friend_id);
    }

    public void acceptFriend(int user_id, int friend_id) {
         friendsDAO.updateFriend(user_id, friend_id);
    }

}
