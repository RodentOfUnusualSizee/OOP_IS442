package com.app.Portfolio;
import org.springframework.stereotype.Service;

import com.app.Stock.Stock;

@Service
public class Investment {
    private int investmentID;
    private Stock stock;
    private float price;
    private char position;
    private int quantity;
    // ------------------ Getters and Setters (Start) ------------------
    public int getInvestmentID() {
        return investmentID;
    }
    public void setInvestmentID(int investmentID) {
        this.investmentID = investmentID;
    }
    public Stock getStock() {
        return stock;
    }
    public void setStock(Stock stock) {
        this.stock = stock;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public char getPosition() {
        return position;
    }
    public void setPosition(char position) {
        this.position = position;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    // ------------------- Getters and Setters (End) -------------------
}
