import React, { useEffect, useState } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import {
    BriefcaseIcon,
    CalendarIcon,
    CurrencyDollarIcon,
    PencilIcon,
    ArrowUpIcon,
    ArrowDownIcon,
    TagIcon,
    BanknotesIcon
} from '@heroicons/react/20/solid';
import LineChartComponent from '../components/LineChartComponent';
import PieChartComponent from '../components/PieChartComponent';
import Table from '../components/Table';
import { createPortfolioPosition, getPortfolioByUserId, getTickerData, getStockPrice, createNewUserEvent } from '../utils/api';
import { useAuth } from '../context/AuthContext';
import { useSearchParams } from "react-router-dom";
import { Slide, toast, ToastContainer } from 'react-toastify';
import { roundTo, roundToString } from '../utils/transform';


function classNames(...classes: String[]) {
    return classes.filter(Boolean).join(' ')
}

function Portfolio() {
    interface portfolioDetails {
        name: string;
        strategy: string;
        value: string
        capital: string;
    }

    const [stockTableData, setStockTableData] = useState<any[]>([]);
    const [piechartdata, setPiechartdata] = useState<any[]>([]);
    const [linechartdata, setLinechartdata] = useState<any[]>([]);
    const [PortfolioData, setPortfolioData] = useState<portfolioDetails>({ name: "", strategy: "", value: "", capital: "" });

    const [stats, setStats] = useState<any[]>([
        { name: 'Capital Change ($)', stat: "" },
        { name: 'Capital Change (%)', stat: "" },
        { name: 'Days since Portfolio Active', stat: "" },
    ]);

    const [searchParams] = useSearchParams();
    let portfolioId = searchParams.get("id");
    const today = new Date();

    const [showModal, setShowModal] = useState<boolean>(false);
    const { authUser, isLoggedIn } = useAuth();
    const [hasFetchedData, setHasFetchedData] = useState(false);

    // persist login
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [userIsLoggedIn, setUserIsLoggedIn] = useState<boolean>(true);
    const [userId, setUserId] = useState<number>(1);
    const [userRole, setUserRole] = useState<string>("user");
    const management = userRole === "admin" || userRole === "user";

    // Tables
    const tableHeaders = [
        { header: 'Stock Name', key: 'stockSymbol' },
        { header: 'Stock Sector', key: 'stockSector' },
        { header: 'Total Quantity', key: 'totalQuantity' },
        { header: 'Average Price', key: 'averagePrice' },
        { header: 'Current Price', key: 'currentValue' },
        { header: 'Action', key: 'action' }
    ];

    const tableTitle = 'Stocks';
    const tableDescription = 'List of stocks in portfolio and related data';
    const tableAction = "View Portfolio Stock Record";

    const tableLink = '/StockRecord?id=' + portfolioId + '&stock=';

    // Portfolio Performance
    const [performanceStats, setPerformanceStats] = useState<any[]>([
        { name: 'Portfolio MoM Growth %', stat: "" },
        { name: 'Portfolio QoQ Growth %', stat: "" },
        { name: 'Portfolio YoY Growth %', stat: "" },
    ]);

    const [quarterlyStats, setQuarterlyStats] = useState<any[]>([
        { name: 'Quarterly Returns ($)', stat: [] },
        { name: 'Quarterly Returns (%)', stat: [] },
    ]);

    // Portfolio Benchmarks
    const [benchmarks, setBenchmarks] = useState<any[]>([
        { name: 'Portfolio Beta', stat: "", desc: "" },
        { name: 'Information Ratio', stat: "", desc: "" },
    ]);



    // Ticker 
    const [tickers, setTickers] = useState<any[]>([]);
    const [activeTab, setActiveTab] = useState(0);
    const [selectedTicker, setSelectedTicker] = useState<any>({
        symbol: '',
        name: '',
        type: '',
        currency: '',
    });

    const handleTabClick = (index: any) => {
        setActiveTab(index);
        setSelectedTicker(tickers[index]);
        setStockCode(tickers[index].symbol);
    };

    const handleMarketPrice = () => {
        const priceData = getStockPrice(selectedTicker.symbol);
        priceData.then((response) => {
            setPrice(response["timeSeries"][0]["close"]);

            const currentDateTime = new Date().toISOString().slice(0, 19);

            const event_data = {
                "event": "GET_MARKET_PRICE " + selectedTicker.symbol,
                "timestamp": currentDateTime,
            }

            createNewUserEvent(authUser.id, event_data);
        }).catch((error) => {
            console.log(error);
        });
    }


    useEffect(() => {
        if (authUser) {
            setUserIsLoggedIn(true);
            setUserId(authUser.id);
            setUserRole(authUser.role);

            const currentDateTime = new Date().toISOString().slice(0, 19);


            const event_data = {
                "event": "VIEW_PORTFOLIO " + portfolioId,
                "timestamp": currentDateTime,
            }

            createNewUserEvent(authUser.id, event_data);

            if (!hasFetchedData) {
                refreshData(portfolioId);
                setHasFetchedData(true);
                setIsLoading(false);
            }
        } else {
            setUserIsLoggedIn(false);
            setIsLoading(false);
        }

    }, [authUser, isLoggedIn, hasFetchedData, portfolioId]);

    async function refreshData(portfolioId: any) {
        try {
            const response = await getPortfolioByUserId(userId);
            organiseData(response.data);
        } catch (error) {
            console.log(error);
        };
    }

    async function tickerData(stockCode: string) {
        try {
            const response = await getTickerData(stockCode);


            const currentDateTime = new Date().toISOString().slice(0, 19);

            const event_data = {
                "event": "SEARCH_TICKER " + stockCode,
                "timestamp": currentDateTime,
            }

            createNewUserEvent(authUser.id, event_data);

            return response;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    async function handleSearch() {
        try {
            const stockSymbol = (document.getElementById("stockSearch") as HTMLInputElement).value;
            const tickers = await tickerData(stockSymbol);
            setTickers(
                tickers['bestMatches'].map((ticker: any) => ({
                    symbol: ticker.symbol,
                    name: ticker.name,
                    type: ticker.type,
                    currency: ticker.currency,
                }))
            );
        } catch (error) {
            console.error(error);
        }
    }


    function organiseData(data: any) {
        const portfolio = data.find((portfolio: any) => portfolio.portfolioID.toString() === portfolioId);

        setPortfolioData({ name: portfolio.portfolioName, strategy: portfolio.strategyDesc, value: roundToString(parseFloat(portfolio.currentTotalPortfolioValue), 2), capital: roundToString(parseFloat(portfolio.capitalUSD), 2) });

        // table
        setStockTableData(portfolio.cumPositions.map((position: any) => ({
            id: position.stockSymbol,
            stockSymbol: position.stockSymbol,
            stockSector: position.stockSector,
            totalQuantity: position.totalQuantity,
            averagePrice: "$" + roundToString(position.averagePrice, 2),
            currentValue: "$" + roundToString(position.currentValue, 2),
        })));

        // parse through portfolio data
        let allocation = portfolio.portfolioAllocationBySector;
        let historicalVal = portfolio.portfolioHistoricalValue;
        let positions = portfolio.positions;

        // pie chart
        const tempPieChartData = [];

        for (var k in allocation) {
            let val = roundTo(allocation[k], 4);
            tempPieChartData.push({ "name": k, "value": roundTo(val, 2) });
        }

        setPiechartdata(tempPieChartData);

        // line chart
        let firstVal = 0;
        let lastVal = 0;
        const tempLineChartData = [];


        for (var h in historicalVal) {
            tempLineChartData.push({ "date": h, "price": roundTo(historicalVal[h], 2) })
        }

        tempLineChartData.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()); // sort by date
        firstVal = tempLineChartData[0].price; // get first historical price value
        lastVal = tempLineChartData[tempLineChartData.length - 1].price; // get last historical price value

        setLinechartdata(tempLineChartData);

        // capital change metrics
        let diffAbs = lastVal - firstVal;
        let diffPercent = ((lastVal - firstVal) / lastVal) * 100

        diffAbs = roundTo(diffAbs, 2)
        diffPercent = roundTo(diffPercent, 2)

        // last modified date metric
        let modified = new Date("1998-01-01T00:00:00.000+00:00"); // initialise to 1st Jan 1998

        for (let p = 0; p < positions.length; p++) {
            let position = positions[p];
            let timestamp = new Date(position.lastModifiedTimestamp);
            if (timestamp > modified) {
                modified = timestamp;
            }
        }

        let timeDiff = today.getTime() - modified.getTime();

        setStats([
            { name: 'Capital Change ($)', stat: diffAbs > 0 ? `+$${diffAbs}` : `-$${-diffAbs}` },
            { name: 'Capital Change (%)', stat: isNaN(diffPercent) ? `0%` : `${diffPercent}%` },
            { name: 'Days since Portfolio Active', stat: `${roundTo(timeDiff / (1000 * 60 * 60 * 24), 0)} days` },
        ]);

        // performance 
        const MoM = portfolio.portfolioMoM;
        const QoQ = portfolio.portfolioQoQ;
        const YoY = portfolio.portfolioYoY;


        setPerformanceStats([
            { name: 'Portfolio MoM Growth %', stat: MoM },
            { name: 'Portfolio QoQ Growth %', stat: QoQ },
            { name: 'Portfolio YoY Growth %', stat: YoY },
        ]);

        // quarterly
        const quarterlyReturns = portfolio.quarterlyReturns;
        const quarterlyReturnsPercent = portfolio.quarterlyReturnsPercentage;

        const tempQuarterlyStats = [];
        const tempQuarterlyStatsPercent = [];

        for (var h in quarterlyReturns) {
            tempQuarterlyStats.push({ "date": h, "price": quarterlyReturns[h] })
            if (!lastVal) {
                lastVal = quarterlyReturns[h];
            }
            firstVal = quarterlyReturns[h];
        }

        for (var h in quarterlyReturnsPercent) {
            tempQuarterlyStatsPercent.push({ "date": h, "price": quarterlyReturnsPercent[h] })
            if (!lastVal) {
                lastVal = quarterlyReturnsPercent[h];
            }
            firstVal = quarterlyReturnsPercent[h];
        }

        setQuarterlyStats([
            { name: 'Quarterly Returns ($)', stat: tempQuarterlyStats },
            { name: 'Quarterly Returns (%)', stat: tempQuarterlyStatsPercent },
        ]);

        // benchmarks
        setBenchmarks([
            { name: 'Portfolio Beta', stat: roundTo(portfolio.portfolioBeta, 6), desc: "Measures a portfolio's volatility relative to the market, with a beta of 1 indicating movement with the market and a beta less than 1 indicating lower volatility." },
            { name: 'Information Ratio', stat: roundTo(portfolio.informationRatio, 6), desc: "Measures skill and consistency in generating excess returns relative to the benchmark, with higher information ratio indicating higher skill and consistency." },
        ]);

    }

    // modal 
    const [side, setSide] = useState<string>("");
    const [stockCode, setStockCode] = useState<string>("");
    const [date, setDate] = useState<string>("");
    const [quantity, setQuantity] = useState<string>("");
    const [price, setPrice] = useState<string>("");

    const todayString = today.getFullYear() + '-' + String(today.getMonth() + 1) + '-' + String(today.getDate()).padStart(2, '0');

    const handleAddClick = () => {
        setShowModal(true);
    }

    const handleModalClose = () => {
        setShowModal(false);
        setSide("");
        setStockCode("");
        setDate("");
        setQuantity("");
        setPrice("");
    }

    const handleButtons = (e: any) => {
        e.preventDefault();
        let position = {};
        if (side === "BUY") {
            position = { "stockSymbol": stockCode, "price": price, "position": "LONG", "quantity": quantity, "positionAddDate": date };
        } else {
            position = { "stockSymbol": stockCode, "price": price, "position": "SELLTOCLOSE", "quantity": quantity, "positionAddDate": date };
        }

        const positionAPI = createPortfolioPosition(portfolioId, position);
        positionAPI.then((response) => {
            if (response["success"]) {
                toast.success('Position successfully added to Portfolio', {
                    position: "top-right",
                    autoClose: 5000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: false,
                    progress: undefined,
                    theme: "colored",
                });
                console.log(response);


                const currentDateTime = new Date().toISOString().slice(0, 19);

                const event_data = {
                    "event": "ADD_POSITION " + stockCode + " TO PORTFOLIO " + portfolioId,
                    "timestamp": currentDateTime,
                }

                createNewUserEvent(authUser.id, event_data);

                refreshData(portfolioId);
            } else {
                toast.error('Error adding position to portfolio, please try again later', {
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
            console.log(error);
        });
        handleModalClose();
    }

    const summary = () => {
        const summary = document.getElementById("summary") as HTMLSpanElement;
        summary.innerHTML = side + " " + quantity + " " + stockCode + " @ $" + price + " on " + date;
    }

    // TODO isloading

    return (
        <div className='overflow-x-hidden'>
            <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
            <div>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6">
                    <h3 className="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight">
                        {PortfolioData.name}
                    </h3>
                    <div className="min-w-0 flex-1">
                        <div className="mt-1 flex flex-col sm:mt-0 sm:flex-row sm:flex-wrap sm:space-x-6 mx-2">
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <BriefcaseIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {PortfolioData.strategy}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <CurrencyDollarIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                ${PortfolioData.value}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <BanknotesIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                ${PortfolioData.capital}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <CalendarIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {todayString}

                            </div>
                        </div>
                    </div>
                    <div className="mt-5 flex lg:ml-4 lg:mt-0">
                        <span className="hidden sm:block">
                            <button
                                type="button"
                                className="inline-flex items-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                                onClick={handleAddClick}
                            >
                                <PencilIcon className="-ml-0.5 mr-1.5 h-5 w-5 text-gray-400" aria-hidden="true" />
                                Add
                            </button>
                        </span>
                    </div>
                </div>
                <div className="my-2 px-6">
                    <h3 className="text-base font-semibold leading-6 text-gray-900">Portfolio Stats</h3>
                    <dl className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-3">
                        {stats.map((item) => (
                            <div
                                key={item.name}
                                className={`overflow-hidden rounded-lg bg-white px-4 py-5 shadow sm:p-6 ${item.stat.includes('-') ? 'text-red-500' : item.stat.includes('+') ? 'text-green-500' : 'text-gray-900'
                                    }`}
                            >
                                <dt className="truncate text-sm font-medium text-gray-500">{item.name}</dt>
                                <dd className="mt-1 text-3xl font-semibold tracking-tight">{item.stat}</dd>
                            </div>
                        ))}
                    </dl>
                </div>
            </div>

            <div className="my-6 px-6">
                <h3 className='font-semibold'>Portfolio Performance</h3>
                <div className="flex my-6">
                    <LineChartComponent data={linechartdata} width={600} height={300} ></LineChartComponent>
                    <PieChartComponent data={piechartdata}></PieChartComponent>
                </div>
                <div className='my-2 px-6'>
                    <dl className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-3">
                        {performanceStats.map((item) => (
                            <div key={item.name} className="overflow-hidden rounded-lg bg-white px-4 py-5 shadow sm:p-6">
                                <dt className="truncate text-sm font-medium text-gray-500">{item.name}</dt>

                                <dd className={classNames(
                                    item.stat.includes('-') ? 'text-red-600' : 'text-green-600', 'mt-1 text-3xl font-semibold tracking-tight text-gray-900')}>{item.stat}</dd>
                                <p
                                    className={classNames(
                                        item.stat.includes('-') ? 'text-red-600' : 'text-green-600',
                                        'ml-2 flex items-baseline text-sm font-semibold'
                                    )}
                                >
                                    {item.stat.includes('-') ? (
                                        <ArrowDownIcon className="h-5 w-5 flex-shrink-0 self-center text-red-500" aria-hidden="true" />
                                    ) : (
                                        <ArrowUpIcon className="h-5 w-5 flex-shrink-0 self-center text-green-500" aria-hidden="true" />
                                    )}
                                </p>
                            </div>
                        ))}
                    </dl>
                </div>
            </div>

            <div className="my-6 px-6">
                <h3 className="text-base font-semibold leading-6 text-gray-900">Portfolio Benchmarks</h3>
                <dl className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-2">
                    {benchmarks.map((item) => (
                        <div
                            key={item.name}
                            className={`overflow-hidden rounded-lg bg-white px-4 py-5 shadow sm:p-6 ${parseInt(item.stat) > 0.5 ? 'text-green-600' : 'text-red-600'
                                }`}
                        >
                            <dt className="truncate text-sm font-medium text-gray-500">{item.name}</dt>
                            <dt className="truncate text-xs font-small text-gray-500 py-2" style={{ maxWidth: '500px', overflow: 'visible', whiteSpace: 'normal' }}>{item.desc}</dt>
                            <dd className="mt-1 text-3xl font-semibold tracking-tight">{item.stat}</dd>
                        </div>
                    ))}
                </dl>
            </div>

            <div className="my-6 px-6">
                <Table
                    tableData={stockTableData}
                    tableHeaders={tableHeaders}
                    tableTitle={tableTitle}
                    tableDescription={tableDescription}
                    tableAction={tableAction}
                    tableLink={tableLink}
                />
            </div>
            {showModal && (
                <div className="fixed z-10 inset-0 overflow-y-auto">
                    <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
                        <div className="fixed inset-0 transition-opacity" aria-hidden="true">
                            <div onClick={handleModalClose} className="absolute inset-0 bg-gsgray20 opacity-75"></div>
                        </div>
                        <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
                        <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
                            <div className="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
                                <div className="items-center">
                                    <div className="mt-3 text-center sm:ml-4 sm:mt-0 sm:text-center">
                                        <h3 className="font-semibold leading-6 text-gsgray90 text-3xl" id="modal-title">Add New Position</h3>
                                        <form id="modalForm" className="my-6">
                                            <div className="mb-3">
                                                <select required value={side} onChange={(e) => setSide(e.target.value)} onMouseUp={summary}
                                                    className="w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight"
                                                    id="side">
                                                    <option value="" disabled selected className="font-bold">Side</option>
                                                    <option value="BUY">BUY</option>
                                                    <option value="SELL">SELL</option>
                                                </select>
                                            </div>

                                            <div className="mb-3 flex">
                                                <input className="appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                                    id="stockSearch"
                                                    type="text"
                                                    placeholder="Stock Symbol"
                                                    required
                                                >
                                                </input>
                                                <button onClick={(e) => handleSearch()} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 mx-2 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Search</button>
                                            </div>

                                            <div>
                                                {tickers.map((ticker, index) => (
                                                    <button
                                                        key={ticker.symbol}
                                                        className={`my-1 px-4 py-2 mx-1 rounded-full ${activeTab === index
                                                            ? 'bg-blue-500 text-white'
                                                            : 'bg-gray-300 text-gray-700'
                                                            }`}
                                                        onClick={(e) => handleTabClick(index)}
                                                    >
                                                        {ticker.name}
                                                    </button>
                                                ))}
                                                {selectedTicker.symbol !== '' ?
                                                    <p className='text-gray-400 my-2 '>
                                                        Type: {tickers[activeTab].type} | Currency: {tickers[activeTab].currency}
                                                    </p> : <p></p>
                                                }
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

                                            <div className="mb-3 flex">
                                                <input className="appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                                    id="price"
                                                    type="number"
                                                    placeholder="Stock Price"
                                                    required
                                                    value={price}
                                                    onChange={(e) => setPrice(e.target.value)}
                                                    onKeyUp={summary}
                                                    onMouseUp={summary}
                                                >
                                                </input>
                                                <button onClick={(e) => handleMarketPrice()} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 mx-2 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Use Market Price</button>
                                            </div>

                                            <div className="mb-3">
                                                <input className="appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                                    id="date"
                                                    type="date"
                                                    required
                                                    value={date}
                                                    onChange={(e) => setDate(e.target.value)}
                                                    onKeyUp={summary} // for typing
                                                    onMouseUp={summary} // for clicking
                                                    max={todayString} // limit date because you cannot buy/sell in the future
                                                >
                                                </input>
                                            </div>
                                        </form>

                                        <div className="mb-3">
                                            <span id="summary"></span>
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

export default Portfolio;