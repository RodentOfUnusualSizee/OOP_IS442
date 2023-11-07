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
  private final LocalDateTime testTime = LocalDateTime.now();

  @BeforeEach
  void setUp() {
    userActivityLog = new UserActivityLog();
  }

  @Test
  void addNewEvent_ShouldAddEventAndSetLastLogin_WhenEventIsLogin() {
    String event = "login";
    long userId = 1L;

    UserEvent userEvent = userActivityLog.addNewEvent(event, testTime, userId);

    // Assert the event is added to the list
    assertEquals(1, userActivityLog.getAllEvents().size());
    assertEquals(event, userEvent.getEvent());
    assertEquals(testTime, userEvent.getTimestamp());
    assertEquals(userId, userEvent.getUserId());

    // Assert last login is set
    assertEquals(testTime, userActivityLog.getLastLogin());
  }

  @Test
  void addNewEvent_ShouldAddEventAndNotSetLastLogin_WhenEventIsNotLogin() {
    String event = "logout";
    long userId = 1L;

    UserEvent userEvent = userActivityLog.addNewEvent(event, testTime, userId);

    // Assert the event is added to the list
    assertEquals(1, userActivityLog.getAllEvents().size());
    assertEquals(event, userEvent.getEvent());
    assertEquals(testTime, userEvent.getTimestamp());
    assertEquals(userId, userEvent.getUserId());

    // Assert last login is not set
    assertNull(userActivityLog.getLastLogin());
  }

  @Test
  void getAllEvents_ShouldReturnAllAddedEvents() {
    userActivityLog.addNewEvent("login", testTime, 1L);
    userActivityLog.addNewEvent("click", testTime.plusMinutes(5), 1L);

    List<UserEvent> events = userActivityLog.getAllEvents();

    // Assert we have all the events added
    assertEquals(2, events.size());
  }

  @Test
  void getLastLogin_ShouldReturnLastLoginTime_WhenLoginEventExists() {
    userActivityLog.addNewEvent("login", testTime, 1L);

    LocalDateTime lastLogin = userActivityLog.getLastLogin();

    assertEquals(testTime, lastLogin);
  }

  @Test
  void getLastLogin_ShouldReturnNull_WhenNoLoginEventExists() {
    userActivityLog.addNewEvent("click", testTime, 1L);

    LocalDateTime lastLogin = userActivityLog.getLastLogin();

    assertNull(lastLogin);
  }

  @Test
  void getLastActivity_ShouldReturnLastEventAdded() {
    LocalDateTime firstEventTime = testTime;
    LocalDateTime secondEventTime = testTime.plusMinutes(5);
    userActivityLog.addNewEvent("login", firstEventTime, 1L);
    userActivityLog.addNewEvent("click", secondEventTime, 1L);

    UserEvent lastEvent = userActivityLog.getLastActivity();

    assertNotNull(lastEvent);
    assertEquals("click", lastEvent.getEvent());
    assertEquals(secondEventTime, lastEvent.getTimestamp());
  }

  @Test
  void getLastActivity_ShouldReturnNull_WhenNoEventsAdded() {
    UserEvent lastEvent = userActivityLog.getLastActivity();

    assertNull(lastEvent);
  }
}
