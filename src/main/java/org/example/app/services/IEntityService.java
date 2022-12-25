package org.example.app.services;

import org.example.app.exceptions.ItemNotFoundException;

import java.util.List;

public interface IEntityService<T> {

    List<T> getAll();

    boolean save(T t) throws ItemNotFoundException;

    boolean removeById(String id) throws ItemNotFoundException;

    int removeByRegex(String queryRegex) throws ItemNotFoundException;
}
