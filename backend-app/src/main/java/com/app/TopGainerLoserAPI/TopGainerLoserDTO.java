package com.app.TopGainerLoserAPI;

import java.util.List;

public class TopGainerLoserDTO {

    private String metadata;
    private String lastUpdated;
    private List<StockInfo> topGainers;
    private List<StockInfo> topLosers;
    private List<StockInfo> mostActivelyTraded;

    // Default constructor
    public TopGainerLoserDTO() {}

    // Getters
    public String getMetadata() {
        return metadata;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public List<StockInfo> getTopGainers() {
        return topGainers;
    }

    public List<StockInfo> getTopLosers() {
        return topLosers;
    }

    public List<StockInfo> getMostActivelyTraded() {
        return mostActivelyTraded;
    }

    // Setters
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setTopGainers(List<StockInfo> topGainers) {
        this.topGainers = topGainers;
    }

    public void setTopLosers(List<StockInfo> topLosers) {
        this.topLosers = topLosers;
    }

    public void setMostActivelyTraded(List<StockInfo> mostActivelyTraded) {
        this.mostActivelyTraded = mostActivelyTraded;
    }

    // Inner static class to capture the details of each stock
    public static class StockInfo {
        private String ticker;
        private String price;
        private String changeAmount;
        private String changePercentage;
        private String volume;

        // Getters
        public String getTicker() {
            return ticker;
        }

        public String getPrice() {
            return price;
        }

        public String getChangeAmount() {
            return changeAmount;
        }

        public String getChangePercentage() {
            return changePercentage;
        }

        public String getVolume() {
            return volume;
        }

        // Setters
        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setChangeAmount(String changeAmount) {
            this.changeAmount = changeAmount;
        }

        public void setChangePercentage(String changePercentage) {
            this.changePercentage = changePercentage;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }
    }
}
