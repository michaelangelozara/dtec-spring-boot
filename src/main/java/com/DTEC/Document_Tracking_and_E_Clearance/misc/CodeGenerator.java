package com.DTEC.Document_Tracking_and_E_Clearance.misc;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class CodeGenerator {
    private final DecimalFormat formatter = new DecimalFormat("0000"); // Pads numbers to 3 digits

    public String generateCode(String prefix, int count){
        return prefix + formatter.format(count);
    }
}
