package org.softclicker.server.dao;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DAOUtil {

    private static final Logger log = LogManager.getLogger(DAOUtil.class);

    public static User loadUser(ResultSet rs) throws SQLException {
        String firstName = rs.getString("FIRST_NAME");
        String lastName = rs.getString("LAST_NAME");
        String userName = rs.getString("USER_NAME");
        char[] password = rs.getString("PASSWORD").toCharArray();
        String[] rolesArr = rs.getString("ROLES").split(",");
        List<User.Role> roles = new ArrayList<>();
        for (String role : rolesArr) {
            try {
                roles.add(User.Role.valueOf(role));
            } catch (IllegalArgumentException e) {
                log.error("Invalid role with the name of `" + role + "`");
            }
        }
        return new User(firstName, lastName, userName, password, roles);
    }
}
