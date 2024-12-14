package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRegisterRequestDto(
        @JsonProperty("first_name") String firstName,
        @JsonProperty("middle_name") String middleName,
        String lastname,
        String username,
        String email,
        Role role,
        @JsonProperty("year_level") int yearLevel,
        @JsonProperty("course_id") int courseId,
        @JsonProperty("department_id") int departmentId,
        @JsonProperty("social_club_id") int socialClubId,
        @JsonProperty("social_club_role") ClubRole socialClubRole,
        @JsonProperty("department_club_id") int departmentClubId,
        @JsonProperty("department_club_role") ClubRole departmentClubRole,
        @JsonProperty("moderator_club_id") int moderatorClubId
) {
}
