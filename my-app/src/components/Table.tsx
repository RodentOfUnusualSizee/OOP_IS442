import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';

interface TableProps {
    tableData: any[];
    tableHeaders: { header: string; key: string }[];
    tableTitle: string;
    tableDescription: string;
    tableAction: string;
    tableLink: string;
}

const Table = ({ tableData, tableHeaders, tableTitle, tableDescription, tableAction, tableLink }: TableProps) => {
    return (
        <div>
            <div className="sm:flex sm:items-center">
                <h1 className="text-base font-semibold leading-6 text-gray-900">
                    {tableTitle}
                </h1>
                <div className="sm:flex-auto">
                    <p className="mt-2 text-sm text-gray-700">{tableDescription}</p>
                </div>
            </div>
            <div className="flex flex-col mt-4">
                <div className="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
                    <div className="py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8">
                        <div className="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50">
                                    <tr>
                                        {tableHeaders.map((header) => (
                                            header.key.toLowerCase() === 'action' ? (
                                                <th key={header.key} />
                                            ) : (
                                                <th
                                                    key={header.key}
                                                    scope="col"
                                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                                                >
                                                    {header.header}
                                                </th>
                                            )
                                        ))}
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                    {tableData.map((row) => (
                                        <tr key={row.id}>
                                            {tableHeaders.map((header) => (
                                                header.key === 'action' ? (
                                                    <td key={header.key}>
                                                        <Link to={tableLink + row.id}
                                                            className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                                                        >
                                                            {tableAction}
                                                        </Link>
                                                    </td>
                                                ) : (
                                                    <td
                                                        key={header.key}
                                                        className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
                                                    >
                                                        {row[header.key]}
                                                    </td>
                                                )
                                            ))}
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Table;