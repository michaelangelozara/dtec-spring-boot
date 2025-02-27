package com.DTEC.Document_Tracking_and_E_Clearance.e_signature;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/e-signature")
public class ESignatureController {

    private final ESignatureRepository eSignatureRepository;

    public ESignatureController(ESignatureRepository eSignatureRepository) {
        this.eSignatureRepository = eSignatureRepository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<String>> getESignature(
            @PathVariable("username") String username
    ) {
        var fingerprints = this.eSignatureRepository.findByUsername(username);
        if (fingerprints.isEmpty())
            throw new ForbiddenException("No registered E-Signature yet");

        var fingerprint = fingerprints.get(0);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                true,
                "",
                fingerprint.getImage(),
                "",
                ""
        ));
    }
}
