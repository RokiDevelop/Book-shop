package org.example.app.repositories;

import org.example.app.exceptions.ItemNotFoundException;

import java.util.List;

public interface IProjectRepository<T> {
    
    List <T> retrieveAll();

    boolean store(T t) throws ItemNotFoundException;

    boolean removeItemById(Integer bookIdToRemove) throws ItemNotFoundException;

    int removeItemByRegex(String queryRegex) throws ItemNotFoundException;
}
