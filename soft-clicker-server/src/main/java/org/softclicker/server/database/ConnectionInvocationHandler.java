package org.softclicker.server.database;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * This class intends to provide a wrapper for Connection object. All Connection.close() methods are ignored.
 */
public class ConnectionInvocationHandler implements InvocationHandler {

    private final Connection connection;

    public ConnectionInvocationHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("close")){
            return null;
        }
        return method.invoke(connection, args);
    }
}
