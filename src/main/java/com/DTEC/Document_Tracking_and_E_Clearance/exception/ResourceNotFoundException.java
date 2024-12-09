package com.DTEC.Document_Tracking_and_E_Clearance.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
