package com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly;

import java.util.*;
import com.app.StockDataPoint.StockDataPoint;
// Stock Time Series Data Transfer Object
public class StockTimeSeriesMonthlyDTO {

    private MetaData metaData;
    private List<StockDataPoint> timeSeries;

    // Getters
    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public List<StockDataPoint> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(List<StockDataPoint> timeSeries) {
        this.timeSeries = timeSeries;
    }

    // Inner class for Meta Data
    public static class MetaData {
        private String information;
        private String symbol;
        private String lastRefreshed;
        // private String outputSize;
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

        // public String getOutputSize() {
        // return outputSize;
        // }
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

        // public void setOutputSize(String outputSize) {
        // this.outputSize = outputSize;
        // }
        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }
    }
   
}
