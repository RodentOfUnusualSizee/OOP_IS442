package com.app.UserActivityLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import com.app.User.UserEvent;

/**
 * Entity representing the user activity log.
 * It maps to the 'user_activity' table in the database and keeps a record of user events.
 */
@Entity
@Table(name = "user_activity")
public class UserActivityLog {

    /**
     * Unique identifier for the user activity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userActivityId;

    /**
     * Timestamp of the last login event.
     */
    private LocalDateTime lastLogin;

    /**
     * List of user events associated with this activity log.
     * Each event is mapped to a row in the 'UserEvent' table with a foreign key reference.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_activity_id") // This is the foreign key column in the UserEvent table
    private List<UserEvent> events = new ArrayList<>();

    /**
     * Adds a new event to the user activity log.
     * If the event is a login, the last login time is also updated.
     *
     * @param event     the name of the event (e.g., "login").
     * @param timestamp the time at which the event occurred.
     * @param userId    the identifier of the user associated with the event.
     * @return the created UserEvent object.
     */
    public UserEvent addNewEvent(String event, LocalDateTime timestamp, long userId) {
        UserEvent newEvent = new UserEvent(event, timestamp, userId);
        events.add(newEvent);

        // Update Last login
        if ("login".equals(event)) {
            lastLogin = timestamp;
        }
        return newEvent;
    }

    /**
     * Retrieves all the events from the user activity log.
     *
     * @return a list of UserEvent objects.
     */
    public List<UserEvent> getAllEvents() {
        return events;
    }

    /**
     * Gets the timestamp of the last login event.
     *
     * @return the timestamp of the last login, or null if no login event is recorded.
     */
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    /**
     * Retrieves the last recorded activity for the user.
     *
     * @return the most recent UserEvent, or null if there are no events.
     */
    public UserEvent getLastActivity() {
        if (!events.isEmpty()) {
            return events.get(events.size() - 1);
        } else {
            return null; // Handle the case where there are no events
        }
    }
}
