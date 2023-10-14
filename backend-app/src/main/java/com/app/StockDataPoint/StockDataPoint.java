package com.app.StockDataPoint;
import java.time.LocalDateTime;

import javax.persistence.*;

import com.app.StockSeries.StockSeries;

@Entity
@Table(name = "stock_data_points")
public class StockDataPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timeStamp;
    private float price;
    private int volume;

    @ManyToOne
    @JoinColumn(name = "stock_series_id")
    private StockSeries stockSeries;

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

}
