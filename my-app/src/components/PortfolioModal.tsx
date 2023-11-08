import React from 'react';

// Define a generic Props type for your component
interface ModalProps<T> {
    data: T;
}

// Create a generic functional component
function PortfolioModal<T>({ data }: ModalProps<T>) {

    const [side, setSide] = React.useState<string>("-");
    const [stockCode, setStockCode] = React.useState<string>("-");
    const [date, setDate] = React.useState<string>("-");
    const [quantity, setQuantity] = React.useState<string>("-");
    const [price, setPrice] = React.useState<string>("-");

    const summary = () => {
        const summary = document.getElementById("summary") as HTMLSpanElement;
        summary.innerHTML = side + " " + quantity + " " + stockCode + " @ $" + price + " on " + date;
    }

    const handleButtons = () => {
        const form = document.getElementById("my-form") as HTMLFormElement;
        form.submit();
        // alert("Side: " + side + ", Stock Code: " + stockCode + ", Date: " + date + ", Quantity: " + quantity + ", Price: " + price);
    }

    return (
            <div className="relative transform overflow-hidden rounded-lg bg-white shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg">
                <div className="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
                    <div className="items-center">
                        <div className="mt-3 text-center sm:ml-4 sm:mt-0 sm:text-center">
                            <h3 className="font-semibold leading-6 text-gray-900 text-3xl" id="modal-title">Add New Position</h3>
                            <form className="my-6" id="my-form">

                                <div className="mb-3">
                                    <select required value={side} onChange={(e) => setSide(e.target.value)} onMouseUp={summary}
                                    className= "w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight" 
                                    id="side">
                                        <option value="" disabled selected className="font-bold">Side</option>
                                        <option value="BUY">BUY</option>
                                        <option value="SELL">SELL</option>
                                    </select>
                                </div>

                                <div className="mb-3">
                                    <select required value={stockCode} onChange={(e) => setStockCode(e.target.value)} onMouseUp={summary}
                                    className= "w-full appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight"
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
                                        id="date" 
                                        type="date"
                                        required
                                        value={date}
                                        onChange={(e) => setDate(e.target.value)}
                                        onMouseUp={summary}
                                    >
                                    </input>
                                </div>

                                <div className="mb-3">
                                    <input className="appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                        id="quantity" 
                                        type="text" 
                                        placeholder="Stock Quantity"
                                        required
                                        value={quantity}
                                        onChange={(e) => setQuantity(e.target.value)}
                                        onKeyUp={summary}
                                    >
                                    </input>
                                </div>

                                <div className="">
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

                            </form>

                            <div>
                                <span id="summary"></span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="bg-gray-50 px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">

                <button type="button" className="inline-flex w-full justify-center rounded-md bg-gsgray70 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-gsgray90 sm:ml-3 sm:w-auto">Cancel</button>
                
                <button type="submit" onClick={handleButtons} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Add Position</button>
                
                </div>

            </div>
        );
    }

// Export the component
export default PortfolioModal;
