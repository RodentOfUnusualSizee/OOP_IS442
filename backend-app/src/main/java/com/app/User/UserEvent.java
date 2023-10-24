package com.app.User;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "user_event") 
public class UserEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private long userEventId;
    private long userId;
    private String event;
    private LocalDateTime timestamp;

    // Constructor requirement by Hibernate (used by Spring Data JPA)
    public UserEvent() {
    }
    
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
