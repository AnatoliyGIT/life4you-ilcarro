package com.telran.ilcarro.exception;

public class RequestArgumentsException extends RuntimeException {
    public RequestArgumentsException(String message) {
        super(message);
    }

    public RequestArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
