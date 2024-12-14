package com.DTEC.Document_Tracking_and_E_Clearance.misc;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeFormatterUtil {

    public String formatIntoDateTime(LocalDateTime dateTime) {
        // Define a formatter with the desired pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime into a string
        return dateTime.format(formatter);
    }

    public LocalDateTime formatIntoDateTime(String input){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return LocalDateTime.parse(input, formatter);
    }

    public String formatIntoDateTime() {
        // Define a formatter with the desired pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime into a string
        return LocalDateTime.now().format(formatter);
    }
}
