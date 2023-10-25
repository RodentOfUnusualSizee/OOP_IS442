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

interface StockValue {
    date: string;
    open: number;
    high: number;
    low: number;
    close: number;
    volume: number;
}

interface StockPriceChange {
    change: string;
    percentageChange: string;
    changeType: string;
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
            timePublished: formatStockTimestamp(item.timePublished),
            summary: item.summary,
            tickerSentimentScore: formattedSentimentScore.toString(),
        };
    });

    return feed;
}

function formatStockTimestamp(timestamp: string): string {
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

export function formatTimestamp(timestamp: string): string {

    if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}\+\d{2}:\d{2}$/.test(timestamp)) {
        const parsedDate = new Date(timestamp);
        const formattedDateString = parsedDate.toLocaleString();

        return formattedDateString;
    }

    throw new Error('Invalid timestamp format');
}

export function getStockPriceStats(timeSeries: StockValue[]) {
    const latest = timeSeries[timeSeries.length - 1].close;
    const oneDay = timeSeries[timeSeries.length - 2].close;
    const sevenDays = timeSeries[timeSeries.length - 7].close;
    const thirtyDays = timeSeries[timeSeries.length - 30].close;

    const calculateChange = (current: number, previous: number | undefined) => {
        if (!previous) {
            return { change: "0", percentageChange: "0", changeType: 'no data' };
        }
        const change = current - previous;
        const percentageChange = ((change / previous) * 100);
        return {
            change: change.toFixed(2),
            percentageChange: percentageChange.toFixed(2),
            changeType: change > 0 ? 'increase' : change < 0 ? 'decrease' : 'no change'
        };
    }


    const oneDayChange = calculateChange(latest, oneDay);
    const sevenDaysChange = calculateChange(latest, sevenDays);
    const thirtyDaysChange = calculateChange(latest, thirtyDays);

    const formatStats = (period: string, stats: StockPriceChange) => ({
        name: `Change from ${period} ago`,
        stat: latest.toFixed(2),
        previousStat: period === '1 day' ? oneDay : period === '7 days' ? sevenDays : thirtyDays,
        change: stats.change,
        changeType: stats.changeType,
        percentageChange: stats.percentageChange,
    });

    console.log(formatStats('1 day', oneDayChange));
    return [
        formatStats('1 day', oneDayChange),
        formatStats('7 days', sevenDaysChange),
        formatStats('30 days', thirtyDaysChange)
    ];
}

export function getStockRecordsByStockCode(positions: any[], stockCode: any) {
    const stockRecords = positions.filter((position) => position.stockSymbol === stockCode);
    return stockRecords;
}