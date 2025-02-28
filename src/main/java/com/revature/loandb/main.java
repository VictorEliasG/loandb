package com.revature.loandb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class main {

    public static void jdbcConnection() {
        String url = "jdbc:postgresql://localhost:5432/loandb";
        String username = "postgres";
        String password = "123456";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println(connection.isValid(5));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {

        jdbcConnection();
        System.out.println("Hello World");

        // var app = Javalin.create(/*config*/)
        //         .get("/", ctx -> ctx.result("Hello World"))
        //         .start(7070);
    }
}