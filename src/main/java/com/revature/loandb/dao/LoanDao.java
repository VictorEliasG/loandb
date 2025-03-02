package com.revature.loandb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.revature.loandb.model.Loan;

public class LoanDao {
private final String url;
    private final String dbUser;
    private final String dbPassword;

    public LoanDao(String jdbcUrl, String dbUser, String dbPassword) {
        this.url = jdbcUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

    public boolean createLoan(Loan loan) {
        String sql = "INSERT INTO loans (user_id, amount, type, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loan.getUserId());
            stmt.setInt(2, loan.getAmount());
            stmt.setString(3, loan.getType());
            stmt.setString(4, loan.getStatus());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging instead.
            return false;
        }
    }
    
}
