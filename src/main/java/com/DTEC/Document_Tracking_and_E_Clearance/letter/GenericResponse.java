package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import lombok.*;

import java.time.LocalDateTime;
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
}
