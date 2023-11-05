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

/**
 * Service class that manages the retrieval and processing of monthly stock time series data.
 */
@Service
public class MonthlyService {
    @Autowired
    private StockService stockService;

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    /**
     * Retrieves processed monthly time series data for a given stock symbol.
     * Data may come from the Stock database or from an external API if not present or outdated.
     * 
     * @param symbol Stock symbol for which monthly data is to be retrieved.
     * @return A DTO containing the monthly time series data.
     */
    public StockTimeSeriesMonthlyDTO getMonthlyTimeSeriesProcessed(String symbol) {
        Stock stock = stockService.getStock(symbol);
        if (stock != null && isLastRefreshedLessThanOneMonthAgo(stock.getLastRefreshed())) {

            System.out.println("Using stock monthly price from database for:" + stock.getSymbol());
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

        System.out.println("Using stock monthly price from API for:" + symbol);
        StockTimeSeriesMonthlyDTO StockTimeSeriesMonthlyDTO = mapResponseToDTO(getMonthlyTimeSeriesRaw(symbol));

        return StockTimeSeriesMonthlyDTO;
    }

    /**
     * Fetches raw monthly time series data from an external API for a given stock symbol.
     *
     * @param symbol Stock symbol for which raw monthly time series data is to be fetched.
     * @return A Map representing the raw JSON response from the API.
     */
    public Map<String, Object> getMonthlyTimeSeriesRaw(String symbol) {
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

    /**
     * Maps raw API response data into a DTO suitable for application use, and updates the stock repository.
     *
     * @param responseBody The raw API response body to be mapped.
     * @return A DTO representation of the monthly time series data.
     */
    private StockTimeSeriesMonthlyDTO mapResponseToDTO(Map<String, Object> responseBody) {
        // For saving into repo
        Stock newStock = new Stock();
        // For conversion to DTO
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
            monthlyStockData.setSymbol(apiMetaData.get("2. Symbol"));
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

        // Save to Stock repo
        newStock.setTimeSeries(timeSeriesMonthly);

        stockService.save(newStock);
        return stockTimeSeriesMonthlyDTO;
    }

    /**
     * Determines if the 'last refreshed' date of a stock's data is less than one month old.
     *
     * @param lastRefreshed The date string of the last time the stock data was refreshed.
     * @return True if the last refresh date is less than one month ago, false otherwise.
     */
    private boolean isLastRefreshedLessThanOneMonthAgo(String lastRefreshed) {
        try {
            // Parse the lastRefreshed date string into a LocalDate object
            LocalDate lastRefreshedDate = LocalDate.parse(lastRefreshed, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Calculate the date one month ago from today
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

            // Compare lastRefreshedDate with oneMonthAgo
            return lastRefreshedDate.isAfter(oneMonthAgo);
        } catch (Exception e) {
            return false; // For simplicity, return false if there's an error
        }
    }

}
