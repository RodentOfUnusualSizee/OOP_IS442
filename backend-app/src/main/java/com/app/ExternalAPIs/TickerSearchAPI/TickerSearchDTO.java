package com.app.ExternalAPIs.TickerSearchAPI;

import java.util.List;

/**
 * Data Transfer Object to hold the search results for ticker symbols.
 */
public class TickerSearchDTO {

    private List<Match> bestMatches;

    /**
     * Gets the list of best match ticker symbols.
     * @return A list of {@link Match} instances representing the best matches.
     */
    public List<Match> getBestMatches() {
        return bestMatches;
    }

    /**
     * Sets the list of best match ticker symbols.
     * @param bestMatches A list of {@link Match} instances representing the best matches.
     */
    public void setBestMatches(List<Match> bestMatches) {
        this.bestMatches = bestMatches;
    }

    /**
     * Static inner class representing a single match of a ticker symbol search.
     */
    public static class Match {
        private String symbol;
        private String name;
        private String type;
        private String region;
        private String marketOpen;
        private String marketClose;
        private String timezone;
        private String currency;
        private String matchScore;

        // Getters
        /**
         * Gets the symbol of the match.
         * @return The ticker symbol.
         */
        public String getSymbol() {
            return symbol;
        }
        public String getName() {
            return name;
        }
        public String getType() {
            return type;
        }
        public String getRegion() {
            return region;
        }
        public String getMarketOpen() {
            return marketOpen;
        }
        public String getMarketClose() {
            return marketClose;
        }
        public String getTimezone() {
            return timezone;
        }
        public String getCurrency() {
            return currency;
        }
        public String getMatchScore() {
            return matchScore;
        }

        // Setters
        /**
         * Sets the symbol of the match.
         * @param symbol The ticker symbol to set.
         */
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setType(String type) {
            this.type = type;
        }
        public void setRegion(String region) {
            this.region = region;
        }
        public void setMarketOpen(String marketOpen) {
            this.marketOpen = marketOpen;
        }
        public void setMarketClose(String marketClose) {
            this.marketClose = marketClose;
        }
        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
        public void setCurrency(String currency) {
            this.currency = currency;
        }
        public void setMatchScore(String matchScore) {
            this.matchScore = matchScore;
        }
    }
}
