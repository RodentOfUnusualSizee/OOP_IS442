package com.app.ExternalAPIs.NewsSentimentByStockAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.app.ExternalAPIs.NewsSentimentByStockAPI.NewsSentimentByStockDTO.Feed;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.*;

// PostMan: http://localhost:8080/api/stock/NewsSentimentByStock/IBM
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/NewsSentimentByStock")
public class NewsSentimentByStockController {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, NewsSentimentByStockDTO> newsSentimentCache = new HashMap<>();

    @GetMapping("/{ticker}")
    public NewsSentimentByStockDTO getNewsSentimentByTicker(@PathVariable String ticker) {
        if (newsSentimentCache.containsKey(ticker)) {
            return newsSentimentCache.get(ticker);
        }

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = String.format(
                "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers=%s&apikey=%s",
                ticker, apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> responseBody = response.getBody();

        NewsSentimentByStockDTO newsSentimentDTO = mapResponseToDTO(responseBody);
        newsSentimentCache.put(ticker, newsSentimentDTO);

        return newsSentimentDTO;
    }

    private NewsSentimentByStockDTO mapResponseToDTO(Map<String, Object> responseBody) {
        NewsSentimentByStockDTO newsSentimentDTO = new NewsSentimentByStockDTO();
    
        // Parse the main fields
        newsSentimentDTO.setItems((String) responseBody.get("items"));
        newsSentimentDTO.setSentimentScoreDefinition((String) responseBody.get("sentiment_score_definition"));
        newsSentimentDTO.setRelevanceScoreDefinition((String) responseBody.get("relevance_score_definition"));
    
        // Parse each feed
        List<Map<String, Object>> apiFeeds = (List<Map<String, Object>>) responseBody.get("feed");
        for (Map<String, Object> apiFeed : apiFeeds) {
            NewsSentimentByStockDTO.Feed feed = new NewsSentimentByStockDTO.Feed();
    
            feed.setTitle((String) apiFeed.get("title"));
            feed.setUrl((String) apiFeed.get("url"));
            feed.setTimePublished((String) apiFeed.get("time_published"));
            feed.setAuthors((List<String>) apiFeed.get("authors"));
            feed.setSummary((String) apiFeed.get("summary"));
            feed.setBannerImage((String) apiFeed.get("banner_image"));
            feed.setSource((String) apiFeed.get("source"));
            feed.setCategoryWithinSource((String) apiFeed.get("category_within_source"));
            feed.setSourceDomain((String) apiFeed.get("source_domain"));
            feed.setOverallSentimentScore((Double) apiFeed.get("overall_sentiment_score"));
            feed.setOverallSentimentLabel((String) apiFeed.get("overall_sentiment_label"));
    
            // Parse topics
            List<Map<String, Object>> apiTopics = (List<Map<String, Object>>) apiFeed.get("topics");
            for (Map<String, Object> apiTopic : apiTopics) {
                NewsSentimentByStockDTO.Feed.Topic topic = new NewsSentimentByStockDTO.Feed.Topic();
                topic.setTopic((String) apiTopic.get("topic"));
                topic.setRelevanceScore(Double.parseDouble((String) apiTopic.get("relevance_score")));
                feed.getTopics().add(topic);
            }
    
            // Parse each ticker sentiment
            List<Map<String, Object>> apiTickers = (List<Map<String, Object>>) apiFeed.get("ticker_sentiment");
            for (Map<String, Object> apiTicker : apiTickers) {
                NewsSentimentByStockDTO.Feed.TickerSentiment tickerSentiment = new NewsSentimentByStockDTO.Feed.TickerSentiment();
                tickerSentiment.setTicker((String) apiTicker.get("ticker"));
                tickerSentiment.setRelevanceScore(Double.parseDouble((String) apiTicker.get("relevance_score")));
                tickerSentiment.setTickerSentimentScore(Double.parseDouble((String) apiTicker.get("ticker_sentiment_score")));
                tickerSentiment.setTickerSentimentLabel((String) apiTicker.get("ticker_sentiment_label"));
                feed.getTickerSentiment().add(tickerSentiment);
            }

            List<Feed>getFeed = newsSentimentDTO.getFeed();
            if (getFeed != null) {
                getFeed.add(feed);
            } else {
                
            }
            
        }
    
        return newsSentimentDTO;
    }
    
}
