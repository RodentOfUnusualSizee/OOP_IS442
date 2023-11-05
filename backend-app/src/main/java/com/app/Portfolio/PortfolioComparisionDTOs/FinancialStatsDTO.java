package com.app.Portfolio.PortfolioComparisionDTOs;

import java.util.Map;

/**
 * Data Transfer Object for holding financial statistics of a portfolio.
 */
public class FinancialStatsDTO {
    private double currentTotalPortfolioValue;
    private double portfolioBeta;
    private double informationRatio;
    private Map<String, String> quarterlyReturns;
    private String annualizedReturnsPercentage;
    private Map<String, String> quarterlyReturnsPercentage;

    /**
     * Default constructor.
     */
    public FinancialStatsDTO() {
    };

    /**
     * Constructs a new FinancialStatsDTO with specified financial details.
     *
     * @param currentTotalPortfolioValue The current total value of the portfolio.
     * @param portfolioBeta              The beta value of the portfolio.
     * @param informationRatio           The information ratio of the portfolio.
     * @param quarterlyReturns           A map of quarterly returns.
     * @param annualizedReturnsPercentage The annualized returns percentage.
     * @param quarterlyReturnsPercentage A map of quarterly returns percentages.
     */
    public FinancialStatsDTO(double currentTotalPortfolioValue, double portfolioBeta, double informationRatio,
            Map<String, String> quarterlyReturns, String annualizedReturnsPercentage,
            Map<String, String> quarterlyReturnsPercentage) {
        this.currentTotalPortfolioValue = currentTotalPortfolioValue;
        this.portfolioBeta = portfolioBeta;
        this.informationRatio = informationRatio;
        this.quarterlyReturns = quarterlyReturns;
        this.annualizedReturnsPercentage = annualizedReturnsPercentage;
        this.quarterlyReturnsPercentage = quarterlyReturnsPercentage;
    };

    /**
     * Gets the current total portfolio value.
     *
     * @return The current total value of the portfolio.
     */
    public double getCurrentTotalPortfolioValue() {
        return currentTotalPortfolioValue;
    };

    /**
     * Sets the current total portfolio value.
     *
     * @param currentTotalPortfolioValue The current total value to set for the portfolio.
     */
    public void setCurrentTotalPortfolioValue(double currentTotalPortfolioValue) {
        this.currentTotalPortfolioValue = currentTotalPortfolioValue;
    };

    /**
     * Gets the portfolio beta.
     *
     * @return The beta value of the portfolio.
     */
    public double getPortfolioBeta() {
        return portfolioBeta;
    };

    /**
     * Sets the portfolio beta.
     *
     * @param portfolioBeta The beta value to set for the portfolio.
     */
    public void setPortfolioBeta(double portfolioBeta) {
        this.portfolioBeta = portfolioBeta;
    };

    /**
     * Gets the information ratio.
     *
     * @return The information ratio of the portfolio.
     */
    public double getInformationRatio() {
        return informationRatio;
    };

    /**
     * Sets the information ratio.
     *
     * @param informationRatio The information ratio to set for the portfolio.
     */
    public void setInformationRatio(double informationRatio) {
        this.informationRatio = informationRatio;
    };

    /**
     * Gets the quarterly returns.
     *
     * @return A map of the quarterly returns.
     */
    public Map<String, String> getQuarterlyReturns() {
        return quarterlyReturns;
    };

    /**
     * Sets the quarterly returns.
     *
     * @param quarterlyReturns A map of the quarterly returns to set.
     */
    public void setQuarterlyReturns(Map<String, String> quarterlyReturns) {
        this.quarterlyReturns = quarterlyReturns;
    };

    /**
     * Gets the annualized returns percentage.
     *
     * @return The annualized returns percentage as a String.
     */

    public String getAnnualizedReturnsPercentage() {
        return annualizedReturnsPercentage;
    };

    /**
     * Sets the annualized returns percentage.
     *
     * @param annualizedReturnsPercentage The annualized returns percentage to set.
     */
    public void setAnnualizedReturnsPercentage(String annualizedReturnsPercentage) {
        this.annualizedReturnsPercentage = annualizedReturnsPercentage;
    };

    /**
     * Gets the quarterly returns percentage.
     *
     * @return A map of the quarterly returns percentages.
     */
    public Map<String, String> getQuarterlyReturnsPercentage() {
        return quarterlyReturnsPercentage;
    };

    /**
     * Sets the quarterly returns percentage.
     *
     * @param quarterlyReturnsPercentage A map of the quarterly returns percentages to set.
     */
    public void setQuarterlyReturnsPercentage(Map<String, String> quarterlyReturnsPercentage) {
        this.quarterlyReturnsPercentage = quarterlyReturnsPercentage;
    };

}
