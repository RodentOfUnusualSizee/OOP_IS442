package com.app.StockTimeSeriesAPI.Weekly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.StockTimeSeriesAPI.Weekly.StockTimeSeriesWeeklyDTO.WeeklyStockData;
import com.app.StockTimeSeriesAPI.Weekly.StockTimeSeriesWeeklyDTO.MetaData;

import java.text.SimpleDateFormat;
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

// Postman: http://localhost:8080/api/stock/weeklyTimeSeries/{TickerSymbol}
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/weeklyTimeSeries")
public class WeeklyController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, StockTimeSeriesWeeklyDTO> stockDataCache = new HashMap<>();

    @GetMapping("/{symbol}")
    public StockTimeSeriesWeeklyDTO getWeeklyTimeSeries(@PathVariable String symbol) {
        if (stockDataCache.containsKey(symbol)) {
            return stockDataCache.get(symbol);
        }

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=%s&apikey=%s",
                symbol, apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> responseBody = response.getBody();

        // Implement your mapping logic to map the response to your
        // StockTimeSeriesWeeklyDTO.
        // Note: Normally, you would want to create a service layer to handle the
        // business logic
        // and keep your controller clean.
        StockTimeSeriesWeeklyDTO StockTimeSeriesWeeklyDTO = mapResponseToDTO(responseBody);
        stockDataCache.put(symbol, StockTimeSeriesWeeklyDTO);
        // Pass StockTimeSeriesWeeklyDTO to your internal system here

        return StockTimeSeriesWeeklyDTO;
        // return responseBody;
    }

    private StockTimeSeriesWeeklyDTO mapResponseToDTO(Map<String, Object> responseBody) {
        StockTimeSeriesWeeklyDTO StockTimeSeriesWeeklyDTO = new StockTimeSeriesWeeklyDTO();
        MetaData metaData = new MetaData();
        Map<String, WeeklyStockData> timeSeriesWeekly = new HashMap<>();

        // Parsing MetaData
        Map<String, String> apiMetaData = (Map<String, String>) responseBody.get("Meta Data");
        metaData.setInformation(apiMetaData.get("1. Information"));
        metaData.setSymbol(apiMetaData.get("2. Symbol"));
        metaData.setLastRefreshed(apiMetaData.get("3. Last Refreshed"));
        // metaData.setOutputSize(apiMetaData.get("4. Output Size"));
        metaData.setTimeZone(apiMetaData.get("4. Time Zone"));
        StockTimeSeriesWeeklyDTO.setMetaData(metaData);

        // Parsing Time Series (Weekly)
        Map<String, Map<String, String>> apiTimeSeriesWeekly = (Map<String, Map<String, String>>) responseBody
                .get("Weekly Time Series");

        // Date format in API response is: "yyyy-MM-dd"
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map.Entry<String, Map<String, String>> entry : apiTimeSeriesWeekly.entrySet()) {
            WeeklyStockData weeklyStockData = new WeeklyStockData();
            weeklyStockData.setOpen(Double.parseDouble(entry.getValue().get("1. open")));
            weeklyStockData.setHigh(Double.parseDouble(entry.getValue().get("2. high")));
            weeklyStockData.setLow(Double.parseDouble(entry.getValue().get("3. low")));
            weeklyStockData.setClose(Double.parseDouble(entry.getValue().get("4. close")));
            weeklyStockData.setVolume(Long.parseLong(entry.getValue().get("5. volume")));

            try {
                String dateKey = entry.getKey();
                timeSeriesWeekly.put(dateKey, weeklyStockData);
            } catch (Exception e) {
                e.printStackTrace(); // Consider better exception handling
            }
        }
        StockTimeSeriesWeeklyDTO.setTimeSeries(timeSeriesWeekly);

        return StockTimeSeriesWeeklyDTO;
    }
}
