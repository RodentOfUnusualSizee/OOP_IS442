package com.app.Portfolio.PortfolioComparisionDTOs;

public class PortfolioComparisonDTO {
    private FinancialStatsDTO portfolio1Stats;
    private FinancialStatsDTO portfolio2Stats;
    private FinancialStatsDTO differenceStats;

    public PortfolioComparisonDTO() {
    };

    public PortfolioComparisonDTO(FinancialStatsDTO portfolio1Stats, FinancialStatsDTO portfolio2Stats,
            FinancialStatsDTO differenceStats) {
        this.portfolio1Stats = portfolio1Stats;
        this.portfolio2Stats = portfolio2Stats;
        this.differenceStats = differenceStats;
    }

    public FinancialStatsDTO getPortfolio1Stats() {
        return portfolio1Stats;
    }

    public void setPortfolio1Stats(FinancialStatsDTO portfolio1Stats) {
        this.portfolio1Stats = portfolio1Stats;
    }

    public FinancialStatsDTO getPortfolio2Stats() {
        return portfolio2Stats;
    }

    public void setPortfolio2Stats(FinancialStatsDTO portfolio2Stats) {
        this.portfolio2Stats = portfolio2Stats;
    }

    public FinancialStatsDTO getDifferenceStats() {
        return differenceStats;
    }

    public void setDifferenceStats(FinancialStatsDTO differenceStats) {
        this.differenceStats = differenceStats;
    }

}
