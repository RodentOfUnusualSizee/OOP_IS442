package com.app.User.EmailValidationComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Base64;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class EmailServiceController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestParam String toEmail, @RequestParam String subject,
            @RequestParam String message) {

        try {
            emailService.sendEmail(toEmail, subject, message);
            return ResponseEntity.ok("Email sent");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email error:" + e.getMessage());
        }

    }

    @GetMapping("/validate")
    public String validateEmail(@RequestParam String token, Model model) {
        // Decode the token (Base64) to get the original email
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String userEmail = new String(decodedBytes);

        // Validate the user's email using your UserService
        boolean isValid = emailService.validateUserEmail(userEmail);

        if (isValid) {
            // Email validation successful, you can update the user's status or perform
            // other actions
            model.addAttribute("validationMessage", "Email validation successful. Your account is now active.");
        } else {
            // Email validation failed
            model.addAttribute("validationMessage", "Email validation failed. Please contact support for assistance.");
        }

        // Return the name of the HTML template (e.g., "validationResult.html")
        return "validationResult";
    }
}
