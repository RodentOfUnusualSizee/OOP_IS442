package com.app.UserTest.EmailValidationComponent;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import com.app.User.EmailValidationComponent.EmailService;
import com.app.User.EmailValidationComponent.EmailServiceController;

public class EmailServiceControllerTest {

    @InjectMocks
    private EmailServiceController emailServiceController;

    @Mock
    private EmailService emailService;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateEmail_success() {
        String token = "c29tZUBlbWFpbC5jb20=";  // Base64 encoded "some@email.com"
        when(emailService.validateUserEmail(anyString())).thenReturn(true);

        String result = emailServiceController.validateEmail(token, model);

        assertEquals("validationResult", result);
        verify(model).addAttribute("validationMessage", "Email validation successful. Your account is now active.");
    }

    @Test
    public void testValidateEmail_failure() {
        String token = "c29tZUBlbWFpbC5jb20=";
        when(emailService.validateUserEmail(anyString())).thenReturn(false);

        String result = emailServiceController.validateEmail(token, model);

        assertEquals("validationResult", result);
        verify(model).addAttribute("validationMessage", "Email validation failed. Please contact support for assistance.");
    }
}
