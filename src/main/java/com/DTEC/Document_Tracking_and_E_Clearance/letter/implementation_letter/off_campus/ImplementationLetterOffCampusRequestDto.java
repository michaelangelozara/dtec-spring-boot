package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity.CAOORequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ImplementationLetterOffCampusRequestDto(
        String title,
        String description,
        String reason,
        @JsonProperty("date_and_time") String dateAndTime,
        List<CAOORequestDto> caoos,
        @JsonProperty("program_of_flow_of_activity") String programOrFlowOfActivity,
        String signature
) {
}
