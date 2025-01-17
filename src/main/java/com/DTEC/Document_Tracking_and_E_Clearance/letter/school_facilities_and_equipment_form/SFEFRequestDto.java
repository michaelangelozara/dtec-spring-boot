package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form.facility_or_equipment.FacilityOrEquipmentRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record SFEFRequestDto(
        String venue,
        String activity,
        LocalDate date,
        @JsonProperty("time_from") String timeFrom,
        @JsonProperty("time_to") String timeTo,
        List<FacilityOrEquipmentRequestDto> facilityOrEquipments,
        String signature
) {
}
