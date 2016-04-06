package org.softclicker.server.exception;

public class SoftClickerRuntimeException extends RuntimeException {

    public SoftClickerRuntimeException(String message) {
        super(message);
    }

    public SoftClickerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

