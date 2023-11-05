package com.app.User.EmailValidationComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Base64;

/**
 * The EmailServiceController handles web requests related to email operations,
 * such as sending emails and validating user email addresses via a web interface.
 * This controller is part of the EmailValidationComponent.
 */
@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class EmailServiceController {
    @Autowired
    private EmailService emailService;

    /**
     * Endpoint to trigger the sending of an email.
     * Accepts the recipient's email, subject, and message as request parameters.
     *
     * @param toEmail The recipient's email address.
     * @param subject The subject of the email.
     * @param message The content of the email message.
     * @return A ResponseEntity with a success or error message.
     */
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

    /**
     * Endpoint for email validation. It decodes a Base64 encoded token to retrieve the user email,
     * and attempts to validate it. The result of the validation is then added to the model,
     * which is used to populate the response view.
     *
     * @param token A Base64 encoded token representing the user's email to be validated.
     * @param model A Model instance used to pass attributes to the view layer.
     * @return A string representing the name of the view to be rendered, with model attributes populated.
     */
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
