import React, { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import StockLineChart from '../components/StockLineChart';
import Loading from './Loading';
import NewsFeed from '../components/NewsFeed';
import NoStock from './NoStock';
import { getStockOverview, getStockNews, getStockHistoricalValues, getPortfolioByUserId, createPortfolioPosition, createNewUserEvent } from '../utils/api';
import { getTickerFeed, getStockPriceStats } from '../utils/transform';
import {
    MapPinIcon,
    CalendarIcon,
    TagIcon,
    PlusCircleIcon,
    ArrowDownIcon,
    ArrowUpIcon
} from '@heroicons/react/20/solid';
import { useAuth } from '../context/AuthContext';
import { Slide, toast, ToastContainer } from 'react-toastify';
import { roundTo } from '../utils/transform';



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

    function getStockValues(days: number, symbol: string = stockCode) {
        const getValuesCall = async () => {
            try {
                const historicalValues = await getStockHistoricalValues(symbol, days);
                setStockHistoricalValues(historicalValues.timeSeries);
                if (stockPriceStats.length === 0) {
                    setStockPriceStats(getStockPriceStats(historicalValues.timeSeries));
                }

                const currentDateTime = new Date().toISOString().slice(0, 19);

                const event_data = {
                    "event": "FETCH STOCK " + symbol + " PRICE FOR " + days + " DAYS",
                    "timestamp": currentDateTime,
                }

                createNewUserEvent(authUser.id, event_data);
            } catch (error) {
                console.error("Error fetching stock details:", error);
            }
        };
        getValuesCall();
    }

    const location = useLocation();
    const { authUser, isLoggedIn } = useAuth();
    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [userId, setUserId] = React.useState<number>(1);
    const [userRole, setUserRole] = React.useState<string>("");
    const [userIsLoggedIn, setUserIsLoggedIn] = React.useState<boolean>(false);
    const management = userRole === "management" || userRole === "user";

    useEffect(() => {
        if (authUser) {
            setUserId(authUser.id);
            setUserRole(authUser.role);
            setUserIsLoggedIn(true);

            const queryParams = new URLSearchParams(location.search);
            const symbol = queryParams.get('ticker');

            const currentDateTime = new Date().toISOString().slice(0, 19);

            const event_data = {
                "event": "VIEW STOCK " + symbol,
                "timestamp": currentDateTime,
            }

            createNewUserEvent(authUser.id, event_data);

            if (!symbol) {
                console.error("Ticker not provided in the query parameter");
                return;
            }
            setStockCode(symbol);
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
                        { name: "Profit Margin", stat: roundTo(stockOverview.profitMargin, 2) },
                        { name: "Quarterly Earnings Growth YOY", stat: roundTo(stockOverview.quarterlyEarningsGrowthYOY, 2) },
                        { name: "Quarterly Revenue Growth YOY", stat: roundTo(stockOverview.quarterlyRevenueGrowthYOY, 2) }
                    ])

                    getStockValues(60, stockOverview.symbol);

                    const tickerFeed = getTickerFeed(stockNews, stockOverview.symbol);
                    setStockNews(tickerFeed);

                } catch (error) {
                    console.error("Error fetching stock details:", error);
                    getStockValues(60, symbol);
                    setStockNews([]);
                }
                // Portfolio
                const portfolioAPI = getPortfolioByUserId(authUser.id);
                portfolioAPI.then((response) => {
                    if (response["success"]) {
                        setPortfolios(response["data"]);
                    } else {
                        console.log("Error fetching portfolios");
                    }
                }).catch((error) => {
                    console.log(error);
                });
                setIsLoading(false);
            };
            getStockDetails();
        } else {
            console.log("auth never loaded");
        }
    }, [location.search, authUser, isLoggedIn]);


    const [stockDetails, setStockDetails] = React.useState<StockDetails>({ symbol: "", country: "", currency: "", industry: "", exchange: "", description: "" });
    const [stockStats, setStockStats] = React.useState<StockStats[]>([]);
    const [stockHistoricalValues, setStockHistoricalValues] = React.useState<StockHistoricalValue[]>([]);
    const [stockNews, setStockNews] = React.useState<any[]>([]);
    const [stockPriceStats, setStockPriceStats] = React.useState<StockPriceStats[]>([]);

    // Modal Variables
    const todayString = today.getFullYear() + '-' + String(today.getMonth() + 1) + '-' + String(today.getDate()).padStart(2, '0');

    const [showModal, setShowModal] = React.useState<boolean>(false);
    const [portfolios, setPortfolios] = React.useState<any[]>([]);
    const [selectedPortfolio, setSelectedPortfolio] = React.useState<string>("");
    const [side, setSide] = React.useState<string>("");
    const [stockCode, setStockCode] = React.useState<string>("");
    const [date, setDate] = React.useState<string>("");
    const [quantity, setQuantity] = React.useState<string>("");
    const [price, setPrice] = React.useState<string>("");

    const handleAddClick = () => {
        setShowModal(true);
    }

    const handleModalClose = () => {
        setShowModal(false);
        //clear form inputs
        setSelectedPortfolio("");
        setSide("");
        setDate("");
        setQuantity("");
        setPrice("");
        setSummaryStr("");
    }

    const handleButtons = (e: any) => {
        e.preventDefault();

        let position = {};
        if (side === "BUY") {
            position = { "stockSymbol": stockCode, "price": price, "position": "LONG", "quantity": quantity, "positionAddDate": date }
        } else {
            position = { "stockSymbol": stockCode, "price": price, "position": "SELLTOCLOSE", "quantity": quantity, "positionAddDate": date }
        }

        const positionAPI = createPortfolioPosition(selectedPortfolio, position);
        positionAPI.then((response) => {
            if (response["success"]) {
                toast.success('Successfully added ' + stockCode + ' to portfolio', {
                    position: "top-right",
                    autoClose: 5000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: false,
                    progress: undefined,
                    theme: "colored",
                });

                if (side === "BUY") {
                    const currentDateTime = new Date().toISOString().slice(0, 19);

                    const event_data = {
                        "event": "ADDED LONG " + stockCode + " POSITION IN PORTFOLIO " + selectedPortfolio,
                        "timestamp": currentDateTime
                    }

                    createNewUserEvent(authUser.id, event_data);
                } else {
                    const currentDateTime = new Date().toISOString().slice(0, 19);

                    const event_data = {
                        "event": "ADDED SELLTOCLOSE " + stockCode + " POSITION IN PORTFOLIO " + selectedPortfolio,
                        "timestamp": currentDateTime
                    }

                    createNewUserEvent(authUser.id, event_data);
                }

            } else {
                toast.error('Error adding ' + stockCode + ' to portfolio, please try again later', {
                    position: "top-right",
                    autoClose: 5000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: false,
                    progress: undefined,
                    theme: "colored",
                });
            }
        }).catch((error) => {
            toast.error('Error adding stock to portfolio, Not enough capital in portfolio', {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: false,
                progress: undefined,
                theme: "colored",
            });
        });
        handleModalClose();
    }

    const [summaryStr, setSummaryStr] = React.useState<string>("");
    const summary = () => {
        // construct the string
        setSummaryStr(`${side} ${quantity} ${stockCode} @ ${price} USD on ${date}`);
    }

    if (isLoading) {
        return (
            <Loading></Loading>
        )
    }


    // Website code 
    return (
        <div>
            <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
            <div>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6 max-w-screen-2xl mx-auto">
                    <h3 className="text-2xl font-bold leading-7 text-gsgray90 sm:truncate sm:text-3xl sm:tracking-tight">
                        {stockCode}
                    </h3>
                    <div className="min-w-0 flex-1">
                        <div className="mt-1 flex flex-col sm:mt-0 sm:flex-row sm:flex-wrap sm:space-x-6 mx-2">
                            {stockDetails.country !== "" ? (
                                <div className="mt-2 flex items-center text-sm text-gsgray70">
                                    <MapPinIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gsgray70" aria-hidden="true" />
                                    {stockDetails.country}
                                </div>
                            ) : (
                                <div></div>
                            )}
                            {stockDetails.industry !== "" ? (
                                <div className="mt-2 flex items-center text-sm text-gsgray70">
                                    <TagIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gsgray70" aria-hidden="true" />
                                    {stockDetails.industry}
                                </div>
                            ) : (
                                <div></div>
                            )}
                            <div className="mt-2 flex items-center text-sm text-gsgray70">
                                <CalendarIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gsgray70" aria-hidden="true" />
                                {formattedDate}
                            </div>
                        </div>
                    </div>
                    <div className="mt-5 flex lg:ml-4 lg:mt-0">
                        <span className="hidden sm:block">
                            <button
                                type="button"
                                className="inline-flex items-center rounded-md bg-gswhite px-3 py-2 text-sm font-semibold text-gsgray90 shadow-sm ring-1 ring-inset ring-gsgray40 hover:bg-gray-50"
                                onClick={handleAddClick}
                            >
                                <PlusCircleIcon className="-ml-0.5 mr-1.5 h-5 w-5 text-gsgray70" aria-hidden="true" />
                                Add Stock to Portfolio
                            </button>
                        </span>
                    </div>
                </div>
            </div>
            <div className='my-2 px-6'>
                <dl className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-3 max-w-screen-2xl mx-auto">
                    {stockStats.map((item) => (
                        <div key={item.name} className="overflow-hidden rounded-lg bg-gswhite px-4 py-5 shadow sm:p-6">
                            <dt className="truncate text-sm font-medium text-gsgray90">{item.name}</dt>

                            <dd className={classNames(
                                item.stat > 0 ? 'text-gsgreen60' : 'text-gsred60', 'mt-1 text-3xl font-semibold tracking-tight text-gsgray90')}>{item.stat}%</dd>
                            <p
                                className={classNames(
                                    item.stat > 0 ? 'text-gsgreen60' : 'text-gsred60',
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
            <div className='my-6 px-6 w-full max-w-screen-2xl mx-auto'>
                <div className="relative">
                    <div className="w-full inset-0 flex items-center" aria-hidden="true">
                        <div className=" border-t border-gray-300" />
                    </div>
                    <div className="relative flex justify-start">
                        <span className="bg-gswhite pr-3 text-base font-semibold leading-6 text-gsgray90">Stock Price History</span>
                    </div>
                </div>
                <span className="isolate inline-flex rounded-md shadow-sm">
                    <button
                        type="button" onClick={() => getStockValues(30)}
                        className="relative inline-flex items-center rounded-l-md bg-gswhite px-3 py-2 text-sm font-semibold text-gsgray90 ring-1 ring-inset ring-gsgray40 hover:bg-gray-50 focus:z-10"
                    >
                        30 Days
                    </button>
                    <button
                        type="button" onClick={() => getStockValues(60)}
                        className="relative -ml-px inline-flex items-center bg-gswhite px-3 py-2 text-sm font-semibold text-gsgray90 ring-1 ring-inset ring-gsgray40 hover:bg-gray-50 focus:z-10"
                    >
                        60 Days
                    </button>
                    <button
                        type="button" onClick={() => getStockValues(90)}
                        className="relative -ml-px inline-flex items-center rounded-r-md bg-gswhite px-3 py-2 text-sm font-semibold text-gsgray90 ring-1 ring-inset ring-gsgray40 hover:bg-gray-50 focus:z-10"
                    >
                        90 Days
                    </button>
                </span>
            </div>
            <div className='my-2 px-6 max-w-screen-2xl mx-auto'>
                {stockHistoricalValues.length > 0 && <StockLineChart data={stockHistoricalValues}></StockLineChart>}
            </div>
            <div className='my-4 px-6 max-w-screen-2xl mx-auto'>
                <h3 className="text-base font-semibold leading-6 text-gsgray90">Last 30 days</h3>
                <dl className="mt-5 grid grid-cols-1 divide-y divide-gsgray40 overflow-hidden rounded-lg bg-gswhite border border-gsgray40 md:grid-cols-3 md:divide-x md:divide-y-0">
                    {stockPriceStats.map((item) => (
                        <div key={item.name} className="px-4 py-5 sm:p-6">
                            <dt className="text-base font-normal text-gsgray90">{item.name}</dt>
                            <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                <div className="flex items-baseline text-3xl font-semibold text-gsgray90">
                                    ${item.stat}
                                    <span className="ml-2 text-sm font-medium text-gsgray50">from ${item.previousStat}</span>
                                </div>

                                <div
                                    className={classNames(
                                        item.changeType === 'increase' ? 'bg-green-100 text-gsgreen60' : 'bg-red-100 text-gsred60',
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
                                    {(item.change.includes("-")) 
                                                    ? "-$" + item.change.replace("-", "") + " (" + ((parseInt(item.change)/item.previousStat) *-100).toFixed(2) + "%)"
                                                    : "$" + item.change + " (" + ((parseInt(item.change)/item.previousStat) *100).toFixed(2) + "%)"}
                                </div>
                            </dd>
                        </div>
                    ))}
                </dl>
            </div>

            <div className="sm:px-6 lg:px-8 py-4 px-6 my-2 max-w-screen-2xl mx-auto">
                <div className="relative">
                    <div className="absolute inset-0 flex items-center" aria-hidden="true">
                        <div className="w-full border-t border-gsgray30" />
                    </div>
                    <div className="relative flex justify-start">
                        <span className="bg-gswhite pr-3 text-base font-semibold leading-6 text-gsgray90">Company</span>
                    </div>
                </div>
                {stockNews.length > 0 ? (
                    <div className="my-4 divide-y divide-gsgray20 overflow-hidden rounded-lg bg-gswhite shadow">
                        <div className="px-4 py-5 sm:px-6 font-semibold bg-gsblue60 text-gswhite">
                            Description
                        </div>
                        <div className="px-4 py-5 sm:p-6 text-center">
                            {stockDetails.description}
                        </div>
                    </div>
                ) : (
                    <NoStock></NoStock>
                )}
            </div>

            {stockNews.length > 0 ? (
                <div className="mx-auto max-w-7xl sm:px-6 lg:px-8 py-4 px-6 my-2">
                    <div className="divide-y divide-gsgray20 overflow-hidden rounded-lg bg-gswhite shadow">
                        <div className="px-4 py-5 sm:px-6 font-semibold bg-gsblue60 text-gswhite">
                            News Feed
                        </div>
                        <div className="px-4 py-5 sm:p-6">
                            <NewsFeed data={stockNews}></NewsFeed>
                        </div>
                    </div>
                </div>
            ) : (
                <div></div>
            )}

            {showModal && (
                <div className="fixed z-10 inset-0 overflow-y-auto">
                    <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
                        <div className="fixed inset-0 transition-opacity" aria-hidden="true">
                            <div onClick={handleModalClose} className="absolute inset-0 bg-gsgray20 opacity-75"></div>
                        </div>
                        <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
                        <div className="inline-block align-bottom bg-gswhite rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
                            <div className="bg-gswhite px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
                                <div className="items-center">
                                    <div className="mt-3 text-center sm:ml-4 sm:mt-0 sm:text-center">
                                        <h3 className="font-semibold leading-6 text-gsgray90 text-3xl" id="modal-title">Add New Position</h3>
                                        <form id="modalForm" className="my-6">
                                            <div className="mb-3">
                                                <select required value={selectedPortfolio} onChange={(e) => setSelectedPortfolio(e.target.value)} onMouseUp={summary}
                                                    className="w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight"
                                                    id="side">
                                                    <option value="" disabled selected className="font-bold">Portfolio</option>
                                                    {portfolios.map((portfolio) => (
                                                        <option key={portfolio.portfolioID} value={portfolio.portfolioID}>{portfolio.portfolioName}</option>
                                                    )
                                                    )}
                                                </select>
                                            </div>
                                            <div className="mb-3">
                                                <select required value={side} onChange={(e) => setSide(e.target.value)} onMouseUp={summary}
                                                    className="w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight"
                                                    id="side">
                                                    <option value="" disabled selected className="font-bold">Side</option>
                                                    <option value="BUY">BUY</option>
                                                </select>
                                            </div>

                                            <div className="mb-3">
                                                <div id="stockCode" className="w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight">
                                                    {stockCode}
                                                </div>
                                                <input type="hidden" name="stockCode" value={stockCode} />
                                            </div>

                                            <div className="mb-3">
                                                <input className="appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                                    id="quantity"
                                                    type="number"
                                                    placeholder="Stock Quantity"
                                                    required
                                                    value={quantity}
                                                    onChange={(e) => setQuantity(e.target.value)}
                                                    onKeyUp={summary}
                                                    onMouseUp={summary}
                                                >
                                                </input>
                                            </div>

                                            <div className="mb-3">
                                                <input className="appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                                    id="price"
                                                    type="number"
                                                    placeholder="Stock Price"
                                                    required
                                                    value={price}
                                                    onChange={(e) => setPrice(e.target.value)}
                                                    onKeyUp={summary}
                                                    onMouseUp={summary}
                                                    step={0.01}
                                                >
                                                </input>
                                            </div>

                                            <div className="mb-3">
                                                <input className="appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                                    id="date"
                                                    type="date"
                                                    required
                                                    value={date}
                                                    onChange={(e) => setDate(e.target.value)}
                                                    onKeyUp={summary}
                                                    onMouseUp={summary}
                                                    max={todayString}
                                                >
                                                </input>
                                            </div>
                                        </form>

                                        <div className="mb-3">
                                            <span id="summary">{summaryStr}</span>
                                        </div>
                                        <hr className=""></hr>
                                        <div className="px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
                                            <button type="button" onClick={handleModalClose} className="inline-flex w-full justify-center rounded-md bg-gsgray70 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-gsgray90 sm:ml-3 sm:w-auto">Cancel</button>

                                            <button type="submit" onClick={(e) => handleButtons(e)} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Add Position</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            {/* MODALSSS*/}
            <ToastContainer transition={Slide} />
            <Footer></Footer>
        </div>
    );
}

export default Stock;