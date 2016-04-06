package org.softclicker.server.manage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.dao.impl.UserDAO;
import org.softclicker.server.entity.User;

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

    public List<User> getAllUsers() {
        try {
            scopingDataSource.beginConnectionScope();
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            log.error("Error while retrieving users list", e);
            return new ArrayList<>();
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }
}
