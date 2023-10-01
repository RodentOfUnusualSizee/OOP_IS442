package com.app.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "user_activity")
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userActivityId;
    private LocalDateTime lastLogin;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_activity_id") // This is the foreign key column in the UserEvent table
    private List<UserEvent> events = new ArrayList<>();

    public void addNewEvent(String event, LocalDateTime timestamp) {
        UserEvent newEvent = new UserEvent(event, timestamp);
        events.add(newEvent);

        // Update Last LogIn
        if ("LogIn".equals(event)) {
            lastLogin = timestamp;
        }
    }

    public List<UserEvent> getAllEvents() {
        return events;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public UserEvent getLastActivity() {
        if (!events.isEmpty()) {
            return events.get(events.size() - 1);
        } else {
            return null; // Handle the case where there are no events
        }
    }
}
