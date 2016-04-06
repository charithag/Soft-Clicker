package org.softclicker.server.entity;

import java.util.List;

/**
 * This class acts as the User entity and intends to use as an immutable object.
 * You can create new {@link User} only by passing a parameters through the constructor.
 */
public class User {

    private final String firstName;
    private final String lastName;
    private final String userName;
    private final char[] password;
    private List<Role> roles;

    public User(String firstName, String lastName, String userName, char[] password, List<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.roles = roles;
    }

    public String getFullName() {
        return firstName + " " + lastName;
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

    public List<Role> getRoles() {
        return roles;
    }

    public char[] getPassword() {
        return password;
    }

    public enum Role {
        ADMIN, TEACHER, STUDENT
    }

    @Override
    public String toString() {
        return "Name:" + getFullName() + "\tUser Name:" + getUserName() + "\tRoles:" + getRoles().toString();
    }
}
