package ru.demotasks.jdbc;

public class DatabaseException extends RuntimeException {

    public DatabaseException(Exception e) {
        super(e);
    }
}
