package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import java.time.LocalDate;

public class SchoolYear {

    public static String generateSchoolYear(){
        int currentYear = LocalDate.now().getYear();
        return "S.Y " + currentYear + "-" + (currentYear + 1);
    }
}
