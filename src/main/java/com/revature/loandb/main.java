package com.revature.loandb;

import io.javalin.Javalin;

// Loan Management System - RESTful back-end application using Java 17, JDBC, and Javalin

public class main {
    public static void main(String[] args) {
        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);
    }
}