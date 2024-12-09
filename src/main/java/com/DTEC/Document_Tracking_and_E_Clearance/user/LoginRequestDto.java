package com.DTEC.Document_Tracking_and_E_Clearance.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank String username,
        @NotBlank String password
) {
}
