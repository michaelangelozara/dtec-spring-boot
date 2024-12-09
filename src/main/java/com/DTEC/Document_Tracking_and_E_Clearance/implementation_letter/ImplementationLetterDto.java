package com.DTEC.Document_Tracking_and_E_Clearance.implementation_letter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ImplementationLetterDto(
        @JsonProperty("club_name") String clubName,
        @JsonProperty("name_of_activity") String nameOfActivity,
        String title,
        @JsonProperty("date_and_time") String dateTime,
        String venue,
        String participants,
        String rationale,
        String objectives,
        @JsonProperty("source_of_fund") String sourceOfFund,
        @JsonProperty("projected_expenses") String projectedExpenses,
        @JsonProperty("expected_output") String expectedOutput,
        byte[] signature
) {
}
