package ua.petproject.repository;

import ua.petproject.annotations.SaveMethod;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<E,ID> {
    E save(E e) throws IllegalAccessException, SQLException, NoSuchFieldException;
    List<E> saveAll(Iterable<E> itrb) throws IllegalAccessException, SQLException, NoSuchFieldException;
    void deleteById(ID id) throws SQLException;
    Optional<E> findById(ID id) throws SQLException;
    List<E> findAll() throws SQLException;
}