package com.app.TickerSearchAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.StockTimeSeriesAPI.Daily.StockTimeSeriesDailyDTO;

import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

// Postman: http://localhost:8080/api/stock/tickerSearch/{keyword}
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/tickerSearch")
public class TickerSearchController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, TickerSearchDTO> dataCache = new HashMap<>();

    @GetMapping("/{keywords}")
    public TickerSearchDTO getTickerSearch(@PathVariable String keywords) {
        if (dataCache.containsKey(keywords)) {
            return dataCache.get(keywords);
        }

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = String.format(
                "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=%s&apikey=%s",
                keywords, apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> responseBody = response.getBody();

        TickerSearchDTO tickerSearchDTO = mapResponseToDTO(responseBody);

        dataCache.put(keywords, tickerSearchDTO);

        return tickerSearchDTO;
    }

    private TickerSearchDTO mapResponseToDTO(Map<String, Object> responseBody) {
        TickerSearchDTO tickerSearchDTO = new TickerSearchDTO();
        List<TickerSearchDTO.Match> bestMatches = new ArrayList<>();

        List<Map<String, String>> apiBestMatches = (List<Map<String, String>>) responseBody.get("bestMatches");

        for (Map<String, String> apiMatch : apiBestMatches) {
            TickerSearchDTO.Match match = new TickerSearchDTO.Match();
            match.setSymbol(apiMatch.get("1. symbol"));
            match.setName(apiMatch.get("2. name"));
            match.setType(apiMatch.get("3. type"));
            match.setRegion(apiMatch.get("4. region"));
            match.setMarketOpen(apiMatch.get("5. marketOpen"));
            match.setMarketClose(apiMatch.get("6. marketClose"));
            match.setTimezone(apiMatch.get("7. timezone"));
            match.setCurrency(apiMatch.get("8. currency"));
            match.setMatchScore(apiMatch.get("9. matchScore"));

            bestMatches.add(match);
        }

        tickerSearchDTO.setBestMatches(bestMatches);
        return tickerSearchDTO;
    }
}
