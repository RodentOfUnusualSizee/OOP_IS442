interface TickerSentiment {
    ticker: string;
    relevanceScore: number;
    tickerSentimentScore: number;
    tickerSentimentLabel: string;
}

interface FeedItem {
    title: string;
    url: string;
    timePublished: string;
    authors: string[];
    summary: string;
    bannerImage: string;
    source: string;
    categoryWithinSource: string;
    sourceDomain: string;
    topics: { topic: string; relevanceScore: number }[];
    overallSentimentScore: number;
    overallSentimentLabel: string;
    tickerSentiment: TickerSentiment[];
}

interface Data {
    feed: FeedItem[];
}

interface MappedFeedItem {
    title: string;
    url: string;
    timePublished: string;
    summary: string;
    tickerSentimentScore: string;
}

export function getTickerFeed(data: Data, ticker: string, limit = 20): MappedFeedItem[] {
    const filteredData = data.feed.filter((item: FeedItem) =>
        item.tickerSentiment.some((tickerItem: TickerSentiment) => tickerItem.ticker === ticker)
    );

    const feed = filteredData.slice(0, limit).map((item: FeedItem) => {
        const tickerSentiment = item.tickerSentiment.find((tickerItem: TickerSentiment) => tickerItem.ticker === ticker);

        const formattedSentimentScore = tickerSentiment
            ? parseFloat(tickerSentiment.tickerSentimentScore.toFixed(2))
            : "N/A";

        return {
            title: item.title,
            url: item.url,
            timePublished: formatTimestamp(item.timePublished),
            summary: item.summary,
            tickerSentimentScore: formattedSentimentScore.toString(),
        };
    });

    return feed;
}

function formatTimestamp(timestamp: string): string {
    if (!/^\d{8}T\d{6}$/.test(timestamp)) {
        throw new Error('Invalid timestamp format');
    }

    const year = timestamp.substring(0, 4);
    const month = timestamp.substring(4, 6);
    const day = timestamp.substring(6, 8);
    const hour = timestamp.substring(9, 11);
    const minute = timestamp.substring(11, 13);
    const second = timestamp.substring(13, 15);

    const date = new Date(`${year}-${month}-${day}T${hour}:${minute}:${second}`);

    return date.toLocaleString();
}