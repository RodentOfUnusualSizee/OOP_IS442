import React from 'react';


interface TableProps {
    // TODO: improve to include buttons configurations
    tableData: DataItem[];
    tableHeaders: string[];
    tableTitle: string;
    tableDescription: string;
    tableAction: string;
}

interface DataItem {
    id: number;
    name: string;
    value: string;
}

const Table = ({ tableData, tableHeaders, tableTitle, tableDescription, tableAction }: TableProps) => {
    return (
        <div>
            <div>
                <div className="sm:flex sm:items-center">
                    <div className="sm:flex-auto">
                        <h1 className="text-base font-semibold leading-6 text-gray-900">{tableTitle}</h1>
                        <p className="mt-2 text-sm text-gray-700">
                            {tableDescription}
                        </p>
                    </div>
                    <div className="mt-4 sm:ml-16 sm:mt-0 sm:flex-none">
                        <button
                            type="button"
                            className="block rounded-md bg-indigo-600 px-3 py-2 text-center text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600">
                            Create
                        </button>
                    </div>
                </div>
                <div className="mt-8 flow-root">
                    <div className="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
                        <div className="inline-block min-w-full py-2 align-middle sm:px-6 lg:px-8">
                            <table className="min-w-full divide-y divide-gray-300">
                                <thead>
                                    <tr>
                                        {tableHeaders.map((header) => (
                                            <th
                                                scope="col"
                                                className="whitespace-nowrap py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-0"
                                            >
                                                {header}
                                            </th>
                                        ))}
                                        <th scope="col" className="relative whitespace-nowrap py-3.5 pl-3 pr-4 sm:pr-0">
                                            <span className="sr-only">Edit</span>
                                        </th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-200 bg-white">
                                    {tableData.map((data, index) => (
                                        <tr key={index}>
                                            {tableHeaders.map((header, headerIndex) => (
                                                <td
                                                    key={headerIndex}
                                                    className="whitespace-nowrap py-2 pl-4 pr-3 text-sm text-gray-500 sm:pl-0"
                                                >
                                                    {(data as any)[header.toLowerCase()]}
                                                </td>
                                            ))}
                                            <td className="whitespace-nowrap py-2 pl-3 pr-4 sm:pr-0">
                                                {/* TODO: clickable + action using Link */}
                                                {tableAction}
                                            </td>
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
}

export default Table;