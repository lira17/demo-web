package ru.demotasks.jdbc;

import org.apache.log4j.Logger;
import ru.demotasks.model.Task;
import ru.demotasks.util.ServletUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with CRUD operations for {@link Task}
 */
public class TaskDAO extends BaseDAO implements EntityDAO<Task, Integer> {
    private final static Logger logger = Logger.getLogger(TaskDAO.class);

    @Override
    public Task readById(Integer task_id) {
        final Task result = new Task();
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.GET_ONE.QUERY)) {
            statement.setInt(1, task_id);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                fillTask(resultSet, result);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public List<Task> readAll() {
        List<Task> result;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.GET_ALL.QUERY)) {
            final ResultSet resultSet = statement.executeQuery();
            result = getTaskList(resultSet);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public List<Task> readAllById(Integer id, String SQLQuery) {
        List<Task> result = new ArrayList<>();
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLQuery)) {
            statement.setInt(1, id);
            final ResultSet resultSet = statement.executeQuery();
            result = getTaskList(resultSet);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public int create(Task task) {
        int task_id = 0;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.INSERT.QUERY)) {
            statement.setInt(1, task.getUser_id());
            statement.setString(2, task.getTitle());
            statement.setString(3, task.getDescription());
            statement.setBoolean(4, task.isDone());
            statement.setDate(5, Date.valueOf(task.getDueDate()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                task_id = resultSet.getInt("task_id");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return task_id;
    }

    @Override
    public boolean update(Integer id, Task task) {
        boolean result = false;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.UPDATE.QUERY)) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setBoolean(3, task.isDone());
            statement.setDate(4, Date.valueOf(task.getDueDate()));
            statement.setInt(5, task.getId());
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public void delete(Integer task_id) {
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.DELETE.QUERY)) {
            statement.setInt(1, task_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
    }

    public boolean giveTask(int friend_id, int task_id) {
        boolean result = false;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.GIVE_TASK.QUERY)) {
            statement.setInt(1, friend_id);
            statement.setInt(2, task_id);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        closeConnection(connection);
        return result;

    }

    private List<Task> getTaskList(ResultSet resultSet) throws SQLException {
        final List<Task> result = new ArrayList<>();
        while (resultSet.next()) {
            Task task = new Task();
            fillTask(resultSet, task);
            result.add(task);
        }
        return result;
    }

    private void fillTask(ResultSet resultSet, Task task) throws SQLException {
        task.setId(resultSet.getInt("task_id"));
        task.setUser_id(resultSet.getInt("user_id"));
        task.setGoalId(resultSet.getInt("goal_id"));
        task.setTitle(resultSet.getString("title"));
        task.setDescription(resultSet.getString("description"));
        task.setDone(resultSet.getBoolean("is_done"));
        task.setDueDate(ServletUtil.convertToLocalDate(resultSet.getDate("due_date")));
    }

    enum SQLTask {
        GET_ONE("SELECT * FROM tasks WHERE task_id = (?)"),
        GET_ALL("SELECT * FROM tasks"),
        INSERT("INSERT INTO tasks (user_id,title, description,is_done,due_date) VALUES((?),(?),(?),(?),(?))" +
                "RETURNING task_id"),
        DELETE("DELETE  FROM tasks WHERE task_id = (?)"),
        UPDATE("UPDATE tasks SET title = (?), description = (?), is_done = (?), due_date = (?) WHERE task_id = (?)"),
        GIVE_TASK("UPDATE tasks SET user_id =(?) WHERE task_id = (?)");

        String QUERY;

        SQLTask(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
