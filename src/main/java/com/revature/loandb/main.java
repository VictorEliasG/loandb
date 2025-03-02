package com.revature.loandb;

import com.revature.loandb.config.DatabaseConfig;
import com.revature.loandb.config.GenerateDatabase;
import com.revature.loandb.controller.LoanController;
import com.revature.loandb.controller.UserController;
import com.revature.loandb.dao.LoanDao;
import com.revature.loandb.dao.UserDao;
import com.revature.loandb.service.LoanService;
import com.revature.loandb.service.UserService;

import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World");

        // Database credentials
        String jdbcUrl = "jdbc:postgresql://localhost:5432/loandb";
        String dbUser = "postgres";
        String dbPassword = "123456";

        DatabaseConfig dbConfig = new DatabaseConfig(jdbcUrl, dbUser, dbPassword);
        GenerateDatabase dbInit = new GenerateDatabase();

        dbConfig.jdbcConnection();
        // Initialize DB
        dbInit.resetDatabase(jdbcUrl, dbUser, dbPassword);

        //Create DAOs, Services, Controllers
        // ------ USER ------
        UserDao userDao = new UserDao(jdbcUrl, dbUser, dbPassword);
        UserService userService = new UserService(userDao);
        UserController userController = new UserController(userService);
        // ------ LOAN ------
        LoanDao loanDao = new LoanDao(jdbcUrl, dbUser, dbPassword);
        LoanService loanService = new LoanService(loanDao);
        LoanController loanController = new LoanController(loanService);
        
        

        
        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);

        // Define routes using the new {param} syntax
        // ------ AUTH ROUTES ------
        //  POST
        app.post("/auth/register", userController::register);
        app.post("/auth/login", userController::login);
        app.post("/auth/logout", userController::logout);

        // ------ USER ROUTES ------
        //  GET
        app.get("/users", userController::getUsers);
        app.get("/user/{id}", userController::getUserById);
        
        //  PUT
        app.put("/user/{id}", userController::updateUser);

        // ------ LOAN ROUTES ------
        // GET
        app.get("/loans", loanController::getLoans);
        app.get("/loan/{id}", loanController::getLoan);
        //  POST
        app.post("/loans", loanController::createLoan);
        // PUT
        app.put("/loan/{id}/approve", loanController::updateLoan);
        //app.put("/loan/{id}/reject", loanController::updateLoan);
    }
}