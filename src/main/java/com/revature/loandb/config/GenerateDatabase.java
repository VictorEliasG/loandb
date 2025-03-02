package com.revature.loandb.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GenerateDatabase {
    // 1) (Optional) DROP TABLES (to reset each time, ensuring a clean state for testing)

    private static final String DROP_TABLES_SQL = """
        
        DROP TABLE IF EXISTS users CASCADE;
        DROP TABLE IF EXISTS loans CASCADE;
        """;

    // 2) CREATE TABLES
    private static final String CREATE_TABLES_SQL = """
        CREATE TABLE IF NOT EXISTS users (
            id SERIAL PRIMARY KEY,
            username VARCHAR(50) NOT NULL UNIQUE,
            password_hash VARCHAR(255) NOT NULL,
            role VARCHAR(50) NOT NULL DEFAULT 'user'
        );
        CREATE TABLE IF NOT EXISTS loans (
            id SERIAL PRIMARY KEY,
            user_id INTEGER REFERENCES users(id),
            amount DECIMAL(15, 2) NOT NULL,
            type VARCHAR(50) NOT NULL,
            status VARCHAR(10) DEFAULT 'pending',
            CONSTRAINT status_check CHECK (status IN ('pending', 'approved', 'rejected'))
        );
        """;

    // 3) INSERT SAMPLE DATA
    private static final String INSERT_DATA_SQL = """
        -- Insert sample users
        INSERT INTO users (username, password_hash, role)
        VALUES
            ('Tony Stark', '$2a$10$HnnCbk7S/MlBesBt3i1Ub.2YMC4mEsfnc9IeX0FmUgs/ACajZlnt.', 'manager'),
            ('Peter Parker', '$2a$10$zslYvI15cPv.XC0fd0Xx.ufPgsuEKWtJ9oarc1ZNbmQOlGxnHdRQW', 'user'),
            ('Bruce Banner', '$2a$10$05bCV5hR.KQgmK8hvJIFD.zYAG6nvQ1TlmiiIKYkk47QXgsnf7lXi', 'user'),
            ('Natasha Romanov', '$2a$10$QBsl3d2xEdT5M4Y3kA.KbO0Or0Hi4tTLUKOe4tBfphlNK0XbTn6YW', 'user'),
            ('Thor Odinson', '$2a$10$SQ4F12P56QVFNzi2WGzZveEoAsJP20rIyC3c/8IhrmKgHpTYtYgoO', 'user');

        -- Insert sample loans
        INSERT INTO loans (user_id, amount, type, status) VALUES 
            (1, 50000.00, 'Business', 'approved'),
            (2, 1200.00, 'Education', 'pending'), 
            (3, 800.00, 'Personal', 'rejected'), 
            (4, 2500.00, 'Home', 'rejected'), 
            (5, 750.00, 'Personal', 'pending');
        """;

    //UNHASHED: PASSWORDS ironman, spiderman, hulk, blackwidow, thor

    /*
        ('Tony Stark', 'HASHED_ir0nm4n', 'manager'),
        ('Peter Parker', 'HASHED_spiderman', 'user'),
         ('Bruce Banner', 'HASHED_hulk', 'user'),
            ('Natasha Romanoff', 'HASHED_blackwidow', 'user'),
            ('Thor Odinson', 'HASHED_thor', 'user')  
     */
 
    private static void runSql(String sql, String jdbcUrl, String dbUser, String dbPassword) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword); Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Executed SQL:\n" + sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Reset the DB to a clean state each time
    public void resetDatabase(String jdbcUrl, String dbUser, String dbPassword) {
        runSql(DROP_TABLES_SQL, jdbcUrl, dbUser, dbPassword);
        runSql(CREATE_TABLES_SQL, jdbcUrl, dbUser, dbPassword);
        runSql(INSERT_DATA_SQL, jdbcUrl, dbUser, dbPassword);
    }
}
