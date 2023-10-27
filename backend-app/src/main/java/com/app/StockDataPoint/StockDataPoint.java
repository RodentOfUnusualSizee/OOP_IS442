package com.app.StockDataPoint;
import java.time.LocalDateTime;

import javax.persistence.*;

import com.app.Stock.Stock;
@Entity
public class StockDataPoint {
    @Id
    private String symbol;
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    @ManyToOne
    @JoinColumn(name = "stock_symbol", referencedColumnName = "symbol")
    private Stock stock;

    //Default
    public StockDataPoint() {
    }


    public StockDataPoint(String date, double open, double high, double low, double close, int volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    //Getters
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


    public Stock getStock() {
        return stock;
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


    public void setStock(Stock stock) {
        this.stock = stock;
    }
    
    



}
