package com.app.User;

import java.util.*;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for user-related information.
 * This class is used for transferring user data between processes,
 * while hiding the implementation details of the user entity.
 */
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

    /**
     * Default constructor for UserDTO.
     */
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

    /**
     * Gets the list of portfolio IDs associated with the user.
     * @return A List of Integer representing the portfolio IDs.
     */
    public List<Integer> getPortfolioIds() {
        return portfolioIds;
    }

    /**
     * Gets the last login time of the user.
     * @return A LocalDateTime object representing the last login time.
     */
    public LocalDateTime getLastLogin(){
        return lastLogin;
    }

    /**
     * Gets the last activity event of the user.
     * @return A UserEvent object representing the last recorded activity.
     */
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

    /**
     * Sets the email verified status.
     * @param emailVerified A boolean indicating if the user's email is verified.
     */
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
