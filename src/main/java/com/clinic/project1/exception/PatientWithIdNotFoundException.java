package com.clinic.project1.exception;

public class PatientWithIdNotFoundException extends RuntimeException {

    public PatientWithIdNotFoundException(String message) {
        super(message);
    }

    public PatientWithIdNotFoundException(){
    }
}
