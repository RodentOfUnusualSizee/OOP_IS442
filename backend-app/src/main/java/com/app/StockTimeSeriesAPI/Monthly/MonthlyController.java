package com.app.StockTimeSeriesAPI.Monthly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO.MonthlyStockData;
import com.app.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO.MetaData;

import java.text.SimpleDateFormat;
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

// Postman: http://localhost:8080/api/stock/monthlyTimeSeries/{TickerSymbol}
@RestController
@Service
@RequestMapping("/api/stock/monthlyTimeSeries")
public class MonthlyController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, Map<String, Object>> stockDataCache = new HashMap<>();

    @GetMapping("/{symbol}")
    public Map<String, Object> getMonthlyTimeSeries(@PathVariable String symbol) {
        // Check if data for the given symbol exists in cache
        if (stockDataCache.containsKey(symbol)) {
            return stockDataCache.get(symbol);
        }

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=%s&apikey=%s",
                symbol, apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
        Map<String, Object> responseBody = response.getBody();

        // Save the result into the cache
        stockDataCache.put(symbol, responseBody);

        return responseBody;
    }

    private StockTimeSeriesMonthlyDTO mapResponseToDTO(Map<String, Object> responseBody) {
        StockTimeSeriesMonthlyDTO StockTimeSeriesMonthlyDTO = new StockTimeSeriesMonthlyDTO();
        MetaData metaData = new MetaData();
        Map<String, MonthlyStockData> timeSeriesMonthly = new HashMap<>();

        // Parsing MetaData
        Map<String, String> apiMetaData = (Map<String, String>) responseBody.get("Meta Data");
        metaData.setInformation(apiMetaData.get("1. Information"));
        metaData.setSymbol(apiMetaData.get("2. Symbol"));
        metaData.setLastRefreshed(apiMetaData.get("3. Last Refreshed"));
        metaData.setOutputSize(apiMetaData.get("4. Output Size"));
        metaData.setTimeZone(apiMetaData.get("5. Time Zone"));
        StockTimeSeriesMonthlyDTO.setMetaData(metaData);

        // Parsing Time Series (Monthly)
        Map<String, Map<String, String>> apiTimeSeriesMonthly = (Map<String, Map<String, String>>) responseBody
                .get("Monthly Time Series");

        // Date format in API response is: "yyyy-MM-dd"
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map.Entry<String, Map<String, String>> entry : apiTimeSeriesMonthly.entrySet()) {
            MonthlyStockData monthlyStockData = new MonthlyStockData();
            monthlyStockData.setOpen(Double.parseDouble(entry.getValue().get("1. open")));
            monthlyStockData.setHigh(Double.parseDouble(entry.getValue().get("2. high")));
            monthlyStockData.setLow(Double.parseDouble(entry.getValue().get("3. low")));
            monthlyStockData.setClose(Double.parseDouble(entry.getValue().get("4. close")));
            monthlyStockData.setVolume(Long.parseLong(entry.getValue().get("5. volume")));

            try {
                String dateKey = entry.getKey();
                timeSeriesMonthly.put(dateKey, monthlyStockData);
            } catch (Exception e) {
                e.printStackTrace(); // Consider better exception handling
            }
        }
        StockTimeSeriesMonthlyDTO.setTimeSeries(timeSeriesMonthly);

        return StockTimeSeriesMonthlyDTO;
    }
}
