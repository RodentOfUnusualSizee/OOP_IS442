package com.app.Portfolio;

import java.util.*;

import javax.persistence.*;

import com.app.Position.Position;
import com.app.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "portfolioID")
@Entity
@Table(name = "user_portfolio")
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
    private ArrayList<Position> positions;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedTimestamp;

    // ------------------ Getters and Setters (Start) ------------------
    public Portfolio() {
    }

    public Portfolio(int portfolioID, User user, String portfolioName, String strategyDesc, float capitalUSD,
            ArrayList<Position> positions) {
        this.portfolioID = portfolioID;
        this.user = user;
        this.portfolioName = portfolioName;
        this.strategyDesc = strategyDesc;
        this.capitalUSD = capitalUSD;
        this.positions = positions;
        this.createdTimestamp = new Date();
        this.lastModifiedTimestamp = new Date();
    }

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

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }

    // Method to set the user of the portfolio
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // ------------------- Getters and Setters (End) -------------------

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
}