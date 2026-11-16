package com.mycompany.jacinto_bookbridge_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.util.Optional;
import java.util.List;

public interface Repository<T, ID> {
    void add(ID id, T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void remove(ID id);
}