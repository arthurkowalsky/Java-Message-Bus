package com.kov.messagebus.exceptions;

public class NoHandlerFoundException extends RuntimeException {

    public NoHandlerFoundException(String message) {
        super(message);
    }
}