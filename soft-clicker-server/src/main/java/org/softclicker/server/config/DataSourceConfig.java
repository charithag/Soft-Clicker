package org.softclicker.server.config;

public interface DataSourceConfig {

    String getUrl();

    String getUserName();

    String getPassword();

    String getDriverClassName();

    int getMaxActive();

    long getMaxWait();

    boolean getTestOnBorrow();

    String getValidationQuery();

    int getValidationQueryTimeout();
}