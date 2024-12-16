package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record CommunicationLetterResponseDto(
        int id,
        LocalDate date,
        @JsonProperty("letter_of_content") String letterOfContent,
        CommunicationLetterType type,
        LetterStatus status,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("last_modified") String lastModified,
        @JsonProperty("student_officer_signature") String studentOfficerSignature,
        @JsonProperty("moderator_signature") String moderatorSignature,
        String moderator,
        @JsonProperty("student_officer") String studentOfficer,
        @JsonProperty("current_location") CurrentLocation currentLocation,
        @JsonProperty("dsa_signature") String dsaSignature,
        @JsonProperty("office_head_signature") String officeHeadSignature,
        @JsonProperty("president_signature") String presidentSignature
) {
}
