package com.clinic.project1.exception;

public class DateInThePastExcpetion extends RuntimeException {
     public DateInThePastExcpetion(String message) {
         super(message);
     }

     public DateInThePastExcpetion(){
     }
}
