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
 * Class with CRUD operations for {@link User}
 */

public class UserDAO extends BaseDAO implements EntityDAO<User, Integer> {

    private final static Logger logger = Logger.getLogger(UserDAO.class);

    @Override
    public User readById(Integer id) {
        final User result = new User();
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLUser.GET_ONE.QUERY)) {
            statement.setInt(1, id);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result.setId(resultSet.getInt("user_id"));
                result.setLogin(resultSet.getString("login"));
                result.setEmail(resultSet.getString("email"));
                result.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    public List<User> readAll() {
        List<User> result = new ArrayList<>();
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLUser.GET_ALL.QUERY)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setLogin(rs.getString("login"));
                result.add(user);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public int create(User user) {
        int user_id = 0;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLUser.INSERT.QUERY)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user_id = resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return user_id;
    }

    @Override
    public boolean update(Integer key, User user) {
        boolean result = false;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLUser.UPDATE.QUERY)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setInt(4, key);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public void delete(Integer user_id) {
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLUser.DELETE.QUERY)) {
            statement.setInt(1, user_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
    }

    @Override
    public List<User> readAllById(Integer integer, String SQLQuery) {
        return null;
    }

    enum SQLUser {
        GET_ALL("SELECT user_id, login FROM users"),
        GET_ONE("SELECT * FROM users WHERE user_id = (?)"),
        INSERT("INSERT INTO users (login, password, email) VALUES((?),(?),(?)) RETURNING user_id"),
        UPDATE("UPDATE users SET email=(?), login=(?), password=(?) WHERE user_id=(?)"),
        DELETE("DELETE  FROM users WHERE user_id=(?)");

        String QUERY;

        SQLUser(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}

