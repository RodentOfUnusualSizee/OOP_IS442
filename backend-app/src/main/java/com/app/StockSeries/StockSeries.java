package com.app.StockSeries;
import java.util.*;
import javax.persistence.*;

import com.app.StockDataPoint.StockDataPoint;

@Entity
@Table(name = "stock_series")
public class StockSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "stockSeries", cascade = CascadeType.ALL)
    private List<StockDataPoint> priceData;
    private String dataInterval;
    private String dataSource;

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

}
