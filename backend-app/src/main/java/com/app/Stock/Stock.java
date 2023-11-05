package com.app.Stock;

import javax.persistence.*;
import java.util.*;
import com.app.StockDataPoint.*;
import com.app.StockDataPoint.StockDataPoint;

/**
 * The Stock class represents the entity model for a stock in the database.
 * It encapsulates details about a stock's symbol, information, last refreshed data,
 * the time zone of the last refresh, and a collection of time series StockDataPoint
 * associated with this stock.
 */
@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    private String symbol;
    private String information;
    private String lastRefreshed;
    private String timeZone;

    /**
     * The list of data points representing the time series data for this stock.
     * Each data point includes information such as the date, opening price, highest price, etc.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "symbol")
    private List<StockDataPoint> timeSeries = new ArrayList<>();

    // Hide for now cause not ready
    // @OneToOne(cascade = CascadeType.ALL)
    // @JoinColumn(name = "stock_series_id")
    // private StockSeries stockPrices;

    /**
     * Default constructor for JPA.
     */
    public Stock() {
    }

    /**
     * Constructs a new Stock with the given symbol, information about the stock, the date
     * and time when it was last refreshed, and the applicable time zone.
     *
     * @param symbol         The unique symbol identifying the stock.
     * @param information    Information or metadata about the stock.
     * @param lastRefreshed  The date and time at which the stock information was last refreshed.
     * @param timeZone       The time zone of the last refreshed date and time.
     */
    public Stock(String symbol, String information, String lastRefreshed, String timeZone) {
        this.symbol = symbol;
        this.information = information;
        this.lastRefreshed = lastRefreshed;
        this.timeZone = timeZone;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(String lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<StockDataPoint> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(List<StockDataPoint> timeSeries) {
        this.timeSeries = timeSeries;
    }

    /**
     * Returns a string representation of the Stock object, which includes its symbol,
     * information, last refresh date and time, time zone, and time series data points.
     *
     * @return A string representation of the Stock object.
     */
    // for debug
    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", information='" + information + '\'' +
                ", lastRefreshed='" + lastRefreshed + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", timeSeries=" + timeSeries +
                '}';
    }
}
