package org.softclicker.server.dao.exception;

public class IllegalTransactionStateException extends RuntimeException {

    private static final long serialVersionUID = -3151279331929070297L;

    private String errorMessage;

    public IllegalTransactionStateException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
        setErrorMessage(msg);
    }

    public IllegalTransactionStateException(String message, Throwable cause) {
        super(message, cause);
        setErrorMessage(message);
    }

    public IllegalTransactionStateException(String msg) {
        super(msg);
        setErrorMessage(msg);
    }

    public IllegalTransactionStateException() {
        super();
    }

    public IllegalTransactionStateException(Throwable cause) {
        super(cause);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
