package com.app.ExternalAPIs.StockTimeSeriesAPI.Daily;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.ExternalAPIs.StockTimeSeriesAPI.Daily.StockTimeSeriesDailyDTO.DailyStockData;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Daily.StockTimeSeriesDailyDTO.MetaData;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Weekly.StockTimeSeriesWeeklyDTO;

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

        // StockTimeSeriesDailyDTO.
        StockTimeSeriesDailyDTO StockTimeSeriesDailyDTO = mapResponseToDTO(responseBody);
        stockDataCache.put(symbol, StockTimeSeriesDailyDTO);

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

        List<DailyStockData> filteredTimeSeries = 
            fullData.getTimeSeries().stream()
                                    .filter(entry -> {
                                        LocalDate date = LocalDate.parse(entry.getDate(), formatter);
                                        return date.isAfter(startDate) && date.isBefore(endDate);
                                    })
                                    .sorted(Comparator.comparing(entry -> LocalDate.parse(entry.getDate(), formatter))) // Sort by date
                                    .collect(Collectors.toList());

        filteredData.setTimeSeries(filteredTimeSeries);
        return filteredData;
    }

    private StockTimeSeriesDailyDTO mapResponseToDTO(Map<String, Object> responseBody) {
        StockTimeSeriesDailyDTO StockTimeSeriesDailyDTO = new StockTimeSeriesDailyDTO();
        MetaData metaData = new MetaData();
        List<DailyStockData> timeSeriesDaily = new ArrayList<>();

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

        for (Map.Entry<String, Map<String, String>> entry : apiTimeSeriesDaily.entrySet()) {
            DailyStockData dailyStockData = new DailyStockData();
            dailyStockData.setDate(entry.getKey());
            dailyStockData.setOpen(Double.parseDouble(entry.getValue().get("1. open")));
            dailyStockData.setHigh(Double.parseDouble(entry.getValue().get("2. high")));
            dailyStockData.setLow(Double.parseDouble(entry.getValue().get("3. low")));
            dailyStockData.setClose(Double.parseDouble(entry.getValue().get("4. close")));
            dailyStockData.setVolume(Long.parseLong(entry.getValue().get("5. volume")));

            timeSeriesDaily.add(dailyStockData);
        }
        StockTimeSeriesDailyDTO.setTimeSeries(timeSeriesDaily);

        return StockTimeSeriesDailyDTO;
    }
}
