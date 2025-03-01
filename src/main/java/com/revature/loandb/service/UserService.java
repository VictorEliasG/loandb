package com.revature.loandb.service;

import java.util.List;
import com.revature.loandb.dao.UserDao;
import com.revature.loandb.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
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

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }
}