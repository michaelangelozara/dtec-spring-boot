package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserInfoResponseDto;

import java.time.LocalDate;
import java.util.List;

public record ClearanceResponseDto(
        int id,
        String schoolYear,
        List<ClearanceSignoff> clearanceSignoffs,
        LocalDate createdAt,
        LocalDate lastModified,
        UserInfoResponseDto student
) {
}
