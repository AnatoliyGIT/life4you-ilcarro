package com.telran.ilcarro.exception;

public class CarAlreadyExistException extends RuntimeException {
    public CarAlreadyExistException(String message) {
        super(message);
    }

    public CarAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
