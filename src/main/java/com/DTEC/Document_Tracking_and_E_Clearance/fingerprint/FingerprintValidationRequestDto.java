package com.DTEC.Document_Tracking_and_E_Clearance.fingerprint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FingerprintValidationRequestDto {
    private String ip;
    private List<Map<String, String>> fingerprints;
}
