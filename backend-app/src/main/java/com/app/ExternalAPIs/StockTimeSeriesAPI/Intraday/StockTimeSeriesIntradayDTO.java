package com.app.ExternalAPIs.StockTimeSeriesAPI.Intraday;
import java.util.*;

// Stock Time Series Data Transfer Object
public class StockTimeSeriesIntradayDTO {

    private MetaData metaData;
    private Map<String, IntradayStockData> intradayTimeSeries;

    // Getters
    public MetaData getMetaData() {
        return metaData;
    }
    public Map<String, IntradayStockData> getTimeSeries() {
        return intradayTimeSeries;
    }

    // Setters
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
    public void setTimeSeries(Map<String, IntradayStockData> intradayTimeSeries) {
        this.intradayTimeSeries = intradayTimeSeries;
    }

    // Inner class for Meta Data
    public static class MetaData {
        private String information;
        private String symbol;
        private String lastRefreshed;
        private String interval; // 1,5,15,30,60
        private String outputSize;
        private String timeZone;

        // Getters
        public String getInformation() {
            return information;
        }
        public String getSymbol() {
            return symbol;
        }
        public String getLastRefreshed() {
            return lastRefreshed;
        }
        public String getInterval() {
            return interval;
        }
        public String getOutputSize() {
            return outputSize;
        }
        public String getTimeZone() {
            return timeZone;
        }

        // Setters
        public void setInformation(String information) {
            this.information = information;
        }
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        public void setLastRefreshed(String lastRefreshed) {
            this.lastRefreshed = lastRefreshed;
        }
        public void setInterval(String interval) {
            this.interval = interval;
        }
        public void setOutputSize(String outputSize) {
            this.outputSize = outputSize;
        }
        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }
    }

    // Inner class for Intraday Stock Data
    public static class IntradayStockData {
        private double open;
        private double high;
        private double low;
        private double close;
        private long volume;

        // Getters
        public double getOpen() {
            return open;
        }
        public double getHigh() {
            return high;
        }
        public double getLow() {
            return low;
        }
        public double getClose() {
            return close;
        }
        public long getVolume() {
            return volume;
        }

        // Setters
        public void setOpen(double open) {
            this.open = open;
        }
        public void setHigh(double high) {
            this.high = high;
        }
        public void setLow(double low) {
            this.low = low;
        }
        public void setClose(double close) {
            this.close = close;
        }
        public void setVolume(long volume) {
            this.volume = volume;
        }
    }
}
