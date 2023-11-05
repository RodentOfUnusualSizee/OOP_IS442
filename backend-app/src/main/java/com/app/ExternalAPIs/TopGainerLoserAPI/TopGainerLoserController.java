package com.app.ExternalAPIs.TopGainerLoserAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.ExternalAPIs.TickerSearchAPI.TickerSearchDTO;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;

// PostMan: http://localhost:8080/api/stock/topGainerLoser
/**
 * REST controller for fetching top gainers and losers from the stock market data.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/topGainerLoser")
public class TopGainerLoserController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, TopGainerLoserDTO> dataCache = new HashMap<>();

    /**
     * Retrieves the top gainers and losers from the stock market.
     * If data is cached, it returns the cached data.
     * Otherwise, it fetches the data from an external API and caches it.
     * @return A {@link TopGainerLoserDTO} object containing the top gainers, losers, and most actively traded stocks.
     */
    @GetMapping
    public TopGainerLoserDTO getTopGainersAndLosers() {
        if (dataCache.containsKey("existingData")) {
            return dataCache.get("existingData");
        }

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = "https://www.alphavantage.co/query?function=TOP_GAINERS_LOSERS&apikey=" + apiKey;
        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
        Map<String, Object> responseBody = response.getBody();

        TopGainerLoserDTO topGainerLoserDTO = mapResponseToDTO(responseBody);

        dataCache.put("existingData", topGainerLoserDTO);

        return topGainerLoserDTO;
    }

    /**
     * Maps the API response to the {@link TopGainerLoserDTO} object.
     * @param responseBody The response body from the API call.
     * @return A {@link TopGainerLoserDTO} with the mapped data.
     */
    private TopGainerLoserDTO mapResponseToDTO(Map<String, Object> responseBody) {
        TopGainerLoserDTO dto = new TopGainerLoserDTO();

        dto.setMetadata((String) responseBody.get("metadata"));
        dto.setLastUpdated((String) responseBody.get("last_updated"));
        
        // Convert the nested lists of maps to List<StockInfo>
        dto.setTopGainers(convertToListOfStockInfo((List<Map<String, String>>) responseBody.get("top_gainers")));
        dto.setTopLosers(convertToListOfStockInfo((List<Map<String, String>>) responseBody.get("top_losers")));
        dto.setMostActivelyTraded(convertToListOfStockInfo((List<Map<String, String>>) responseBody.get("most_actively_traded")));

        return dto;
    }

    /**
     * Converts a list of maps to a list of {@link TopGainerLoserDTO.StockInfo} objects.
     * @param data A list of maps, each representing stock info.
     * @return A list of {@link TopGainerLoserDTO.StockInfo} objects.
     */
    private List<TopGainerLoserDTO.StockInfo> convertToListOfStockInfo(List<Map<String, String>> data) {
        List<TopGainerLoserDTO.StockInfo> stockInfos = new ArrayList<>();

        for (Map<String, String> item : data) {
            TopGainerLoserDTO.StockInfo stockInfo = new TopGainerLoserDTO.StockInfo();
            stockInfo.setTicker(item.get("ticker"));
            stockInfo.setPrice(item.get("price"));
            stockInfo.setChangeAmount(item.get("change_amount"));
            stockInfo.setChangePercentage(item.get("change_percentage"));
            stockInfo.setVolume(item.get("volume"));
            stockInfos.add(stockInfo);
        }

        return stockInfos;
    }
}
