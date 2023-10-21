package com.app.Position;

import java.io.Serializable;

import javax.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.app.Stock.Stock;
import java.util.Date;

@Entity
@Table(name = "user_position")
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define the generation strategy for the ID
    private int positionID;

    private String stockSymbol;
    private float price;
    private String position;
    private int quantity;
    private String stockSector;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedTimestamp;

    @Temporal(TemporalType.DATE)
    private Date positionAddDate;

    public Position() {
    }

    public Position(int positionID, String stockSymbol, float price, String position, int quantity,
            Date positionAddDate) {
        this.positionID = positionID;
        this.stockSymbol = stockSymbol;
        this.price = price;
        this.position = position;
        this.quantity = quantity;
        this.createdTimestamp = new Date();
        this.positionAddDate = positionAddDate;
    }

    // ------------------ Getters and Setters (Start) ------------------
    public int getPositionID() {
        return positionID;
    }

    public void setPositionID(int positionID) {
        this.positionID = positionID;
    }

    public String getStockSector() {
        return stockSector;
    }

    public void setStockSector(String stockSector) {
        this.stockSector = stockSector;
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

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    public Date getPositionAddDate() {
        return positionAddDate;
    }

    public void setPositionAddDate(Date positionAddDate) {
        this.positionAddDate = positionAddDate;
    }
}