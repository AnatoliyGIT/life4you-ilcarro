package com.telran.ilcarro.exception;

public class CarDetailsException extends RuntimeException {
    public CarDetailsException(String message) {
        super(message);
    }
    public CarDetailsException(String message, Throwable cause) {
        super(message, cause);
    }
}
