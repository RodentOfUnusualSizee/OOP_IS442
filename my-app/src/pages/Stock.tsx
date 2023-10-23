import React, { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import StockLineChart from '../components/StockLineChart';
import Table from '../components/Table';
import NewsFeed from '../components/NewsFeed';
import { getStockOverview, getStockNews, getStockHistoricalValues } from '../utils/api';
import { getTickerFeed, getStockPriceStats } from '../utils/transform';
import {
    MapPinIcon,
    CalendarIcon,
    TagIcon,
    PlusCircleIcon,
    ArrowDownIcon,
    ArrowUpIcon
} from '@heroicons/react/20/solid';

function classNames(...classes: String[]) {
    return classes.filter(Boolean).join(' ')
}

function Stock() {
    const today = new Date();
    const dd = String(today.getDate()).padStart(2, '0');
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const yyyy = today.getFullYear();
    const formattedDate = dd + '/' + mm + '/' + yyyy;

    interface StockDetails {
        symbol: string;
        country: string;
        currency: string;
        industry: string;
        exchange: string;
        description: string;
    }

    interface StockStats {
        name: string;
        stat: number;
    }

    interface StockHistoricalValue {
        date: string;
        open: number;
        high: number;
        low: number;
        close: number;
    }

    interface StockPriceStats {
        name: string;
        stat: string;
        previousStat: number;
        percentageChange: string;
        change: string;
        changeType: string;
    }

    const location = useLocation();
    const [stockDetails, setStockDetails] = React.useState<StockDetails>({ symbol: "", country: "", currency: "", industry: "", exchange: "", description: "" });
    const [stockStats, setStockStats] = React.useState<StockStats[]>([]);
    const [stockHistoricalValues, setStockHistoricalValues] = React.useState<StockHistoricalValue[]>([]);
    const [stockNews, setStockNews] = React.useState<any[]>([]);
    const [stockPriceStats, setStockPriceStats] = React.useState<StockPriceStats[]>([]);

    function getStockValues(days: number, symbol: string = stockDetails.symbol) {
        const getValuesCall = async () => {
            try {
                const historicalValues = await getStockHistoricalValues(symbol, days);
                setStockHistoricalValues(historicalValues.timeSeries);
                console.log(historicalValues.timeSeries);
                if (stockPriceStats.length === 0) {
                    setStockPriceStats(getStockPriceStats(historicalValues.timeSeries));
                }
            } catch (error) {
                console.error("Error fetching stock details:", error);
            }
        };
        getValuesCall();
    }

    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        const symbol = queryParams.get('ticker');

        console.log(symbol);

        if (!symbol) {
            console.error("Ticker not provided in the query parameter");
            return; // if symbol is null or undefined, don't proceed to make API calls
        }

        const getStockDetails = async () => {
            try {
                const stockOverview = await getStockOverview(symbol);
                const stockNews = await getStockNews(symbol);

                setStockDetails({
                    symbol: stockOverview.symbol,
                    country: stockOverview.country,
                    currency: stockOverview.currency,
                    industry: stockOverview.industry,
                    exchange: stockOverview.exchange,
                    description: stockOverview.description
                });

                setStockStats([
                    { name: "Profit Margin", stat: stockOverview.profitMargin },
                    { name: "Quarterly Earnings Growth YOY", stat: stockOverview.quarterlyEarningsGrowthYOY },
                    { name: "Quarterly Revenue Growth YOY", stat: stockOverview.quarterlyRevenueGrowthYOY }
                ])

                getStockValues(60, stockOverview.symbol);

                const tickerFeed = getTickerFeed(stockNews, stockOverview.symbol);
                setStockNews(tickerFeed);
                console.log(tickerFeed);
            } catch (error) {
                console.error("Error fetching stock details:", error);
            }
        };
        getStockDetails();
    }, [location.search]);



    return (
        <div>
            <Header management={true} userType={"user"} login={true} ></Header>
            <div>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6">
                    <h3 className="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight">
                        {stockDetails.symbol}
                    </h3>
                    <div className="min-w-0 flex-1">
                        <div className="mt-1 flex flex-col sm:mt-0 sm:flex-row sm:flex-wrap sm:space-x-6 mx-2">
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <MapPinIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {stockDetails.country}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <TagIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {stockDetails.industry}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <CalendarIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {formattedDate}
                            </div>
                        </div>
                    </div>
                    <div className="mt-5 flex lg:ml-4 lg:mt-0">
                        <span className="hidden sm:block">
                            <button
                                type="button"
                                className="inline-flex items-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                            // onClick={handleAddClick}
                            >
                                <PlusCircleIcon className="-ml-0.5 mr-1.5 h-5 w-5 text-gray-400" aria-hidden="true" />
                                Add Stock to Portfolio
                            </button>
                        </span>
                    </div>
                </div>
            </div>
            <div className='my-2 px-6'>
                <dl className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-3">
                    {stockStats.map((item) => (
                        <div key={item.name} className="overflow-hidden rounded-lg bg-white px-4 py-5 shadow sm:p-6">
                            <dt className="truncate text-sm font-medium text-gray-500">{item.name}</dt>

                            <dd className={classNames(
                                item.stat > 0 ? 'text-green-600' : 'text-red-600', 'mt-1 text-3xl font-semibold tracking-tight text-gray-900')}>{item.stat}%</dd>
                            <p
                                className={classNames(
                                    item.stat > 0 ? 'text-green-600' : 'text-red-600',
                                    'ml-2 flex items-baseline text-sm font-semibold'
                                )}
                            >
                                {item.stat > 0 ? (
                                    <ArrowUpIcon className="h-5 w-5 flex-shrink-0 self-center text-green-500" aria-hidden="true" />
                                ) : (
                                    <ArrowDownIcon className="h-5 w-5 flex-shrink-0 self-center text-red-500" aria-hidden="true" />
                                )}
                            </p>
                        </div>
                    ))}
                </dl>
            </div>
            <div className='my-6 px-6 w-full'>
                <div className="relative">
                    <div className="w-full inset-0 flex items-center" aria-hidden="true">
                        <div className=" border-t border-gray-300" />
                    </div>
                    <div className="relative flex justify-start">
                        <span className="bg-white pr-3 text-base font-semibold leading-6 text-gray-900">Stock Price History</span>
                    </div>
                </div>
                <span className="isolate inline-flex rounded-md shadow-sm">
                    <button
                        type="button" onClick={() => getStockValues(30)}
                        className="relative inline-flex items-center rounded-l-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        30 Days
                    </button>
                    <button
                        type="button" onClick={() => getStockValues(60)}
                        className="relative -ml-px inline-flex items-center bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        60 Days
                    </button>
                    <button
                        type="button" onClick={() => getStockValues(90)}
                        className="relative -ml-px inline-flex items-center rounded-r-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        90 Days
                    </button>
                </span>
            </div>
            <div className='my-2 px-6'>
                {stockHistoricalValues.length > 0 && <StockLineChart data={stockHistoricalValues}></StockLineChart>}
            </div>
            <div className='my-4 px-6'>
                <h3 className="text-base font-semibold leading-6 text-gray-900">Last 30 days</h3>
                <dl className="mt-5 grid grid-cols-1 divide-y divide-gray-200 overflow-hidden rounded-lg bg-white shadow md:grid-cols-3 md:divide-x md:divide-y-0">
                    {stockPriceStats.map((item) => (
                        <div key={item.name} className="px-4 py-5 sm:p-6">
                            <dt className="text-base font-normal text-gray-900">{item.name}</dt>
                            <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                <div className="flex items-baseline text-2xl font-semibold text-indigo-600">
                                    {item.stat}
                                    <span className="ml-2 text-sm font-medium text-gray-500">from {item.previousStat}</span>
                                </div>

                                <div
                                    className={classNames(
                                        item.changeType === 'increase' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                                        'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                    )}
                                >
                                    {item.changeType === 'increase' ? (
                                        <ArrowUpIcon
                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                            aria-hidden="true"
                                        />
                                    ) : (
                                        <ArrowDownIcon
                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                            aria-hidden="true"
                                        />
                                    )}

                                    <span className="sr-only"> {item.changeType === 'increase' ? 'Increased' : 'Decreased'} by </span>
                                    {item.change}
                                </div>
                            </dd>
                        </div>
                    ))}
                </dl>
            </div>

            <div className="mx-auto max-w-7xl sm:px-6 lg:px-8 py-4 px-6 my-2">
                <div className="relative">
                    <div className="absolute inset-0 flex items-center" aria-hidden="true">
                        <div className="w-full border-t border-gray-300" />
                    </div>
                    <div className="relative flex justify-start">
                        <span className="bg-white pr-3 text-base font-semibold leading-6 text-gray-900">Company</span>
                    </div>
                </div>
                <div className="my-4 divide-y divide-gray-200 overflow-hidden rounded-lg bg-white shadow">
                    <div className="px-4 py-5 sm:px-6 font-semibold">
                        Description
                    </div>
                    <div className="px-4 py-5 sm:p-6">
                        {stockDetails.description}
                    </div>
                </div>
            </div>

            <div className="mx-auto max-w-7xl sm:px-6 lg:px-8 py-4 px-6 my-2">
                <div className="divide-y divide-gray-200 overflow-hidden rounded-lg bg-white shadow">
                    <div className="px-4 py-5 sm:px-6 font-semibold">
                        News Feed
                    </div>
                    <div className="px-4 py-5 sm:p-6">
                        <NewsFeed data={stockNews}></NewsFeed>
                    </div>
                </div>
            </div>
            <Footer></Footer>
        </div>
    );
}

export default Stock;