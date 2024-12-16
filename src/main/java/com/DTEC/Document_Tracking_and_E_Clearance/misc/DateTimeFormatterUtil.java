package com.DTEC.Document_Tracking_and_E_Clearance.misc;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.InternalServerErrorException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@Component
public class DateTimeFormatterUtil {

    public String formatIntoDateTime(LocalDateTime intermediateTime) {
        if(intermediateTime == null)
            return "N/A";

        final String output;
        String dateTimeString = intermediateTime.toString();
        try {
            // Create a DateTimeFormatter that can handle variable fractional seconds
            DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart()
                    .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true) // Handle up to 9 digits
                    .optionalEnd()
                    .toFormatter();

            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");

            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, inputFormatter);
            output = dateTime.format(outputFormat);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }

        return output;
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
