package com.app.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // Get all stocks
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
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
