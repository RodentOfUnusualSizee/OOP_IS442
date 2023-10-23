package com.app.ExternalAPIs.TopGainerLoserAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.ExternalAPIs.TickerSearchAPI.TickerSearchDTO;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;

// PostMan: http://localhost:8080/api/stock/topGainerLoser
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/topGainerLoser")
public class TopGainerLoserController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, TopGainerLoserDTO> dataCache = new HashMap<>();

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
