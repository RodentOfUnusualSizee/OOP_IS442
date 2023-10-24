package com.app.UserTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.app.User.UserEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserEventTest {

    private UserEvent userEvent;

    @BeforeEach
    public void setUp() {
        LocalDateTime timestamp = LocalDateTime.now();
        userEvent = new UserEvent("LogIn", timestamp,1);
    }

    @Test
    public void testGetEvent() {
        assertEquals("login", userEvent.getEvent());
    }

    @Test
    public void testGetTimestamp() {
        LocalDateTime timestamp = userEvent.getTimestamp();
        assertNotNull(timestamp);
        assertTrue(timestamp.isBefore(LocalDateTime.now().plusSeconds(1))); // Ensure the timestamp is not in the future
    }
}
