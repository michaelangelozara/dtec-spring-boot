package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ImplementationLetterInCampusResponseDto(
        int id,
        @JsonProperty("name_of_activity") String nameOfActivity,
        @JsonProperty("semester_and_school_year") String semesterAndSchoolYear,
        String venue,
        @JsonProperty("date_time") String dateTime,
        @JsonProperty("expected_output") String expectedOutput,
        String objective,
        @JsonProperty("projected_expenses") String projectedExpense,
        @JsonProperty("source_of_fund") String sourceOfFund,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("last_modified") String lastModified,
        String participants,
        String rationale,
        LetterStatus status,
        @JsonProperty("student_officer_signature") String studentOfficerSignature,
        @JsonProperty("moderator_signature") String moderatorSignature,
        TypeOfLetter type,
        String club,
        @JsonProperty("student_officer") String studentOfficer,
        String moderator,
        @JsonProperty("current_location") CurrentLocation currentLocation,
        @JsonProperty("signed_people") List<SignedPeopleResponseDto> signedPeople
) {
}
