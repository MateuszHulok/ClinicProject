package com.clinic.project1.exception;

public class AppointmentWithIdNotFoundException extends RuntimeException {
     public AppointmentWithIdNotFoundException(String message) {
         super(message);
     }

     public AppointmentWithIdNotFoundException(){

     }
}
