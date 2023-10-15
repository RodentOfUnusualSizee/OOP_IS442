package com.app.StockTimeSeriesAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.StockTimeSeriesAPI.StockTimeSeriesDTO.DailyStockData;
import com.app.StockTimeSeriesAPI.StockTimeSeriesDTO.MetaData;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/stock/timeSeries")
public class DailyController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/daily/{symbol}")
    // StockTimeSeriesDTO
    public Map<String, Object> getDailyTimeSeries(@PathVariable String symbol) {
        // public Map<String, Object> getDailyTimeSeries(@PathVariable String symbol,
        // @RequestParam String apiKey) {

        // String apiUrl = String.format(
        // "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
        // symbol, apiKey);
        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBM&apikey=OKRP7XRTHZE2LCWM";

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> responseBody = response.getBody();

        // Implement your mapping logic to map the response to your StockTimeSeriesDTO.
        // Note: Normally, you would want to create a service layer to handle the
        // business logic
        // and keep your controller clean.
        StockTimeSeriesDTO stockTimeSeriesDTO = mapResponseToDTO(responseBody);

        // Pass stockTimeSeriesDTO to your internal system here

        // return stockTimeSeriesDTO;
        return responseBody;
    }

    private StockTimeSeriesDTO mapResponseToDTO(Map<String, Object> responseBody) {
        StockTimeSeriesDTO stockTimeSeriesDTO = new StockTimeSeriesDTO();
        MetaData metaData = new MetaData();
        Map<Date, DailyStockData> timeSeriesDaily = new HashMap<>();

        // Parsing MetaData
        Map<String, String> apiMetaData = (Map<String, String>) responseBody.get("Meta Data");
        metaData.setInformation(apiMetaData.get("1. Information"));
        metaData.setSymbol(apiMetaData.get("2. Symbol"));
        metaData.setLastRefreshed(apiMetaData.get("3. Last Refreshed"));
        metaData.setOutputSize(apiMetaData.get("4. Output Size"));
        metaData.setTimeZone(apiMetaData.get("5. Time Zone"));
        stockTimeSeriesDTO.setMetaData(metaData);

        // Parsing Time Series (Daily)
        Map<String, Map<String, String>> apiTimeSeriesDaily = (Map<String, Map<String, String>>) responseBody
                .get("Time Series (Daily)");

        // Date format in API response is: "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map.Entry<String, Map<String, String>> entry : apiTimeSeriesDaily.entrySet()) {
            DailyStockData dailyStockData = new DailyStockData();
            dailyStockData.setOpen(Double.parseDouble(entry.getValue().get("1. open")));
            dailyStockData.setHigh(Double.parseDouble(entry.getValue().get("2. high")));
            dailyStockData.setLow(Double.parseDouble(entry.getValue().get("3. low")));
            dailyStockData.setClose(Double.parseDouble(entry.getValue().get("4. close")));
            dailyStockData.setVolume(Long.parseLong(entry.getValue().get("5. volume")));

            try {
                Date dateKey = dateFormat.parse(entry.getKey());
                timeSeriesDaily.put(dateKey, dailyStockData);
            } catch (Exception e) {
                e.printStackTrace(); // Consider better exception handling
            }
        }
        stockTimeSeriesDTO.setTimeSeries(timeSeriesDaily);

        return stockTimeSeriesDTO;
    }
}
