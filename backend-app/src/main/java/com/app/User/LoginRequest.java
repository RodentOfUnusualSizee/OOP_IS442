package com.app.User;

/**
 * The LoginRequest class encapsulates the data required for a user login attempt.
 * It includes the user's email and password.
 */
public class LoginRequest {
    private String email;
    private String password;
    
    /**
     * Constructs a new LoginRequest with the provided email and password.
     *
     * @param email    the email address associated with the user's account.
     * @param password the password for the user's account.
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Retrieves the email address associated with this login request.
     *
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retrieves the password associated with this login request.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }
    
}