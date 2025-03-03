package com.revature.loandb.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.revature.loandb.dto.UserAuthRequestDTO;
import com.revature.loandb.main;
import com.revature.loandb.model.User;
import com.revature.loandb.service.UserService;

import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController {

    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(main.class);
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /users Returns JSON of all users only if the user is a manager
     */
    public void getUsers(Context ctx) {
        String sessionUser = ctx.sessionAttribute("user");
        String sessionRole = ctx.sessionAttribute("role");
        
        if (sessionUser == null || sessionRole == null || !sessionRole.equalsIgnoreCase("manager")) {
            ctx.status(403).json("{\"error\":\"Forbidden\"}");
            logger.warn("Forbidden");
            return;
        }

        try {
            List<User> users = userService.getUsers();
            System.out.println("Users: " + users);
            ctx.json(users);
        } catch (Exception e) {
            ctx.status(500).json("{\"error\":\"Internal server error\"}");
            logger.error("Internal server error");
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
            logger.error("Invalid user ID format");
            return;
        }

        String sessionUser = ctx.sessionAttribute("user");

        User user = userService.getUserById(userId);

        if (user == null) {
            ctx.status(404).json("{\"error\":\"User not found\"}");
            logger.info("User not found");
        } else {
            ctx.json(user);
            logger.info("User retrieved successfully");;
        }

        try {

            if (sessionUser.equals(user.getUsername()) || userService.isManager(sessionUser)) {
                ctx.json(user);
            } else {
                ctx.status(403).json("{\"error\":\"Forbidden\"}");
                logger.warn("Forbidden");
            }
        } catch (Exception e) {
            ctx.status(403).json("{\"error\":\"Invalid user\"}");
            logger.error("Invalid User");
        }

    }

    /**
     * Handles POST /register with a JSON body: { "username": "someName",
     * "password": "somePass" }
     */
    public void register(Context ctx) {
        try {
            // Parsed request JSON into our DTO
            UserAuthRequestDTO req = ctx.bodyAsClass(UserAuthRequestDTO.class);

            if (req.getUsername() == null || req.getPassword() == null) {
                ctx.status(400).json("{\"error\":\"Missing username or password\"}");
                logger.warn("Missing username or password");
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
                logger.warn("Invalid role provided");
                return;
            }

            boolean success = userService.registerUser(req.getUsername(), req.getPassword(), req.getRole());

            if (success) {
                ctx.status(201).json("{\"message\":\"User registered successfully\"}");
                logger.info("User register successfullu");
            } else {
                ctx.status(409).json("{\"error\":\"Username already exists\"}");
                logger.warn("Username already exists");
            }
        } catch (Exception e) {
            ctx.status(403).json("{\"error\":\"Invalid user format\"}");
            logger.error("Invalid user format");
        }


    }

    /**
     * Handles POST /login with a JSON body: { "username": "someName",
     * "password": "somePass" }
     */
    public void login(Context ctx) {
        try {
            UserAuthRequestDTO user = ctx.bodyAsClass(UserAuthRequestDTO.class);

            if (user.getUsername() == null || user.getPassword() == null) {
                ctx.status(400).json("{\"error\":\"Missing username or password\"}");
                logger.warn("Missing username or password");
                return;
            }

            // Check if the user is already logged in
            String sessionUser = ctx.sessionAttribute("user");
            if (sessionUser != null) {
                ctx.json(Map.of("username", sessionUser, "message", "User already logged in")).status(200);
                logger.warn("User already logged in");
                return;
            }

            boolean success = userService.loginUser(user.getUsername(), user.getPassword());
            if (success) {
                User loggedInUser = userService.getUserByUsername(user.getUsername());
                ctx.sessionAttribute("user", loggedInUser.getUsername());
                ctx.sessionAttribute("role", loggedInUser.getRole());
                ctx.sessionAttribute("userId", loggedInUser.getId()); // Set userId in session
                ctx.status(200).json("{\"message\":\"Login successful\"}");
                logger.info("Login sucessful");
            } else {
                ctx.status(401).json("{\"error\":\"Invalid credentials\"}");
                logger.warn("Invalid credencials");
            }
        } catch (Exception e) {
            ctx.status(403).json("{\"error\":\"Invalid user format\"}");
            logger.error("Invalid user format");
        }

    }

    public void logout(Context ctx) {
        // get user name from session
        String sessionUser = ctx.sessionAttribute("user");
        System.out.println("User logged out " + sessionUser);
        // invalidate session
        ctx.req().getSession().invalidate();
        ctx.json(Map.of("username", sessionUser, "message", "Logout successful")).status(200);
        logger.info("Logout successful");

    }

    public void updateUser(Context ctx) {
        int userId;
        try {
            userId = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"error\":\"Invalid user ID format\"}");
            logger.error("Invalid user ID format");
            return;
        }

        String sessionUser = ctx.sessionAttribute("user");
        User user = userService.getUserById(userId);

        if (user == null) {
            ctx.status(404).json("{\"error\":\"User not found\"}");
            logger.warn("User not found");
            return;
        }

        if (sessionUser == null || (!sessionUser.equals(user.getUsername()) && !userService.isManager(sessionUser))) {
            ctx.status(403).json("{\"error\":\"Forbidden\"}");
            logger.warn("Forbidden");
            return;
        }

        UserAuthRequestDTO req = ctx.bodyAsClass(UserAuthRequestDTO.class);

        if (req.getUsername() == null || req.getPassword() == null) {
            ctx.status(400).json("{\"error\":\"Missing username or password\"}");
            logger.warn("Missing username or password");
            return;
        }

        // Check if the username already exists
        boolean success = userService.updateUser(userId, req.getUsername(), req.getPassword());

        if (success) {
            ctx.status(200).json("{\"message\":\"User updated successfully\"}");
            logger.info("User updated successfullu");
        } else {
            ctx.status(409).json("{\"error\":\"Username already exists\"}");
            logger.warn("Username already exists");
        }
    }

}
