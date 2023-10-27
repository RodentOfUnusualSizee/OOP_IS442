package com.app.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.WildcardResponse;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    public WildcardResponse save(Stock stock) {
        try {
            stockRepository.save(stock);
            return new WildcardResponse(true, "Success", stock);
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), stock);
        }
    }

    public Stock getStock(String symbol) {
        Stock res = stockRepository.findById(symbol).orElse(null);
        return res;
      
    }

    // Get all stocks
    public WildcardResponse getAllStocks() {
        try {
            List<Stock> res = stockRepository.findAll();
            return new WildcardResponse(true, "Success", res);
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), null);
        }
    }

    // Check if exists
    public boolean existsBySymbol(String symbol) {
        return stockRepository.existsById(symbol);
    }

    // Get a single stock by its symbol
    public Optional<Stock> getStockBySymbol(String symbol) {
        return stockRepository.findById(symbol);
    }

    // Save or update a stock
    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    // Delete a stock by its symbol
    public void deleteStockBySymbol(String symbol) {
        stockRepository.deleteById(symbol);
    }
}
