import React from 'react';
import Table from '../components/Table';
import Header from '../components/Header';
import Footer from '../components/Footer';
import PortfolioCard from '../components/PortfolioCard';

function UserHome() {
    interface DataItem {
        id: number;
        name: string;
        value: string;
    }

    const tableData = [
        { id: 1, name: 'John', age: 30, email: 'john@example.com' },
        { id: 2, name: 'Jane', age: 25, email: 'jane@example.com' },
        { id: 3, name: 'Bob', age: 40, email: 'bob@example.com' },
    ];

    const tableHeaders = [
        { header: 'ID', key: 'id' },
        { header: 'Name', key: 'name' },
        { header: 'Age', key: 'age' },
        { header: 'Email', key: 'email' },
    ];

    const samplePortfolioData = [
        { name: "Portfolio 1", strategy: "Strategy A", capital: 10000 },
        { name: "Portfolio 2", strategy: "Strategy B", capital: 15000 },
        { name: "Portfolio 3", strategy: "Strategy C", capital: 20000 },
    ];


    return (
        <div>
            <div className="UserHome">
                <Header management={true} userType={"user"} login={true} ></Header>
                <div className="bg-white py-12 sm:py-12 my-2">
                    <div className="mx-auto max-w-7xl px-6 lg:px-8">
                        <div className="mx-auto max-w-2xl lg:text-center">
                            <h2 className="text-base font-semibold leading-7 text-indigo-600">Management</h2>
                            <p className="mt-2 text-3xl font-bold tracking-tight text-gray-900 sm:text-4xl">
                                Your Portfolios
                            </p>
                            <p className="mt-6 text-lg leading-8 text-gray-600">
                                All your portfolios at Goldman Sachs at a glance. View your portfolios, create new strategies or edit the portfolios you manage. Feel free to
                                browse stocks on the market using our in-house tool and enhance your portfolios.
                            </p>
                        </div>
                    </div>
                </div>
                <div className="mx-auto mt-16 max-w-2xl my-16 sm:mt-20 lg:mt-24 lg:max-w-4xl text-left">
                    <Table tableData={tableData} tableHeaders={tableHeaders} tableTitle='Sample Table' tableDescription='sample description' tableAction='View Portfolio' ></Table>
                </div>
                <div className="mx-auto mt-16 max-w-2xl my-16 sm:mt-20 lg:mt-24 lg:max-w-4xl">
                    <PortfolioCard portfolioList={samplePortfolioData}></PortfolioCard>
                </div>
            </div>
            <Footer></Footer>
        </div>
    );
}

export default UserHome;