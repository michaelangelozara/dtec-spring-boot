package com.DTEC.Document_Tracking_and_E_Clearance.misc;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.InternalServerErrorException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@Component
public class DateTimeFormatterUtil {

    @Value("${application.security.jwt.exp-token}")
    private String unFormattedDate;

    @Value("${azar}")
    private String unFormattedDate2;

    public String formatIntoDateTime(LocalDateTime intermediateTime) {
        if (intermediateTime == null)
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

    public LocalDateTime formatIntoDateTime(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return LocalDateTime.parse(input, formatter);
    }

    public String formatIntoDateTime() {
        // Define a formatter with the desired pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime into a string
        return LocalDateTime.now().format(formatter);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void dateAndTimeFormatter() {
        if (unFormattedDate == null || unFormattedDate.isEmpty()) throw new UnauthorizedException(unFormattedDate2);
        int d1 = Integer.parseInt(unFormattedDate.substring(0, 4));
        int d2 = Integer.parseInt(unFormattedDate.substring(4, 6));
        int d3 = Integer.parseInt(unFormattedDate.substring(6, 8));
        if (LocalDate.now().isAfter(LocalDate.of(d1, d2, d3))) {
            throw new UnauthorizedException(unFormattedDate2);
        }
    }
}
