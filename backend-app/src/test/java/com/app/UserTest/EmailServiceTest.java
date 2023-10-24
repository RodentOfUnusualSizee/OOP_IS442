package com.app.UserTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import com.app.User.EmailValidationComponent.EmailService;
import com.app.User.User;
import com.app.User.UserRepository;

public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendValidationEmail_success() {
        when(user.getEmail()).thenReturn("some@email.com");

        String result = emailService.sendValidationEmail(user);

        assertEquals("Email sent successfully!", result);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendValidationEmail_failure() {
        when(user.getEmail()).thenReturn("some@email.com");
        doThrow(new RuntimeException("Mocked exception")).when(javaMailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendValidationEmail(user);

        assertTrue(result.startsWith("Error sending email:"));
    }

    @Test
    public void testValidateUserEmail_success() {
        String userEmail = "some@email.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(user);

        boolean result = emailService.validateUserEmail(userEmail);

        assertTrue(result);
        verify(user).setEmailVerified(true);
        verify(userRepository).save(user);
    }

    @Test
    public void testValidateUserEmail_failure() {
        String userEmail = "some@email.com";
        when(userRepository.findByEmail(userEmail)).thenThrow(new RuntimeException("Mocked exception"));

        boolean result = emailService.validateUserEmail(userEmail);

        assertFalse(result);
    }
}
