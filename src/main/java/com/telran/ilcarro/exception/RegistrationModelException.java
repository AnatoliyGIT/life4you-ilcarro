package com.telran.ilcarro.exception;

public class RegistrationModelException extends RuntimeException {
    public RegistrationModelException(String message) {
        super(message);
    }

    public RegistrationModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
