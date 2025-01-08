package com.DTEC.Document_Tracking_and_E_Clearance.web_socket;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.FingerprintResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.FingerprintService;
import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.FingerprintValidationRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@Slf4j
public class WebSocketController {

    private final FingerprintService fingerprintService;

    private final CopyOnWriteArrayList<FingerprintValidationRequestDto> fingerprints = new CopyOnWriteArrayList<>();

    public WebSocketController(FingerprintService fingerprintService) {
        this.fingerprintService = fingerprintService;
    }

    @MessageMapping("/validate.fingerprint")
    @SendTo("/topic/receive-fingerprints")
    public ResponseEntity<ApiResponse<CopyOnWriteArrayList<FingerprintValidationRequestDto>>> broadcast(
            @Payload FingerprintValidationRequestDto data
    ) {
        fingerprints.add(data);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        fingerprints,
                        "",
                        null
                )
        );
    }

    @MessageMapping("/enroll.fingerprint")
    public ResponseEntity<ApiResponse<Void>> enroll(
            @Payload Map<String, String> data
    ) {
        String username = data.get("username");
        String fingerprint = data.get("data"); // data of the fingerprint
        if (username == null || username.isEmpty() || fingerprint == null || fingerprint.isEmpty())
            throw new ForbiddenException("Invalid Data");

        log.info("This is the data that will be storing {}",fingerprint);

        this.fingerprintService.enroll(username, fingerprint);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Fingerprint Successfully Enrolled",
                        null,
                        "",
                        null
                )
        );

    }

    @MessageMapping("/success.fingerprint")
    @SendTo("/topic/receive-success-fingerprints")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendSuccessFingerprints(
            @Payload Map<String, String> data
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "",
                                data,
                                "",
                                null
                        )
                );
    }

    @MessageMapping("/disconnect")
    public void disconnect(
            @Payload FingerprintValidationRequestDto data
    ) {
        for (var fingerprint : fingerprints) {
            if (fingerprint.getIp().equals(data.getIp())) {
                fingerprints.remove(fingerprint);
            }
        }
    }
}