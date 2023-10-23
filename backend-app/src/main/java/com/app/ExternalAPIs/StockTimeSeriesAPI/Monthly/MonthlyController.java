package com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

// Postman: http://localhost:8080/api/stock/monthlyTimeSeries/{TickerSymbol}
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

    @GetMapping("/{symbol}")
    public StockTimeSeriesMonthlyDTO getMonthlyTimeSeries(@PathVariable String symbol) {

        return monthlyService.getMonthlyTimeSeriesProcessed(symbol);
    }

}
