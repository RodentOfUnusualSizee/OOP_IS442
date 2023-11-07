package com.app.StockDataPoint;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.app.Stock.Stock;

/**
 * The StockDataPoint class represents a single data point for a stock's performance
 * on a given date, including its opening, closing, high, and low prices, as well as
 * the volume of shares traded.
 */
@Entity
public class StockDataPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    /**
     * Default constructor for JPA.
     */
    public StockDataPoint() {
    }

    /**
     * Constructs a new StockDataPoint with the specified date, opening price, high price, 
     * low price, closing price, and volume of shares traded.
     *
     * @param date   The date of the stock data.
     * @param open   The opening price of the stock.
     * @param high   The highest price of the stock.
     * @param low    The lowest price of the stock.
     * @param close  The closing price of the stock.
     * @param volume The volume of shares traded.
     */
    public StockDataPoint(String date, double open, double high, double low, double close, int volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    // Getters
    public String getDate() {
        return date;
    }

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

    public void setDate(String date) {
        this.date = date;
    }

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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
