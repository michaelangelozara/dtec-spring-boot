package com.DTEC.Document_Tracking_and_E_Clearance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class DocumentTrackingAndEClearanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentTrackingAndEClearanceApplication.class, args);
    }

}
