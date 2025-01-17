package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record PermitToEnterResponseDto(
        int id,
        String activity,
        LocalDate date,
        @JsonProperty("time_from") String timeFrom,
        @JsonProperty("time_to") String timeTo,
        String participants,
        String requisitioner,
        String club,
        String position,
        @JsonProperty("signed_people") List<SignedPeopleResponseDto> signedPeople
) {
}
