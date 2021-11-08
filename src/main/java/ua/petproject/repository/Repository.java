package ua.petproject.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<E,ID> {
    E save(E e);
    List<E> saveAll(Iterable<E> itrb);
    void deleteById(ID id);
    Optional<E> findById(ID id);
    List<E> findAll();
}