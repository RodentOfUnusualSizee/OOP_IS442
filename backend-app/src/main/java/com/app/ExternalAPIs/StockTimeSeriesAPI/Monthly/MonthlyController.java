package com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

// Postman: http://localhost:8080/api/stock/monthlyTimeSeries/{TickerSymbol}
/**
 * REST Controller providing endpoints for retrieving monthly stock time series data.
 */
@RestController
@Service
@RequestMapping("/api/stock/monthlyTimeSeries")
public class MonthlyController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MonthlyService monthlyService;

    private String apiKey;

    private Map<String, StockTimeSeriesMonthlyDTO> stockDataCache = new HashMap<>();

    /**
     * Handles GET requests for monthly stock time series data by the stock symbol.
     *
     * @param symbol The stock ticker symbol.
     * @return A StockTimeSeriesMonthlyDTO containing the stock's monthly time series data.
     */
    @GetMapping("/{symbol}")
    public StockTimeSeriesMonthlyDTO getMonthlyTimeSeries(@PathVariable String symbol) {

        return monthlyService.getMonthlyTimeSeriesProcessed(symbol);
    }

}
