package org.softclicker.server.transport;

public interface Server {
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