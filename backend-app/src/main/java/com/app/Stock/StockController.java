package com.app.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.WildcardResponse;
import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * The StockController class handles HTTP requests related to stock operations.
 * It provides RESTful endpoints for creating, retrieving, checking existence,
 * and deleting stock records via the StockService.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock")
public class StockController {
    @Autowired
    private StockService stockService;

    /**
     * Creates a new stock record in the database.
     * 
     * @param stock The Stock object to be saved.
     * @return ResponseEntity with status 200 and WildcardResponse containing the result of the save operation.
     */
    @PostMapping("/create")
    public ResponseEntity<WildcardResponse> saveStock(@RequestBody Stock stock) {
        WildcardResponse res = stockService.save(stock);
        return ResponseEntity.status(200).body(res);
    }

    /**
     * Retrieves a stock by its symbol.
     * 
     * @param symbol The symbol of the stock to retrieve.
     * @return The Stock object if found, or null if not found.
     */
    @GetMapping("/{symbol}")
    public Stock getStockBySymbol(@PathVariable String symbol) {
        Stock response = stockService.getStock(symbol);
        if (response != null) {
            return response;
        } else {
            return null;
        }
    }

    /**
     * Retrieves all stocks in the database.
     * 
     * @return ResponseEntity containing a WildcardResponse with the data of all stocks, or an error message.
     */
    @GetMapping("/all")
    public ResponseEntity<WildcardResponse> getAllStocks() {
        WildcardResponse response = stockService.getAllStocks();
        if (response.getData() != null) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Checks whether a stock with the given symbol exists in the database.
     * 
     * @param symbol The symbol to check for existence.
     * @return ResponseEntity with status 200 and a Boolean indicating the existence of the stock.
     */
    @GetMapping("/exists/{symbol}")
    public ResponseEntity<Boolean> existsBySymbol(@PathVariable String symbol) {
        boolean exists = stockService.existsBySymbol(symbol);
        return ResponseEntity.status(HttpStatus.OK).body(exists);
    }

    /**
     * Deletes a stock by its symbol.
     * 
     * @param symbol The symbol of the stock to be deleted.
     * @return ResponseEntity with no content if the deletion was successful.
     */
    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> deleteStockBySymbol(@PathVariable String symbol) {
        stockService.deleteStockBySymbol(symbol);
        return ResponseEntity.noContent().build();
    }
}
