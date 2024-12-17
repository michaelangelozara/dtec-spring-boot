package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

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
        @JsonProperty("signed_people") List<SignedPeopleResponseDto> signedPeople
) {
}
