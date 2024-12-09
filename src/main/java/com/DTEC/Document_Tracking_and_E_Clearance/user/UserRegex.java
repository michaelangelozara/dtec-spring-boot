package com.DTEC.Document_Tracking_and_E_Clearance.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegex {

    public static boolean validateStudentUsername(String username) {
        Pattern pattern = Pattern.compile("^[A-Z]-\\d{4}-\\d{4}");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public static boolean validatePersonnelUsername(String username) {
        Pattern pattern = Pattern.compile("^[A-Z]-\\d{4}-\\d{4}");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
