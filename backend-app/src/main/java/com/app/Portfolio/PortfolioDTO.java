package com.app.Portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.app.Position.Position;

/**
 * The PortfolioDTO class represents a data transfer object for a Portfolio.
 * This class includes a combination of identification information, financial metrics,
 * time-related data, historical and analytical data, as well as performance metrics.
 */
public class PortfolioDTO {
    // --- Identification Information ---
    /**
     * The unique identifier for the portfolio.
     */
    private int portfolioID;

    /**
     * The name of the portfolio.
     */
    private String portfolioName;

    // --- Portfolio Description ---
    /**
     * A description of the investment strategy used by the portfolio.
     */
    private String strategyDesc;

    // --- Financial Metrics ---
    /**
     * The amount of capital in USD allocated to the portfolio.
     */
    private Float capitalUSD;

    /**
     * A list of positions held within the portfolio.
     */
    private ArrayList<Position> positions;

    /**
     * A list of cumulative positions held within the portfolio represented as maps.
     */
    private List<Map<String, Object>> cumPositions;

    /**
     * The current total value of the portfolio.
     */
    private Double currentTotalPortfolioValue;

    /**
     * The beta of the portfolio, representing its volatility relative to the market.
     */
    private Double portfolioBeta;

    /**
     * The information ratio of the portfolio, indicating the active return of an investment manager
     * relative to the return of a benchmark.
     */
    private Double informationRatio;

    /**
     * Year-over-Year growth rate of the portfolio's value.
     */
    private String portfolioYoY;

    /**
     * Quarter-over-Quarter growth rate of the portfolio's value.
     */
    private String portfolioQoQ;

    /**
     * Month-over-Month growth rate of the portfolio's value.
     */
    private String portfolioMoM;

    // --- Time-Related Information ---
    /**
     * The timestamp indicating when the portfolio was created.
     */
    private Date createdTimestamp;

    /**
     * The timestamp of the last modification made to the portfolio.
     */
    private Date lastModifiedTimestamp;

    // --- Historical and Analytical Data ---
    /**
     * A map representing the historical value of the portfolio over time.
     */
    private Map<String, Double> portfolioHistoricalValue;

    /**
     * A map representing the allocation of the portfolio's assets by sector.
     */
    private Map<String, Double> portfolioAllocationBySector;

    /**
     * A map representing the allocation of the portfolio's assets by geographical location.
     */
    private Map<String, Double> portfolioAllocationByGeographicalLocation;

    // --- Performance Metrics ---
    /**
     * A map of the portfolio's quarterly returns in absolute figures.
     */
    private Map<String, String> quarterlyReturns;

    /**
     * The annualized returns of the portfolio represented as a percentage.
     */
    private String annualizedReturnsPercentage;

    /**
     * A map of the portfolio's quarterly returns represented as percentages.
     */
    private Map<String, String> quarterlyReturnsPercentage;

    /**
     * A map indicating the date ranges for each quarter represented in the quarterly returns.
     */
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

    public String getPortfolioYoY() {
        return portfolioYoY;
    }

    public void setPortfolioYoY(String portfolioYoY) {
        this.portfolioYoY = portfolioYoY;
    }

    public String getPortfolioQoQ() {
        return portfolioQoQ;
    }

    public void setPortfolioQoQ(String portfolioQoQ) {
        this.portfolioQoQ = portfolioQoQ;
    }

    public String getPortfolioMoM() {
        return portfolioMoM;
    }

    public void setPortfolioMoM(String portfolioMoM) {
        this.portfolioMoM = portfolioMoM;
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
