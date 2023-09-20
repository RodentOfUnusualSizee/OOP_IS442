package org.system.backendapp;

public class User {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private UserActivity activity;
    private ArrayList<Portfolio> portfolios;

    // ------------------ Getters and Setters (Start) ------------------
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public UserActivity getActivity() {
        return activity;
    }
    public void setActivity(UserActivity activity) {
        this.activity = activity;
    }
    public ArrayList<Portfolio> getPortfolios() {
        return portfolios;
    }
    public void setPortfolios(ArrayList<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    // ------------------- Getters and Setters (End) -------------------
    
    public boolean verifyLogin(){

    }

    public boolean resetPassword(){

    }
    
}
