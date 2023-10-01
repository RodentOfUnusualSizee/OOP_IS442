package com.app.Stock;

import javax.persistence.*;

import org.springframework.stereotype.Service;

@Entity //@Controller @Respository
@Table(name = "stocks") // Specify a custom table name
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private String symbol;
    private String companyName;
    private String exchangeTraded;
    private String currency;
    private String companySector;
    private StockSeries stockPrices;
    private String stockId;

    // ------------------ Getters and Setters (Start) ------------------

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

    // ------------------- Getters and Setters (End) -------------------
}
