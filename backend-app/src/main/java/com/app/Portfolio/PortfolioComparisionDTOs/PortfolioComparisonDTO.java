package com.app.Portfolio.PortfolioComparisionDTOs;

/**
 * Data Transfer Object for holding the comparison between two financial portfolios.
 */
public class PortfolioComparisonDTO {
    private FinancialStatsDTO portfolio1Stats;
    private FinancialStatsDTO portfolio2Stats;
    private FinancialStatsDTO differenceStats;

    /**
     * Default constructor for PortfolioComparisonDTO.
     */
    public PortfolioComparisonDTO() {
    };

    /**
     * Constructs a PortfolioComparisonDTO with individual financial statistics for two portfolios
     * and their difference.
     *
     * @param portfolio1Stats The financial statistics of the first portfolio.
     * @param portfolio2Stats The financial statistics of the second portfolio.
     * @param differenceStats The calculated difference in financial statistics between the two portfolios.
     */
    public PortfolioComparisonDTO(FinancialStatsDTO portfolio1Stats, FinancialStatsDTO portfolio2Stats,
            FinancialStatsDTO differenceStats) {
        this.portfolio1Stats = portfolio1Stats;
        this.portfolio2Stats = portfolio2Stats;
        this.differenceStats = differenceStats;
    }

    /**
     * Gets the financial statistics of the first portfolio.
     *
     * @return The FinancialStatsDTO object representing the first portfolio's stats.
     */
    public FinancialStatsDTO getPortfolio1Stats() {
        return portfolio1Stats;
    }

    /**
     * Sets the financial statistics for the first portfolio.
     *
     * @param portfolio1Stats The FinancialStatsDTO to set for the first portfolio.
     */
    public void setPortfolio1Stats(FinancialStatsDTO portfolio1Stats) {
        this.portfolio1Stats = portfolio1Stats;
    }

    /**
     * Gets the financial statistics of the second portfolio.
     *
     * @return The FinancialStatsDTO object representing the second portfolio's stats.
     */
    public FinancialStatsDTO getPortfolio2Stats() {
        return portfolio2Stats;
    }

    /**
     * Sets the financial statistics for the second portfolio.
     *
     * @param portfolio2Stats The FinancialStatsDTO to set for the second portfolio.
     */
    public void setPortfolio2Stats(FinancialStatsDTO portfolio2Stats) {
        this.portfolio2Stats = portfolio2Stats;
    }

    /**
     * Gets the financial statistics representing the difference between the two portfolios.
     *
     * @return The FinancialStatsDTO object representing the difference in stats.
     */
    public FinancialStatsDTO getDifferenceStats() {
        return differenceStats;
    }

    /**
     * Sets the financial statistics representing the difference between the two portfolios.
     *
     * @param differenceStats The FinancialStatsDTO representing the difference to set.
     */
    public void setDifferenceStats(FinancialStatsDTO differenceStats) {
        this.differenceStats = differenceStats;
    }

}
