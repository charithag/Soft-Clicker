package org.softclicker.server.manage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.softclicker.server.config.ServerConfigManager;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.database.EmbeddedServer;
import org.softclicker.server.entity.User;
import org.softclicker.server.starup.DatabaseCreator;

import java.io.File;
import java.sql.SQLException;

public class UserManagerTest {

    private final static Logger log = LogManager.getLogger(UserManagerTest.class);
    private ScopingDataSource scopingDataSource;

    @Before
    public void initialize() throws SQLException {
        String sep = File.separator;
        System.setProperty("SOFTCLICKER_HOME",
                System.getProperty("user.dir") + sep + "target" + sep + "classes" + sep);
        EmbeddedServer.startDB();
        scopingDataSource = new ScopingDataSource(new ServerConfigManager());
        DatabaseCreator creator = new DatabaseCreator(scopingDataSource);
        creator.createDbStructureIfNotExists("SELECT * FROM USER");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        UserManager userManager = new UserManager(scopingDataSource);
        Assert.assertNotEquals((long)userManager.getAllUsers().size(), 0L);
        for(User user : userManager.getAllUsers()){
            log.info(user);
        }
    }
}