package com.app.User;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "user_event") 
public class UserEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private long userEventId;
    private String event;
    private LocalDateTime timestamp;

    // Constructor requirement by Hibernate (used by Spring Data JPA)
    public UserEvent() {
    }
    
    public UserEvent(String event, LocalDateTime timestamp) {
        this.event = event;
        this.timestamp = timestamp;
    }

    public String getEvent() {
        return event;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
