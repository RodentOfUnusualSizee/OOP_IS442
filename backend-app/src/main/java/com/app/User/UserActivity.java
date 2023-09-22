package com.app.User;

import java.time.LocalDateTime;
import javax.persistence.*;


@Entity
@Table(name = "user_activity") // Specify a custom table name
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private long activityId;
    private LocalDateTime lastLogin;
    private String lastActivity;

    // ------------------ Getters and Setters (Start) ------------------
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }
    // ------------------- Getters and Setters (End) -------------------
}
