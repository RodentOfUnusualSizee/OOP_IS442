package com.app.Portfolio;

import java.util.*;

import javax.persistence.*;

import com.app.Order.Order;
import com.app.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "portfolioID")
@Entity
@Table(name = "user_portfolio") // Specify a custom table name
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private int portfolioID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private String portfolioName;
    private String strategyDesc;
    private float capitalUSD;
    private ArrayList<Order> orders;

    // ------------------ Getters and Setters (Start) ------------------

    public int getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(int portfolioID) {
        this.portfolioID = portfolioID;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getStrategyDesc() {
        return strategyDesc;
    }

    public void setStrategyDesc(String strategyDesc) {
        this.strategyDesc = strategyDesc;
    }

    public float getCapitalUSD() {
        return capitalUSD;
    }

    public void setCapitalUSD(float capitalUSD) {
        this.capitalUSD = capitalUSD;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    // Method to set the user of the portfolio
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // ------------------- Getters and Setters (End) -------------------

}
