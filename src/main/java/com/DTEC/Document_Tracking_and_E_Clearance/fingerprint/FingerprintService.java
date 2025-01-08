package com.DTEC.Document_Tracking_and_E_Clearance.fingerprint;

import com.DTEC.Document_Tracking_and_E_Clearance.e_signature.ESignatureResponseDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public interface FingerprintService {

    void enroll(String username, String fingerprint);

    List<FingerprintResponseDto> getFingerprints();

    void addESignature(Map<String, String> data);

    ESignatureResponseDto getMyESignature();

    Map<String, List<String>> getAllFingerprints(String code);
}
