package com.app.User;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import jakarta.persistence.Entity;

@Entity
public class UserActivity {
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
