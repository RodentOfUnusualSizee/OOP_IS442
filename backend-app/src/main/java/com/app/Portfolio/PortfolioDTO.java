package com.app.Portfolio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.app.Position.Position;
import com.app.StockTimeSeriesAPI.Monthly.MonthlyController;

public class PortfolioDTO {
    private int portfolioID;
    private String portfolioName;
    private String strategyDesc;
    private Float capitalUSD;
    private ArrayList<Position> positions;
    private List<Map<String, Object>> cumPositions;
    private Double currentTotalPortfolioValue;
    private Date createdTimestamp;
    private Date lastModifiedTimestamp;
    private Map<String, Double> portfolioHistoricalValue;
    private Map<String, Double> portfolioAllocationBySector;

    // Constructors, getters, setters, etc.
    public PortfolioDTO() {
    }

    public PortfolioDTO(Portfolio portfolio, List<Map<String, Object>> cumPositions) {

        this.portfolioID = portfolio.getPortfolioID();
        this.portfolioName = portfolio.getPortfolioName();
        this.strategyDesc = portfolio.getStrategyDesc();
        this.capitalUSD = portfolio.getCapitalUSD();
        this.positions = portfolio.getPositions();
        this.cumPositions = cumPositions;
        this.createdTimestamp = portfolio.getCreatedTimestamp();
        this.lastModifiedTimestamp = portfolio.getLastModifiedTimestamp();
    }

    public void setCurrentTotalPortfolioValue(Double currentTotalPortfolioValue) {
        this.currentTotalPortfolioValue = currentTotalPortfolioValue;
    }

    public Map<String, Double> getPortfolioAllocationBySector() {
        return portfolioAllocationBySector;
    }

    public void setPortfolioAllocationBySector(Map<String, Double> portfolioAllocationBySector) {
        this.portfolioAllocationBySector = portfolioAllocationBySector;
    }

    public Double getCurrentTotalPortfolioValue() {
        return currentTotalPortfolioValue;
    }

    public void setCurrentTotalPortfolioValue(double currentTotalPortfolioValue) {
        this.currentTotalPortfolioValue = currentTotalPortfolioValue;
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

    public Float getCapitalUSD() {
        return capitalUSD;
    }

    public void setCapitalUSD(Float capitalUSD) {
        this.capitalUSD = capitalUSD;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }

    public List<Map<String, Object>> getCumPositions() {
        return cumPositions;
    }

    public void setCumPositions(List<Map<String, Object>> cumPositions) {
        this.cumPositions = cumPositions;
    }

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

    public Map<String, Double> getPortfolioHistoricalValue() {
        return portfolioHistoricalValue;
    }

    public void setPortfolioHistoricalValue(Map<String, Double> portfolioHistoricalValue) {
        this.portfolioHistoricalValue = portfolioHistoricalValue;
    }
}
