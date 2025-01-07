package com.DTEC.Document_Tracking_and_E_Clearance.misc;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SchoolYearGenerator {

    public String generateSchoolYear() {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        String semester = (currentMonth >= 1 && currentMonth <= 6) ? "2nd" : "1st";
        return semester + " SEM S.Y " + currentYear + "-" + (currentYear + 1);
    }
}
