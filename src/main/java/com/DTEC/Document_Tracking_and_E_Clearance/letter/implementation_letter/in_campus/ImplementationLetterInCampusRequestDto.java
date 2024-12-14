package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImplementationLetterInCampusRequestDto(
        @JsonProperty("name_of_activity") String nameOfActivity,
        String title,
        @JsonProperty("date_and_times") String dateTimes,
        String venue,
        String participants,
        String rationale,
        String objectives,
        @JsonProperty("source_of_funds") String sourceOfFunds,
        @JsonProperty("projected_expenses") String projectedExpenses,
        @JsonProperty("expected_outputs") String expectedOutputs,
        String signature
) {
}
