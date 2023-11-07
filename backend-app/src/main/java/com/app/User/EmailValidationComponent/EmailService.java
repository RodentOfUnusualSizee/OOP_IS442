package com.app.User.EmailValidationComponent;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.app.User.User;
import com.app.User.UserRepository;

/**
 * The EmailService class provides functionality for sending emails.
 * It uses the JavaMailSender for sending out simple text emails.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    /**
     * Sends an email with the specified content to the specified recipient.
     * 
     * @param toEmail The recipient's email address.
     * @param subject The subject of the email.
     * @param message The text content of the email body.
     */
    public void sendEmail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

    /**
     * Sends a validation email to a new user for email verification purposes.
     * The email contains a link with a unique token for the user to verify their email.
     * 
     * @param user The user to whom the validation email will be sent.
     * @return A string message indicating the result of the email sending operation.
     */
    public String sendValidationEmail(User user) {
        String toEmail = user.getEmail();
        String token = Base64.getEncoder().encodeToString(toEmail.getBytes()); // which is base64 encoded userEmail
        String validationUrl = "http://localhost:8080/validate?token=" + token;
        String subject = "Email Verification for Your Account";
        String message = "Dear User,\n\n"
                + "Thank you for registering with our application. "
                + "To activate your account, please click on the following link:\n\n"
                + validationUrl + "\n\n"
                + "If you did not register with our application, you can ignore this email.\n\n"
                + "Best regards,\n"
                + "Your Application Team";

        try {
            sendEmail(toEmail, subject, message);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }

    /**
     * Validates a user's email based on the provided email address.
     * This method assumes that the user's email token has been previously verified.
     * 
     * @param userEmail The email address of the user to validate.
     * @return A Boolean indicating whether the user's email was successfully validated.
     */
    public Boolean validateUserEmail(String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail);
            user.setEmailVerified(true);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
