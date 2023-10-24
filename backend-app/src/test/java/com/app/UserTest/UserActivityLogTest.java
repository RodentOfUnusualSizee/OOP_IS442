package com.app.UserTest;

import com.app.User.UserEvent;
import com.app.UserActivityLog.UserActivityLog;

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
        UserActivityLog.addNewEvent("login", timestamp,1);
        UserActivityLog.addNewEvent("logout", timestamp.plusMinutes(5),1);

        assertEquals(2, UserActivityLog.getAllEvents().size());
        assertEquals("login", UserActivityLog.getAllEvents().get(0).getEvent());
        assertEquals("logout", UserActivityLog.getAllEvents().get(1).getEvent());
        assertEquals(timestamp, UserActivityLog.getLastLogin());
    }

    @Test
    public void testGetLastActivity() {
        assertNull(UserActivityLog.getLastActivity());

        LocalDateTime timestamp = LocalDateTime.now();
        UserActivityLog.addNewEvent("login", timestamp,1);
        UserEvent lastEvent = UserActivityLog.getLastActivity();

        assertNotNull(lastEvent);
        assertEquals("login", lastEvent.getEvent());
        assertEquals(timestamp, lastEvent.getTimestamp());
    }
}
