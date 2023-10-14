package com.app.User;

import java.time.LocalDateTime;
import java.util.*;

import com.app.Portfolio.Portfolio;
import com.app.UserActivityLog.UserActivityLog;

import javax.persistence.*;

@Entity
@Table(name = "users") // Specify a custom table name
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private long id;

    @Column(unique = true) // Assuming email should be unique
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;

    @OneToOne(cascade = CascadeType.ALL) // Define the relationship with UserActivity
    @JoinColumn(name = "activity_id")
    private UserActivityLog userActivityLog;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Portfolio> portfolios;

    // Constructor requirement by Hibernate (used by Spring Data JPA)
    public User() {
    }

    // Constructor
    public User(String email, String password, String firstName, String lastName, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.userActivityLog = new UserActivityLog();
        this.portfolios = new ArrayList<>();
    }

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

    public UserActivityLog getUserActivityLog() {
        return this.userActivityLog;
    }

    public void setUserActivityLog(UserActivityLog userActivityLog) {
        this.userActivityLog = userActivityLog;
    }

    public void addNewActivityEvent(String event, LocalDateTime timestamp) {
        this.userActivityLog.addNewEvent(event, timestamp);
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    
    // ------------------- Getters and Setters (End) -------------------
    
    // Method to add a new portfolio
    public void addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
        portfolio.setUser(this);
    }
    
    // Method to update an existing portfolio
    public void updatePortfolio(Portfolio portfolio) {
        for (int i = 0; i < this.portfolios.size(); i++) {
            if (this.portfolios.get(i).getPorfolioID() == portfolio.getPorfolioID()) {
                this.portfolios.set(i, portfolio);
                break;
            }
        }
    }
    
    // Method to delete a portfolio
    public void deletePortfolio(int portfolioID) {
        this.portfolios.removeIf(p -> p.getPorfolioID() == portfolioID);
    }
    

    public boolean verifyLogin() {
        return true;
    }

    public boolean resetPassword() {
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
