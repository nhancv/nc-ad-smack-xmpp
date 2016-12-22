package com.nhancv.xmpp.model;

/**
 * Created by nhancao on 12/22/16.
 */

public class BaseError {

    private boolean error;
    private String message;
    private Exception exception;

    public BaseError() {
    }

    public BaseError(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public BaseError(boolean error, String message, Exception exception) {
        this.error = error;
        this.message = message;
        this.exception = exception;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }
}
