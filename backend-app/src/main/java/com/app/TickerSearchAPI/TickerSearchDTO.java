package com.app.TickerSearchAPI;

import java.util.List;

public class TickerSearchDTO {

    private List<Match> bestMatches;

    public List<Match> getBestMatches() {
        return bestMatches;
    }

    public void setBestMatches(List<Match> bestMatches) {
        this.bestMatches = bestMatches;
    }

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
