package com.app.NewsSentimentByStockAPI;

import java.time.LocalDateTime;
import java.util.List;

public class NewsSentimentByStockDTO {

    private int items;
    private String sentimentScoreDefinition;
    private String relevanceScoreDefinition;
    private List<FeedItem> feed;

    // Constructors
    public NewsSentimentByStockDTO(int items, String sentimentScoreDefinition, String relevanceScoreDefinition,
            List<FeedItem> feed) {
        this.items = items;
        this.sentimentScoreDefinition = sentimentScoreDefinition;
        this.relevanceScoreDefinition = relevanceScoreDefinition;
        this.feed = feed;
    }
    public NewsSentimentByStockDTO(){
        
    }

    // Getters
    public int getItems() {
        return items;
    }

    public String getSentimentScoreDefinition() {
        return sentimentScoreDefinition;
    }

    public String getRelevanceScoreDefinition() {
        return relevanceScoreDefinition;
    }

    public List<FeedItem> getFeed() {
        return feed;
    }

    // Setters

    public void setItems(int items) {
        this.items = items;
    }

    public void setSentimentScoreDefinition(String sentimentScoreDefinition) {
        this.sentimentScoreDefinition = sentimentScoreDefinition;
    }

    public void setRelevanceScoreDefinition(String relevanceScoreDefinition) {
        this.relevanceScoreDefinition = relevanceScoreDefinition;
    }

    public void setFeed(List<FeedItem> feed) {
        this.feed = feed;
    }

    public static class FeedItem {
        private String title;
        private String url;
        private LocalDateTime timePublished;
        private List<String> authors;
        private String summary;
        private String bannerImage;
        private String source;
        private String categoryWithinSource;
        private String sourceDomain;
        private List<Topic> topics;
        private double overallSentimentScore;
        private String overallSentimentLabel;
        private List<TickerSentiment> tickerSentiment;

        // Constructors
        public FeedItem(String title, String url, LocalDateTime timePublished, List<String> authors, String summary,
                String bannerImage, String source, String categoryWithinSource, String sourceDomain, List<Topic> topics,
                double overallSentimentScore, String overallSentimentLabel, List<TickerSentiment> tickerSentiment) {
            this.title = title;
            this.url = url;
            this.timePublished = timePublished;
            this.authors = authors;
            this.summary = summary;
            this.bannerImage = bannerImage;
            this.source = source;
            this.categoryWithinSource = categoryWithinSource;
            this.sourceDomain = sourceDomain;
            this.topics = topics;
            this.overallSentimentScore = overallSentimentScore;
            this.overallSentimentLabel = overallSentimentLabel;
            this.tickerSentiment = tickerSentiment;
        }

        // Getters

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public LocalDateTime getTimePublished() {
            return timePublished;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public String getSummary() {
            return summary;
        }

        public String getBannerImage() {
            return bannerImage;
        }

        public String getSource() {
            return source;
        }

        public String getCategoryWithinSource() {
            return categoryWithinSource;
        }

        public String getSourceDomain() {
            return sourceDomain;
        }

        public List<Topic> getTopics() {
            return topics;
        }

        public double getOverallSentimentScore() {
            return overallSentimentScore;
        }

        public String getOverallSentimentLabel() {
            return overallSentimentLabel;
        }

        public List<TickerSentiment> getTickerSentiment() {
            return tickerSentiment;
        }

        // Setters

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTimePublished(LocalDateTime timePublished) {
            this.timePublished = timePublished;
        }

        public void setAuthors(List<String> authors) {
            this.authors = authors;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public void setBannerImage(String bannerImage) {
            this.bannerImage = bannerImage;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public void setCategoryWithinSource(String categoryWithinSource) {
            this.categoryWithinSource = categoryWithinSource;
        }

        public void setSourceDomain(String sourceDomain) {
            this.sourceDomain = sourceDomain;
        }

        public void setTopics(List<Topic> topics) {
            this.topics = topics;
        }

        public void setOverallSentimentScore(double overallSentimentScore) {
            this.overallSentimentScore = overallSentimentScore;
        }

        public void setOverallSentimentLabel(String overallSentimentLabel) {
            this.overallSentimentLabel = overallSentimentLabel;
        }

        public void setTickerSentiment(List<TickerSentiment> tickerSentiment) {
            this.tickerSentiment = tickerSentiment;
        }

        public static class Topic {
            private String topic;
            private double relevanceScore;

            // Constructors
            public Topic(String topic, double relevanceScore) {
                this.topic = topic;
                this.relevanceScore = relevanceScore;
            }

            // Getters

            public String getTopic() {
                return topic;
            }

            public double getRelevanceScore() {
                return relevanceScore;
            }

            // Setters

            public void setTopic(String topic) {
                this.topic = topic;
            }

            public void setRelevanceScore(double relevanceScore) {
                this.relevanceScore = relevanceScore;
            }

        }

        public static class TickerSentiment {
            private String ticker;
            private double relevanceScore;
            private double tickerSentimentScore;
            private String tickerSentimentLabel;

            // Constructors
            public TickerSentiment(String ticker, double relevanceScore, double tickerSentimentScore,
                    String tickerSentimentLabel) {
                this.ticker = ticker;
                this.relevanceScore = relevanceScore;
                this.tickerSentimentScore = tickerSentimentScore;
                this.tickerSentimentLabel = tickerSentimentLabel;
            }

            // Getters
            public String getTicker() {
                return ticker;
            }

            public double getRelevanceScore() {
                return relevanceScore;
            }

            public double getTickerSentimentScore() {
                return tickerSentimentScore;
            }

            public String getTickerSentimentLabel() {
                return tickerSentimentLabel;
            }

            // Setters
            public void setTicker(String ticker) {
                this.ticker = ticker;
            }

            public void setRelevanceScore(double relevanceScore) {
                this.relevanceScore = relevanceScore;
            }

            public void setTickerSentimentScore(double tickerSentimentScore) {
                this.tickerSentimentScore = tickerSentimentScore;
            }

            public void setTickerSentimentLabel(String tickerSentimentLabel) {
                this.tickerSentimentLabel = tickerSentimentLabel;
            }
        }
    }
}
