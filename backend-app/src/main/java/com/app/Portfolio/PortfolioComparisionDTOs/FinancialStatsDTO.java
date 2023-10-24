package com.app.Portfolio.PortfolioComparisionDTOs;

import java.util.Map;

public class FinancialStatsDTO {
    private double currentTotalPortfolioValue;
    private double portfolioBeta;
    private double informationRatio;
    private Map<String, String> quarterlyReturns;
    private String annualizedReturnsPercentage;
    private Map<String, String> quarterlyReturnsPercentage;

    public FinancialStatsDTO() {
    };

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

    public double getCurrentTotalPortfolioValue() {
        return currentTotalPortfolioValue;
    };

    public void setCurrentTotalPortfolioValue(double currentTotalPortfolioValue) {
        this.currentTotalPortfolioValue = currentTotalPortfolioValue;
    };

    public double getPortfolioBeta() {
        return portfolioBeta;
    };

    public void setPortfolioBeta(double portfolioBeta) {
        this.portfolioBeta = portfolioBeta;
    };

    public double getInformationRatio() {
        return informationRatio;
    };

    public void setInformationRatio(double informationRatio) {
        this.informationRatio = informationRatio;
    };

    public Map<String, String> getQuarterlyReturns() {
        return quarterlyReturns;
    };

    public void setQuarterlyReturns(Map<String, String> quarterlyReturns) {
        this.quarterlyReturns = quarterlyReturns;
    };

    public String getAnnualizedReturnsPercentage() {
        return annualizedReturnsPercentage;
    };

    public void setAnnualizedReturnsPercentage(String annualizedReturnsPercentage) {
        this.annualizedReturnsPercentage = annualizedReturnsPercentage;
    };

    public Map<String, String> getQuarterlyReturnsPercentage() {
        return quarterlyReturnsPercentage;
    };

    public void setQuarterlyReturnsPercentage(Map<String, String> quarterlyReturnsPercentage) {
        this.quarterlyReturnsPercentage = quarterlyReturnsPercentage;
    };

}
