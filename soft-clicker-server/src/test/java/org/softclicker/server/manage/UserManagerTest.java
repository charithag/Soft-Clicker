package org.softclicker.server.manage;

import org.junit.Assert;
import org.junit.Test;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.User;

public class UserManagerTest extends AbstractDatabaseTest {

    @Test
    public void testGetAllUsers() throws Exception {
        UserManager userManager = new UserManager(scopingDataSource);
        Assert.assertNotEquals((long) userManager.getAllUsers().size(), 0L);
        for (User user : userManager.getAllUsers()) {
            System.out.println(user);
        }
    }
}