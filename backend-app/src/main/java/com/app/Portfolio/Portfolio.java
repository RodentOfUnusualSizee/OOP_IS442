package com.app.Portfolio;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class Portfolio {
    private int porfolioID;
    private String portfolioName;
    private String strategyDesc;
    private float capitalUSD;
    private ArrayList<Investment> investments;

    // ------------------ Getters and Setters (Start) ------------------

    public int getPorfolioID() {
        return porfolioID;
    }

    public void setPorfolioID(int porfolioID) {
        this.porfolioID = porfolioID;
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

    public ArrayList<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(ArrayList<Investment> investments) {
        this.investments = investments;
    }

    // ------------------- Getters and Setters (End) -------------------
}
