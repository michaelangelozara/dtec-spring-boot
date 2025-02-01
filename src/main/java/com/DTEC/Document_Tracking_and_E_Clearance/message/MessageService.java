package com.DTEC.Document_Tracking_and_E_Clearance.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageService {

    private final RestTemplate restTemplate;

    @Value("${application.semaphore.api.key}")
    private String apiKey;

    private static final String SEMAPHORE_URL = "https://semaphore.co/api/v4/messages";

    public MessageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendMessage(String to, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Prepare the request body
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("apikey", apiKey);
            requestBody.add("number", to);
            requestBody.add("message", message);

            // Create the HttpEntity with headers and body
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Send the POST request to Semaphore
            return this.restTemplate.postForObject(SEMAPHORE_URL, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
