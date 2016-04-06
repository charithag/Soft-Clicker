package org.softclicker.server.exception;

/**
 * This runtime exception will be thrown if the server has configured with unsupported DB engine.
 */
public class UnsupportedDatabaseEngineException extends RuntimeException {

    private static final long serialVersionUID = -3151279311929070297L;

    public UnsupportedDatabaseEngineException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
    }

    public UnsupportedDatabaseEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedDatabaseEngineException(String msg) {
        super(msg);
    }

    public UnsupportedDatabaseEngineException() {
        super();
    }

    public UnsupportedDatabaseEngineException(Throwable cause) {
        super(cause);
    }

}
