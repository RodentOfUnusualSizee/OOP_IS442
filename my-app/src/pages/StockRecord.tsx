import React, { useEffect } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { Link } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import { useAuth } from "../context/AuthContext";
import Table from "../components/Table";
import { getPortfolioByPortfolioId, getStockPrice, createPortfolioPosition } from "../utils/api";
import { getStockRecordsByStockCode, formatTimestamp } from "../utils/transform";
import { PencilIcon, EyeIcon } from '@heroicons/react/20/solid';
import { toast, ToastContainer, Slide } from 'react-toastify';


function StockRecord() {

    const location = useLocation();
    const { authUser, isLoggedIn } = useAuth();
    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [userId, setUserId] = React.useState<number>(1);
    const [userRole, setUserRole] = React.useState<string>("user");
    const [userIsLoggedIn, setUserIsLoggedIn] = React.useState<boolean>(true);
    const management = userRole === "management" || userRole === "user";
    const [stockCode, setStockCode] = React.useState<string>("");
    const [portfolioId, setPortfolioId] = React.useState<string>("");

    async function getStockRecords(stockCode: any, portfolioId: any) {
        console.log("Function called");
        try {
            const stockPriceData = await getStockPrice(stockCode);
            const currentValue = stockPriceData.timeSeries[0].close;
            const response = await getPortfolioByPortfolioId(portfolioId);
            const positions = response.data.positions;
            const filteredStockRecords = getStockRecordsByStockCode(positions, stockCode);

            const stockRecords = filteredStockRecords.map((record) => ({
                id: record.positionId,
                dateAdded: formatTimestamp(record.createdTimestamp),
                position: record.position,
                quantity: record.quantity,
                price: record.price,
                totalCost: record.price * record.quantity,
                currentValue:
                    record.position === "LONG"
                        ? currentValue * record.quantity
                        : record.price * record.quantity,
            }));

            const cumPositions = response.data.cumPositions;
            const filteredCumPositions = getStockRecordsByStockCode(cumPositions, stockCode);
            const netStockQuantity = filteredCumPositions[0].totalQuantity;
            const totalStockCost = filteredCumPositions[0].totalQuantity * filteredCumPositions[0].averagePrice;
            const profitLoss = filteredCumPositions[0].currentValue * netStockQuantity - totalStockCost;

            setStockRecords(stockRecords);

            setStockStats([
                { name: "Total Net Stock Quantity", stat: netStockQuantity },
                { name: "Total Stock Cost", stat: '$' + totalStockCost },
                { name: "Unrealized Profit/Loss", stat: '$' + profitLoss },
            ]);
        } catch (error) {
            console.log(error);
        }
    }

    useEffect(() => {
        if (authUser) {
            console.log("auth loaded");

            setUserId(authUser.id);
            setUserRole(authUser.role);
            setUserIsLoggedIn(true);

            const queryParams = new URLSearchParams(location.search);
            const stockCode = queryParams.get("stock");
            const portfolioId = queryParams.get("id");

            setStockCode(stockCode || "");
            setPortfolioId(portfolioId || "");
            getStockRecords(stockCode, portfolioId);
            setIsLoading(false);
        } else {
            console.log("auth never loaded");
        }
    }, [authUser, location.search])

    // Stats
    const [stockStats, setStockStats] = React.useState<any[]>([
        { name: "Total Net Stock Quantity", stat: "" },
        { name: "Total Stock Cost", stat: "" },
        { name: "Unrealized Profit/Loss", stat: "" }
    ]);


    // Table
    const tableHeaders = [
        { header: "Date Added", key: "dateAdded" },
        { header: "Position", key: "position" },
        { header: "Quantity", key: "quantity" },
        { header: "Price", key: "price" },
        { header: "Total Cost", key: "totalCost" },
        { header: "Current Value", key: "currentValue" }
    ]

    const [stockRecords, setStockRecords] = React.useState<any[]>([]);

    const tableTitle = "";
    const tableDescription = "";
    const tableAction = "";
    const tableLink = "";

    // modal

    const [showModal, setShowModal] = React.useState<boolean>(false);

    const handleAddClick = () => {
        setShowModal(true);
    }

    const handleModalClose = () => {
        setShowModal(false);
        //clear form inputs
        setSide("");
        setDate("");
        setQuantity("");
        setPrice("");
    }

    // form inputs
    const [side, setSide] = React.useState<string>("");
    const [date, setDate] = React.useState<string>("");
    const [quantity, setQuantity] = React.useState<string>("");
    const [price, setPrice] = React.useState<string>("");


    // get today's date and parse as string containing yy/mm/dd
    const today = new Date();
    const todayString = today.getFullYear() + '-' + String(today.getMonth() + 1) + '-' + String(today.getDate()).padStart(2, '0');

    const handleButtons = (e: any) => {
        e.preventDefault();

        let position = {};
        if (side === "BUY") {
            position = { "stockSymbol": stockCode, "price": price, "position": "LONG", "quantity": quantity, "positionAddDate": date }
        } else {
            position = { "stockSymbol": stockCode, "price": price, "position": "SELLTOCLOSE", "quantity": quantity, "positionAddDate": date }
        }

        const positionAPI = createPortfolioPosition(portfolioId, position);
        positionAPI.then((response) => {
            console.log(response);
            if (response['success']) {
                toast.success('Successfully added ' + stockCode + 'record to portfolio', {
                    position: "top-right",
                    autoClose: 5000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: false,
                    progress: undefined,
                    theme: "colored",
                });
                getStockRecords(stockCode, portfolioId);
            } else {
                toast.error('Failed to add ' + stockCode + 'record to portfolio', {
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
            toast.error('Failed to add ' + stockCode + 'record to portfolio (ez)', {
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


    return (
        <div className="PortfolioRecord">
            <Header management={management} userType={userRole} login={userIsLoggedIn} />
            <div className='my-2 px-6'>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6">
                    <h3 className="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight">
                        {stockCode + " Records"}
                    </h3>
                    <div className="mt-5 flex lg:ml-4 lg:mt-0">
                        <span className="hidden sm:block">
                            <button
                                type="button"
                                className="inline-flex items-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                                onClick={handleAddClick}
                            >
                                <PencilIcon className="-ml-0.5 mr-1.5 h-5 w-5 text-gray-400" aria-hidden="true" />
                                Add New Position
                            </button>
                        </span>
                        <span className="hidden sm:block mx-1">
                            <Link to={'/Stock?ticker=' + stockCode}>
                                <button
                                    type="button"
                                    className="inline-flex items-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                                >
                                    <EyeIcon className="-ml-0.5 mr-1.5 h-5 w-5 text-gray-400" aria-hidden="true" />
                                    View Stock Performance
                                </button>
                            </Link>
                        </span>
                    </div>
                </div>
                <div className='my-2 px-6'>
                    <dl className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-3">
                        {stockStats.map((item) => (
                            <div key={item.name} className="overflow-hidden rounded-lg bg-white px-4 py-5 shadow sm:p-6">
                                <dt className="truncate text-sm font-medium text-gray-500">{item.name}</dt>

                                <dd className="mt-1 text-3xl font-semibold tracking-tight text-gray-900">{item.stat}</dd>
                                <p className="ml-2 flex items-baseline text-sm font-semibold"></p>
                            </div>
                        ))}
                    </dl>
                </div>
                <div className='my-2 px-6'>
                    <Table tableTitle={tableTitle} tableData={stockRecords} tableDescription={tableDescription} tableHeaders={tableHeaders} tableAction={tableAction} tableLink={tableLink}></Table>
                    <Link to={"/portfolio?id=" + portfolioId} className="inline-block rounded border border-indigo-600 my-4 px-12 py-3 text-sm font-medium text-indigo-600 hover:bg-indigo-600 hover:text-white focus:outline-none focus:ring active:bg-indigo-500">
                        Back to Portfolio
                    </Link>
                </div>
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
                                                <div id="stockCode" className="w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight">
                                                    {stockCode}
                                                </div>
                                                <input type="hidden" name="stockCode" value={stockCode} />
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

                                            <button type="submit" onClick={handleButtons} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Add Stock Position</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            <Footer />
            <ToastContainer transition={Slide} />
        </div>
    );
}

export default StockRecord;