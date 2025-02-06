package com.DTEC.Document_Tracking_and_E_Clearance.message;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private RestTemplate restTemplate;  // Mock the RestTemplate

    @InjectMocks
    private static MessageService messageService;  // The service under test (assuming it's called MessageService)

    private static final String SEMAPHORE_URL = "https://semaphore.co/api/v4/messages";  // The URL you're calling


    @Test
    void testSendMessage_ShouldSendMessage() {
        // Arrange
        String to = "1234567890";
        String message = "Hello, this is a test message";
        String expectedResponse = "Message sent successfully";  // The expected response from the external API

        messageService.setApiKeyForUnitTesting("Fake Api Key");

        // Mock the behavior of the RestTemplate to return the expected response
        Mockito.when(restTemplate.postForObject(Mockito.eq(SEMAPHORE_URL), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(expectedResponse);

        // Act
        String result = messageService.sendMessage(to, message);  // Call the method under test

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);  // Verify the result matches the expected response

        // Verify that the RestTemplate's postForObject method was called with the correct arguments
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(Mockito.eq(SEMAPHORE_URL), Mockito.any(), Mockito.eq(String.class));
    }
}
