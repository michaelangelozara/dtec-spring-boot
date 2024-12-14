package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity.CAOO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ImplementationLetterOffCampusResponseDto(
        int id,
        @JsonProperty("name_of_activity") String nameOfActivity,
        String description,
        String reason,
        @JsonProperty("date_time") String dateTime,
        @JsonProperty("program_or_flow") String programOrFlow,
        @JsonProperty("student_officer_signature") String studentOfficerSignature,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("last_modified") String lastModified,
        LetterStatus status,
        TypeOfLetter type,
        List<CAOO> caoos,
        String club,
        String moderator,
        @JsonProperty("student_officer") String studentOfficer,
        @JsonProperty("moderator_signature") String moderatorSignature
) {
}
