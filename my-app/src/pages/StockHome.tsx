import React, { useEffect, useState } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import Table from '../components/Table';
import { ArrowDownIcon, ArrowUpIcon } from '@heroicons/react/20/solid'
import { getStockStats } from '../utils/api';
import { formatVolume, formatPercentages, roundTo, roundToString } from '../utils/transform';
import { useAuth } from '../context/AuthContext';

function StockHome() {

    function classNames(...classes: String[]) {
        return classes.filter(Boolean).join(' ')
    }

    interface StockStat {
        name: string;
        ticker: string;
        state: number;
        changeAmount: number;
        changePercentage: string;
        volume: string;
        changeType: string;
    }

    interface StockData {
        id: number;
        ticker: string;
        price: string;
        changeAmount: number;
        changePercentage: string;
        volume: string;
    }

    interface StockDataGroup {
        mostActivelyTraded: StockData[];
        topGainers: StockData[];
        topLosers: StockData[];
    }

    const { authUser, isLoggedIn } = useAuth();
    const [isLoading, setIsLoading] = React.useState<boolean>(true);

    const [userIsLoggedIn, setUserIsLoggedIn] = useState<boolean>(true);
    const [userId, setUserId] = useState<number>(1);
    const [userRole, setUserRole] = useState<string>("user");
    const management = userRole === "admin" || userRole === "user";

    const [tableData, setTableData] = useState<StockDataGroup>({
        mostActivelyTraded: [],
        topGainers: [],
        topLosers: [],
    });

    const [statsDetails, setStatsDetails] = useState<StockStat[]>([]);

    const tableHeaders = [
        { "header": "Stock", "key": "ticker" },
        { "header": "Current Price", "key": "price" },
        { "header": "Change Amount", "key": "changeAmount" },
        { "header": "Change Percentage", "key": "changePercentage" },
        { "header": "Volume", "key": "volume" },
        { "header": "Action", "key": "action" }]

    const tableTitle = {
        'mostActivelyTraded': 'Most Actively Traded Stocks for the Day',
        'topGainers': 'Top Gainer Stocks for the Day',
        'topLosers': 'Top Loser Stocks for the Day'
    }
    const tableDescription = '';
    const tableLink = '/Stock?ticker=';

    const tableAction = 'View Stock';

    const [selectedTable, setSelectedTable] = useState('mostActivelyTraded');


    useEffect(() => {
        if (authUser) {
            console.log(authUser);
            setUserId(authUser.id);
            setUserRole(authUser.role);
            setUserIsLoggedIn(true);
        }

        const getStocks = async () => {
            const stocksFromServer = await getStockStats();
            const mostActiveStock = stocksFromServer['mostActivelyTraded'][0];
            const topGainerStock = stocksFromServer['topGainers'][0];
            const topLoserStock = stocksFromServer['topLosers'][0];

            const mostActivelyTradedWithId = stocksFromServer['mostActivelyTraded'].map((stock: StockData, index: number) => ({
                ...stock,
                id: stock['ticker'],
                price: "$" + roundToString(parseFloat(stock['price']), 2),
                changeAmount: roundTo(stock['changeAmount'], 2),
                changePercentage: formatPercentages(2, stock['changePercentage']),
                volume: formatVolume(stock['volume']),
            }));

            const topGainersWithId = stocksFromServer['topGainers'].map((stock: StockData, index: number) => ({
                ...stock,
                id: stock['ticker'],
                price: "$" + roundToString(parseFloat(stock['price']), 2),
                changeAmount: roundTo(stock['changeAmount'], 2),
                changePercentage: formatPercentages(2, stock['changePercentage']),
                volume: formatVolume(stock['volume'])
            }));

            const topLosersWithId = stocksFromServer['topLosers'].map((stock: StockData, index: number) => ({
                ...stock,
                id: stock['ticker'],
                price: "$" + roundToString(parseFloat(stock['price']), 2),
                changeAmount: roundTo(stock['changeAmount'], 2),
                changePercentage: formatPercentages(2, stock['changePercentage']),
                volume: formatVolume(stock['volume'])
            }));

            setStatsDetails([
                {
                    name: 'Most Actively Traded for the Day',
                    ticker: mostActiveStock['ticker'],
                    state: mostActiveStock['price'],
                    changeAmount: mostActiveStock['changeAmount'],
                    changePercentage: formatPercentages(2, mostActiveStock['changePercentage']),
                    volume: mostActiveStock['volume'],
                    changeType: mostActiveStock['changePercentage'].includes('-') ? 'decrease' : 'increase',
                },
                {
                    name: 'Top Gainer Stock for the Day',
                    ticker: topGainerStock['ticker'],
                    state: topGainerStock['price'],
                    changeAmount: topGainerStock['changeAmount'],
                    changePercentage: formatPercentages(2, topGainerStock['changePercentage']),
                    volume: topGainerStock['volume'],
                    changeType: topGainerStock['changePercentage'].includes('-') ? 'decrease' : 'increase',
                },
                {
                    name: 'Top Loser Stock for the Day',
                    ticker: topLoserStock['ticker'],
                    state: topLoserStock['price'],
                    changeAmount: topLoserStock['changeAmount'],
                    changePercentage: formatPercentages(2, topLoserStock['changePercentage']),
                    volume: topLoserStock['volume'],
                    changeType: topLoserStock['changePercentage'].includes('-') ? 'decrease' : 'increase',
                }
            ])

            setTableData({
                mostActivelyTraded: mostActivelyTradedWithId,
                topGainers: topGainersWithId,
                topLosers: topLosersWithId,
            });
        };
        getStocks()
    },[authUser])

    return (
        <div className="overflow-x-hidden">
            <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
            <div>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6">
                    <h3 className="mx-auto text-2xl font-bold leading-7 text-gsgray90 sm:truncate sm:text-3xl sm:tracking-tight">
                        All Stocks
                    </h3>
                </div>
                <div className="my-2 px-6 max-w-7xl mx-auto">
                    <dl className="mt-5 grid grid-cols-1 divide-y divide-gsgray20 overflow-hidden rounded-lg bg-gswhite shadow md:grid-cols-3 md:divide-x md:divide-y-0">
                        {statsDetails.map((item) => (
                            <div key={item.name} className="px-4 py-5 sm:p-6">
                                <dt className="text-base font-normal text-gsgray90">{item.name}</dt>
                                <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                    <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                        {item.ticker}
                                    </div>
                                    <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                        ${item.state}
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
                                        {item.changePercentage}
                                    </div>
                                </dd>
                            </div>
                        ))}
                    </dl>
                </div>
            </div >
            <span className="isolate inline-flex rounded-md shadow-sm my-8">
                <button
                    type="button"
                    onClick={() => setSelectedTable('mostActivelyTraded')}
                    className={`relative inline-flex items-center rounded-l-md bg-gswhite px-3 py-2 text-sm font-semibold text-gsgray90 ring-1 ring-inset ring-gsgray30 hover:bg-gsgray20 focus:z-10 ${selectedTable === 'mostActivelyTraded' ? '' : ''
                        }`}
                >
                    Most Actively Traded Stocks
                </button>
                <button
                    type="button"
                    onClick={() => setSelectedTable('topGainers')}
                    className={`relative -ml-px inline-flex items-center bg-gswhite px-3 py-2 text-sm font-semibold text-gsgray90 ring-1 ring-inset ring-gsgray30 hover:bg-gsgray20 focus:z-10 ${selectedTable === 'topGainers' ? 'bg-gsgray90' : ''
                        }`}
                >
                    Top Gainer Stocks
                </button>
                <button
                    type="button"
                    onClick={() => setSelectedTable('topLosers')}
                    className={`relative -ml-px inline-flex items-center rounded-r-md bg-gswhite px-3 py-2 text-sm font-semibold text-gsgray90 ring-1 ring-inset ring-gsgray30 hover:bg-gsgray20 focus:z-10 ${selectedTable === 'topLosers' ? 'bg-gsgray90' : ''
                        }`}
                >
                    Top Loser Stocks
                </button>
            </span>
            <div className='px-6 mt-2 mb-12 max-w-7xl mx-auto'>
                <Table
                    tableTitle={tableTitle[selectedTable as keyof typeof tableTitle]}
                    tableData={tableData[selectedTable as keyof typeof tableData]}
                    tableHeaders={tableHeaders}
                    tableDescription={tableDescription}
                    tableAction={tableAction}
                    tableLink={tableLink}
                ></Table>
            </div>
            <Footer></Footer>
        </div>
    );
}

export default StockHome;