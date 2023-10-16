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

function Portfolio() {

    // Sample data 
    let samplePortfolioData =
        { id: 1, name: "Portfolio 1", strategy: "Strategy A", capital: 10000 };

    const stats = [
        { name: 'Capital Change ($) ', stat: '+$980' },
        { name: 'Capital Change (%)', stat: '+58.16%' },
        { name: 'Days since Portfolio Active', stat: '30 days' },
    ]

    const stockTableData = [
        {
            id: 1,
            stockName: 'Company A',
            totalQuantity: 1000,
            averagePrice: 50.25,
            totalAllocation: 50250,
            allocationPercentage: 25,
            rawGain: 2500,
            gainPercentage: 5,
        },
        {
            id: 2,
            stockName: 'Company B',
            totalQuantity: 500,
            averagePrice: 75.75,
            totalAllocation: 37875,
            allocationPercentage: 18,
            rawGain: -1250,
            gainPercentage: -3,
        },
        {
            id: 3,
            stockName: 'Company C',
            totalQuantity: 800,
            averagePrice: 30.50,
            totalAllocation: 24400,
            allocationPercentage: 12,
            rawGain: 800,
            gainPercentage: 4,
        },
        {
            id: 4,
            stockName: 'Company D',
            totalQuantity: 1200,
            averagePrice: 45.60,
            totalAllocation: 54720,
            allocationPercentage: 27,
            rawGain: 1920,
            gainPercentage: 8,
        },
    ];

    const tableHeaders = [
        { header: 'Stock Name', key: 'stockName' },
        { header: 'Total Quantity', key: 'totalQuantity' },
        { header: 'Average Price', key: 'averagePrice' },
        { header: 'Total Allocation', key: 'totalAllocation' },
        { header: 'Allocation %', key: 'allocationPercentage' },
        { header: 'Raw Gain', key: 'rawGain' },
        { header: 'Gain %', key: 'gainPercentage' },
        { header: 'Action', key: 'action' },
    ];

    const tableTitle = 'Stocks';
    const tableDescription = 'List of stocks in portfolio and related data';
    const tableAction = "View Stock";

    //End of sample data

    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const id = queryParams.get('id');

    return (
        <div>
            <Header management={true} userType={"user"} login={true} ></Header>
            <div>
                <div className="lg:flex lg:items-center lg:justify-between my-6 px-6">
                    <h3 className="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight">
                        {samplePortfolioData.name}
                    </h3>
                    <div className="min-w-0 flex-1">
                        <div className="mt-1 flex flex-col sm:mt-0 sm:flex-row sm:flex-wrap sm:space-x-6 mx-2">
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <BriefcaseIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {samplePortfolioData.strategy}
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500">
                                <CurrencyDollarIcon className="mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400" aria-hidden="true" />
                                {samplePortfolioData.capital}
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
                <LineChartComponent></LineChartComponent>
                <PieChartComponent></PieChartComponent>
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
            <Footer></Footer>
        </div>
    );
}

export default Portfolio;