package ru.demotasks.jdbc;

import java.util.List;

public interface DAO<Entity, Key> {
    //todo return value is never used
    boolean create(Entity model);

    Entity readById(Key key);

    List<Entity> readAll();

    //todo return value is never used, it may worth to validate update result internally or externally
    boolean update(Entity model);

    boolean delete(Key key);

}
