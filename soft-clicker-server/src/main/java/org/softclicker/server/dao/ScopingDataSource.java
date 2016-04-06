package org.softclicker.server.dao;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.softclicker.server.config.ConfigManager;
import org.softclicker.server.config.DataSourceConfig;
import org.softclicker.server.dao.exception.IllegalTransactionStateException;
import org.softclicker.server.dao.exception.TransactionManagementException;
import org.softclicker.server.database.ConnectionInvocationHandler;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>
 * This class intends to act as primary scoping data source implementation for transactions across multiple
 * DAOs. The connection object is being shared on the thread scope.
 * </p>
 * <p>
 * Any transaction that <b>commits</b> data into the underlying data persistence mechanism MUST follow the sequence of
 * operations mentioned below.
 * </p>
 * <pre>
 * {@code
 * try {
 *      ScopingDataSource.beginTransactionScope();
 *      .....
 *      ScopingDataSource.commitTransactionScope();
 *      return success;
 * } catch (TransactionManagementException e) {
 *      ScopingDataSource.abortTransactionScope();
 *      throw new SoftClickerException("Error occurred while ...", e);
 * } finally {
 *      ScopingDataSource.endTransactionScope();
 * }
 * }
 * </pre>
 * <p>
 * Any transaction that <b>retrives</b> data into the underlying data persistence mechanism MUST follow the sequence of
 * operations mentioned below.
 * </p>
 * <pre>
 * {@code
 * try {
 *      ScopingDataSource.beginConnectionScope();
 *      .....
 * } catch (TransactionManagementException e) {
 *      throw new SoftClickerException("Error occurred while ...", e);
 * } finally {
 *      ScopingDataSource.endConnectionScope();
 * }
 * }
 * </pre>
 *
 * This pattern is inspired by following post;
 * http://tutorials.jenkov.com/java-persistence/advanced-connection-and-transaction-demarcation-and-propagation.html
 */
public class ScopingDataSource {

    private static final Logger log = Logger.getLogger(ScopingDataSource.class);
    private final DataSource dataSource;
    private final ThreadLocal<Connection> currentConnection = new ThreadLocal<Connection>();

    public ScopingDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ScopingDataSource(ConfigManager configManager) {
        this(resolveDataSource(configManager));
    }

    private static DataSource resolveDataSource(ConfigManager configManager) {
        DataSourceConfig configs = configManager.getDatasourceConfiguration();
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(configs.getDriverClassName());
        dataSource.setUrl(configs.getUrl());
        dataSource.setUsername(configs.getUserName());
        dataSource.setPassword(configs.getPassword());
        dataSource.setMaxActive(configs.getMaxActive());
        dataSource.setMaxWait(configs.getMaxWait());
        dataSource.setTestOnBorrow(configs.getTestOnBorrow());
        dataSource.setValidationQuery(configs.getValidationQuery());
        dataSource.setValidationQueryTimeout(configs.getValidationQueryTimeout());
        return dataSource;
    }

    public void beginConnectionScope() throws SQLException {
        Connection conn = currentConnection.get();
        if (conn != null) {
            throw new IllegalTransactionStateException(
                    "A transaction is already active within the context of current thread.");
        }
        conn = dataSource.getConnection();
        currentConnection.set(conn);
    }

    public void endConnectionScope() {
        closeThreadScopedConnection();
    }

    public void beginTransactionScope() throws TransactionManagementException {
        Connection conn = currentConnection.get();
        try {
            if (conn == null || conn.isClosed()) {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false);
                currentConnection.set(conn);
            } else {
                throw new IllegalTransactionStateException(
                        "A transaction is already active within the context of current thread.");
            }
        } catch (SQLException e) {
            throw new TransactionManagementException(
                    "Error occurred while retrieving config.datasource connection", e);
        }
    }

    public void commitTransactionScope() throws TransactionManagementException {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException(
                    "No connection is associated with the current transaction.");
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            log.error("Error occurred while committing the transaction", e);
        }
    }

    public void endTransactionScope() {
        closeThreadScopedConnection();
    }

    public void abortTransactionScope() throws TransactionManagementException {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException(
                    "No connection is associated with the current transaction.");
        }
        try {
            conn.rollback();
        } catch (SQLException e) {
            log.warn("Error occurred while roll-backing the transaction", e);
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = currentConnection.get();
        if (conn == null || conn.isClosed()) {
            //Let's ensure our connection is open
            this.openThreadScopedConnection();
            conn = currentConnection.get();
        }
        return getProxyConnection(conn);
    }

    private void closeThreadScopedConnection() {
        Connection conn = currentConnection.get();
        if (conn == null) {
            return;
        }
        try {
            conn.close();
            currentConnection.remove();
        } catch (SQLException e) {
            log.warn("Error occurred while close the connection");
        }
    }

    private void openThreadScopedConnection() throws SQLException {
        Connection conn = currentConnection.get();
        if (conn == null || conn.isClosed()) {
            conn = dataSource.getConnection();
            currentConnection.set(conn);
        }
    }

    @SuppressWarnings("unchecked")
    private static Connection getProxyConnection(Connection conn) {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                new Class<?>[] { Connection.class }, new ConnectionInvocationHandler(conn));
    }
}