package com.DTEC.Document_Tracking_and_E_Clearance.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponseDto(
        @JsonProperty("access_token") String accessToken
) {
}
