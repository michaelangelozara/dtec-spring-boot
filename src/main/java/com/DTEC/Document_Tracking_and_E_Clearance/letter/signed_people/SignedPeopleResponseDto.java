package com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people;

import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;

public record SignedPeopleResponseDto(
        Role role,
        String signature
) {
}
