package com.DTEC.Document_Tracking_and_E_Clearance.api_response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String description;
    private String localDateTime;
}
