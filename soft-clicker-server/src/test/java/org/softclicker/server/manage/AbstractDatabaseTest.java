package org.softclicker.server.manage;

import org.junit.Before;
import org.softclicker.server.config.ServerConfigManager;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.database.EmbeddedServer;
import org.softclicker.server.starup.DatabaseCreator;

import java.io.File;
import java.sql.SQLException;

public class AbstractDatabaseTest {

    protected ScopingDataSource scopingDataSource;

    @Before
    public void initialize() throws SQLException {
        String sep = File.separator;
        System.setProperty("SOFTCLICKER_HOME",
                System.getProperty("user.dir") + sep + "target" + sep + "classes" + sep);
        EmbeddedServer.startDB();
        scopingDataSource = new ScopingDataSource(new ServerConfigManager());
        DatabaseCreator creator = new DatabaseCreator(scopingDataSource);
        creator.createDbStructureIfNotExists();
    }

}