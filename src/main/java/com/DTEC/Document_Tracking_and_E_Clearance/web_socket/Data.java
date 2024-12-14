package com.DTEC.Document_Tracking_and_E_Clearance.web_socket;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Data {
    private String username;
    private String data;
    private LocalDate date;
}
