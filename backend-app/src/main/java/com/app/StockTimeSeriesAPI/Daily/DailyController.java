package com.app.StockTimeSeriesAPI.Daily;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.StockTimeSeriesAPI.Daily.StockTimeSeriesDailyDTO.DailyStockData;
import com.app.StockTimeSeriesAPI.Daily.StockTimeSeriesDailyDTO.MetaData;
import com.app.StockTimeSeriesAPI.Weekly.StockTimeSeriesWeeklyDTO;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import io.github.cdimascio.dotenv.Dotenv;

// Postman: http://localhost:8080/api/stock/dailyTimeSeries/{TickerSymbol}
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/dailyTimeSeries")
public class DailyController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, StockTimeSeriesDailyDTO> stockDataCache = new HashMap<>();

    @GetMapping("/{symbol}")
    public StockTimeSeriesDailyDTO getDailyTimeSeries(@PathVariable String symbol) {
        if (stockDataCache.containsKey(symbol)) {
            return stockDataCache.get(symbol);
        }

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                symbol, apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> responseBody = response.getBody();

        // Implement your mapping logic to map the response to your
        // StockTimeSeriesDailyDTO.
        // Note: Normally, you would want to create a service layer to handle the
        // business logic
        // and keep your controller clean.
        StockTimeSeriesDailyDTO StockTimeSeriesDailyDTO = mapResponseToDTO(responseBody);
        stockDataCache.put(symbol, StockTimeSeriesDailyDTO);
        // Pass StockTimeSeriesDailyDTO to your internal system here

        return StockTimeSeriesDailyDTO;
        // return responseBody;
    }

    @GetMapping("/30/{symbol}")
    public StockTimeSeriesDailyDTO getDailyTimeSeriesFor30Days(@PathVariable String symbol) {
        return filterDataByDays(getDailyTimeSeries(symbol), 30);
    }

    @GetMapping("/60/{symbol}")
    public StockTimeSeriesDailyDTO getDailyTimeSeriesFor60Days(@PathVariable String symbol) {
        return filterDataByDays(getDailyTimeSeries(symbol), 60);
    }

    @GetMapping("/90/{symbol}")
    public StockTimeSeriesDailyDTO getDailyTimeSeriesFor90Days(@PathVariable String symbol) {
        return filterDataByDays(getDailyTimeSeries(symbol), 90);
    }

    private StockTimeSeriesDailyDTO filterDataByDays(StockTimeSeriesDailyDTO fullData, int days) {
        StockTimeSeriesDailyDTO filteredData = new StockTimeSeriesDailyDTO();
        filteredData.setMetaData(fullData.getMetaData());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        Map<String, DailyStockData> filteredTimeSeries = fullData.getTimeSeries().entrySet().stream()
                .filter(entry -> LocalDate.parse(entry.getKey(), formatter).isAfter(startDate)
                        && LocalDate.parse(entry.getKey(), formatter).isBefore(endDate))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        filteredData.setTimeSeries(filteredTimeSeries);
        return filteredData;
    }

    private StockTimeSeriesDailyDTO mapResponseToDTO(Map<String, Object> responseBody) {
        StockTimeSeriesDailyDTO StockTimeSeriesDailyDTO = new StockTimeSeriesDailyDTO();
        MetaData metaData = new MetaData();
        Map<String, DailyStockData> timeSeriesDaily = new HashMap<>();

        // Parsing MetaData
        Map<String, String> apiMetaData = (Map<String, String>) responseBody.get("Meta Data");
        metaData.setInformation(apiMetaData.get("1. Information"));
        metaData.setSymbol(apiMetaData.get("2. Symbol"));
        metaData.setLastRefreshed(apiMetaData.get("3. Last Refreshed"));
        metaData.setOutputSize(apiMetaData.get("4. Output Size"));
        metaData.setTimeZone(apiMetaData.get("5. Time Zone"));
        StockTimeSeriesDailyDTO.setMetaData(metaData);

        // Parsing Time Series (Daily)
        Map<String, Map<String, String>> apiTimeSeriesDaily = (Map<String, Map<String, String>>) responseBody
                .get("Time Series (Daily)");

        // Date format in API response is: "yyyy-MM-dd"
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map.Entry<String, Map<String, String>> entry : apiTimeSeriesDaily.entrySet()) {
            DailyStockData dailyStockData = new DailyStockData();
            dailyStockData.setOpen(Double.parseDouble(entry.getValue().get("1. open")));
            dailyStockData.setHigh(Double.parseDouble(entry.getValue().get("2. high")));
            dailyStockData.setLow(Double.parseDouble(entry.getValue().get("3. low")));
            dailyStockData.setClose(Double.parseDouble(entry.getValue().get("4. close")));
            dailyStockData.setVolume(Long.parseLong(entry.getValue().get("5. volume")));

            try {
                String dateKey = entry.getKey();
                timeSeriesDaily.put(dateKey, dailyStockData);
            } catch (Exception e) {
                e.printStackTrace(); // Consider better exception handling
            }
        }
        StockTimeSeriesDailyDTO.setTimeSeries(timeSeriesDaily);

        return StockTimeSeriesDailyDTO;
    }
}
