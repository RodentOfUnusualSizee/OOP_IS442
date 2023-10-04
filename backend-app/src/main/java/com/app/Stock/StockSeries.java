package com.app.Stock;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class StockSeries {
    private List<StockDataPoint> priceData;
    // private Stock stock;
    private String dataInterval;
    private String dataSource;

    // ------------------ Getters and Setters (Start) ------------------

    // public Stock getStock() {
    //     return stock;
    // }

    // public void setStock(Stock stock) {
    //     this.stock = stock;
    // }

    public List<StockDataPoint> getPriceData() {
        return priceData;
    }

    public void setPriceData(ArrayList<StockDataPoint> priceData) {
        this.priceData = priceData;
    }

    public String getDataInterval() {
        return dataInterval;
    }

    public void setDataInterval(String dataInterval) {
        this.dataInterval = dataInterval;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    

    // ------------------- Getters and Setters (End) -------------------
}
