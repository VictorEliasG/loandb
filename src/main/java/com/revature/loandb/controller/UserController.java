package com.revature.loandb.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.revature.loandb.dto.UserAuthRequestDTO;
import com.revature.loandb.model.User;
import com.revature.loandb.service.UserService;

import io.javalin.http.Context;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /users Returns JSON of all users only if the user is a manager
     */
    public void getUsers(Context ctx) {
        String sessionUser = ctx.sessionAttribute("user");

        if (sessionUser == null || !userService.isManager(sessionUser)) {
            ctx.status(403).json("{\"error\":\"Forbidden\"}");
        } else {
            List<User> users = userService.getUsers();
            System.out.println("Users: " + users);
            ctx.json(users);
        }

    }

    /**
     * GET /users/{id} Returns JSON of a user by id only if the user is a
     * manager or the user is viewing their own profile
     */
    public void getUserById(Context ctx) {
        int userId;
        try {
            userId = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"error\":\"Invalid user ID format\"}");
            return;
        }

        String sessionUser = ctx.sessionAttribute("user");

        User user = userService.getUserById(userId);

        if (user == null) {
            ctx.status(404).json("{\"error\":\"User not found\"}");

        } else {
            ctx.json(user);
        }

        try {

            System.out.println("User currently logged in " + sessionUser);
            System.out.println("Are you a manager? " + userService.isManager(sessionUser));
            System.out.println("Are you requesting your own profile? " + sessionUser.equals(user.getUsername()));

            if (sessionUser.equals(user.getUsername()) || userService.isManager(sessionUser)) {
                ctx.json(user);
            } else {
                ctx.status(403).json("{\"error\":\"Forbidden\"}");
            }
        } catch (Exception e) {
            ctx.status(403).json("{\"error\":\"Invalid user\"}");

        }

    }

    /**
     * Handles POST /register with a JSON body: { "username": "someName",
     * "password": "somePass" }
     */
    public void register(Context ctx) {
        // Parsed request JSON into our DTO
        UserAuthRequestDTO req = ctx.bodyAsClass(UserAuthRequestDTO.class);

        if (req.getUsername() == null || req.getPassword() == null) {
            ctx.status(400).json("{\"error\":\"Missing username or password\"}");
            return;
        }

        // Defined allowed roles
        Set<String> allowedRoles = Set.of("user", "manager");

        // Set default role if not provided
        if (req.getRole() == null) {
            req.setRole("user");
        }

        // Validate the role
        if (!allowedRoles.contains(req.getRole())) {
            ctx.status(400).json("{\"error\":\"Invalid role provided\"}");
            return;
        }

        boolean success = userService.registerUser(req.getUsername(), req.getPassword(), req.getRole());

        if (success) {
            ctx.status(201).json("{\"message\":\"User registered successfully\"}");
        } else {
            ctx.status(409).json("{\"error\":\"Username already exists\"}");
        }
    }

    /**
     * Handles POST /login with a JSON body: { "username": "someName",
     * "password": "somePass" }
     */
    public void login(Context ctx) {
        UserAuthRequestDTO user = ctx.bodyAsClass(UserAuthRequestDTO.class);

        if (user.getUsername() == null || user.getPassword() == null) {
            ctx.status(400).json("{\"error\":\"Missing username or password\"}");
            return;
        }

        boolean success = userService.loginUser(user.getUsername(), user.getPassword());
        if (success) {
            ctx.sessionAttribute("user", user.getUsername());
            ctx.status(200).json("{\"message\":\"Login successful\"}");

        } else {
            ctx.status(401).json("{\"error\":\"Invalid credentials\"}");
        }
    }

    public void logout(Context ctx) {
        // get user name from session
        String sessionUser = ctx.sessionAttribute("user");
        System.out.println("User logged out "+sessionUser);
        // add username in map

        ctx.req().getSession().invalidate();
        ctx.json(Map.of("username",sessionUser,"message", "Logout successful")).status(200);

    }

    public void updateUser(Context ctx) {
        int userId;
        try {
            userId = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"error\":\"Invalid user ID format\"}");
            return;
        }

        String sessionUser = ctx.sessionAttribute("user");
        User user = userService.getUserById(userId);

        if (user == null) {
            ctx.status(404).json("{\"error\":\"User not found\"}");
            return;
        }

        if (sessionUser == null || (!sessionUser.equals(user.getUsername()) && !userService.isManager(sessionUser))) {
            ctx.status(403).json("{\"error\":\"Forbidden\"}");
            return;
        }

        UserAuthRequestDTO req = ctx.bodyAsClass(UserAuthRequestDTO.class);

        if (req.getUsername() == null || req.getPassword() == null) {
            ctx.status(400).json("{\"error\":\"Missing username or password\"}");
            return;
        }

        boolean success = userService.updateUser(userId, req.getUsername(), req.getPassword());

        if (success) {
            ctx.status(200).json("{\"message\":\"User updated successfully\"}");
        } else {
            ctx.status(409).json("{\"error\":\"Username already exists\"}");
        }
    }

}
