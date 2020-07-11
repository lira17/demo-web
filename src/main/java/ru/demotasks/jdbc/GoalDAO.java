package ru.demotasks.jdbc;

import org.apache.log4j.Logger;
import ru.demotasks.model.Goal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with CRUD operations for {@link Goal}
 */
public class GoalDAO extends BaseDAO implements EntityDAO<Goal, Integer> {

    private final static Logger logger = Logger.getLogger(GoalDAO.class);

    @Override
    public Goal readById(Integer id) {
        final Goal result = new Goal();
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLGoal.GET_ONE.QUERY)) {
            statement.setInt(1, id);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                fillGoal(resultSet, result);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public List<Goal> readAll() {
        List<Goal> result;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLGoal.GET_ALL.QUERY)) {
            final ResultSet resultSet = statement.executeQuery();
            result = getGoalList(resultSet);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public List<Goal> readAllById(Integer id, String SQLQuery) {
        List<Goal> result;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLQuery)) {
            statement.setInt(1, id);
            final ResultSet resultSet = statement.executeQuery();
            result = getGoalList(resultSet);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public int create(Goal goal) {
        int goal_id = 0;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLGoal.INSERT.QUERY)) {
            statement.setString(1, goal.getTitle());
            statement.setInt(2, goal.getUser_id());
            statement.setString(3, goal.getDescription());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                goal_id = resultSet.getInt("goal_id");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return goal_id;
    }

    @Override
    public boolean update(Integer goal_id, Goal goal) {
        boolean result = false;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLGoal.UPDATE.QUERY)) {
            statement.setString(1, goal.getTitle());
            statement.setString(2, goal.getDescription());
            statement.setInt(3, goal_id);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    public void updateParentGoal(int goal_id, int parentgoal_id) {
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLGoal.SET_PARENTGOAL.QUERY)) {
            statement.setInt(1, parentgoal_id);
            statement.setInt(2, goal_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
    }

    @Override
    public void delete(Integer goal_id) {
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLGoal.DELETE.QUERY)) {
            statement.setInt(1, goal_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
    }

    private List<Goal> getGoalList(ResultSet resultSet) throws SQLException {
        final List<Goal> result = new ArrayList<>();
        while (resultSet.next()) {
            Goal goal = new Goal();
            fillGoal(resultSet, goal);
            result.add(goal);
        }
        return result;
    }

    private void fillGoal(ResultSet resultSet, Goal goal) throws SQLException {
        goal.setId(resultSet.getInt("goal_id"));
        goal.setUser_id(resultSet.getInt("user_id"));
        goal.setTitle(resultSet.getString("title"));
        goal.setDescription(resultSet.getString("description"));
    }

    enum SQLGoal {
        GET_ONE("SELECT * FROM goals WHERE goal_id = (?)"),
        GET_ALL("SELECT * FROM goals"),
        INSERT("INSERT INTO goals (title, user_id, description) VALUES((?),(?),(?)) RETURNING goal_id"),
        DELETE("DELETE  FROM goals WHERE goal_id = (?)"),
        SET_PARENTGOAL("UPDATE goals SET parentgoal_id=(?) WHERE goal_id=(?)"),
        UPDATE("UPDATE goals SET title = (?), description = (?) WHERE goal_id = (?)");

        String QUERY;

        SQLGoal(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
