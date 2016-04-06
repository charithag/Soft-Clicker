package org.softclicker.server.starup;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.config.ServerConfigManager;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.User;
import org.softclicker.server.manage.UserManager;

public class Bootstrap {

    private final static Logger log = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        ScopingDataSource scopingDataSource = new ScopingDataSource(new ServerConfigManager());
        DatabaseCreator creator = new DatabaseCreator(scopingDataSource);
        creator.createDbStructureIfNotExists("SELECT * FROM USER");
        UserManager userManager = new UserManager(scopingDataSource);
        for(User user : userManager.getAllUsers()){
            log.info(user);
        }
        log.info("SoftClicker Server Started!");
    }
}
