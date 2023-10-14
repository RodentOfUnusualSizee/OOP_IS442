package com.app.Order;
import javax.persistence.*;

import com.app.Stock.Stock;

@Entity
@Table(name = "user_order") // Specify a custom table name
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private int orderID;

    @OneToOne // Define a one-to-one relationship
    private Stock stock;

    private float price;
    private char position;
    private int quantity;
    // ------------------ Getters and Setters (Start) ------------------
    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
        this.orderID = orderID;
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
