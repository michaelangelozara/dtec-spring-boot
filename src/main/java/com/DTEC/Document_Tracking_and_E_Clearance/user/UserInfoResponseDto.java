package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record UserInfoResponseDto(
        int id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("middle_name") String middleName,
        String lastname,
        @JsonProperty("birth_date") LocalDate birthDate,
        String address,
        String username,
        Role role,
        @JsonProperty("created_at") LocalDate createAt,
        @JsonProperty("last_modified") LocalDate lastModified
) {
}
