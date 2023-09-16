package org.system.backendapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class StockDataPoint {
    private LocalDataTime timeStamp;
    private float price;
    private int volume;
    // ------------------ Getters and Setters (Start) ------------------

    public LocalDataTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDataTime timeStamp) {
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
