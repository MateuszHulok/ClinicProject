package com.clinic.project1.exception;

public class InvalidSpecializationException extends RuntimeException {

    public InvalidSpecializationException(String message) {
        super(message);
    }

    public InvalidSpecializationException() {
    }
}
