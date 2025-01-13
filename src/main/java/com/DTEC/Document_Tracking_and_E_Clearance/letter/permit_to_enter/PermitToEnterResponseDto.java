package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record PermitToEnterResponseDto(
        int id,
        String activity,
        LocalDate date,
        @JsonProperty("time_from") String timeFrom,
        @JsonProperty("time_to") String timeTo,
        String participants
) {
}
