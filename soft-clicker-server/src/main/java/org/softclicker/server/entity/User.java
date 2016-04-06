package org.softclicker.server.entity;

import java.util.List;

/**
 * This class acts as the User entity and intends to use as an immutable object.
 * You can create new {@link User} only by passing a parameters through the constructor.
 */
public class User {
    final private int userId;
    final private String firstName;
    final private String lastName;
    final private String userName;
    final private char[] password;
    final private List<Role> roles;

    public User(int userId, String firstName, String lastName, String userName, char[] password, List<Role> roles) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.roles = roles;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public char[] getPassword() {
        return password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public enum Role {
        ADMIN, TEACHER, STUDENT
    }

    @Override
    public String toString() {
        return "User ID:" + userId + "\tName:" + getFullName() + "\tUser Name:" + userName + "\tRoles:" + roles.toString();
    }
}
