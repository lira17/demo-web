package ru.demotasks.jdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TaskConnectionPool {
    //todo spacing at the beginning of a class is a little bit uncommon
    private TaskConnectionPool() {
    }

    private static TaskConnectionPool instance = null;

    public static TaskConnectionPool getInstance() {
        if (instance == null) {
            instance = new TaskConnectionPool();
        }
        return instance;
    }

    public Connection getConnection() {
        Context ctx;
        Connection connection = null;
        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/demodb");
            connection = ds.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace(); // todo improve exception handling
        }
        return connection;
    }
}
