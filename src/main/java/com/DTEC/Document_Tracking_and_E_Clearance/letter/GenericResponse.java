package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import lombok.*;

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

    private Object object;
}
