package org.softclicker.server.handler;

public interface ServerHandler {
    /**
     * Starts the server.
     */
    void start();

    /**
     * Stops the server.
     */
    void stop();

    /**
     * Returns true when server is running; false otherwise.
     * @return true when running false otherwise
     */
    boolean isRunning();
}