package com.app.Stock;

import javax.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String symbol;
    private String companyName;
    private String exchangeTraded;
    private String currency;
    private String companySector;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_series_id")
    private StockSeries stockPrices;

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

    public StockSeries getStockPrices() {
        return stockPrices;
    }

    public void setStockPrices(StockSeries stockPrices) {
        this.stockPrices = stockPrices;
    }

}
