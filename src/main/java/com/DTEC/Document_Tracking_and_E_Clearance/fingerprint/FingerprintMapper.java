package com.DTEC.Document_Tracking_and_E_Clearance.fingerprint;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FingerprintMapper {

    public FingerprintResponseDto toFingerprintResponse(Fingerprint fingerprint){
        return new FingerprintResponseDto(
                fingerprint.getId(),
                fingerprint.getFingerprint()
        );
    }

    public List<FingerprintResponseDto> toFingerprintResponseDtoList(List<Fingerprint> fingerprints){
        return fingerprints
                .stream()
                .map(this::toFingerprintResponse)
                .toList();
    }
}
