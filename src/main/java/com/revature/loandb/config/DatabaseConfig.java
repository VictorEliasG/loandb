/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.revature.loandb.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Win 10
 */
public class DatabaseConfig {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void jdbcConnection() {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println(connection.isValid(5));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
