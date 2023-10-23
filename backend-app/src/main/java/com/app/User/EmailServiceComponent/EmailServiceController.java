package com.app.User.EmailServiceComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailServiceController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send")
    public String sendSampleEmail(@RequestParam String toEmail) {
        String subject = "Hello from Spring Boot";
        String message = "This is a sample email sent from a Spring Boot application.";

        try {
            emailService.sendEmail(toEmail, subject, message);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }
}
