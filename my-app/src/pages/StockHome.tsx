import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import Table from '../components/Table';
import MultiSelectDropdown from '../components/MultiSelectDropdown';

function StockHome() {

    // Start of sample data
    const stockTableData = [
        {
            id: 1,
            stock: 'Company A',
            category: 'Technology',
            market: 'US',
            region: 'North America',
            currentPrice: 50.25,
            lastUpdated: '2023-10-16',
            previousPrice: 48.50,
            priceChangePercentage: 3.57,
            sevenDaysPrice: 52.00,
            sevenDaysPriceChangePercentage: -3.85,
            action: 'View Stock',
        },
        {
            id: 2,
            stock: 'Company B',
            category: 'Finance',
            market: 'Europe',
            region: 'Europe',
            currentPrice: 75.75,
            lastUpdated: '2023-10-16',
            previousPrice: 76.20,
            priceChangePercentage: -0.59,
            sevenDaysPrice: 74.50,
            sevenDaysPriceChangePercentage: 1.68,
            action: 'View Stock',
        }
    ];

    const tableHeaders = [
        { header: 'Stock', key: 'stock' },
        { header: 'Category', key: 'category' },
        { header: 'Market', key: 'market' },
        { header: 'Region', key: 'region' },
        { header: 'Current Price', key: 'currentPrice' },
        { header: 'Last Updated', key: 'lastUpdated' },
        { header: 'Previous Price', key: 'previousPrice' },
        { header: 'Price Change (%)', key: 'priceChangePercentage' },
        { header: '7 Days Price', key: 'sevenDaysPrice' },
        { header: '7 Days Price Change (%)', key: 'sevenDaysPriceChangePercentage' },
        { header: 'Action', key: 'action' },
    ];

    const tableTitle = 'Explore Stocks';
    const tableDescription = 'List of all stocks and related data';
    const tableAction = 'View Stock';

    const stats = [
        { name: 'Stocks', value: '120' },
        { name: 'Markets', value: '4' },
        { name: 'Regions', value: '5' },
        { name: 'Equity Categories', value: '4' }
    ]

    function classNames(...classes: String[]) {
        return classes.filter(Boolean).join(' ')
    }

    const filterOptions = {
        "Stocks": [
            { label: "AAA", value: "AAA" },
            { label: "BBB", value: "BBB" },
            { label: "CCC", value: "CCC" },
        ],
        "Markets": [
            { label: "HK", value: "HK" },
            { label: "US", value: "US" },
            { label: "CN", value: "CN" },
        ],
        "Regions": [
            { label: "Asia", value: "Asia" },
            { label: "Europe", value: "Europe" },
            { label: "North America", value: "North America" },
        ],
        "Categories": [
            { label: "Stocks", value: "Stocks" },
            { label: "ETFs", value: "ETFs" },
            { label: "Bonds", value: "Bonds" },
        ]
    }

    // End of sample data

    interface SelectedOptions {
        [key: string]: { label: string; value: string }[];
    }

    const [selectedOptions, setSelectedOptions] = useState<SelectedOptions>({});


    return (
        <div>
            <Header management={true} userType={"user"} login={true} ></Header>
            <div>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6">
                    <h3 className="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight">
                        All Stocks
                    </h3>
                </div>
                <div className="my-2 px-6">
                    <dl className="mx-auto grid grid-cols-1 gap-px bg-gray-900/5 sm:grid-cols-2 lg:grid-cols-4">
                        {stats.map((stat) => (
                            <div
                                key={stat.name}
                                className="flex flex-wrap items-baseline justify-between gap-x-4 gap-y-2 bg-white px-4 py-10 sm:px-6 xl:px-8"
                            >
                                <dt className="text-sm font-medium leading-6 text-gray-500">{stat.name}</dt>
                                <dd className="w-full flex-none text-3xl font-medium leading-10 tracking-tight text-gray-900">
                                    {stat.value}
                                </dd>
                            </div>
                        ))}
                    </dl>
                </div>
                <h3 className="text-base font-semibold leading-6 text-gray-900">Stock Filters</h3>
                <div className="mt-5 flex lg:mt-0 lg:ml-4 justify-center">
                    {Object.entries(filterOptions).map(([key, options]) => (
                        <div key={key} className="mr-4">
                            <h6 className='text-sm font-medium leading-6 text-gray-500'>{key}</h6>
                            <MultiSelectDropdown
                                options={options}
                                selectedOptions={selectedOptions[key] || []}
                                onSelectedOptionsChange={(newSelectedOptions) =>
                                    setSelectedOptions((prevSelectedOptions) => ({
                                        ...prevSelectedOptions,
                                        [key]: newSelectedOptions,
                                    }))
                                }
                                key={key}
                            />
                            {/* For testing values and tracking purposes */}
                            {selectedOptions[key] && selectedOptions[key].length > 0 && (
                                <div className="text-sm text-gray-500 mt-1">
                                    Selected: {selectedOptions[key].map((option) => option.label).join(", ")}
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
            <div className='my-6 px-6'>
                <Table
                    tableData={stockTableData}
                    tableHeaders={tableHeaders}
                    tableTitle={tableTitle}
                    tableDescription={tableDescription}
                    tableAction={tableAction}
                ></Table>
            </div>
            <Footer></Footer>
        </div >
    );
}

export default StockHome;