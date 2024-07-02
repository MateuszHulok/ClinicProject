package com.clinic.project1.exception;

public class DoctorWithIdNotFoundException extends RuntimeException {

    public DoctorWithIdNotFoundException(String message) {
        super(message);
    }

    public DoctorWithIdNotFoundException(){
    }
}
