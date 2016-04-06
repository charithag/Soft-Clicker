package org.softclicker.server.dao;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class DAOUtil {

    private static final Logger log = LogManager.getLogger(DAOUtil.class);

    public static User loadUser(ResultSet rs, String columnPrefix) throws SQLException {
        int userId = rs.getInt(columnPrefix + "USER_ID");
        String firstName = rs.getString(columnPrefix + "FIRST_NAME");
        String lastName = rs.getString(columnPrefix + "LAST_NAME");
        String userName = rs.getString(columnPrefix + "USER_NAME");
        char[] password = rs.getString(columnPrefix + "PASSWORD").toCharArray();
        String[] rolesArr = rs.getString(columnPrefix + "ROLES").split(",");
        List<User.Role> roles = new ArrayList<>();
        for (String role : rolesArr) {
            try {
                roles.add(User.Role.valueOf(role));
            } catch (IllegalArgumentException e) {
                log.error("Invalid role with the name of `" + role + "`");
            }
        }
        return new User(userId, firstName, lastName, userName, password, roles);
    }

    public static User loadUser(ResultSet rs) throws SQLException {
        return loadUser(rs, "");
    }

    public static Question loadQuestion(ResultSet rs, User owner, String columnPrefix) throws SQLException {
        int questionId =  rs.getInt(columnPrefix + "QUESTION_ID");
        String question = rs.getString(columnPrefix + "QUESTION");
        String answer = rs.getString(columnPrefix + "ANSWER");
        Date createdTime = rs.getTimestamp(columnPrefix + "CREATED_TIME");
        Date expireTime = rs.getTimestamp(columnPrefix + "EXPIRE_TIME");
        return new Question(questionId, question, answer, owner, createdTime, expireTime);
    }

    public static Question loadQuestion(ResultSet rs, User owner) throws SQLException {
        return loadQuestion(rs, owner, "");
    }

}
