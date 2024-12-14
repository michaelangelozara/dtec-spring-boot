package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record UserInfoResponseDto(
        int id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("middle_name") String middleName,
        String lastname,
        String username,
        Role role,
        @JsonProperty("year_level") int yearLevel,
        @JsonProperty("created_at") LocalDate createAt,
        @JsonProperty("last_modified") LocalDate lastModified,
        CourseResponseDto course,
        DepartmentResponseDto department,
        ClubResponseDto club
) {
}
