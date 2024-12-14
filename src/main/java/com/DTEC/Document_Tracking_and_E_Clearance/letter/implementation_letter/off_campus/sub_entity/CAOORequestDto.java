package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CAOORequestDto(
        String activity,
        String objective,
        @JsonProperty("expected_output") String expectedOutput,
        String committee
) {
}
