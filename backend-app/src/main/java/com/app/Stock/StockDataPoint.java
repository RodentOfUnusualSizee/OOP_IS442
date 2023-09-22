package com.app.Stock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class StockDataPoint {
    private LocalDateTime timeStamp;
    private float price;
    private int volume;
    // ------------------ Getters and Setters (Start) ------------------

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

    // ------------------- Getters and Setters (End) -------------------
}
