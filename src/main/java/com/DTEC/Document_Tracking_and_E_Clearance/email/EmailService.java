package com.DTEC.Document_Tracking_and_E_Clearance.email;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.InternalServerErrorException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${frontend.origin}")
    private String DOMAIN;
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("dtec2025@gmail.com");
            helper.setTo(email);
            helper.setSubject("DTEC EMAIL VERIFICATION");

            String htmlContent = "<p>Welcome to DTEC! Please click the link below to set up your password and access your account for the first time:</p>" +
                    "<p><a href='" + DOMAIN + "/first-time-login/update-password?t=" + token + "' target='_blank'>FTLP Link</a></p>" +
                    "<p>If you have any questions or need assistance, feel free to visit the ICTS Office.</p>";

            helper.setText(htmlContent, true); // Pass true to indicate the email is HTML

            this.mailSender.send(message);
        } catch (Exception e) {
            throw new InternalServerErrorException("Something Went Wrong with the Email Server");
        }
    }
}
