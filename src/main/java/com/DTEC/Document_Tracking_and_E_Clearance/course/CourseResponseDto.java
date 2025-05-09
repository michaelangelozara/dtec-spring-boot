package com.DTEC.Document_Tracking_and_E_Clearance.course;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record CourseResponseDto(
        int id,
        String name,
        @JsonProperty("created_at") LocalDate createdAt,
        @JsonProperty("last_modified") LocalDate lastModified,
        @JsonProperty("department_id") int departmentId,
        @JsonProperty("short_name") String shortName
) {
}
