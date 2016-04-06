package org.softclicker.server.dao.impl;

import org.apache.log4j.Logger;
import org.softclicker.server.dao.DAOUtil;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends AbstractGenericDAO<User> {

    private final static String TABLE_NAME = "USERS";
    private final static Logger log = Logger.getLogger(UserDAO.class);

    public UserDAO(ScopingDataSource scopingDataSource) {
        super(scopingDataSource, TABLE_NAME);
    }

    @Override
    public int count() throws SQLException {
        return 0;
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM `USER`";
        List<User> users = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
        ) {
            while (rs.next()) {
                User user = DAOUtil.loadUser(rs);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            log.error("Error occurred while retrieving users", e);
            return null;
        }
    }
}