package com.app.UserTest;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.app.User.UserEvent;

public class UserEventTest {

    @InjectMocks
    private UserEvent userEvent;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(userEvent);
    }

    @Test
    public void testParameterizedConstructor() {
        LocalDateTime timestamp = LocalDateTime.now();
        UserEvent event = new UserEvent("Login", timestamp, 1L);
        
        assertNotNull(event);
        assertEquals("Login", event.getEvent());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals(1L, event.getUserId());
    }

    @Test
    public void testGettersAndSetters() {
        LocalDateTime timestamp = LocalDateTime.now();
        
        userEvent.setEvent("Logout");
        userEvent.setTimestamp(timestamp);
        userEvent.setUserId(2L);
        userEvent.setUserEventId(1L);
        
        assertEquals("Logout", userEvent.getEvent());
        assertEquals(timestamp, userEvent.getTimestamp());
        assertEquals(2L, userEvent.getUserId());
        assertEquals(1L, userEvent.getUserEventId());
    }

}
