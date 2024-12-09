package com.DTEC.Document_Tracking_and_E_Clearance.department;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record DepartmentResponseDto(
        int id,
        String name,
        String logo,
        @JsonProperty("created_at") LocalDate createdAt,
        @JsonProperty("last_modified") LocalDate lastModified
) {
}
