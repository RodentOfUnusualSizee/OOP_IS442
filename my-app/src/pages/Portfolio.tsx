import { Fragment } from 'react';
import React from 'react';
import { useLocation } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import {
    BriefcaseIcon,
    CalendarIcon,
    CurrencyDollarIcon,
    PencilIcon,
} from '@heroicons/react/20/solid';
import LineChartComponent from '../components/LineChartComponent';
import PieChartComponent from '../components/PieChartComponent';
import Table from '../components/Table';
import { getPortfolioByUserId, roundTo } from '../utils/api';
import { useAuth } from '../context/AuthContext';
import { useSearchParams } from "react-router-dom";

function Portfolio() {

    const [showModal, setShowModal] = React.useState<boolean>(false);
    const { authUser, isLoggedIn } = useAuth();
    const [hasFetchedData, setHasFetchedData] = React.useState(false);
    const [searchParams] = useSearchParams();

    const handleAddClick = () => {
        setShowModal(true);
    }

    const handleModalClose = () => {
        setShowModal(false);
        //clear form inputs
        setSide("");
        setStockCode("");
        setDate("");
        setQuantity("");
        setPrice("");
    }

    // form inputs
    const [side, setSide] = React.useState<string>("");
    const [stockCode, setStockCode] = React.useState<string>("");
    const [date, setDate] = React.useState<string>("");
    const [quantity, setQuantity] = React.useState<string>("");
    const [price, setPrice] = React.useState<string>("");
    const [data, setData] = React.useState<[]>([]);

    // get today's date and parse as string containing yy/mm/dd
    const today = new Date();
    const todayString = today.getFullYear() + '-' + String(today.getMonth() + 1) + '-' + String(today.getDate()).padStart(2, '0');

    const handleButtons = () => {
        const form = document.getElementById("modalForm") as HTMLFormElement;
        form.submit();
        alert("Side: " + side + ", Stock Code: " + stockCode + ", Date: " + date + ", Quantity: " + quantity + ", Price: " + price);
        // insert API call below to handle adding the position into DB
        // Think the page refreshes upon refresh, not sure if need to handle async portion
    }

    const summary = () => {
        const summary = document.getElementById("summary") as HTMLSpanElement;
        summary.innerHTML = side + " " + quantity + " " + stockCode + " @ $" + price + " on " + date;
    }

    const userId = authUser.id;
    let portfolioId = searchParams.get("id")

    // get portfolios
    React.useEffect(() => {
        if (!hasFetchedData) {
            const portfolio = getPortfolioByUserId(userId);
            portfolio.then((response) => {
                setData(response.data)
                setHasFetchedData(true);
            }).catch((error) => {
                console.log(error);
            });
        }
    }, [portfolioId]);

    let portfolio = {};
    let stockTableData: any[] = [];
    let piechartdata = [];
    let linechartdata = [];
    let capitalChangeAbs = "";
    let capitalChangePercent = "";
    let lastModified = "";
    let samplePortfolioData = {};


    // for (let i = 0; i < data.length; i++) {
    //     // get specific portfolio
    //     if (data[i].portfolioID == portfolioId) {
    //         portfolio = data[i];

    //         // portfolio metrics
    //         samplePortfolioData = { id: portfolio.portfolioID, name: portfolio.portfolioName, strategy: portfolio.strategyDesc, capital: portfolio.capitalUSD };

    //         // table
    //         stockTableData = portfolio.cumPositions;

    //         // parse through portfolio data
    //         let allocation = portfolio.portfolioAllocationBySector;
    //         let historicalVal = portfolio.portfolioHistoricalValue;
    //         let positions = portfolio.positions;

    //         for (var k in allocation) {
    //             let val = roundTo(allocation[k], 4);
    //             piechartdata.push({ "name": k, "value": val });
    //         }

    //         let firstVal = ""; // first historical price value
    //         let lastVal = ""; // last historical price value

    //         // line chart
    //         for (var h in historicalVal) {
    //             linechartdata.push({ "date": h, "price": historicalVal[h] })
    //             if (!lastVal) {
    //                 lastVal = historicalVal[h];
    //             }
    //             firstVal = historicalVal[h];
    //         }

    //         // capital change metrics
    //         let diffAbs = parseFloat(lastVal) - parseFloat(firstVal);
    //         let diffPercent = ((parseFloat(lastVal) - parseFloat(firstVal)) / parseFloat(lastVal)) * 100

    //         diffAbs = roundTo(diffAbs, 2)
    //         diffPercent = roundTo(diffPercent, 2)

    //         if (diffAbs > 0) {
    //             capitalChangeAbs = "+$" + diffAbs;
    //             capitalChangePercent = diffPercent + "%";
    //         } else {
    //             capitalChangeAbs = "-$" + diffAbs;
    //             capitalChangePercent = diffPercent + "%";
    //         }

    //         // last modified date metric

    //         let modified = new Date("1998-01-01T00:00:00.000+00:00"); // initialise to 1st Jan 1998

    //         for (let p = 0; p < positions.length; p++) {
    //             let position = positions[p];
    //             let timestamp = new Date(position.lastModifiedTimestamp);
    //             if (timestamp > modified) {
    //                 modified = timestamp;
    //             }
    //         }

    //         let currDate = new Date();
    //         let timeDiff = currDate.getTime() - modified.getTime();
    //         lastModified = roundTo(timeDiff / (1000 * 60 * 60 * 24), 0) + " days";

    //     }
    // }

    const stats = [
        { name: 'Capital Change ($) ', stat: capitalChangeAbs },
        { name: 'Capital Change (%)', stat: capitalChangePercent },
        { name: 'Days since Portfolio Active', stat: lastModified },
    ]

    const tableHeaders = [
        { header: 'Stock Name', key: 'stockSymbol' },
        { header: 'Stock Sector', key: 'stockSector' },
        { header: 'Total Quantity', key: 'totalQuantity' },
        { header: 'Average Price', key: 'averagePrice' },
        { header: 'Current Price', key: 'currentValue' },
    ];


    const tableTitle = 'Stocks';
    const tableDescription = 'List of stocks in portfolio and related data';
    const tableAction = "View Stock";

    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const id = queryParams.get('id');

    return (
        <div>
            <Header management={true} userType={"user"} login={true} ></Header>
            <div>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6">
                    <h3 className="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight">
                        {/* {samplePortfolioData.name} */}
                    </h3>
                    <div className="min-w-0 flex-1">
                        <div className="mt-1 flex flex-col sm:mt-0 sm:flex-row sm:flex-wrap sm:space-x-6 mx-2">
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <BriefcaseIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {/* {samplePortfolioData.strategy} */}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <CurrencyDollarIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {/* {samplePortfolioData.capital} */}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <CalendarIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                Sample Date
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
            <h4 className='font-semibold'>Portfolio Performance</h4>
            <div className="flex my-6">
                {/* <LineChartComponent data={linechartdata}></LineChartComponent> */}
                {/* <PieChartComponent data={piechartdata}></PieChartComponent> */}
            </div>
            <div className="my-6 px-6">
                <Table
                    tableData={stockTableData}
                    tableHeaders={tableHeaders}
                    tableTitle={tableTitle}
                    tableDescription={tableDescription}
                    tableAction={tableAction}
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
                                        <form id="modalForm" className="my-6" onSubmit={(e) => {
                                            e.preventDefault();
                                            handleModalClose();
                                        }}>
                                            <div className="mb-3">
                                                <select required value={side} onChange={(e) => setSide(e.target.value)} onMouseUp={summary}
                                                    className="w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight"
                                                    id="side">
                                                    <option value="" disabled selected className="font-bold">Side</option>
                                                    <option value="BUY">BUY</option>
                                                    <option value="SELL">SELL</option>
                                                </select>
                                            </div>

                                            <div className="mb-3">
                                                <select required value={stockCode} onChange={(e) => setStockCode(e.target.value)} onMouseUp={summary}
                                                    className="w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight"
                                                    id="stock">
                                                    {/* These should be autofilled in the future, hardcoded for now */}
                                                    <option value="" disabled selected className="font-bold">Stock Code</option>
                                                    <option value="AAPL">AAPL</option>
                                                    <option value="TSLA">TSLA</option>
                                                    <option value="NVDA">NVDA</option>
                                                    <option value="META">META</option>
                                                </select>
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
                                                    onKeyUp={summary} // for typing
                                                    onMouseUp={summary} // for clicking
                                                    max={todayString} // limit date because you cannot buy/sell in the future
                                                >
                                                </input>
                                            </div>
                                        </form>

                                        <div className="mb-3">
                                            <span id="summary">-</span>
                                        </div>
                                        <hr className=""></hr>
                                        <div className="px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
                                            <button type="button" onClick={handleModalClose} className="inline-flex w-full justify-center rounded-md bg-gsgray70 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-gsgray90 sm:ml-3 sm:w-auto">Cancel</button>

                                            <button type="submit" onClick={handleButtons} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Add Position</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            {/* MODALSSS*/}
            <Footer></Footer>
        </div>
    );
}

export default Portfolio;