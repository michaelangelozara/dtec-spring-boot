package com.DTEC.Document_Tracking_and_E_Clearance.course;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddCourseRequestDto(
        String name,
        @JsonProperty("department_id") int departmentId,
        @JsonProperty("department_club_id") int departmentClubId
) {
}
