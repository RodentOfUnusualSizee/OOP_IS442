import React, { useState } from 'react';
import { ChevronRightIcon } from '@heroicons/react/20/solid'



interface FeedItem {
    title: string;
    url: string;
    timePublished: string;
    summary: string;
    tickerSentimentScore: string;
}

const NewsFeed = (props: { data: FeedItem[] }) => {
    const feed = props.data;
    return (
        <div>
            <ul role="list" className="divide-y divide-gray-100">
                {feed.map((news) => (
                    <li key={news.title} className="relative flex justify-between gap-x-6 py-5 text-left">
                        <div className="flex min-w-0 gap-x-4">
                            <div className="min-w-0 flex-auto text-left">
                                <p className="text-sm font-semibold leading-6 text-gray-900">
                                    <a href={news.url} target='_blank'>
                                        <span className="absolute inset-x-0 -top-px bottom-0" />
                                        {news.title}
                                    </a>
                                </p>
                                <p className="mt-1 flex text-xs leading-5 text-gray-500">
                                    {news.summary}
                                </p>
                            </div>
                        </div>
                        <div className="flex shrink-0 items-center gap-x-4">
                            <div className="hidden sm:flex sm:flex-col sm:items-end">
                                <p className="text-sm leading-6 text-gray-900">Sentiment Score: {news.tickerSentimentScore}</p>
                                <p className="mt-1 text-xs leading-5 text-gray-500">
                                    {news.timePublished}
                                </p>
                            </div>
                            <a href={news.url} target='_blank'>
                                <ChevronRightIcon className="h-5 w-5 flex-none text-gray-400" aria-hidden="true" />
                            </a>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default NewsFeed;