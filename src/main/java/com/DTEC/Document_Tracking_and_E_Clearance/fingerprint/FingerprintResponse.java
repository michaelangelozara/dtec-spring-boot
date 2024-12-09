package com.DTEC.Document_Tracking_and_E_Clearance.fingerprint;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FingerprintResponse {
    private byte[] data;
    private Type type;
    private String localDateTime;
}
