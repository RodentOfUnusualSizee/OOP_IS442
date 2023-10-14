import React, { useEffect } from 'react';

interface TableProps {
    tableData: any[];
    tableHeaders: { header: string; key: string }[];
    tableTitle: string;
    tableDescription: string;
    tableAction: string;
}

const Table = ({tableData,tableHeaders,tableTitle,tableDescription,tableAction}: TableProps) => {
    return (
        <div>
            <div>
                <div className="sm:flex sm:items-center">
                    <div className="sm:flex-auto">
                        <h1 className="text-base font-semibold leading-6 text-gray-900">
                            {tableTitle}
                        </h1>
                        <p className="mt-2 text-sm text-gray-700">{tableDescription}</p>
                    </div>
                    <div className="mt-4 sm:ml-16 sm:mt-0 sm:flex-none">
                        {tableAction}
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
                                                <th
                                                    key={header.key}
                                                    scope="col"
                                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                                                >
                                                    {header.header}
                                                </th>
                                            ))}
                                        </tr>
                                    </thead>
                                    <tbody className="bg-white divide-y divide-gray-200">
                                        {tableData.map((row) => (
                                            <tr key={row.id}>
                                                {tableHeaders.map((header) => (
                                                    <td
                                                        key={header.key}
                                                        className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
                                                    >
                                                        {row[header.key]}
                                                    </td>
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
        </div>
    );
};

export default Table;