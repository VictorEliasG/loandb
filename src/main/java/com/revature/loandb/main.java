package com.revature.loandb;

import com.revature.loandb.config.DatabaseConfig;
import com.revature.loandb.config.GenerateDatabase;
import com.revature.loandb.controller.UserController;
import com.revature.loandb.dao.UserDao;
import com.revature.loandb.service.UserService;

import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World");

        // Database credentials
        String jdbcUrl = "jdbc:postgresql://localhost:5432/loandb";
        String dbUser = "postgres";
        String dbPassword = "123456"; //final2kk

        DatabaseConfig dbConfig = new DatabaseConfig(jdbcUrl, dbUser, dbPassword);
        GenerateDatabase dbInit = new GenerateDatabase();

        dbConfig.jdbcConnection();
        // Initialize DB
        dbInit.resetDatabase(jdbcUrl, dbUser, dbPassword);

        //Create DAOs, Services, Controllers
        UserDao userDao = new UserDao(jdbcUrl, dbUser, dbPassword);
        UserService userService = new UserService(userDao);
        UserController userController = new UserController(userService);
        
        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);

        // Define routes using the new {param} syntax
        //  GET
        app.get("/users", userController::getUsers);
        app.get("/user/{id}", userController::getUserById);
        //  POST
        app.post("/register", userController::register);
        app.post("/login", userController::login);
        app.post("/logout", userController::logout);
        //  PUT
        app.put("/user/{id}", userController::updateUser);

    }
}