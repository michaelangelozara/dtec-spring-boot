package com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff;

import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ClearanceSignoffResponseDto(
        int id,
        @JsonProperty("last_modified") String dateAndTime,
        String signature,
        Role role,
        ClearanceSignOffStatus status,
        String note,
        @JsonProperty("office_in_charge") String officeInCharge
) {
}
