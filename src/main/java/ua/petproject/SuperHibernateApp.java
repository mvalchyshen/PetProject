package ua.petproject;

import ua.petproject.annotations.*;
import ua.petproject.entity.*;
import ua.petproject.repository.Repository;
import ua.petproject.repository.RepositoryFactory;

import javax.persistence.Lob;
import java.lang.reflect.Field;
import java.sql.SQLException;

/**
 * Class for running the app
 */
public class SuperHibernateApp {
    public static void main(String[] args) throws IllegalAccessException, SQLException, NoSuchFieldException {
        // initializing tables in DB if they don't exist
        CreateEntityTables.init();
    }
}
