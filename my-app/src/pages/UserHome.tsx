import React, { useEffect } from 'react';
import Table from '../components/Table';
import Header from '../components/Header';
import Footer from '../components/Footer';
import PortfolioCard from '../components/PortfolioCard';
import { createPortfolio, getPortfolioByUserId } from '../utils/api';
import { useAuth } from '../context/AuthContext';
import { Slide, toast, ToastContainer } from 'react-toastify';


function UserHome() {
    interface Portfolio {
        portfolioID: number;
        portfolioName: string;
        strategyDesc: string;
        capitalUSD: number;
    }

    const [hasFetchedData, setHasFetchedData] = React.useState(false);
    const [data, setData] = React.useState<Portfolio[]>([]);

    const { authUser, isLoggedIn } = useAuth();
    const [isLoading, setIsLoading] = React.useState<boolean>(true);

    const [userId, setUserId] = React.useState<number>(1);
    const [userRole, setUserRole] = React.useState<string>("");
    const [userIsLoggedIn, setUserIsLoggedIn] = React.useState<boolean>(false);
    const management = userRole === "admin" || userRole === "user";
    console.log(authUser);

    function fetchPortfolios(user: number = userId){
        const portfolio = getPortfolioByUserId(user);
        portfolio.then((response) => {
            setData(response.data)
        }).catch((error) => {
            console.log(error);
        });
    }

    useEffect(() => {
        if (authUser) {
            setIsLoading(false);
            setUserId(authUser.id);
            setUserRole(authUser.role);
            setUserIsLoggedIn(true);
            console.log("login part");
            if (!hasFetchedData) {
                fetchPortfolios(authUser.id);
                setHasFetchedData(true);
            }
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
                toast.success('Successfully created Portfolio', {
                    position: "top-right",
                    autoClose: 5000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: false,
                    progress: undefined,
                    theme: "colored",
                });
                fetchPortfolios();
            } else {
                toast.error('Error creating portfolio', {
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
            toast.success('Error creating portfolio', {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: false,
                progress: undefined,
                theme: "colored",
            });
        });
        handleModalClose();
    }


    if (isLoading) {
        return (
            <div>Loading...</div>
        )
    }

    return (
        <div>
            <div className="UserHome">
                <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
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
                <div className="grid grid-cols-6" id="AddNewPortBtn">
                    <button onClick={handleAddClick} className="col-end-6 col-span-1 bg-gsgreen50 hover:bg-gsgreen60 text-white font-bold py-2 px-4 rounded">Create New Portfolio</button>
                </div>
                <div className="mx-auto mt-8 max-w-2xl mb-16 sm:mt-20 lg:mt-24 lg:max-w-4xl">
                    <PortfolioCard portfolioList={portfolioData}></PortfolioCard>
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
                                            <button type="button" onClick={handleModalClose} className="inline-flex w-full justify-center rounded-md bg-gsgray70 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-gsgray90 sm:ml-3 sm:w-auto">Cancel</button>

                                            <button type="submit" onClick={(e) => handleSubmit(e)} className="mt-3 inline-flex w-full justify-center rounded-md bg-gsgreen50 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gsgreen60 sm:mt-0 sm:w-auto">Add Portfolio</button>
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