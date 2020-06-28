package ru.demotasks.jdbc;

import java.util.List;

public interface DAO<Entity, Key> {
    boolean create(Entity model);

    Entity readById(Key key);

    List<Entity> readAll();

    boolean update(Entity model);

    boolean delete(Key key);

}
