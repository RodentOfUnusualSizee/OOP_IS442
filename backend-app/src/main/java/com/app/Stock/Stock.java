package com.app.Stock;

import javax.persistence.*;

import com.app.StockSeries.StockSeries;

@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    private String symbol;
    private String companyName;
    private String exchangeTraded;
    private String currency;
    private String companySector;

    // Hide for now cause not ready
    // @OneToOne(cascade = CascadeType.ALL)
    // @JoinColumn(name = "stock_series_id")
    // private StockSeries stockPrices;

    public Stock() {
        // Default constructor
    }

    public Stock(String symbol, String companyName, String exchangeTraded, String currency, String companySector) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.exchangeTraded = exchangeTraded;
        this.currency = currency;
        this.companySector = companySector;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getExchangeTraded() {
        return exchangeTraded;
    }

    public void setExchangeTraded(String exchangeTraded) {
        this.exchangeTraded = exchangeTraded;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCompanySector() {
        return companySector;
    }

    public void setCompanySector(String companySector) {
        this.companySector = companySector;
    }

    // Commenting out the getter and setter for stockPrices
    // public StockSeries getStockPrices() {
    // return stockPrices;
    // }

    // public void setStockPrices(StockSeries stockPrices) {
    // this.stockPrices = stockPrices;
    // }
}
