package com.DTEC.Document_Tracking_and_E_Clearance;


import com.DTEC.Document_Tracking_and_E_Clearance.message.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class MessageServiceApiTest {

    @Test
    void shouldSendMessageByInputtedNumber() {
        MessageService messageService = new MessageService(new RestTemplate());
        messageService.sendMessage("09097421385", "Hey Bro");
    }
}
