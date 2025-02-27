package com.DTEC.Document_Tracking_and_E_Clearance.fingerprint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fingerprints")
public class FingerprintController {

    private final FingerprintServiceImp fingerprintServiceImp;

    public FingerprintController(FingerprintServiceImp fingerprintServiceImp) {
        this.fingerprintServiceImp = fingerprintServiceImp;
    }

    @GetMapping
    public Map<String, List<String>> getFingerprints(){
        return this.fingerprintServiceImp.findAllFingerprints();
    }
}
