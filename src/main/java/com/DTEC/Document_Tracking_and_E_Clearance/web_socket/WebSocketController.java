package com.DTEC.Document_Tracking_and_E_Clearance.web_socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {

    @MessageMapping("/fingerprint")
    @SendTo("/topic/receive-fingerprint")
    public Data fingerprint(
            @Payload Data data
    ) {
        log.info("This is the received data coming from the fingerprint {}", data.getData());
        return data;
    }
}