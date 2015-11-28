package com.arborsoft.platform.exception;

public class DatabaseOperationException extends Exception {
    public DatabaseOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
