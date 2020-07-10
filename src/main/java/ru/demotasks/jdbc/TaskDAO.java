package ru.demotasks.jdbc;

import ru.demotasks.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO implements DAO<Task, Integer> {
    //todo 2 empty lines at the beginning

    public TaskDAO() {
    }


    @Override
    public boolean create(Task task) {
        boolean result = false;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.INSERT.QUERY)) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setBoolean(3, task.isDone());
            statement.setString(4, task.getDueDate());
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); //todo exception handling
        }
        closeConnection(connection);
        //todo just return result without processing is bad practice : check results
        return result;
    }

    @Override
    public Task readById(Integer integer) {
        final Task result = new Task();
        result.setId(-1); // todo what is the meaning? It is usual approach for new tasks value objects prior saving to DB, but would it be used as new task?

        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.GET_ONE.QUERY)) {
            statement.setInt(1, integer);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result.setId(integer);
                result.setTitle(resultSet.getString("title"));
                result.setDescription(resultSet.getString("description"));
                result.setDone(resultSet.getBoolean("is_done"));
                result.setDueDate(resultSet.getString("due_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); //todo exception handling
        }
        closeConnection(connection); // todo what if exception occurred, would this statement be executed?
        return result;

    }

    @Override
    public List<Task> readAll() {
        final List<Task> result = new ArrayList<>();
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.GET_ALL.QUERY)) {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getInt("task_id"));
                task.setTitle(resultSet.getString("title"));
                task.setDescription(resultSet.getString("description"));
                task.setDone(resultSet.getBoolean("is_done"));
                task.setDueDate(resultSet.getString("due_date"));
                result.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace(); //todo exceptino handling
        }
        closeConnection(connection); // todo consider finally
        return result;

    }

    @Override
    public boolean update(Task task) {
        boolean result = false;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.UPDATE.QUERY)) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setBoolean(3, task.isDone());
            statement.setString(4, task.getDueDate());
            statement.setInt(5, task.getId());
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); //todo exception handling
        }
        closeConnection(connection);
        return result;
    }

    @Override
    public boolean delete(Integer id) {
        boolean result = false;
        final Connection connection = TaskConnectionPool.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SQLTask.DELETE.QUERY)) {
            statement.setInt(1, id);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); //todo
        }
        closeConnection(connection); ///todo
        return result;
    }

    //todo utility method visible only here, consider smaller visibility scope
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //  it may be enough to use string constants for SQL, but this works as well
    enum SQLTask {
        GET_ONE("SELECT * FROM tasks WHERE task_id = (?)"),
        GET_ALL("SELECT * FROM tasks"),
        INSERT("INSERT INTO tasks (title, description,is_done,due_date) VALUES((?),(?),(?),(?))"),
        DELETE("DELETE  FROM tasks WHERE task_id = (?)"),
        UPDATE("UPDATE tasks SET title = (?), description = (?), is_done = (?), due_date = (?) WHERE task_id = (?)");

        String QUERY;

        SQLTask(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
