package com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people;

import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SignedPeopleResponseDto(
        @JsonProperty("user_id") Integer id,
        Role role,
        String signature,
        SignedPeopleStatus status
) {
}
