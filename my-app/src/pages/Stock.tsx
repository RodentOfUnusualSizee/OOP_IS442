import React from 'react';
import { useLocation } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import LineChartComponent from '../components/LineChartComponent';
import PieChartComponent from '../components/PieChartComponent';
import Table from '../components/Table';
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

    const sampleStock = {
        "name": "Company A",
        "category": "Technology",
        "market": "US",
        "region": "North America",
        "currentPrice": 50.25,
        "lastUpdated": "2023-10-16",
        "previousPrice": 48.50,
        "priceChangePercentage": 3.57,
        "sevenDaysPrice": 52.00,
        "sevenDaysPriceChangePercentage": -3.85
    }

    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const id = queryParams.get('id');


    const [showModal, setShowModal] = React.useState<boolean>(false);

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
    
    // get today's date and parse as string containing yy/mm/dd
    const today = new Date();
    const todayString = today.getFullYear() + '-' + String(today.getMonth() + 1) + '-' + String(today.getDate()).padStart(2, '0');

    const handleButtons = () => {
        const form = document.getElementById("modalForm") as HTMLFormElement;
        form.submit();
        alert("Side: " + side + ", Date: " + date + ", Quantity: " + quantity + ", Price: " + price);
        // insert API call below
    }
    
    const summary = () => {
        //const summary = document.getElementById("summary") as HTMLSpanElement;
        //summary.innerHTML = side + " " + quantity + " " + stockCode + " @ $" + price + " on " + date;
        return null;
    }

    return (
        <div>
            <Header management={true} userType={"user"} login={true} ></Header>
            <div>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6">
                    <h3 className="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight">
                        {sampleStock.name}
                    </h3>
                    <div className="min-w-0 flex-1">
                        <div className="mt-1 flex flex-col sm:mt-0 sm:flex-row sm:flex-wrap sm:space-x-6 mx-2">
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <MapPinIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {sampleStock.region}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <TagIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {sampleStock.category}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <CalendarIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {sampleStock.lastUpdated}
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
                                <PlusCircleIcon className="-ml-0.5 mr-1.5 h-5 w-5 text-gray-400" aria-hidden="true" />
                                Add Stock to Portfolio
                            </button>
                        </span>
                    </div>
                </div>
            </div>
            <div className='my-2 px-6 justify-center align-middle'>
                <h3 className="text-base font-semibold leading-6 text-gray-900">Price Comparison</h3>

                <dl className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
                    <div
                        key={sampleStock.name}
                        className="relative overflow-hidden rounded-lg bg-white px-4 pb-12 pt-5 shadow sm:px-6 sm:pt-6"
                    >
                        <dt>
                            <p className="ml-16 truncate text-sm font-medium text-gray-500">Against Yesterday</p>
                        </dt>
                        <dd className="ml-16 flex items-baseline pb-6 sm:pb-7">
                            <p className="text-2xl font-semibold text-gray-900">${sampleStock.previousPrice}</p>
                            <p
                                className={classNames(
                                    sampleStock.priceChangePercentage >= 0 ? 'text-green-600' : 'text-red-600',
                                    'ml-2 flex items-baseline text-sm font-semibold'
                                )}
                            >
                                {sampleStock.priceChangePercentage >= 0 ? (
                                    <ArrowUpIcon className="h-5 w-5 flex-shrink-0 self-center text-green-500" aria-hidden="true" />
                                ) : (
                                    <ArrowDownIcon className="h-5 w-5 flex-shrink-0 self-center text-red-500" aria-hidden="true" />
                                )}

                                <span className="sr-only">{sampleStock.priceChangePercentage >= 0 ? 'Increased' : 'Decreased'} by </span>
                                {Math.abs(sampleStock.currentPrice - sampleStock.previousPrice).toFixed(2)}%
                            </p>
                        </dd>
                    </div>
                    <div
                        key={sampleStock.name}
                        className="relative overflow-hidden rounded-lg bg-white px-4 pb-12 pt-5 shadow sm:px-6 sm:pt-6"
                    >
                        <dt>
                            <p className="ml-16 truncate text-sm font-medium text-gray-500">Against last 7 days</p>
                        </dt>
                        <dd className="ml-16 flex items-baseline pb-6 sm:pb-7">
                            <p className="text-2xl font-semibold text-gray-900">${sampleStock.sevenDaysPrice}</p>
                            <p
                                className={classNames(
                                    sampleStock.sevenDaysPriceChangePercentage >= 0 ? 'text-green-600' : 'text-red-600',
                                    'ml-2 flex items-baseline text-sm font-semibold'
                                )}
                            >
                                {sampleStock.sevenDaysPriceChangePercentage >= 0 ? (
                                    <ArrowUpIcon className="h-5 w-5 flex-shrink-0 self-center text-green-500" aria-hidden="true" />
                                ) : (
                                    <ArrowDownIcon className="h-5 w-5 flex-shrink-0 self-center text-red-500" aria-hidden="true" />
                                )}

                                <span className="sr-only">{sampleStock.sevenDaysPriceChangePercentage >= 0 ? 'Increased' : 'Decreased'} by </span>
                                {Math.abs(sampleStock.sevenDaysPrice - sampleStock.currentPrice).toFixed(2)}%
                            </p>
                        </dd>
                    </div>
                    {/* TODO: Fix logic for 30 days - doesn't exist for now */}
                    <div
                        key={sampleStock.name}
                        className="relative overflow-hidden rounded-lg bg-white px-4 pb-12 pt-5 shadow sm:px-6 sm:pt-6"
                    >
                        <dt>
                            <p className="ml-16 truncate text-sm font-medium text-gray-500">Against last 30 days</p>
                        </dt>
                        <dd className="ml-16 flex items-baseline pb-6 sm:pb-7">
                            <p className="text-2xl font-semibold text-gray-900">${sampleStock.sevenDaysPrice}</p>
                            <p
                                className={classNames(
                                    sampleStock.sevenDaysPriceChangePercentage >= 0 ? 'text-green-600' : 'text-red-600',
                                    'ml-2 flex items-baseline text-sm font-semibold'
                                )}
                            >
                                {sampleStock.sevenDaysPriceChangePercentage >= 0 ? (
                                    <ArrowUpIcon className="h-5 w-5 flex-shrink-0 self-center text-green-500" aria-hidden="true" />
                                ) : (
                                    <ArrowDownIcon className="h-5 w-5 flex-shrink-0 self-center text-red-500" aria-hidden="true" />
                                )}

                                <span className="sr-only">{sampleStock.sevenDaysPriceChangePercentage >= 0 ? 'Increased' : 'Decreased'} by </span>
                                {Math.abs(sampleStock.sevenDaysPrice - sampleStock.currentPrice).toFixed(2)}%
                            </p>
                        </dd>
                    </div>
                </dl>
            </div>
            <div className='my-6 px-6'>
                <span className="isolate inline-flex rounded-md shadow-sm">
                    <button
                        type="button"
                        className="relative inline-flex items-center rounded-l-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        1 Day
                    </button>
                    <button
                        type="button"
                        className="relative -ml-px inline-flex items-center bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        7 Days
                    </button>
                    <button
                        type="button"
                        className="relative -ml-px inline-flex items-center rounded-r-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        30 Days
                    </button>
                    <button
                        type="button"
                        className="relative inline-flex items-center rounded-l-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        60 Days
                    </button>
                    <button
                        type="button"
                        className="relative -ml-px inline-flex items-center bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        90 Days
                    </button>
                    <button
                        type="button"
                        className="relative -ml-px inline-flex items-center rounded-r-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-10"
                    >
                        1 Year
                    </button>
                </span>
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
                                                className= "w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight" 
                                                id="side">
                                                    <option value="" disabled selected className="font-bold">Side</option>
                                                    <option value="BUY">BUY</option>
                                                    <option value="SELL">SELL</option>
                                                </select>
                                            </div>
                                            {/* These should be autofilled in the future, hardcoded for now */}
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
                                                    onChange= {(e) => setPrice(e.target.value)}
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
                                                    max = {todayString} // limit date because you cannot buy/sell in the future
                                                >
                                                </input>
                                            </div>
                                        </form>

                                        <div className="mb-3">
                                            <span id="summary">-</span>
                                        </div>
                                        <hr className=""></hr>
                                        <div className="px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
                                            <button type="button" onClick= {handleModalClose} className="inline-flex w-full justify-center rounded-md bg-gsgray70 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-gsgray90 sm:ml-3 sm:w-auto">Cancel</button>

                                            <button type="submit" onClick={handleButtons} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Add Position</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            <div>
                TODO: Chart switcher, enhance the modal logic
            </div>
            <Footer></Footer>
        </div>
    );
}

export default Stock;