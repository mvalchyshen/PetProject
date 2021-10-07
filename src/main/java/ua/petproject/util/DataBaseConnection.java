package ua.petproject.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
    private HikariDataSource ds;
    @SneakyThrows
    private DataBaseConnection() {
        ds = new HikariDataSource(initDataSource());
    }

    private HikariConfig initDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(JDBC_DRIVER);
        config.setJdbcUrl(url);
        config.setPassword(password);
        config.setUsername(user);
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(10_000);
        config.setConnectionTimeout(10_000);
        return config;
    }

    @SneakyThrows
    public  Connection getConnection() {
        return ds.getConnection();
    }

    @SneakyThrows
    public static DataBaseConnection getInstance()  {
        if (dataBaseConnection == null) {
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
