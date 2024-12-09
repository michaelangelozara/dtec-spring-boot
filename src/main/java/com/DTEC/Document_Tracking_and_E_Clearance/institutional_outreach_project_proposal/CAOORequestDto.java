package com.DTEC.Document_Tracking_and_E_Clearance.institutional_outreach_project_proposal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CAOORequestDto(
        String activities,
        String objectives,
        @JsonProperty("expected_output") String expectedOutput,
        @JsonProperty("committee_in_charge") String committeeInCharges
) {
}
