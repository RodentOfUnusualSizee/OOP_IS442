import React, { useEffect } from 'react';
import Loading from './Loading';
import Header from '../components/Header';
import Footer from '../components/Footer';
import PortfolioCard from '../components/PortfolioCard';
import { createPortfolio, getPortfolioByUserId, comparePortfolio, deletePortfolio, createNewUserEvent } from '../utils/api';
import { useAuth } from '../context/AuthContext';
import { Slide, toast, ToastContainer } from 'react-toastify';
import { PlusIcon, ArrowUpIcon, ArrowDownIcon } from '@heroicons/react/20/solid';
import { showToastMessage, showToastMessageSuccess } from '../utils/transform';
import Swal from 'sweetalert2';

function classNames(...classes: any) {
    return classes.filter(Boolean).join(' ')
}

function UserHome() {
    interface Portfolio {
        portfolioID: number;
        portfolioName: string;
        strategyDesc: string;
        capitalUSD: number;
    }

    interface PortfolioStats {
        currentTotalPortfolioValue: number;
        portfolioBeta: number;
        informationRatio: number;
        quarterlyReturns: {
            Q1: string;
            Q2: string;
            Q3: string;
            Q4: string;
        },
        annualizedReturnsPercentage: string;
        quarterlyReturnsPercentage: {
            Q1: string;
            Q2: string;
            Q3: string;
            Q4: string;
        }
    }

    const [hasFetchedData, setHasFetchedData] = React.useState(false);
    const [data, setData] = React.useState<Portfolio[]>([]);

    const { authUser, isLoggedIn } = useAuth();
    const [isLoading, setIsLoading] = React.useState<boolean>(true);

    const [userId, setUserId] = React.useState<number>(1);
    const [userRole, setUserRole] = React.useState<string>("");
    const [userIsLoggedIn, setUserIsLoggedIn] = React.useState<boolean>(false);
    const management = userRole === "admin" || userRole === "user";

    function fetchPortfolios(user: number = userId) {
        const portfolio = getPortfolioByUserId(user);
        portfolio.then((response) => {
            setData(response.data)
        }).catch((error) => {
            console.log(error);
        });
    }

    useEffect(() => {
        if (authUser) {
            setUserId(authUser.id);
            setUserRole(authUser.role);
            setUserIsLoggedIn(true);
            if (!hasFetchedData) {
                fetchPortfolios(authUser.id);
                setHasFetchedData(true);
                setIsLoading(false);
            }

            const currentDateTime = new Date().toISOString().slice(0, 19);

            const event_data = {
                "event": "VIEW ALL PORTFOLIOS",
                "timestamp": currentDateTime,
            }

            createNewUserEvent(authUser.id, event_data);
        } else {
            console.log("auth never loaded");
        }
    }, [authUser, isLoggedIn, hasFetchedData]);

    let portfolioData: any[] = [];
    data.forEach((item) => {
        let tmp = { id: item.portfolioID, name: item.portfolioName, strategy: item.strategyDesc, capital: item.capitalUSD }
        portfolioData.push(tmp)
    })

    //Start of modal stuff
    const [showModal, setShowModal] = React.useState<boolean>(false);
    const [portfolioName, setPortfolioName] = React.useState<string>("");
    const [portfolioCapital, setPortfolioCapital] = React.useState<string>("");
    const [portfolioStrategy, setPortfolioStrategy] = React.useState<string>("");

    // Modal Open and Close
    const handleAddClick = () => {
        setShowModal(true);
    }

    const handleModalClose = () => {
        setShowModal(false);
        //clear form inputs
        setPortfolioName("");
        setPortfolioCapital("");
        setPortfolioStrategy("");
    }

    // Submit Form handler
    const handleSubmit = (e: any) => {
        e.preventDefault();
        let portfolio = {
            "user": {
                "id": userId
            },
            "portfolioName": portfolioName,
            "strategyDesc": portfolioStrategy,
            "capitalUSD": portfolioCapital
        }
        const portfolioAPI = createPortfolio(portfolio);

        portfolioAPI.then((response) => {
            if (response["success"]) {
                console.log(response);
                showToastMessageSuccess('Successfully created Portfolio');

                const currentDateTime = new Date().toISOString().slice(0, 19);

                const event_data = {
                    "event": "CREATE PORTFOLIO",
                    "timestamp": currentDateTime,
                }

                createNewUserEvent(authUser.id, event_data);

                fetchPortfolios();
            } else {
                showToastMessage('Error creating portfolio');
            }
        }).catch((error) => {
            showToastMessage('Error creating portfolio');
        });
        handleModalClose();
    }

    // Comparison stuff
    const [showComparison, setShowComparison] = React.useState<boolean>(false);
    const [firstPortfolio, setFirstPortfolio] = React.useState<string>("");
    const [secondPortfolio, setSecondPortfolio] = React.useState<string>("");
    const [portfolioCheck, setPortfolioCheck] = React.useState<boolean>(false);
    const [portfolioOneStats, setPortfolioOneStats] = React.useState<PortfolioStats>({} as PortfolioStats);
    const [portfolioTwoStats, setPortfolioTwoStats] = React.useState<PortfolioStats>({} as PortfolioStats);
    const [portfolioDifference, setPortfolioDifference] = React.useState<PortfolioStats>({} as PortfolioStats);


    const handleComparison = async () => {
        if (firstPortfolio === secondPortfolio) {
            setPortfolioCheck(false);
            document.getElementById('summaryError')?.classList.remove('hidden');
        } else {
            document.getElementById('summaryError')?.classList.add('hidden');
            const comparisonData = await comparePortfolio(firstPortfolio, secondPortfolio);
            const portfolioOneStats = comparisonData.data.portfolio1Stats;
            const portfolioTwoStats = comparisonData.data.portfolio2Stats;
            const portfolioDifference = comparisonData.data.differenceStats;

            setPortfolioOneStats(portfolioOneStats);
            setPortfolioTwoStats(portfolioTwoStats);
            setPortfolioDifference(portfolioDifference);

            const currentDateTime = new Date().toISOString().slice(0, 19);

            const event_data = {
                "event": "COMPARE PORTFOLIOS " + firstPortfolio + " | " + secondPortfolio,
                "timestamp": currentDateTime,
            }

            createNewUserEvent(authUser.id, event_data);

            setPortfolioCheck(true);
        }
    }

    const handleDelete = (id : number) => {
        Swal.fire({
            title: 'Are you sure you want to delete Portfolio ' + id + '?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result : any) => {
            if (result.isConfirmed) {
                const deletePortfolioAPI = deletePortfolio(id);
                deletePortfolioAPI.then((response) => {
                    if (response["success"]) {
                        showToastMessageSuccess('Successfully deleted Portfolio');
                        fetchPortfolios();
                    } else {
                        showToastMessage('Error deleting portfolio');
                    }
                }).catch((error) => {
                    showToastMessage('Error deleting portfolio');
                });
            } else if (result.isDismissed) {
                showToastMessage('Delete operation was canceled');
                console.log('Delete operation was canceled');
            }
        });
    };


    const renderOptions = () => {
        return data.map((item) => (
            <option key={item.portfolioID} value={item.portfolioID}>
                {item.portfolioName}
            </option>
        ));
    };

    // Line Divider Component
    const renderDivider = (dividerValue: string) => {
        return (
            <div className="px-4 py-5 sm:p-6">
                <div className="relative">
                    <div className="absolute inset-0 flex items-center" aria-hidden="true">
                        <div className="w-full border-t border-gray-300" />
                    </div>
                    <div className="relative flex justify-center">
                        <span className="bg-gswhite px-2 text-sm text-gray-500">{dividerValue}</span>
                    </div>
                </div>
            </div>
        )
    }


    if (isLoading) {
        return (
            <Loading></Loading>
        )
    }

    return (
        <div>
            <div className="UserHome">
                <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
                <div className='container mx-auto rounded-l place-items-center'>
                    <div className="bg-gswhite py-12 sm:py-12 my-2">
                        <div className="mx-auto max-w-screen-2xl px-6 lg:px-8">
                            <div className="mx-auto max-w-screen-2xl lg:text-center">
                                <p className="mt-2 text-3xl font-bold tracking-tight text-gsgray90 sm:text-4xl">
                                    Your Portfolios
                                </p>
                                <p className="mt-6 text-lg leading-8 text-gsgray70">
                                    All your portfolios at Goldman Sachs at a glance. View your portfolios, create new strategies or edit the portfolios you manage. 
                                    <br></br>Browse stocks on the market using our in-house tool and enhance your portfolios.
                                </p>
                            </div>
                        </div>
                    </div>
                    <div className="grid grid-cols-6" id="AddNewPortBtn">
                        <button onClick={handleAddClick} className="col-start-3 col-span-2 bg-gsgreen50 hover:bg-gsgreen60 text-gswhite font-bold py-2 px-4 rounded">Create New Portfolio</button>
                    </div>
                    <div className="mx-auto mt-8 max-w-2xl mb-16 sm:mt-20 lg:mt-24 lg:max-w-4xl">
                        <PortfolioCard portfolioList={portfolioData} onDelete={handleDelete}></PortfolioCard>
                    </div>

                    {/* START OF COMPARISON */}
                    {
                        data.length >= 2 ? (
                            <div className="relative my-4 px-6 max-w-7xl mx-auto">
                                <div className="absolute inset-0 flex items-center" aria-hidden="true">
                                    <div className="w-full border-t border-gray-300" />
                                </div>
                                <div className="relative flex justify-center">
                                    <button
                                        type="button" onClick={(e) => setShowComparison(!showComparison)}
                                        className="inline-flex items-center gap-x-1.5 rounded-full bg-gswhite px-3 py-1.5 text-sm font-semibold text-gsgray90 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                                    >
                                        <PlusIcon className="-ml-1 -mr-0.5 h-5 w-5 text-gray-400" aria-hidden="true" />
                                        Compare Portfolios
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div></div>
                        )
                    }
                    {showComparison ? (
                        <div className="max-w-7xl mx-auto">
                            <div className="mx-auto max-w-2xl lg:text-center">
                                <p className="mt-2 text-3xl font-bold tracking-tight text-gsgray90 sm:text-4xl">
                                    Portfolio Comparison
                                </p>
                                <p className="mt-6 text-lg leading-8 text-gsgray70">
                                    Compare two portfolios to see how they differ in terms of Performance,
                                    Risk and Returns. Learn more about your portfolios and make better decisions.
                                </p>
                            </div>
                            <div className='flex my-4 px-6'>
                                <div className='flex-1 my-2 mx-2'>
                                    <label htmlFor="firstPortfolio" className="block text-sm font-medium leading-6 text-gsgray90">
                                        First Portfolio
                                    </label>
                                    <select
                                        id="firstPortfolio"
                                        name="firstPortfolio"
                                        className="mt-2 block w-full rounded-md border-0 py-1.5 pl-3 pr-10 text-gsgray90 ring-1 ring-inset ring-gray-300 focus:ring-1 sm:text-sm sm:leading-6"
                                        defaultValue="Select First Portfolio"
                                        onChange={(e) => setFirstPortfolio(e.target.value)}
                                    >
                                        <option value="Select First Portfolio" disabled />
                                        {renderOptions()}
                                    </select>
                                </div>
                                <div className='flex-1 my-2 mx-2'>
                                    <label htmlFor="secondPortfolio" className="block text-sm font-medium leading-6 text-gsgray90">
                                        Second Portfolio
                                    </label>
                                    <select
                                        id="secondPortfolio"
                                        name="secondPortfolio"
                                        onChange={(e) => setSecondPortfolio(e.target.value)}
                                        className="mt-2 block w-full rounded-md border-0 py-1.5 pl-3 pr-10 text-gsgray90 ring-1 ring-inset ring-gray-300 focus:ring-1 sm:text-sm sm:leading-6"
                                        defaultValue="Select Second Portfolio"
                                    >
                                        <option value="Select Second Portfolio" disabled />
                                        {renderOptions()}
                                    </select>
                                </div>
                            </div>

                            <div className='my-2 px-6'>
                                <button onClick={handleComparison} className="col-start-3 col-span-2 bg-gsblue60 hover:bg-gsblue70 text-gswhite font-bold py-2 px-4 rounded">Compare Portfolios</button>
                                {/* Error message */}
                                <div id='summaryError' className="text-gsred60 text-md italic hidden">Please choose 2 different Portfolios</div>
                            </div>

                            {portfolioCheck ? (
                                <div className="pb-8">
                                    <div className='flex my-4 px-6'>


                                        {/* Card One */}
                                        <div className="overflow-hidden bg-gswhite flex-1 my-2 mx-2">
                                            <div className="px-4 pt-5 sm:px-6">
                                                {data.find((item) => item.portfolioID === parseInt(firstPortfolio))?.portfolioName}
                                            </div>
                                            <div className='my-2 mx-2'>
                                                <h4 className="text-lg font-semibold">Total Portfolio Value</h4>
                                                <p className="text-gsgray90">
                                                    ${portfolioOneStats.currentTotalPortfolioValue.toFixed(2)}
                                                </p>
                                            </div>
                                            {renderDivider("Portfolio Statistics")}
                                            <div className='my-1 flex'>
                                                <div className='my-1 mx-1 flex-1'>
                                                    <h6 className="text-md font-semibold">Portfolio Beta</h6>
                                                    <p className="text-gsgray90">{portfolioOneStats.portfolioBeta}</p>
                                                </div>
                                                <div className='my-1 mx-1 flex-1'>
                                                    <h6 className="text-md font-semibold">Information Ratio</h6>
                                                    <p className="text-gsgray90">{portfolioOneStats.informationRatio}</p>
                                                </div>
                                            </div>
                                            {renderDivider("Quarterly Returns")}
                                            <div>
                                                <dl className="mt-5 grid grid-cols-1 overflow-hidden border border-gsgray20 bg-gswhite md:grid-cols-2 md:divide-x">
                                                    <div key="Q1" className="px-4 py-5 sm:p-6">
                                                        <dt className="text-base font-normal text-gsgray90">Q1</dt>
                                                        <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                                            <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                                                {portfolioOneStats.quarterlyReturns.Q1}
                                                            </div>
                                                            <div
                                                                className={classNames(
                                                                    portfolioOneStats.quarterlyReturns.Q1.includes("-")
                                                                        ? 'bg-gsred20 text-gsred60'
                                                                        : 'bg-green-100 text-gsgreen60',
                                                                    'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                                                )}
                                                            >
                                                                {portfolioOneStats.quarterlyReturns.Q1.includes("-") ? (
                                                                    <>
                                                                        <ArrowDownIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Decreased by </span>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <ArrowUpIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Increased by </span>
                                                                    </>
                                                                )}
                                                                {portfolioOneStats.quarterlyReturnsPercentage.Q1}
                                                            </div>
                                                        </dd>
                                                    </div>
                                                    <div key="Q2" className="px-4 py-5 sm:p-6">
                                                        <dt className="text-base font-normal text-gsgray90">Q2</dt>
                                                        <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                                            <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                                                {portfolioOneStats.quarterlyReturns.Q2}
                                                            </div>
                                                            <div
                                                                className={classNames(
                                                                    portfolioOneStats.quarterlyReturns.Q2.includes("-")
                                                                        ? 'bg-gsred20 text-gsred60'
                                                                        : 'bg-green-100 text-gsgreen60',
                                                                    'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                                                )}
                                                            >
                                                                {portfolioOneStats.quarterlyReturns.Q2.includes("-") ? (
                                                                    <>
                                                                        <ArrowDownIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Decreased by </span>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <ArrowUpIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Increased by </span>
                                                                    </>
                                                                )}
                                                                {portfolioOneStats.quarterlyReturnsPercentage.Q2}
                                                            </div>
                                                        </dd>
                                                    </div>
                                                </dl>
                                            </div>
                                            <div>
                                                <dl className="grid grid-cols-1 overflow-hidden bg-gswhite border border-gsgray20 border-t-0 md:grid-cols-2 md:divide-x">
                                                    <div key="Q3" className="px-4 py-5 sm:p-6">
                                                        <dt className="text-base font-normal text-gsgray90">Q3</dt>
                                                        <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                                            <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                                                {portfolioOneStats.quarterlyReturns.Q3}
                                                            </div>
                                                            <div
                                                                className={classNames(
                                                                    portfolioOneStats.quarterlyReturns.Q3.includes("-")
                                                                        ? 'bg-gsred20 text-gsred60'
                                                                        : 'bg-green-100 text-gsgreen60',
                                                                    'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                                                )}
                                                            >
                                                                {portfolioOneStats.quarterlyReturns.Q3.includes("-") ? (
                                                                    <>
                                                                        <ArrowDownIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Decreased by </span>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <ArrowUpIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Increased by </span>
                                                                    </>
                                                                )}
                                                                {portfolioOneStats.quarterlyReturnsPercentage.Q3}
                                                            </div>
                                                        </dd>
                                                    </div>
                                                    <div key="Q4" className="px-4 py-5 sm:p-6">
                                                        <dt className="text-base font-normal text-gsgray90">Q4</dt>
                                                        <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                                            <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                                                {portfolioOneStats.quarterlyReturns.Q4}
                                                            </div>
                                                            <div
                                                                className={classNames(
                                                                    portfolioOneStats.quarterlyReturns.Q4.includes("-")
                                                                        ? 'bg-gsred20 text-gsred60'
                                                                        : 'bg-green-100 text-gsgreen60',
                                                                    'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                                                )}
                                                            >
                                                                {portfolioOneStats.quarterlyReturns.Q4.includes("-") ? (
                                                                    <>
                                                                        <ArrowDownIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Decreased by </span>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <ArrowUpIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Increased by </span>
                                                                    </>
                                                                )}
                                                                {portfolioOneStats.quarterlyReturnsPercentage.Q4}
                                                            </div>
                                                        </dd>
                                                    </div>
                                                </dl>
                                            </div>
                                        </div>


                                        {/* Card Two */}
                                        <div className="overflow-hidden bg-gswhite divide flex-1 my-2 mx-2">
                                            <div className="px-4 pt-5 sm:px-6">
                                                {data.find((item) => item.portfolioID === parseInt(secondPortfolio))?.portfolioName}
                                            </div>
                                            <div className='my-2 mx-2'>
                                                <h4 className="text-lg font-semibold">Total Portfolio Value</h4>
                                                <p className="text-gsgray90">${portfolioTwoStats.currentTotalPortfolioValue.toFixed(2)}</p>
                                            </div>
                                            {renderDivider("Portfolio Statistics")}
                                            <div className='my-1 flex'>
                                                <div className='my-1 mx-1 flex-1'>
                                                    <h6 className="text-md font-semibold">Portfolio Beta</h6>
                                                    <p className="text-gsgray90">{portfolioTwoStats.portfolioBeta}</p>
                                                </div>
                                                <div className='my-1 mx-1 flex-1'>
                                                    <h6 className="text-md font-semibold">Information Ratio</h6>
                                                    <p className="text-gsgray90">{portfolioTwoStats.informationRatio}</p>
                                                </div>
                                            </div>
                                            {renderDivider("Quarterly Returns")}
                                            <div>
                                                <dl className="mt-5 grid grid-cols-1 overflow-hidden border border-gsgray20 bg-gswhite md:grid-cols-2 md:divide-x">
                                                    <div key="Q1" className="px-4 py-5 sm:p-6">
                                                        <dt className="text-base font-normal text-gsgray90">Q1</dt>
                                                        <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                                            <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                                                {portfolioTwoStats.quarterlyReturns.Q1}
                                                            </div>
                                                            <div
                                                                className={classNames(
                                                                    portfolioTwoStats.quarterlyReturns.Q1.includes("-")
                                                                        ? 'bg-gsred20 text-gsred60'
                                                                        : 'bg-green-100 text-gsgreen60',
                                                                    'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                                                )}
                                                            >
                                                                {portfolioTwoStats.quarterlyReturns.Q1.includes("-") ? (
                                                                    <>
                                                                        <ArrowDownIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Decreased by </span>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <ArrowUpIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Increased by </span>
                                                                    </>
                                                                )}
                                                                {portfolioTwoStats.quarterlyReturnsPercentage.Q1}
                                                            </div>
                                                        </dd>
                                                    </div>
                                                    <div key="Q2" className="px-4 py-5 sm:p-6">
                                                        <dt className="text-base font-normal text-gsgray90">Q2</dt>
                                                        <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                                            <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                                                {portfolioTwoStats.quarterlyReturns.Q2}
                                                            </div>
                                                            <div
                                                                className={classNames(
                                                                    portfolioTwoStats.quarterlyReturns.Q2.includes("-")
                                                                        ? 'bg-gsred20 text-gsred60'
                                                                        : 'bg-green-100 text-gsgreen60',
                                                                    'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                                                )}
                                                            >
                                                                {portfolioTwoStats.quarterlyReturns.Q2.includes("-") ? (
                                                                    <>
                                                                        <ArrowDownIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Decreased by </span>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <ArrowUpIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Increased by </span>
                                                                    </>
                                                                )}
                                                                {portfolioTwoStats.quarterlyReturnsPercentage.Q2}
                                                            </div>
                                                        </dd>
                                                    </div>
                                                </dl>
                                            </div>
                                            <div>
                                                <dl className="grid grid-cols-1 overflow-hidden border border-gsgray20 border-t-0 bg-gswhite md:grid-cols-2 md:divide-x">
                                                    <div key="Q3" className="px-4 py-5 sm:p-6">
                                                        <dt className="text-base font-normal text-gsgray90">Q3</dt>
                                                        <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                                            <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                                                {portfolioTwoStats.quarterlyReturns.Q3}
                                                            </div>
                                                            <div
                                                                className={classNames(
                                                                    portfolioTwoStats.quarterlyReturns.Q3.includes("-")
                                                                        ? 'bg-gsred20 text-gsred60'
                                                                        : 'bg-green-100 text-gsgreen60',
                                                                    'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                                                )}
                                                            >
                                                                {portfolioTwoStats.quarterlyReturns.Q3.includes("-") ? (
                                                                    <>
                                                                        <ArrowDownIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Decreased by </span>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <ArrowUpIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Increased by </span>
                                                                    </>
                                                                )}
                                                                {portfolioTwoStats.quarterlyReturnsPercentage.Q3}
                                                            </div>
                                                        </dd>
                                                    </div>
                                                    <div key="Q4" className="px-4 py-5 sm:p-6">
                                                        <dt className="text-base font-normal text-gsgray90">Q4</dt>
                                                        <dd className="mt-1 flex items-baseline justify-between md:block lg:flex">
                                                            <div className="flex items-baseline text-2xl font-semibold text-gsgray90">
                                                                {portfolioTwoStats.quarterlyReturns.Q4}
                                                            </div>
                                                            <div
                                                                className={classNames(
                                                                    portfolioTwoStats.quarterlyReturns.Q4.includes("-")
                                                                        ? 'bg-gsred20 text-gsred60'
                                                                        : 'bg-green-100 text-gsgreen60',
                                                                    'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-sm font-medium md:mt-2 lg:mt-0'
                                                                )}
                                                            >
                                                                {portfolioTwoStats.quarterlyReturns.Q4.includes("-") ? (
                                                                    <>
                                                                        <ArrowDownIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-red-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Decreased by </span>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <ArrowUpIcon
                                                                            className="-ml-1 mr-0.5 h-5 w-5 flex-shrink-0 self-center text-green-500"
                                                                            aria-hidden="true"
                                                                        />
                                                                        <span className="sr-only">Increased by </span>
                                                                    </>
                                                                )}
                                                                {portfolioTwoStats.quarterlyReturnsPercentage.Q4}
                                                            </div>
                                                        </dd>
                                                    </div>
                                                </dl>
                                            </div>
                                        </div>
                                    </div>

                                    {/* Comparison Summary */}
                                    <div className="mx-auto max-w-7xl mt-2 px-4 sm:px-6 lg:px-8">
                                        <h6>Comparison Summary of {data.find((item) => item.portfolioID === parseInt(firstPortfolio))?.portfolioName} to {data.find((item) => item.portfolioID === parseInt(secondPortfolio))?.portfolioName}</h6>
                                        <div className=" overflow-hidden rounded-lg bg-gswhite flex-1 my-2 mx-2">
                                            <div className='my-2 mx-2'>
                                                <h4 className="text-lg font-semibold text-gsgray90">Total Portfolio Value</h4>
                                                <p
                                                    className={classNames(
                                                        portfolioDifference.currentTotalPortfolioValue < 0
                                                            ? 'text-gsred60'
                                                            : ' text-gsgreen60',
                                                        'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-xl font-medium md:mt-2 lg:mt-0'
                                                    )}>
                                                    {(portfolioDifference.currentTotalPortfolioValue < 0)
                                                        ? "-$" + portfolioDifference.currentTotalPortfolioValue.toFixed(2).replace("-", "")
                                                        : "$" + portfolioDifference.currentTotalPortfolioValue.toFixed(2)}
                                                </p>
                                            </div>
                                            {renderDivider("Portfolio Statistics")}
                                            <div className='my-1 flex'>
                                                <div className='my-1 mx-1 flex-1'>
                                                    <h6 className="text-md font-semibold text-gsgray90">Portfolio Beta</h6>
                                                    <p className={classNames(
                                                        portfolioDifference.portfolioBeta < 0
                                                            ? 'text-gsred60'
                                                            : ' text-gsgreen60',
                                                        'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-xl font-medium md:mt-2 lg:mt-0'
                                                    )}>
                                                        {portfolioDifference.portfolioBeta}</p>
                                                </div>
                                                <div className='my-1 mx-1 flex-1'>
                                                    <h6 className="text-md font-semibold text-gsgray90">Information Ratio</h6>
                                                    <p className={classNames(
                                                        portfolioDifference.informationRatio < 0
                                                            ? 'text-gsred60'
                                                            : ' text-gsgreen60',
                                                        'inline-flex items-baseline rounded-full px-2.5 py-0.5 text-xl font-medium md:mt-2 lg:mt-0'
                                                    )}>
                                                        {portfolioDifference.informationRatio}</p>
                                                </div>
                                            </div>
                                        </div>
                                        {renderDivider("Quarterly Returns")}
                                        <div>
                                            <dl className="mt-5 grid grid-cols-1 divide-y divide-gsgray20 border border-gsgray20 overflow-hidden rounded-lg bg-gswhite md:grid-cols-2 md:divide-x md:divide-y-0">
                                                <div key="Q1" className="px-4 py-5 sm:p-6">
                                                    <dt className="text-base font-normal text-gsgray90">Q1</dt>
                                                    <dd className="mt-1 flex justify-center md:block lg:flex">
                                                        <div className={classNames(
                                                            portfolioDifference.quarterlyReturns.Q1.includes("-")
                                                                ? 'text-gsred60'
                                                                : portfolioDifference.quarterlyReturns.Q1 === ("0.0")
                                                                    ? ' text-gsgray70'
                                                                    : ' text-gsgreen60',
                                                            'flex text-2xl font-semibold'
                                                        )}>
                                                            {parseFloat(portfolioDifference.quarterlyReturns.Q1).toFixed(2)}
                                                        </div>
                                                    </dd>
                                                </div>
                                                <div key="Q2" className="px-4 py-5 sm:p-6">
                                                    <dt className="text-base font-normal text-gsgray90">Q2</dt>
                                                    <dd className="mt-1 flex justify-center md:block lg:flex">
                                                        <div className={classNames(
                                                            portfolioDifference.quarterlyReturns.Q2.includes("-")
                                                                ? 'text-gsred60'
                                                                : portfolioDifference.quarterlyReturns.Q2 === ("0.0")
                                                                    ? ' text-gsgray70'
                                                                    : ' text-gsgreen60',
                                                            'flex text-2xl font-semibold'
                                                        )}>
                                                            {parseFloat(portfolioDifference.quarterlyReturns.Q2).toFixed(2)}
                                                        </div>
                                                    </dd>
                                                </div>
                                            </dl>
                                        </div>
                                        <div>
                                            <dl className="grid grid-cols-1 border border-gsgray20 border-t-0 divide-y divide-gsgray20 overflow-hidden bg-gswhite md:grid-cols-2 md:divide-x md:divide-y-0">
                                                <div key="Q3" className="px-4 py-5 sm:p-6">
                                                    <dt className="text-base font-normal text-gsgray90">Q3</dt>
                                                    <dd className="mt-1 flex justify-center md:block lg:flex">
                                                        <div className={classNames(
                                                            portfolioDifference.quarterlyReturns.Q3.includes("-")
                                                                ? 'text-gsred60'
                                                                : portfolioDifference.quarterlyReturns.Q3 === ("0.0")
                                                                    ? ' text-gsgray70'
                                                                    : ' text-gsgreen60',
                                                            'flex text-2xl font-semibold'
                                                        )}>
                                                            {parseFloat(portfolioDifference.quarterlyReturns.Q3).toFixed(2)}
                                                        </div>
                                                    </dd>
                                                </div>
                                                <div key="Q4" className="px-4 py-5 sm:p-6">
                                                    <dt className="text-base font-normal text-gsgray90">Q4</dt>
                                                    <dd className="mt-1 flex justify-center md:block lg:flex">
                                                        <div className={classNames(
                                                            portfolioDifference.quarterlyReturns.Q4.includes("-")
                                                                ? 'text-gsred60'
                                                                : portfolioDifference.quarterlyReturns.Q4 === ("0.0")
                                                                    ? ' text-gsgray70'
                                                                    : ' text-gsgreen60',
                                                            'flex text-2xl font-semibold'
                                                        )}>
                                                            {parseFloat(portfolioDifference.quarterlyReturns.Q4).toFixed(2)}
                                                        </div>
                                                    </dd>
                                                </div>
                                            </dl>
                                        </div>
                                    </div>
                                </div>
                            ) : null}
                        </div>
                    ) : null}
                </div>
            </div>

            {showModal && (
                <div className="fixed z-10 inset-0 overflow-y-auto">
                    <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
                        <div className="fixed inset-0 transition-opacity" aria-hidden="true">
                            <div onClick={handleModalClose} className="absolute inset-0 bg-gsgray20 opacity-75"></div>
                        </div>
                        <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
                        <div className="inline-block align-bottom bg-gswhite rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
                            <div className="bg-gswhite px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
                                <div className="items-center">
                                    <div className="mt-3 text-center sm:ml-4 sm:mt-0 sm:text-center">
                                        <h3 className="font-semibold leading-6 text-gsgray90 text-3xl" id="modal-title">Create a New Portfolio</h3>
                                        <form id="modalForm" className="my-6" onSubmit={(e) => {
                                            handleModalClose();
                                        }}>
                                            <div className="grid grid-cols-2 gap-4">
                                                <div className="mb-3 flex flex-col">
                                                    <input className="appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight"
                                                        id="portfolioName"
                                                        type="text"
                                                        placeholder="Portfolio Name"
                                                        value={portfolioName}
                                                        onChange={(e) => setPortfolioName(e.target.value)}
                                                        required
                                                    >
                                                    </input>
                                                </div>

                                                <div className="mb-3 flex flex-col">
                                                    <input className="appearance-none border rounded py-2 px-3 text-gsgray70 leading-tight"
                                                        id="portfolioCapital"
                                                        type="number"
                                                        placeholder="Capital"
                                                        value={portfolioCapital}
                                                        onChange={(e) => setPortfolioCapital(e.target.value)}
                                                        required
                                                    >
                                                    </input>
                                                </div>
                                            </div>

                                            <div className="mb-3">
                                                <textarea className="appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                                    id="portfolioStrategy"
                                                    placeholder="Strategy"
                                                    value={portfolioStrategy}
                                                    onChange={(e) => setPortfolioStrategy(e.target.value)}
                                                    required
                                                >
                                                </textarea>
                                            </div>
                                        </form>

                                        <div className="mb-3">
                                            <span id="summary"></span>
                                        </div>
                                        <hr className=""></hr>
                                        <div className="px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
                                            <button type="button" onClick={handleModalClose} className="inline-flex w-full justify-center rounded-md bg-gsgray70 px-3 py-2 text-sm font-semibold text-gswhite shadow-sm hover:bg-gsgray90 sm:ml-3 sm:w-auto">Cancel</button>

                                            <button type="submit" onClick={(e) => handleSubmit(e)} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 px-3 py-2 text-sm font-semibold text-gswhite shadow-sm hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Add Portfolio</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            <ToastContainer transition={Slide} />
            <Footer></Footer>
        </div>
    );
}

export default UserHome;