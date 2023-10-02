package com.app.UserTest;

import com.app.User.UserActivityLog;
import com.app.User.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserActivityLogTest {

    private UserActivityLog UserActivityLog;

    @BeforeEach
    public void setUp() {
        UserActivityLog = new UserActivityLog();
    }

    @Test
    public void testAddNewEvent() {
        LocalDateTime timestamp = LocalDateTime.now();
        UserActivityLog.addNewEvent("LogIn", timestamp);
        UserActivityLog.addNewEvent("Logout", timestamp.plusMinutes(5));

        assertEquals(2, UserActivityLog.getAllEvents().size());
        assertEquals("LogIn", UserActivityLog.getAllEvents().get(0).getEvent());
        assertEquals("Logout", UserActivityLog.getAllEvents().get(1).getEvent());
        assertEquals(timestamp, UserActivityLog.getLastLogin());
    }

    @Test
    public void testGetLastActivity() {
        assertNull(UserActivityLog.getLastActivity());

        LocalDateTime timestamp = LocalDateTime.now();
        UserActivityLog.addNewEvent("LogIn", timestamp);
        UserEvent lastEvent = UserActivityLog.getLastActivity();

        assertNotNull(lastEvent);
        assertEquals("LogIn", lastEvent.getEvent());
        assertEquals(timestamp, lastEvent.getTimestamp());
    }
}
