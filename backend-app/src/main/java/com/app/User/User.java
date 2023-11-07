package com.app.User;

import java.time.LocalDateTime;
import java.util.*;
import com.app.Portfolio.Portfolio;
import com.app.UserActivityLog.UserActivityLog;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

/**
 * The User class represents an entity model for a user within the system.
 * It includes details such as email, password, first and last names, roles, email verification status,
 * associated user activity log, and portfolios.
 *
 * The class is annotated with JPA annotations to define the table mapping, unique constraints,
 * and relationships with other entities such as UserActivityLog and Portfolio.
 *
 * JsonIdentityInfo is used to handle circular references correctly when serializing entities to JSON.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name = "users")
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
    private boolean emailVerified;

    @OneToOne(cascade = CascadeType.ALL) // Define the relationship with UserActivity
    @JoinColumn(name = "activity_id")
    private UserActivityLog userActivityLog;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Portfolio> portfolios;

    /**
     * Default constructor required by Hibernate. Initializes a new user with email verification status set to true.
     */
    public User() {
        // this.emailVerified = true;
        this.emailVerified = false;

    }

    /**
     * Parametrized constructor for creating a new User instance with specified attributes.
     * It initializes user activity log and portfolios as well.
     *
     * @param email     the email of the user, must be unique.
     * @param password  the password of the user.
     * @param firstName the first name of the user.
     * @param lastName  the last name of the user.
     * @param role      the role of the user within the system.
     */
    public User(String email, String password, String firstName, String lastName, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.userActivityLog = new UserActivityLog();
        this.portfolios = new ArrayList<>();
        // this.emailVerified = false;
        this.emailVerified = true;

    }

    // ------------------ Getters and Setters (Start) ------------------

    /**
     * Retrieves the email address of the user.
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     * @param email the email address to set.
     */
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

    public void addNewActivityEvent(String event, LocalDateTime timestamp, long id) {
        this.userActivityLog.addNewEvent(event, timestamp, id);
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    // ------------------- Getters and Setters (End) -------------------

    /**
     * Adds a new portfolio to the user's collection of portfolios.
     * @param portfolio the portfolio to add.
     */
    public void addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
        portfolio.setUser(this);
    }

    // Method to update an existing portfolio
    public void updatePortfolio(Portfolio portfolio) {
        for (int i = 0; i < this.portfolios.size(); i++) {
            if (this.portfolios.get(i).getPortfolioID() == portfolio.getPortfolioID()) {
                this.portfolios.set(i, portfolio);
                break;
            }
        }
    }

    // Method to delete a portfolio
    public void deletePortfolio(int portfolioID) {
        this.portfolios.removeIf(p -> p.getPortfolioID() == portfolioID);
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

    /**
     * Returns whether the user's email is verified.
     * @return true if the email is verified, false otherwise.
     */
    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

}
