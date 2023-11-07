package com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly;

import java.util.*;
import com.app.StockDataPoint.StockDataPoint;

/**
 * Data Transfer Object (DTO) for transferring monthly time series data of a stock.
 */
public class StockTimeSeriesMonthlyDTO {

    private MetaData metaData;
    private List<StockDataPoint> timeSeries;

    // Getters
     /**
     * Gets the metadata related to the stock time series.
     *
     * @return MetaData object containing the metadata information.
     */
    public MetaData getMetaData() {
        return metaData;
    }

    /**
     * Sets the metadata for the stock time series.
     *
     * @param metaData MetaData object containing the information to be set.
     */
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * Gets the list of stock data points representing the time series.
     *
     * @return A list of StockDataPoint objects.
     */
    public List<StockDataPoint> getTimeSeries() {
        return timeSeries;
    }

    /**
     * Sets the list of stock data points for the time series.
     *
     * @param timeSeries A list of StockDataPoint objects to be set as the time series.
     */
    public void setTimeSeries(List<StockDataPoint> timeSeries) {
        this.timeSeries = timeSeries;
    }

     /**
     * Inner class to encapsulate metadata related to the stock's monthly time series.
     */
    public static class MetaData {
        private String information;
        private String symbol;
        private String lastRefreshed;
        // private String outputSize;
        private String timeZone;

        // Getters
        /**
         * Gets the information description of the data.
         *
         * @return A string representing the information description.
         */
        public String getInformation() {
            return information;
        }

        /**
         * Gets the stock symbol.
         *
         * @return A string representing the stock symbol.
         */
        public String getSymbol() {
            return symbol;
        }

        /**
         * Gets the last refreshed date of the stock data.
         *
         * @return A string representing the last refreshed date.
         */
        public String getLastRefreshed() {
            return lastRefreshed;
        }

        /**
         * Gets the time zone of the last refresh date.
         *
         * @return A string representing the time zone.
         */
        public String getTimeZone() {
            return timeZone;
        }

        // Setters
        /**
         * Sets the information description for the data.
         *
         * @param information A string containing the description of the information.
         */
        public void setInformation(String information) {
            this.information = information;
        }

        /**
         * Sets the stock symbol.
         *
         * @param symbol A string containing the stock symbol.
         */
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        /**
         * Sets the last refreshed date of the stock data.
         *
         * @param lastRefreshed A string representing the last refreshed date.
         */
        public void setLastRefreshed(String lastRefreshed) {
            this.lastRefreshed = lastRefreshed;
        }

        /**
         * Sets the time zone of the last refresh date.
         *
         * @param timeZone A string containing the time zone information.
         */
        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }
    }
   
}
