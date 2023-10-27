package com.app.Stock;

import javax.persistence.*;
import java.util.*;
import com.app.StockDataPoint.*;

@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    private String symbol;
    private String information;
    private String lastRefreshed;
    private String timeZone;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "symbol")
    private List<StockDataPoint> timeSeries = new ArrayList<>();

    // Hide for now cause not ready
    // @OneToOne(cascade = CascadeType.ALL)
    // @JoinColumn(name = "stock_series_id")
    // private StockSeries stockPrices;

    public Stock() {
        // Default constructor
    }

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
