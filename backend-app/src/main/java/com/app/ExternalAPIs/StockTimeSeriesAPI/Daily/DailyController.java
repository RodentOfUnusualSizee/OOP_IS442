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

/**
 * REST Controller for managing daily stock time series data.
 * It provides endpoints for retrieving daily stock data, with additional
 * filters for 30, 60, and 90 days of data.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/dailyTimeSeries")
public class DailyController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, StockTimeSeriesDailyDTO> stockDataCache = new HashMap<>();

    /**
     * Retrieves the full daily time series for a given stock symbol.
     * The result is cached to improve performance on subsequent requests.
     *
     * @param symbol the stock symbol to retrieve the data for
     * @return a DTO containing the daily stock time series data
     */
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

    /**
     * Retrieves the daily time series data for the last 30 days for a given stock symbol.
     *
     * @param symbol the stock symbol to retrieve the data for
     * @return a DTO containing the daily stock time series data for the last 30 days
     */
    @GetMapping("/30/{symbol}")
    public StockTimeSeriesDailyDTO getDailyTimeSeriesFor30Days(@PathVariable String symbol) {
        return filterDataByDays(getDailyTimeSeries(symbol), 30);
    }

    /**
     * Retrieves the daily time series data for the last 60 days for a given stock symbol.
     *
     * @param symbol the stock symbol to retrieve the data for
     * @return a DTO containing the daily stock time series data for the last 60 days
     */
    @GetMapping("/60/{symbol}")
    public StockTimeSeriesDailyDTO getDailyTimeSeriesFor60Days(@PathVariable String symbol) {
        return filterDataByDays(getDailyTimeSeries(symbol), 60);
    }

    /**
     * Retrieves the daily time series data for the last 90 days for a given stock symbol.
     *
     * @param symbol the stock symbol to retrieve the data for
     * @return a DTO containing the daily stock time series data for the last 90 days
     */
    @GetMapping("/90/{symbol}")
    public StockTimeSeriesDailyDTO getDailyTimeSeriesFor90Days(@PathVariable String symbol) {
        return filterDataByDays(getDailyTimeSeries(symbol), 90);
    }

    /**
     * Filters the daily stock data to only include entries within the specified number of days.
     * The time series data is filtered to include dates after the calculated start date based on
     * the given number of days up to the current date. The filtered data is then sorted by date.
     *
     * @param fullData The complete stock time series data to be filtered.
     * @param days The number of days from current date to include in the filtered data.
     * @return A {@link StockTimeSeriesDailyDTO} containing only the filtered stock data points.
     */
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

    /**
     * Converts the response body from the external API call into a {@link StockTimeSeriesDailyDTO}.
     * This method parses both the meta data and time series data from the given response body map.
     *
     * @param responseBody The response body from the external API call, structured as a map.
     * @return A {@link StockTimeSeriesDailyDTO} populated with the parsed data.
     */
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
