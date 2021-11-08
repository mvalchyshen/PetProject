package ua.petproject;

import ua.petproject.annotations.CreateEntityTables;

/**
 * Class for running the app
 */
public class SuperHibernateApp {
    public static void main(String[] args) {
        // initializing tables in DB if they don't exist
        CreateEntityTables.init();

    }
}