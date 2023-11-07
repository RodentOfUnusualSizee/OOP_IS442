package com.app;

import com.app.Stock.Stock;
import com.app.Stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * MainApp is the entry point of the Spring Boot application.
 * It configures the startup of the Spring ApplicationContext and initializes
 * the necessary beans and services required for the application to run.
 * 
 * @author Gerald Lee, Caleb Cheong, Brandon Christopher, Lin Tao, Lam Ching Rou
 */
@SpringBootApplication
public class MainApp {

    private final StockService stockService;

    /**
     * Constructs the MainApp with necessary dependencies.
     * 
     * @param stockService The service related to stock operations which will be auto-wired.
     */
    @Autowired
    public MainApp(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * The main method which serves as the entry point for the Spring Boot application.
     *
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }

    /**
     * Command line runner that sets up demo data on application startup.
     * Currently, this method is commented out and not in use.
     * To enable, simply uncomment the method.
     *
     * @return CommandLineRunner a callback interface that can be used to run
     *         code after the Spring Application Context has started.
     */
    // @Bean
    // public CommandLineRunner demoData() {
    //     return args -> {
    //         // Create and save mock stocks using constructor
    //         Stock apple = new Stock("AAPL", "Apple Inc.", "NASDAQ", "USD", "Technology");
    //         stockService.saveStock(apple);

    //         // You can add more mock stocks
    //         Stock microsoft = new Stock("MSFT", "Microsoft Corp.", "NASDAQ", "USD", "Technology");
    //         stockService.saveStock(microsoft);
    //     };
    // }

    /**
     * Defines a RestTemplate bean to be used for making REST calls.
     * This is a convenient alternative to standard HTTP client libraries.
     *
     * @return RestTemplate a synchronous client to perform HTTP requests.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
