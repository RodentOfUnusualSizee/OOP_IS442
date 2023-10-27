package com.app.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.WildcardResponse;
import javax.persistence.EntityNotFoundException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock")
public class StockController {
    @Autowired
    private StockService stockService;

    @PostMapping("/create")
    public ResponseEntity<WildcardResponse> saveStock(@RequestBody Stock stock) {
        WildcardResponse res = stockService.save(stock);
        return ResponseEntity.status(200).body(res);
    }


    @GetMapping("/{symbol}")
    public Stock getStockBySymbol(@PathVariable String symbol) {
        Stock response = stockService.getStock(symbol);
        if (response != null) {
            return response;
        } else {
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<WildcardResponse> getAllStocks() {
        WildcardResponse response = stockService.getAllStocks();
        if (response.getData() != null) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/exists/{symbol}")
    public ResponseEntity<Boolean> existsBySymbol(@PathVariable String symbol) {
        boolean exists = stockService.existsBySymbol(symbol);
        return ResponseEntity.status(HttpStatus.OK).body(exists);
    }

    
    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> deleteStockBySymbol(@PathVariable String symbol) {
        stockService.deleteStockBySymbol(symbol);
        return ResponseEntity.noContent().build();
    }
}

   
