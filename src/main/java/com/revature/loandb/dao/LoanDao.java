package com.revature.loandb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    private Loan mapResultSetToUser(ResultSet rs) throws SQLException {
        Loan loan = new Loan(
            rs.getInt("user_id"),
            rs.getInt("amount"),
            rs.getString("type"),
            rs.getString("status")
        );
        loan.setId(rs.getInt("id"));
        return loan;
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
            e.printStackTrace(); 
            return false;
        }
    }

    public List<Loan> getLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                loans.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return loans;
    }

    public List<Loan> getLoansByUserId(int userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return loans;
    }

    public Loan getLoanById(int userId) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return null;
    }

    public boolean updateLoan(Loan loan) {
        String sql = "UPDATE loans SET amount = ?, type = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loan.getAmount());
            stmt.setString(2, loan.getType());
            stmt.setInt(3, loan.getId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

    public boolean statusLoan(Loan loan) {
        String sql = "UPDATE loans SET user_id = ?, amount = ?, type = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loan.getUserId());
            stmt.setInt(2, loan.getAmount());
            stmt.setString(3, loan.getType());
            stmt.setString(4, loan.getStatus());
            stmt.setInt(5, loan.getId());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }
    
}
