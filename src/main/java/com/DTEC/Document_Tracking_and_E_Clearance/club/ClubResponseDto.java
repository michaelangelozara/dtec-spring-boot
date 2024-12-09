package com.DTEC.Document_Tracking_and_E_Clearance.club;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record ClubResponseDto(
        int id,
        String name,
        String logo,
        @JsonProperty("created_at") LocalDate createdAt,
        @JsonProperty("last_modified") LocalDate lastModified
) {
}
