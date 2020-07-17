package ru.demotasks.jdbc;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

abstract class BaseDAO {
    private final static Logger logger = Logger.getLogger(BaseDAO.class);

    protected void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                throw new DatabaseException(e);
            }
        }
    }
}
