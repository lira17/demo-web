package ru.demotasks.jdbc;

import org.apache.log4j.Logger;
import ru.demotasks.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with CRUD operations for Friends
 * @author Victoria Veselova
 */

public class FriendsDAO extends BaseDAO {

    private final static Logger logger = Logger.getLogger(FriendsDAO.class);

    public List<User> readAll(Integer user_id) {
        final List<User> friends = new ArrayList<>();
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLFriend.GET_FRIENDS.QUERY)) {
            statement.setInt(1, user_id);
            statement.setInt(2, user_id);
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User friend = new User();
                friend.setId((resultSet.getInt("user_id")));
                friend.setLogin(resultSet.getString("login"));
                friends.add(friend);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return friends;
    }

    public void createUserFriend(int user_id, int friend_id) {
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLFriend.INSERT_FRIEND.QUERY)) {
            statement.setInt(1, user_id);
            statement.setInt(2, friend_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
    }

    public void deleteFriend(int user_id, int friend_id) {
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLFriend.DELETE_FRIEND.QUERY)) {
            statement.setInt(1, user_id);
            statement.setInt(2, friend_id);
            statement.setInt(3, friend_id);
            statement.setInt(4, user_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
    }

    public void updateFriend(int user_id, int friend_id) {
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLFriend.UPDATE_FRIEND.QUERY)) {
            statement.setInt(1, user_id);
            statement.setInt(2, user_id);
            statement.setInt(3, friend_id);
            statement.setInt(4, friend_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
    }

    public boolean isFriend(int user_id, int friend_id) {
        int status = 0;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLFriend.IS_FRIEND.QUERY)) {
            statement.setInt(1, user_id);
            statement.setInt(2, user_id);
            statement.setInt(3, friend_id);
            statement.setInt(4, friend_id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                status = resultSet.getInt("status");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return status == 1;
    }

    enum SQLFriend {
        GET_FRIENDS("SELECT users.user_id, users.login FROM users, friends WHERE CASE " +
                "WHEN friends.friend_one = (?) THEN friends.friend_two = users.user_id WHEN friends.friend_two = (?) " +
                "THEN friends.friend_one= users.user_id END AND friends.status=1"),
        DELETE_FRIEND("DELETE FROM friends WHERE (friend_one= (?) AND friend_two = (?)) OR (friend_one=(?) AND friend_two=(?))"),
        UPDATE_FRIEND("UPDATE friends SET status=1 WHERE (friend_one=(?) OR friend_two=(?)) AND (friend_one=(?) OR friend_two=(?));"),
        INSERT_FRIEND("INSERT INTO friends (friend_one, friend_two) VALUES((?),(?))"),
        IS_FRIEND("SELECT status FROM friends WHERE (friend_one=(?) OR friend_two=(?)) AND(friend_one=(?) OR friend_two=(?))");

        String QUERY;

        SQLFriend(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
