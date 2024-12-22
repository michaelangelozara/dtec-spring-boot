package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoffResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserInfoResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record ClearanceResponseDto(
        int id,
        @JsonProperty("school_year") String schoolYear,
        @JsonProperty("created_at") LocalDate createdAt,
        @JsonProperty("last_modified") LocalDate lastModified,
        UserInfoResponseDto user,
        @JsonProperty("clearance_signoffs") List<ClearanceSignoffResponseDto> clearanceSignoff,
        ClearanceStatus status,
        @JsonProperty("date_of_student_signature") LocalDate dateOfStudentSignature,
        @JsonProperty("student_signature") String studentSignature,
        ClearanceType type,
        @JsonProperty("is_submitted") boolean isSubmitted
) {
}
