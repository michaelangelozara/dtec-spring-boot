package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegisterRequestDto(
        @NotBlank @JsonProperty("first_name") String firstName,
        @JsonProperty("middle_name") String middleName,
        @NotBlank String lastname,
        @NotBlank String username,
        @NotBlank String password,
        @NotNull Role role
) {
}
