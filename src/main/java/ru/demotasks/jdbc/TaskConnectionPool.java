package ru.demotasks.jdbc;

import org.apache.log4j.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection pool (Tomcat realization, for settings see  webapp\META-INF\context.xml)
 * @author Victoria Veselova
 */

public class TaskConnectionPool {
    private final static Logger logger = Logger.getLogger(TaskConnectionPool.class);
    
    private static TaskConnectionPool instance = null;

    private TaskConnectionPool() {
    }

    synchronized public static TaskConnectionPool getInstance() {
        if (instance == null) {
            instance = new TaskConnectionPool();
        }
        return instance;
    }

    public Connection getConnection() {
        Context ctx;
        Connection connection;
        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/tasksdb");
            connection = ds.getConnection();
        } catch (NamingException | SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e);
        }
        return connection;
    }
}
