package com.app.StockTimeSeriesAPI.Intraday;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.StockTimeSeriesAPI.Intraday.StockTimeSeriesIntradayDTO.IntradayStockData;
import com.app.StockTimeSeriesAPI.Intraday.StockTimeSeriesIntradayDTO.MetaData;

import java.text.SimpleDateFormat;
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

// Postman: http://localhost:8080/api/stock/intradayTimeSeries/{TickerSymbol}/{Interval}
// {Interval} = 1,5,15,30,60
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/intradayTimeSeries")
public class IntradayController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    @GetMapping("/{symbol}/{interval}")
    public StockTimeSeriesIntradayDTO getIntradayTimeSeries(@PathVariable String symbol, @PathVariable String interval) {

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=%smin&apikey=%s",
                symbol, interval, apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> responseBody = response.getBody();

        // Implement your mapping logic to map the response to your StockTimeSeriesIntradayDTO.
        // Note: Normally, you would want to create a service layer to handle the
        // business logic
        // and keep your controller clean.
        StockTimeSeriesIntradayDTO StockTimeSeriesIntradayDTO = mapResponseToDTO(responseBody,interval);

        // Pass StockTimeSeriesIntradayDTO to your internal system here

        return StockTimeSeriesIntradayDTO;
        // return responseBody;
    }

    private StockTimeSeriesIntradayDTO mapResponseToDTO(Map<String, Object> responseBody,String interval) {
        StockTimeSeriesIntradayDTO StockTimeSeriesIntradayDTO = new StockTimeSeriesIntradayDTO();
        MetaData metaData = new MetaData();
        Map<String, IntradayStockData> timeSeriesIntraday = new HashMap<>();

        // Parsing MetaData
        Map<String, String> apiMetaData = (Map<String, String>) responseBody.get("Meta Data");
        metaData.setInformation(apiMetaData.get("1. Information"));
        metaData.setSymbol(apiMetaData.get("2. Symbol"));
        metaData.setLastRefreshed(apiMetaData.get("3. Last Refreshed"));
        metaData.setInterval(apiMetaData.get("4. Interval"));
        metaData.setOutputSize(apiMetaData.get("5. Output Size"));
        metaData.setTimeZone(apiMetaData.get("6. Time Zone"));
        StockTimeSeriesIntradayDTO.setMetaData(metaData);

        // Parsing Time Series (Intraday)
        String getString = String.format("Time Series (%smin)", interval);
        Map<String, Map<String, String>> apiTimeSeriesIntraday = (Map<String, Map<String, String>>) responseBody
                .get(getString);

        // Date format in API response is: "yyyy-MM-dd"
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map.Entry<String, Map<String, String>> entry : apiTimeSeriesIntraday.entrySet()) {
            IntradayStockData intradayStockData = new IntradayStockData();
            intradayStockData.setOpen(Double.parseDouble(entry.getValue().get("1. open")));
            intradayStockData.setHigh(Double.parseDouble(entry.getValue().get("2. high")));
            intradayStockData.setLow(Double.parseDouble(entry.getValue().get("3. low")));
            intradayStockData.setClose(Double.parseDouble(entry.getValue().get("4. close")));
            intradayStockData.setVolume(Long.parseLong(entry.getValue().get("5. volume")));

            try {
                String dateKey = entry.getKey();
                timeSeriesIntraday.put(dateKey, intradayStockData);
            } catch (Exception e) {
                e.printStackTrace(); // Consider better exception handling
            }
        }
        StockTimeSeriesIntradayDTO.setTimeSeries(timeSeriesIntraday);

        return StockTimeSeriesIntradayDTO;
    }
}
