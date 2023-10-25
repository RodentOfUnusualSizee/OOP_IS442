import React from 'react';
import { EyeIcon } from "@heroicons/react/24/solid";
import { Link } from 'react-router-dom';

interface PortfolioCardProps {
    portfolioList: Portfolio[];
    
}

interface Portfolio {
    id: number;
    name: string;
    strategy: string;
    capital: number;
}

const PortfolioCard = ({portfolioList}: PortfolioCardProps) => {
    return (
        <div>
            <ul role="list" className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
                {portfolioList.map((portfolio) => (
                    <li
                        key={portfolio.name}
                        className="col-span-1 flex flex-col divide-y divide-gray-200 rounded-lg bg-white text-center shadow"
                    >
                        <div className="flex flex-1 flex-col p-8">
                            <img className="mx-auto h-32 w-32 flex-shrink-0 rounded-full" src="/images/portfolio.png" alt="" />
                            <h3 className="mt-6 text-sm font-medium text-gray-900">{portfolio.name}</h3>
                            <dl className="mt-1 flex flex-grow flex-col justify-between">
                                <dt className="sr-only">Strategy Description</dt>
                                <dd className="text-sm text-gray-500">{portfolio.strategy}</dd>
                                <dt className="sr-only">Capital</dt>
                                <dd className="mt-3">
                                    <span className="inline-flex items-center rounded-full bg-green-50 px-2 py-1 text-xs font-medium text-green-700 ring-1 ring-inset ring-green-600/20">
                                        ${portfolio.capital}
                                    </span>
                                </dd>
                            </dl>
                        </div>
                        <div>
                            <div className="-mt-px flex divide-x divide-gray-200">
                                <div className="-ml-px flex w-0 flex-1">
                                    <a
                                        className="relative inline-flex w-0 flex-1 items-center justify-center gap-x-3 rounded-br-lg border border-transparent py-4 text-sm font-semibold text-gray-900"
                                    >
                                        <EyeIcon className="h-5 w-5 text-gray-400" aria-hidden="true" />
                                        <Link to={`/portfolio?id=${portfolio.id}`}>View Portfolio</Link>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default PortfolioCard;