package org.softclicker.server.database;

import org.apache.log4j.Logger;
import org.h2.tools.Server;
import org.softclicker.server.config.DataSourceConfig;
import org.softclicker.server.exception.SoftClickerRuntimeException;

import java.sql.SQLException;

/**
 * This class intends to provided embedded data base.
 */
public class EmbeddedServer {

    private static Logger log = Logger.getLogger(EmbeddedServer.class);

    public static void startDB() {
        try {
            Server.createTcpServer("-tcpAllowOthers").start();
        } catch (SQLException e) {
            throw new SoftClickerRuntimeException("Error occurred while starting embedded database server",
                    e);
        }
    }

    public static void stopDB() {
        try {
            Server.shutdownTcpServer("tcp://localhost:9092", "", true, true);
        } catch (SQLException e) {
            throw new SoftClickerRuntimeException(
                    "Error occurred while shutting down embedded database server", e);
        }
    }

}
