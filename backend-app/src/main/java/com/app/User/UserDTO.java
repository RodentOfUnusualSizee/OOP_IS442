package com.app.User;

import java.util.*;
import java.time.LocalDateTime;

// User Data Transfer Object
public class UserDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private boolean emailVerified;
    private List<Integer> portfolioIds; // Store portfolio IDs as integers
    private LocalDateTime lastLogin;
    private UserEvent lastActivity;

    public UserDTO() {
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public List<Integer> getPortfolioIds() {
        return portfolioIds;
    }

    public LocalDateTime getLastLogin(){
        return lastLogin;
    }
    public UserEvent getLastActivity() {
        return lastActivity;
    }
    
    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPortfolioIds(List<Integer> portfolioIds) {
        this.portfolioIds = portfolioIds;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastActivity(UserEvent lastActivity) {
        this.lastActivity = lastActivity;
    }
    
}
