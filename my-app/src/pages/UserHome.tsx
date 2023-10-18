import React, { useEffect, useContext } from 'react';
import Table from '../components/Table';
import Header from '../components/Header';
import Footer from '../components/Footer';
import PortfolioCard from '../components/PortfolioCard';
import { getPortfolioByUserId } from '../utils/api';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';


function ProtectedPage() {
    const { isLoggedIn } = useAuth(); 
    const navigate = useNavigate(); 

    console.log(isLoggedIn);

    useEffect(() => {
        if (!isLoggedIn) {
            navigate('/');
        } else {
            // Do nothing
            // Data fetching logic
        }

    }, [isLoggedIn, navigate]); 

    return <UserHome />;
}

function UserHome() {
    const [hasFetchedData, setHasFetchedData] = React.useState(false);

    const { authUser, isLoggedIn } = useAuth();
    const userId = authUser.id;
    const userRole = authUser.role;
    const userIsLoggedIn = isLoggedIn;
    const management = userRole === "management" || userRole === "user";
    console.log("User role: " + userRole)
    console.log("User logged in: " + userIsLoggedIn)

    let samplePortfolioData = [
        { id: 1, name: "Portfolio 1", strategy: "Strategy A", capital: 10000 },
        { id: 2, name: "Portfolio 2", strategy: "Strategy B", capital: 15000 },
        { id: 3, name: "Portfolio 3", strategy: "Strategy C", capital: 20000 },
    ];

    React.useEffect(() => {
        if (!hasFetchedData) {
            const login = getPortfolioByUserId(userId);
            login.then((response) => {
                setHasFetchedData(true);
            }).catch((error) => {
                console.log(error);
            });
        }
    }, [userId]);


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
                <div className="mx-auto mt-16 max-w-2xl my-16 sm:mt-20 lg:mt-24 lg:max-w-4xl">
                    <PortfolioCard portfolioList={samplePortfolioData}></PortfolioCard>
                </div>
            </div>
            <Footer></Footer>
        </div>
    );
}

export default ProtectedPage;