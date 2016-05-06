package org.softclicker.server.manage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.dao.impl.UserDAO;
import org.softclicker.server.entity.User;
import org.softclicker.server.exception.SoftClickerException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static final Logger log = LogManager.getLogger(UserManager.class);
    private final ScopingDataSource scopingDataSource;
    private final UserDAO userDAO;

    public UserManager(ScopingDataSource scopingDataSource) {
        this.scopingDataSource = scopingDataSource;
        this.userDAO = new UserDAO(scopingDataSource);
    }

    public List<User> getAllUsers() throws SoftClickerException {
        try {
            scopingDataSource.beginConnectionScope();
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            throw new SoftClickerException("Error while retrieving users list", e);
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public User getUserById(int userId) throws SoftClickerException {
        try {
            scopingDataSource.beginConnectionScope();
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            throw new SoftClickerException("Error occurred while retrieving user with user id: `" + userId + "`", e);
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }
}
