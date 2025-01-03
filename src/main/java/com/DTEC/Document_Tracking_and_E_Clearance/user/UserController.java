package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.e_signature.ESignatureResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.FingerprintResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.FingerprintService;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;
    private final FingerprintService fingerprintService;

    public UserController(UserService userService, DateTimeFormatterUtil dateTimeFormatterUtil, FingerprintService fingerprintService) {
        this.userService = userService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
        this.fingerprintService = fingerprintService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getCurrentUser() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.userService.me(),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }

    @GetMapping("/fingerprints")
    public ResponseEntity<ApiResponse<List<FingerprintResponseDto>>> getFingerprints() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "",
                                this.fingerprintService.getFingerprints(),
                                "",
                                this.dateTimeFormatterUtil.formatIntoDateTime()
                        )
                );
    }

    @PostMapping("/add/e-signature")
    public ResponseEntity<ApiResponse<Void>> saveESignature(
            @RequestBody Map<String, String> data
    ) {
        this.fingerprintService.addESignature(data);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "E-Signature has been saved",
                                null,
                                "",
                                null
                        )
                );
    }

    @GetMapping("/my-e-signature/e-signature")
    public ResponseEntity<ApiResponse<ESignatureResponseDto>> getMyESignature() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "E-Signature of the Current User",
                                this.fingerprintService.getMyESignature(),
                                "",
                                null
                        )
                );
    }
}
