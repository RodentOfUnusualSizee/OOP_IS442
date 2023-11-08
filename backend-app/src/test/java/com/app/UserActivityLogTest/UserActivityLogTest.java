package com.app.UserActivityLogTest;

import static org.junit.jupiter.api.Assertions.*;

import com.app.User.UserEvent;
import com.app.UserActivityLog.UserActivityLog;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserActivityLogTest {

    private UserActivityLog userActivityLog;
    private final long userId = 1L;
    private final LocalDateTime testTime = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        userActivityLog = new UserActivityLog();
    }

    @Test
    public void testAddNewEvent() {
        String eventName = "LOGIN";
        UserEvent event = userActivityLog.addNewEvent(eventName, testTime, userId);

        // Assert that the event is added to the list
        assertNotNull(event);
        assertEquals(eventName, event.getEvent());
        assertEquals(testTime, event.getTimestamp());
        assertEquals(userId, event.getUserId());
        assertEquals(testTime, userActivityLog.getLastLogin());

        // Assert the event list size is 1
        assertEquals(1, userActivityLog.getAllEvents().size());

        // Assert the fields in the added event
        UserEvent addedEvent = userActivityLog.getAllEvents().get(0);
        assertEquals(eventName, addedEvent.getEvent());
        assertEquals(testTime, addedEvent.getTimestamp());
        assertEquals(userId, addedEvent.getUserId());
    }

    @Test
    public void testGetAllEvents() {
        // Should be empty initially
        List<UserEvent> events = userActivityLog.getAllEvents();
        assertTrue(events.isEmpty());

        // Add an event and test
        userActivityLog.addNewEvent("LOGIN", testTime, userId);
        events = userActivityLog.getAllEvents();
        assertEquals(1, events.size());

        // Assert the fields in the event
        UserEvent addedEvent = events.get(0);
        assertEquals("LOGIN", addedEvent.getEvent());
        assertEquals(testTime, addedEvent.getTimestamp());
        assertEquals(userId, addedEvent.getUserId());
    }

    @Test
    public void testGetLastLogin() {
        // No login yet, should be null
        assertNull(userActivityLog.getLastLogin());

        // Add a login event
        userActivityLog.addNewEvent("LOGIN", testTime, userId);
        assertEquals(testTime, userActivityLog.getLastLogin());
    }

    @Test
    public void testGetLastActivity() {
        // No activities yet, should be null
        assertNull(userActivityLog.getLastActivity());

        // Add an event and test
        userActivityLog.addNewEvent("LOGIN", testTime, userId);
        UserEvent lastActivity = userActivityLog.getLastActivity();
        assertNotNull(lastActivity);
        assertEquals("LOGIN", lastActivity.getEvent());
        assertEquals(testTime, lastActivity.getTimestamp());
        assertEquals(userId, lastActivity.getUserId());

        // Add another event and test
        LocalDateTime newTestTime = testTime.plusDays(1);
        userActivityLog.addNewEvent("LOGOUT", newTestTime, userId);
        lastActivity = userActivityLog.getLastActivity();
        assertNotNull(lastActivity);
        assertEquals("LOGOUT", lastActivity.getEvent());
        assertEquals(newTestTime, lastActivity.getTimestamp());
        assertEquals(userId, lastActivity.getUserId());
    }
}
