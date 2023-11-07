package com.app.Position;

import java.io.Serializable;

import javax.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.app.Stock.Stock;
import java.util.Date;

/**
 * The Position class represents a user's financial position in a specific stock,
 * including the stock's symbol, price, sector, geographical location, and other relevant details.
 */
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
    private String stockGeographicalLocation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedTimestamp;

    @Temporal(TemporalType.DATE)
    private Date positionAddDate;

     /**
     * Default constructor for JPA.
     */
    public Position() {
    }

    /**
     * Constructs a Position with the specified details.
     *
     * @param positionID      The ID of the position.
     * @param stockSymbol     The symbol of the stock.
     * @param price           The price at which the position was taken.
     * @param position        The type of position (e.g., long, short).
     * @param quantity        The quantity of stocks in the position.
     * @param positionAddDate The date when the position was added.
     */
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

    public String getStockGeographicalLocation() {
        return stockGeographicalLocation;
    }

    public void setStockGeographicalLocation(String stockGeographicalLocation) {
        this.stockGeographicalLocation = stockGeographicalLocation;
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

    /**
     * Gets the timestamp when the position was created.
     *
     * @return The timestamp of position creation.
     */
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Sets the timestamp when the position was created.
     *
     * @param createdTimestamp The creation timestamp to set.
     */
    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * Gets the timestamp when the position was last modified.
     *
     * @return The last modification timestamp.
     */
    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    /**
     * Sets the timestamp when the position was last modified.
     *
     * @param lastModifiedTimestamp The last modification timestamp to set.
     */
    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    /**
     * Gets the date when the position was added.
     *
     * @return The date when the position was added.
     */
    public Date getPositionAddDate() {
        return positionAddDate;
    }

    /**
     * Sets the date when the position was added.
     *
     * @param positionAddDate The date to set when the position was added.
     */
    public void setPositionAddDate(Date positionAddDate) {
        this.positionAddDate = positionAddDate;
    }
}