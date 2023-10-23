package com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO.MonthlyStockData;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO.MetaData;

import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class MonthlyService {
    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, Map<String, Object>> stockDataCache = new HashMap<>();

    public Map<String, Object> getMonthlyTimeSeriesRaw(String symbol) {
        // check if data for the given symbol exists in cache
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
        return responseBody;
    }

    public StockTimeSeriesMonthlyDTO getMonthlyTimeSeriesProcessed(String symbol) {
        StockTimeSeriesMonthlyDTO StockTimeSeriesMonthlyDTO = mapResponseToDTO(getMonthlyTimeSeriesRaw(symbol));

        return StockTimeSeriesMonthlyDTO;
    }

    private static StockTimeSeriesMonthlyDTO mapResponseToDTO(Map<String, Object> responseBody) {
        StockTimeSeriesMonthlyDTO stockTimeSeriesMonthlyDTO = new StockTimeSeriesMonthlyDTO();
        MetaData metaData = new MetaData();
        List<MonthlyStockData> timeSeriesMonthly = new ArrayList<>();

        // Parsing MetaData
        Map<String, String> apiMetaData = (Map<String, String>) responseBody.get("Meta Data");
        metaData.setInformation(apiMetaData.get("1. Information"));
        metaData.setSymbol(apiMetaData.get("2. Symbol"));
        metaData.setLastRefreshed(apiMetaData.get("3. Last Refreshed"));
        metaData.setTimeZone(apiMetaData.get("4. Time Zone"));
        stockTimeSeriesMonthlyDTO.setMetaData(metaData);

        // Parsing Time Series (Monthly)
        Map<String, Map<String, String>> apiTimeSeriesMonthly = (Map<String, Map<String, String>>) responseBody
                .get("Monthly Time Series");

        for (Map.Entry<String, Map<String, String>> entry : apiTimeSeriesMonthly.entrySet()) {
            MonthlyStockData monthlyStockData = new MonthlyStockData();
            monthlyStockData.setDate(entry.getKey());
            monthlyStockData.setOpen(Double.parseDouble(entry.getValue().get("1. open")));
            monthlyStockData.setHigh(Double.parseDouble(entry.getValue().get("2. high")));
            monthlyStockData.setLow(Double.parseDouble(entry.getValue().get("3. low")));
            monthlyStockData.setClose(Double.parseDouble(entry.getValue().get("4. close")));
            monthlyStockData.setVolume(Long.parseLong(entry.getValue().get("5. volume")));

            timeSeriesMonthly.add(monthlyStockData);
        }
        stockTimeSeriesMonthlyDTO.setTimeSeries(timeSeriesMonthly);

        return stockTimeSeriesMonthlyDTO;
    }

}
