package com.revature.loandb.service;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.revature.loandb.dao.UserDao;
import com.revature.loandb.model.User;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public boolean registerUser(String username, String rawPassword, String role) {
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        if (userDao.getUserByUsername(username) != null) {
            return false;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(hashed);
        newUser.setRole(role);
        userDao.createUser(newUser);
        return true;
    }

    public boolean loginUser(String username, String rawPassword) {
        User existingUser = userDao.getUserByUsername(username);
        if (existingUser == null) {
            return false;
        }

        return BCrypt.checkpw(rawPassword, existingUser.getPasswordHash());
    }

    public List<User> getUsers() {
        return userDao.getAllUsers();
    }

    public boolean isManager(String username) {
        User user = userDao.getUserByUsername(username);
        return user != null && "manager".equals(user.getRole());
    }

    public boolean updateUser(int userId, String username, String password) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            return false;
        }

        if (!username.equals(user.getUsername()) && userDao.getUserByUsername(username) != null) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);
        userDao.updateUser(user);
        return true;
    }

    public boolean updateUserRole(int userId, String role) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            return false;
        }

        user.setRole(role);
        userDao.updateUser(user);
        return true;
    }
}
