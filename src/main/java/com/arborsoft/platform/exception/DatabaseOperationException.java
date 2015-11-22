package com.arborsoft.platform.exception;

public class DatabaseOperationException extends Exception {
    public DatabaseOperationException() {
    }

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
