package com.DTEC.Document_Tracking_and_E_Clearance.fingerprint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class FingerprintController {

    @MessageMapping("/fingerprint.connectFingerprint")
    @SendTo("/topic/fingerprint")
    public FingerprintResponse connectFingerprint(
            @Payload FingerprintResponse response
    ){
        log.info("");
        return response;
    }
}
