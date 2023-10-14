package com.app;

import com.app.Stock.Stock;
import com.app.Stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApp {

    private final StockService stockService;

    @Autowired
    public MainApp(StockService stockService) {
        this.stockService = stockService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }

    @Bean
    public CommandLineRunner demoData() {
        return args -> {
            // Create and save mock stocks using constructor
            Stock apple = new Stock("AAPL", "Apple Inc.", "NASDAQ", "USD", "Technology");
            stockService.saveStock(apple);

            // You can add more mock stocks 
            // Stock microsoft = new Stock("MSFT", "Microsoft Corp.", "NASDAQ", "USD", "Technology");
            // stockService.saveStock(microsoft);
        };
    }
}
