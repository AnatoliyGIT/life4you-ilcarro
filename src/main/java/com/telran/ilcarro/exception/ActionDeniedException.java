package com.telran.ilcarro.exception;

public class ActionDeniedException extends RuntimeException {
    public ActionDeniedException(String message) {
        super(message);
    }

    public ActionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
