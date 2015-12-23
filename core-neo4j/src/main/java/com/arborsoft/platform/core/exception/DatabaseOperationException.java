package com.arborsoft.platform.core.exception;

public class DatabaseOperationException extends Exception {
    public DatabaseOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
