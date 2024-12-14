package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record CommunicationLetterRequestDto(
        LocalDate date,
        @JsonProperty("letter_of_content") String letterOfContent,
        String signature
) {
}
