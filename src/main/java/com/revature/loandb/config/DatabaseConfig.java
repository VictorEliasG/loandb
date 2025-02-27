package com.revature.loandb.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/loandb";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);
        return DriverManager.getConnection(DB_URL, props);
    }

    public static void initializeDatabase() {
        // Run SQL scripts here (create_users_table.sql, create_loans_table.sql)
    }
}
