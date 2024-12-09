package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance_signoff.TypeOfSign;

public record SignClearanceRequestDto(
        int userId,
        int clearanceId,
        byte[] signature,
        TypeOfSign type
) {
}
