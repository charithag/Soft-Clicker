package org.softclicker.server.dao.impl;

import org.softclicker.server.dao.ScopingDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractGenericDAO<T> {

    //Protected
    protected final String tableName;
    protected ScopingDataSource scopingDataSource;
    protected AbstractGenericDAO(ScopingDataSource scopingDataSource, String tableName) {
        this.tableName = tableName;
        this.scopingDataSource = scopingDataSource;
    }

    public abstract int count() throws SQLException;

    protected Connection getConnection() throws SQLException {
        return scopingDataSource.getConnection();
    }

}