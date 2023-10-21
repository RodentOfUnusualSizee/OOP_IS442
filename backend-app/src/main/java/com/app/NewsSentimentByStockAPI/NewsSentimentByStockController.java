package com.app.NewsSentimentByStockAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock/newsSentimentByStock")
public class NewsSentimentByStockController {

    @Autowired
    private RestTemplate restTemplate;

    private String ALPHA_VANTAGE_URL_TEMPLATE = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers=%s&apikey=%s";
    private String apiKey;

    @GetMapping("/{ticker}")
    public NewsSentimentByStockDTO getNewsSentiment(@PathVariable String ticker) {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");
        String apiUrl = String.format(ALPHA_VANTAGE_URL_TEMPLATE, ticker, apiKey);
        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map responseBody = response.getBody();

        // Extract and Map the data from the API response to the DTO
        int items = Integer.parseInt(responseBody.get("items").toString());
        String sentimentScoreDefinition = responseBody.get("sentiment_score_definition").toString();
        String relevanceScoreDefinition = responseBody.get("relevance_score_definition").toString();

        List<NewsSentimentByStockDTO.FeedItem> feedItems = new ArrayList<>();
        List<Map> feedData = (List<Map>) responseBody.get("feed");

        for (Map feed : feedData) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
            LocalDateTime timePublished = LocalDateTime.parse(feed.get("time_published").toString(), formatter);
            
            List<String> authors = (List<String>) feed.get("authors");
            List<NewsSentimentByStockDTO.FeedItem.Topic> topics = new ArrayList<>();

            for (Map topicData : (List<Map>) feed.get("topics")) {
                topics.add(new NewsSentimentByStockDTO.FeedItem.Topic(
                        topicData.get("topic").toString(),
                        Double.parseDouble(topicData.get("relevance_score").toString())
                ));
            }

            List<NewsSentimentByStockDTO.FeedItem.TickerSentiment> tickerSentiments = new ArrayList<>();
            for (Map tickerData : (List<Map>) feed.get("ticker_sentiment")) {
                tickerSentiments.add(new NewsSentimentByStockDTO.FeedItem.TickerSentiment(
                        tickerData.get("ticker").toString(),
                        Double.parseDouble(tickerData.get("relevance_score").toString()),
                        Double.parseDouble(tickerData.get("ticker_sentiment_score").toString()),
                        tickerData.get("ticker_sentiment_label").toString()
                ));
            }

            feedItems.add(new NewsSentimentByStockDTO.FeedItem(
                    feed.get("title").toString(),
                    feed.get("url").toString(),
                    timePublished,
                    authors,
                    feed.get("summary").toString(),
                    feed.get("banner_image").toString(),
                    feed.get("source").toString(),
                    feed.get("category_within_source").toString(),
                    feed.get("source_domain").toString(),
                    topics,
                    Double.parseDouble(feed.get("overall_sentiment_score").toString()),
                    feed.get("overall_sentiment_label").toString(),
                    tickerSentiments
            ));
        }
        
        return new NewsSentimentByStockDTO(items, sentimentScoreDefinition, relevanceScoreDefinition, feedItems);
    }
}
