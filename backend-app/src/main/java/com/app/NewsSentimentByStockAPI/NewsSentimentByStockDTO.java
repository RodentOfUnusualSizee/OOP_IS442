package com.app.NewsSentimentByStockAPI;

import java.util.ArrayList;
import java.util.List;

public class NewsSentimentByStockDTO {

    private String items;
    private String sentimentScoreDefinition;
    private String relevanceScoreDefinition;
    private List<Feed> feed;

    public NewsSentimentByStockDTO() {
        this.feed = new ArrayList<>(); // Initialize the topics list here
    }

    public NewsSentimentByStockDTO(String items, String sentimentScoreDefinition, String relevanceScoreDefinition,
            List<Feed> feed) {
        this.items = items;
        this.sentimentScoreDefinition = sentimentScoreDefinition;
        this.relevanceScoreDefinition = relevanceScoreDefinition;
        this.feed = feed;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getSentimentScoreDefinition() {
        return sentimentScoreDefinition;
    }

    public void setSentimentScoreDefinition(String sentimentScoreDefinition) {
        this.sentimentScoreDefinition = sentimentScoreDefinition;
    }

    public String getRelevanceScoreDefinition() {
        return relevanceScoreDefinition;
    }

    public void setRelevanceScoreDefinition(String relevanceScoreDefinition) {
        this.relevanceScoreDefinition = relevanceScoreDefinition;
    }

    public List<Feed> getFeed() {
        return feed;
    }

    public void setFeed(List<Feed> feed) {
        this.feed = feed;
    }

    public static class Feed {
        private String title;
        private String url;
        private String timePublished;
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

        public Feed() {
            this.topics = new ArrayList<>(); // Initialize the topics list here
            this.tickerSentiment = new ArrayList<>(); // Initialize the tickerSentiment list here
        }

        public Feed(String title, String url, String timePublished, List<String> authors, String summary,
                String bannerImage, String source, String categoryWithinSource, String sourceDomain,
                List<Topic> topics, double overallSentimentScore, String overallSentimentLabel,
                List<TickerSentiment> tickerSentiment) {
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTimePublished() {
            return timePublished;
        }

        public void setTimePublished(String timePublished) {
            this.timePublished = timePublished;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public void setAuthors(List<String> authors) {
            this.authors = authors;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getBannerImage() {
            return bannerImage;
        }

        public void setBannerImage(String bannerImage) {
            this.bannerImage = bannerImage;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getCategoryWithinSource() {
            return categoryWithinSource;
        }

        public void setCategoryWithinSource(String categoryWithinSource) {
            this.categoryWithinSource = categoryWithinSource;
        }

        public String getSourceDomain() {
            return sourceDomain;
        }

        public void setSourceDomain(String sourceDomain) {
            this.sourceDomain = sourceDomain;
        }

        public List<Topic> getTopics() {
            return topics;
        }

        public void setTopics(List<Topic> topics) {
            this.topics = topics;
        }

        public double getOverallSentimentScore() {
            return overallSentimentScore;
        }

        public void setOverallSentimentScore(double overallSentimentScore) {
            this.overallSentimentScore = overallSentimentScore;
        }

        public String getOverallSentimentLabel() {
            return overallSentimentLabel;
        }

        public void setOverallSentimentLabel(String overallSentimentLabel) {
            this.overallSentimentLabel = overallSentimentLabel;
        }

        public List<TickerSentiment> getTickerSentiment() {
            return tickerSentiment;
        }

        public void setTickerSentiment(List<TickerSentiment> tickerSentiment) {
            this.tickerSentiment = tickerSentiment;
        }

        public static class Topic {
            private String topic;
            private double relevanceScore;

            public Topic() {
            }

            public Topic(String topic, double relevanceScore) {
                this.topic = topic;
                this.relevanceScore = relevanceScore;
            }

            public String getTopic() {
                return topic;
            }

            public void setTopic(String topic) {
                this.topic = topic;
            }

            public double getRelevanceScore() {
                return relevanceScore;
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

            public TickerSentiment() {
            }

            public TickerSentiment(String ticker, double relevanceScore, double tickerSentimentScore,
                    String tickerSentimentLabel) {
                this.ticker = ticker;
                this.relevanceScore = relevanceScore;
                this.tickerSentimentScore = tickerSentimentScore;
                this.tickerSentimentLabel = tickerSentimentLabel;
            }

            public String getTicker() {
                return ticker;
            }

            public void setTicker(String ticker) {
                this.ticker = ticker;
            }

            public double getRelevanceScore() {
                return relevanceScore;
            }

            public void setRelevanceScore(double relevanceScore) {
                this.relevanceScore = relevanceScore;
            }

            public double getTickerSentimentScore() {
                return tickerSentimentScore;
            }

            public void setTickerSentimentScore(double tickerSentimentScore) {
                this.tickerSentimentScore = tickerSentimentScore;
            }

            public String getTickerSentimentLabel() {
                return tickerSentimentLabel;
            }

            public void setTickerSentimentLabel(String tickerSentimentLabel) {
                this.tickerSentimentLabel = tickerSentimentLabel;
            }
        }
    }
}
