package com.DTEC.Document_Tracking_and_E_Clearance.misc;

import java.text.DecimalFormat;

public class CodeGenerator {
    private static final DecimalFormat formatter = new DecimalFormat("0000"); // Pads numbers to 3 digits

    public static String generateCode(String prefix, int count){
        return prefix + formatter.format(count);
    }
}
