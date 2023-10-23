package com.app.User.EmailValidationComponent;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.app.User.User;
import com.app.User.UserRepository;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    public void sendEmail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

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
