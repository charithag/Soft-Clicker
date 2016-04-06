package org.softclicker.server.exception;

public class SoftClickerException extends Exception {

    public SoftClickerException(String message) {
        super(message);
    }

    public SoftClickerException(String message, Throwable cause) {
        super(message, cause);
    }
}

