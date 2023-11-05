package com.app.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.WildcardResponse;

import java.util.List;
import java.util.Optional;

/**
 * The StockService class provides business logic to interact with the underlying
 * stock repository. It provides methods to save, retrieve, and delete stock entities,
 * as well as check their existence within the database.
 */
@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    /**
     * Saves a stock entity to the repository. If the stock already exists, it updates the existing record.
     *
     * @param stock The Stock entity to be saved.
     * @return A WildcardResponse containing the status of the operation, a message, and the stock entity.
     */
    public WildcardResponse save(Stock stock) {
        try {
            stockRepository.save(stock);
            return new WildcardResponse(true, "Success", stock);
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), stock);
        }
    }

     /**
     * Retrieves a stock by its symbol.
     *
     * @param symbol The symbol identifier of the stock.
     * @return The Stock entity if found, or null if no stock is found with the given symbol.
     */
    public Stock getStock(String symbol) {
        Stock res = stockRepository.findById(symbol).orElse(null);
        return res;
      
    }

     /**
     * Retrieves all stock entities from the repository.
     *
     * @return A WildcardResponse containing the status of the operation, a message, and a list of all stock entities.
     */
    public WildcardResponse getAllStocks() {
        try {
            List<Stock> res = stockRepository.findAll();
            return new WildcardResponse(true, "Success", res);
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), null);
        }
    }

    /**
     * Checks if a stock exists in the repository by its symbol.
     *
     * @param symbol The symbol identifier of the stock.
     * @return true if the stock exists, false otherwise.
     */
    public boolean existsBySymbol(String symbol) {
        return stockRepository.existsById(symbol);
    }

    /**
     * Retrieves a stock by its symbol.
     *
     * @param symbol The symbol identifier of the stock.
     * @return An Optional containing the stock if found, or an empty Optional if not.
     */
    public Optional<Stock> getStockBySymbol(String symbol) {
        return stockRepository.findById(symbol);
    }

    /**
     * Saves a stock entity to the repository. This method can be used for both creating a new stock
     * or updating an existing one.
     *
     * @param stock The Stock entity to be saved.
     * @return The saved Stock entity.
     */
    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    /**
     * Deletes a stock from the repository by its symbol.
     *
     * @param symbol The symbol identifier of the stock to be deleted.
     */
    public void deleteStockBySymbol(String symbol) {
        stockRepository.deleteById(symbol);
    }
}
