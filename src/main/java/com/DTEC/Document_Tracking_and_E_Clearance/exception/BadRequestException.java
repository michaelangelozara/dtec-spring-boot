package com.DTEC.Document_Tracking_and_E_Clearance.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
}
