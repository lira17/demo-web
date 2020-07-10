package ru.demotasks.jdbc;

import java.util.List;

public interface EntityDAO<Entity, Key> {
    Entity readById(Key key);

    List<Entity> readAll();

    List<Entity> readAllById(Key key, String SQLQuery);

    int create(Entity model);

    boolean update(Key key, Entity entity);

    void delete(Key key);
}
