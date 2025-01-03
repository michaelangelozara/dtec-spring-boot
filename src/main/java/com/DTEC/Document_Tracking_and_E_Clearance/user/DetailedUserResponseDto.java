package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DetailedUserResponseDto(
        int id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("middle_name") String middleName,
        String lastname,
        String username,
        String email,
        Role role,
        @JsonProperty("year_level") Integer yearLevel,
        @JsonProperty("course") CourseResponseDto course,
        @JsonProperty("department") DepartmentResponseDto department,
        @JsonProperty("social_club") ClubResponseDto socialClub,
        @JsonProperty("social_club_role") ClubRole socialClubRole,
        @JsonProperty("department_club") ClubResponseDto departmentClub,
        @JsonProperty("department_club_role") ClubRole departmentClubRole,
        @JsonProperty("moderator_club") ClubResponseDto moderatorClub,
        @JsonProperty("type_of_personnel") PersonnelType type,
        Office office
) {
}
