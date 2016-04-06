package org.softclicker.server.config;

import org.softclicker.server.exception.SoftClickerRuntimeException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ServerConfigManager implements ConfigManager {

    private final static String SOFT_CLICKER_DB_NAME = "SoftClickerDB";

    @SuppressWarnings("unchecked")
    public DataSourceConfig getDatasourceConfiguration() {
        String dsConfigName = SOFTCLICKER_CONFIG_DIR + File.separator + "server-datasources.yaml";
        Path resource = Paths.get(dsConfigName);
        try {
            InputStream configFile = Files.newInputStream(resource);
            Map<String, Map<String, Object>> dataSources = new Yaml().loadAs(configFile, Map.class);
            Map<String, Object> softClickerDataSource = dataSources.get(SOFT_CLICKER_DB_NAME);
            final String url = (String) softClickerDataSource.get("url");
            final String userName = (String) softClickerDataSource.get("username");
            final String password = (String) softClickerDataSource.get("password");
            final String driverClassName = (String) softClickerDataSource.get("driverClassName");
            final boolean testOnBorrow = (boolean) softClickerDataSource.get("testOnBorrow");
            final int maxActive = (int) softClickerDataSource.get("maxActive");
            final int maxWait = (int) softClickerDataSource.get("maxWait");
            final String validationQuery = (String) softClickerDataSource.get("validationQuery");
            final int validationQueryTimeout = (int) softClickerDataSource.get("validationInterval");
            return new DataSourceConfig() {

                @Override
                public String getUrl() {
                    return url;
                }

                @Override
                public String getUserName() {
                    return userName;
                }

                @Override
                public String getPassword() {
                    return password;
                }

                @Override
                public String getDriverClassName() {
                    return driverClassName;
                }

                @Override
                public boolean getTestOnBorrow() {
                    return testOnBorrow;
                }

                @Override
                public int getMaxActive() {
                    return maxActive;
                }

                @Override
                public long getMaxWait() {
                    return maxWait;
                }

                @Override
                public String getValidationQuery() {
                    return validationQuery;
                }

                @Override
                public int getValidationQueryTimeout() {
                    return validationQueryTimeout;
                }
            };
        } catch (IOException e) {
            throw new SoftClickerRuntimeException(
                    "Error occurred while reading the configuration file `" + dsConfigName + "`!");
        }
    }

}