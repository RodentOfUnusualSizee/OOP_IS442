package com.app.Position;

import java.io.Serializable;

import javax.persistence.*;
import com.app.Stock.Stock;

@Entity
@Table(name = "user_position")
public class Position implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private int positionID;

    private String stockSymbol;
    private float price;
    private String position;
    private int quantity;

    public Position() {
    }

    public Position(int positionID, String stockSymbol, float price, String position, int quantity) {
        this.positionID = positionID;
        this.stockSymbol = stockSymbol;
        this.price = price;
        this.position = position;
        this.quantity = quantity;
    }

    // ------------------ Getters and Setters (Start) ------------------
    public int getPositionID() {
        return positionID;
    }

    public void setPositionID(int positionID) {
        this.positionID = positionID;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
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
