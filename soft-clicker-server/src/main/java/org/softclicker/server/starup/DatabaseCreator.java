package org.softclicker.server.starup;

import org.apache.log4j.Logger;
import org.softclicker.server.config.ConfigManager;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.dao.exception.TransactionManagementException;
import org.softclicker.server.exception.SoftClickerRuntimeException;
import org.softclicker.server.exception.UnsupportedDatabaseEngineException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Usage:
 * {@code
 * scopingDataSource.beginConnectionScope();
 * creator = new DatabaseCreator(scopingDataSource);
 * creator.createDbStructureIfNotExists();
 * scopingDataSource.endConnectionScope();
 * }
 */
public class DatabaseCreator {

    private static final Logger log = Logger.getLogger(DatabaseCreator.class);
    private final ScopingDataSource scopingDataSource;
    private static final Object LOCK = new Object();

    public DatabaseCreator(ScopingDataSource scopingDataSource) {
        this.scopingDataSource = scopingDataSource;
    }

    public boolean createDbStructureIfNotExists() {
        synchronized (LOCK) {
            try {
                scopingDataSource.beginTransactionScope();
                if (isDbStructureCreated("SELECT * FROM USER")) {
                    return false;
                }
                Path dbScript = getDbScript();
                executeSQLScript(dbScript);
                return true;
            } catch (TransactionManagementException | SQLException | IOException e) {
                throw new SoftClickerRuntimeException("Error occurred while creating initial database!", e);
            } finally {
                scopingDataSource.endTransactionScope();
            }
        }
    }

    private Path getDbScript() {
        String sep = File.separator;
        String dbScriptsBaseDir = ConfigManager.SOFTCLICKER_CONFIG_DIR + sep + "dbscripts" + sep;
        String dbProductName = getCurrentDatabaseEngineName();
        String dbScriptName = "";

        switch (dbProductName) {
        case "H2":
            dbScriptName = "h2.sql";
            break;
        case "MySQL":
            dbScriptName = "mysql.sql";
            break;
        default:
            throw new UnsupportedDatabaseEngineException(dbProductName + " is not supported!");
        }

        return Paths.get(dbScriptsBaseDir + dbScriptName);
    }

    private boolean isDbStructureCreated(String checkSQL) {
        try (
                Connection conn = scopingDataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(checkSQL)) {
            rs.next();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private String getCurrentDatabaseEngineName() {
        try {
            return scopingDataSource.getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            throw new UnsupportedDatabaseEngineException("Error occurred while retrieving DB Product Name!",
                    e);
        }
    }

    private int executeSQLScript(Path sqlScript) throws IOException, SQLException {
        try (
                Connection conn = scopingDataSource.getConnection();
                Statement stmt = conn.createStatement();
        ) {
            Iterator<String> iterator = Files.readAllLines(sqlScript, StandardCharsets.UTF_8).stream().iterator();
            int rowCount = 0;
            while (iterator.hasNext()){
                String sql = iterator.next();
                rowCount += stmt.executeUpdate(sql);
            }
            conn.commit();
            return rowCount;
        }
    }
}
