package com.app.User;

import java.time.LocalDateTime;

import javax.persistence.*;

/**
 * Entity representing a user event, such as a login or action performed by the user.
 * Maps to the 'user_event' table in the database.
 */
@Entity
@Table(name = "user_event") 
public class UserEvent {
    /**
     * The unique ID of the user event. It is generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private long userEventId;

     /**
     * The ID of the user to whom this event relates.
     */
    private long userId;

    /**
     * The unique ID of the user event. It is generated automatically by the database.
     */
    private String event;

    /**
     * The timestamp when the event occurred.
     */
    private LocalDateTime timestamp;

    /**
     * Default constructor required by JPA.
     */
    public UserEvent() {
    }
    
    /**
     * Constructs a new UserEvent with the specified details.
     * 
     * @param event     the type of event
     * @param timestamp the time at which the event occurred
     * @param userId    the ID of the user associated with this event
     */
    public UserEvent(String event, LocalDateTime timestamp, long userId) {
        this.event = event;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getEvent() {
        return event;
    }

    public long getUserEventId() {
        return userEventId;
    }
    public long getUserId() {
        return userId;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setUserEventId(long userEventId) {
        this.userEventId = userEventId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
