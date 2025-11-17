package com.project.docxtopdf.models.bo;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static final String URL;
    private static final String USER;
    private static final String PASS;

    static {
        try {
            String host = System.getenv("DB_HOST");
            String port = System.getenv("DB_PORT");
            String dbName = System.getenv("DB_NAME");
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASS");

            if (host == null || port == null || dbName == null || user == null || pass == null) {
                Properties prop = new Properties();
                InputStream input = Database.class.getClassLoader().getResourceAsStream("config/config.properties");

                if (input == null) {
                    throw new RuntimeException("Cannot load config.properties");
                }

                prop.load(input);

                host = host != null ? host : prop.getProperty("db.host");
                port = port != null ? port : prop.getProperty("db.port");
                dbName = dbName != null ? dbName : prop.getProperty("db.name");
                user = user != null ? user : prop.getProperty("db.user");
                pass = pass != null ? pass : prop.getProperty("db.pass");

                input.close();
            }

            URL = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
            USER = user;
            PASS = pass;

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Connection Failed!");
            throw new RuntimeException(e);
        }
    }
}
