package ua.petproject.util;

import lombok.SneakyThrows;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection implements Closeable {
    private static final String JDBC_DRIVER = PropertiesLoader.getProperty("db.driver");
    private static final String url = PropertiesLoader.getProperty("db.url");
    private static final  String user = PropertiesLoader.getProperty("db.user");
    private static final  String password = PropertiesLoader.getProperty("db.password");

    private static DataBaseConnection dataBaseConnection;
    private Connection connection;
    @SneakyThrows
    private DataBaseConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  Connection getConnection() {
        return connection;
    }

    @SneakyThrows
    public static DataBaseConnection getInstance()  {
        if (dataBaseConnection == null) {
            dataBaseConnection = new DataBaseConnection();
        } else if (dataBaseConnection.getConnection().isClosed()){
            dataBaseConnection = new DataBaseConnection();
        }
        return dataBaseConnection;
    }

    @SneakyThrows
    @Override
    public void close() {
        connection.close();
    }
}
