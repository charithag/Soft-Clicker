package org.softclicker.server.dao.exception;

public class TransactionManagementException extends Exception {

    private static final long serialVersionUID = -3151279321929070297L;

    private String errorMessage;

    public TransactionManagementException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
        setErrorMessage(msg);
    }

    public TransactionManagementException(String message, Throwable cause) {
        super(message, cause);
        setErrorMessage(message);
    }

    public TransactionManagementException(String msg) {
        super(msg);
        setErrorMessage(msg);
    }

    public TransactionManagementException() {
        super();
    }

    public TransactionManagementException(Throwable cause) {
        super(cause);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}