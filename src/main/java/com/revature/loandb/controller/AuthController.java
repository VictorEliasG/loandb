package com.revature.loandb.controller;

import java.util.Map;

import com.revature.loandb.model.User;
import com.revature.loandb.service.AuthService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class AuthController {

    private final AuthService authService;
    private final Javalin app;

    public AuthController(Javalin app) {
        this.app = app;
        this.authService = new AuthService(); // Assume proper DAO initialization elsewhere
    }

    public void registerRoutes() {
        app.post("/auth/register", this::registerUser);
        app.post("/auth/login", this::loginUser);
        app.post("/auth/logout", this::logoutUser);
    }

    private void registerUser(Context ctx) {
        try {
            RegistrationRequest request = ctx.bodyValidator(RegistrationRequest.class)
                    .check(req -> req.username != null && !req.username.isBlank(), "Username required")
                    .check(req -> req.password != null && !req.password.isBlank(), "Password required")
                    .get();

            User newUser = authService.register(
                    request.username,
                    request.password
            );

            ctx.status(HttpStatus.CREATED)
                    .json(Map.of(
                            "id", newUser.getId(),
                            "username", newUser.getUsername(),
                            "role", newUser.getRole()
                    ));

        } catch (Exception e) {//} catch (ValidationException e) {

            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", e.getMessage()));
        }
    }

    private void loginUser(Context ctx) {
        try {
            LoginRequest request = ctx.bodyValidator(LoginRequest.class)
                    .check(req -> req.username != null && !req.username.isBlank(), "Username required")
                    .check(req -> req.password != null && !req.password.isBlank(), "Password required")
                    .get();

            User authenticatedUser = authService.login(request.username, request.password);

            // Set session attributes
            ctx.sessionAttribute("user_id", authenticatedUser.getId());
            ctx.sessionAttribute("user_role", authenticatedUser.getRole());

            ctx.json(Map.of(
                    "message", "Login successful",
                    "user", Map.of(
                            "id", authenticatedUser.getId(),
                            "username", authenticatedUser.getUsername(),
                            "role", authenticatedUser.getRole()
                    )
            ));

        } catch (Exception e) {
            //ValidationException
            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(Map.of("error", "Invalid credentials"));
        }
    }

    private void logoutUser(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.status(HttpStatus.NO_CONTENT);
    }

    // Request DTOs
    private static class RegistrationRequest {

        public String username;
        public String password;
    }

    private static class LoginRequest {

        public String username;
        public String password;
    }
}
