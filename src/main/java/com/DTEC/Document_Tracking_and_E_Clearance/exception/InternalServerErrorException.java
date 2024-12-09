package com.DTEC.Document_Tracking_and_E_Clearance.exception;

public class InternalServerErrorException extends RuntimeException{
    public InternalServerErrorException(String message){
        super(message);
    }
}
