package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GenericResponse {
    private int id;

    private TypeOfLetter type;

    private Map<String, Object> fields;

    private String cml;

    private LocalDateTime createdDate;

    @JsonProperty("signed_people")
    private List<SignedPeopleResponseDto> signedPeople;
}
