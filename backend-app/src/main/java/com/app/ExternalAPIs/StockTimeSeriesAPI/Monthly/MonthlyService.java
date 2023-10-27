package com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.app.Stock.StockService;
import com.app.StockDataPoint.StockDataPoint;
import com.app.WildcardResponse;
import com.app.Stock.Stock;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO.MetaData;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import io.github.cdimascio.dotenv.Dotenv;

@Service
public class MonthlyService {
    @Autowired
    private StockService stockService;

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    public StockTimeSeriesMonthlyDTO getMonthlyTimeSeriesProcessed(String symbol) {
        Stock stock = stockService.getStock(symbol);
        if (stock != null && isLastRefreshedLessThanOneMonthAgo(stock.getLastRefreshed())) {
            StockTimeSeriesMonthlyDTO stockTimeSeriesMonthlyDTO = new StockTimeSeriesMonthlyDTO();
            MetaData metaData = new MetaData();
            List<StockDataPoint> timeSeries = stock.getTimeSeries();
            metaData.setSymbol(stock.getSymbol());
            metaData.setLastRefreshed(stock.getLastRefreshed());
            metaData.setInformation(stock.getInformation());
            metaData.setTimeZone(stock.getTimeZone());
            stockTimeSeriesMonthlyDTO.setMetaData(metaData);
            stockTimeSeriesMonthlyDTO.setTimeSeries(timeSeries);
            return stockTimeSeriesMonthlyDTO;
    
        }
        StockTimeSeriesMonthlyDTO StockTimeSeriesMonthlyDTO = mapResponseToDTO(getMonthlyTimeSeriesRaw(symbol));

        return StockTimeSeriesMonthlyDTO;
    }

    public Map<String, Object> getMonthlyTimeSeriesRaw(String symbol) {
 
        // Gerald: add here to search for Stock data in db
        
        // if stock exist and lastRefreshed is less than 1 month ago use the stock Data
        // else query as u see below

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=%s&apikey=%s",
                symbol, apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
        Map<String, Object> responseBody = response.getBody();

        // Make sure to also add back the stockdata into the database.. i think need to
        // override. either Update or Delete den Insert
        // Make sure when u get back can wrap back to responseBody
        return responseBody;
    }

    private static StockTimeSeriesMonthlyDTO mapResponseToDTO(Map<String, Object> responseBody) {
        //For saving into repo
        Stock newStock = new  Stock();
        //For conversion to DTO
        StockTimeSeriesMonthlyDTO stockTimeSeriesMonthlyDTO = new StockTimeSeriesMonthlyDTO();
        MetaData metaData = new MetaData();
        List<StockDataPoint> timeSeriesMonthly = new ArrayList<>();

        // Parsing MetaData
        Map<String, String> apiMetaData = (Map<String, String>) responseBody.get("Meta Data");
        metaData.setInformation(apiMetaData.get("1. Information"));
        metaData.setSymbol(apiMetaData.get("2. Symbol"));
        metaData.setLastRefreshed(apiMetaData.get("3. Last Refreshed"));
        metaData.setTimeZone(apiMetaData.get("4. Time Zone"));
        newStock.setInformation(apiMetaData.get("1. Information"));
        newStock.setSymbol(apiMetaData.get("2. Symbol"));
        newStock.setLastRefreshed(apiMetaData.get("3. Last Refreshed"));
        newStock.setTimeZone(apiMetaData.get("4. Time Zone"));
        stockTimeSeriesMonthlyDTO.setMetaData(metaData);

        // Parsing Time Series (Monthly)
        Map<String, Map<String, String>> apiTimeSeriesMonthly = (Map<String, Map<String, String>>) responseBody
                .get("Monthly Time Series");

        for (Map.Entry<String, Map<String, String>> entry : apiTimeSeriesMonthly.entrySet()) {
            StockDataPoint monthlyStockData = new StockDataPoint();
            monthlyStockData.setDate(entry.getKey());
            monthlyStockData.setOpen(Double.parseDouble(entry.getValue().get("1. open")));
            monthlyStockData.setHigh(Double.parseDouble(entry.getValue().get("2. high")));
            monthlyStockData.setLow(Double.parseDouble(entry.getValue().get("3. low")));
            monthlyStockData.setClose(Double.parseDouble(entry.getValue().get("4. close")));
            monthlyStockData.setVolume(Long.parseLong(entry.getValue().get("5. volume")));

            timeSeriesMonthly.add(monthlyStockData);
        }
        stockTimeSeriesMonthlyDTO.setTimeSeries(timeSeriesMonthly);
        stockTimeSeriesMonthlyDTO.getMetaData();

        //Save to Stock repo
        newStock.setTimeSeries(timeSeriesMonthly);
        StockService service = new StockService();
        service.save(newStock);
        return stockTimeSeriesMonthlyDTO;
    }

    private boolean isLastRefreshedLessThanOneMonthAgo(String lastRefreshed) {
        try {
            // Parse the lastRefreshed date string into a LocalDate object
            LocalDate lastRefreshedDate = LocalDate.parse(lastRefreshed, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    
            // Calculate the date one month ago from today
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
    
            // Compare lastRefreshedDate with oneMonthAgo
            return lastRefreshedDate.isAfter(oneMonthAgo);
        } catch (Exception e) {
            // Handle parsing errors or invalid date formats here
            // You may want to log the error or return a default value
            return false; // For simplicity, return false if there's an error
        }
    }

}
