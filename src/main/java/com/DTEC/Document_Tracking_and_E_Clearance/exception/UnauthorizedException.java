package com.DTEC.Document_Tracking_and_E_Clearance.exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message){
        super(message);
    }
}
