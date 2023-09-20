package org.system.backendapp;

public class StockSeries {
    private Stock stock;
    private Array<StockDataPoint> priceData;
    private String dataSource;

    // ------------------ Getters and Setters (Start) ------------------

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Array<StockDataPoint> getPriceData() {
        return priceData;
    }

    public void setPriceData(Array<StockDataPoint> priceData) {
        this.priceData = priceData;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    // ------------------- Getters and Setters (End) -------------------
}
