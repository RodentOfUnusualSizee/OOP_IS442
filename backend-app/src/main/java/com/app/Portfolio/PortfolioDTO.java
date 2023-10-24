package com.app.Portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.app.Position.Position;

public class PortfolioDTO {
    // --- Identification Information ---
    private int portfolioID;
    private String portfolioName;

    // --- Portfolio Description ---
    private String strategyDesc;

    // --- Financial Metrics ---
    private Float capitalUSD;
    private ArrayList<Position> positions;
    private List<Map<String, Object>> cumPositions;
    private Double currentTotalPortfolioValue;
    private Double portfolioBeta;
    private Double informationRatio;

    // --- Time-Related Information ---
    private Date createdTimestamp;
    private Date lastModifiedTimestamp;

    // --- Historical and Analytical Data ---
    private Map<String, Double> portfolioHistoricalValue;
    private Map<String, Double> portfolioAllocationBySector;
    private Map<String, Double> portfolioAllocationByGeographicalLocation;

    // --- Performance Metrics ---
    private Map<String, String> quarterlyReturns;
    private String annualizedReturnsPercentage;
    private Map<String, String> quarterlyReturnsPercentage;
    private Map<String, String> quarterlyDateRanges;

    // Constructors, getters, and setters
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

    // --- Identification Information ---
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

    // --- Portfolio Description ---
    public String getStrategyDesc() {
        return strategyDesc;
    }

    public void setStrategyDesc(String strategyDesc) {
        this.strategyDesc = strategyDesc;
    }

    // --- Financial Metrics ---
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

    public Double getCurrentTotalPortfolioValue() {
        return currentTotalPortfolioValue;
    }

    public void setCurrentTotalPortfolioValue(Double currentTotalPortfolioValue) {
        this.currentTotalPortfolioValue = currentTotalPortfolioValue;
    }

    public Double getPortfolioBeta() {
        return portfolioBeta;
    }

    public void setPortfolioBeta(Double portfolioBeta) {
        this.portfolioBeta = portfolioBeta;
    }

    public Double getInformationRatio() {
        return informationRatio;
    }

    public void setInformationRatio(Double informationRatio) {
        this.informationRatio = informationRatio;
    }

    // --- Time-Related Information ---
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

    // --- Historical and Analytical Data ---
    public Map<String, Double> getPortfolioHistoricalValue() {
        return portfolioHistoricalValue;
    }

    public void setPortfolioHistoricalValue(Map<String, Double> portfolioHistoricalValue) {
        this.portfolioHistoricalValue = portfolioHistoricalValue;
    }

    public Map<String, Double> getPortfolioAllocationBySector() {
        return portfolioAllocationBySector;
    }

    public void setPortfolioAllocationBySector(Map<String, Double> portfolioAllocationBySector) {
        this.portfolioAllocationBySector = portfolioAllocationBySector;
    }

    public Map<String, Double> getPortfolioAllocationByGeographicalLocation() {
        return portfolioAllocationByGeographicalLocation;
    }

    public void setPortfolioAllocationByGeographicalLocation(
            Map<String, Double> portfolioAllocationByGeographicalLocation) {
        this.portfolioAllocationByGeographicalLocation = portfolioAllocationByGeographicalLocation;
    }

    // --- Performance Metrics ---
    public Map<String, String> getQuarterlyReturns() {
        return quarterlyReturns;
    }

    public void setQuarterlyReturns(Map<String, String> quarterlyReturns) {
        this.quarterlyReturns = quarterlyReturns;
    }

    public String getAnnualizedReturnsPercentage() {
        return annualizedReturnsPercentage;
    }

    public void setAnnualizedReturnsPercentage(String annualizedReturnsPercentage) {
        this.annualizedReturnsPercentage = annualizedReturnsPercentage;
    }

    public Map<String, String> getQuarterlyReturnsPercentage() {
        return quarterlyReturnsPercentage;
    }

    public void setQuarterlyReturnsPercentage(Map<String, String> quarterlyReturnsPercentage) {
        this.quarterlyReturnsPercentage = quarterlyReturnsPercentage;
    }

    public Map<String, String> getQuarterlyDateRanges() {
        return quarterlyDateRanges;
    }

    public void setQuarterlyDateRanges(Map<String, String> quarterlyDateRanges) {
        this.quarterlyDateRanges = quarterlyDateRanges;
    }
}
