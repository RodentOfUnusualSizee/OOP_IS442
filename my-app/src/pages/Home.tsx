import React, { useEffect } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { CSSTransition } from 'react-transition-group';
import '../styles/home.css';

function Home() {

    const [loginClicked, setLoginClicked] = React.useState<boolean>(false);

    return (
        <div>
            <Header management={true} userType="admin" login={false}></Header>
            <div className="container mx-auto max-w-screen-xl h-screen bg-gray-200 rounded-l shadow border p-8 m-10 grid place-items-center">
                <div className="">
                    <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
                    <p className="text-4xl text-gray-700 font-bold mb-5">
                        Portfolio Performance Analyser App
                    </p>
                    <p className="container mx-auto text-sm">
                        <button className=" bg-gsblue60 hover:bg-gsblue70 focus:outline-none focus:ring-2 focus:ring-black w-20 mx-2 px-2 py-2 text-white rounded-sm font-light" onClick={() => setLoginClicked(true)} >Login</button>
                        <button className=" bg-gsblue60 hover:bg-gsblue70 focus:outline-none focus:ring-2 focus:ring-black w-20 mx-2 px-2 py-2 text-white rounded-sm font-light">Register</button>
                    </p>
                    {loginClicked ? (
                        <CSSTransition
                            in={loginClicked}
                            timeout={300}
                            classNames="fade"
                            unmountOnExit
                        >
                            <div>
                                <form className="bg-white shadow-md rounded p-8 m-10">

                                    <div className="mb-4">
                                        {/*
                              <label className="block text-gray-700 text-sm font-bold mb-2" >
                                User ID
                              </label>
                              */}
                                        <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight" id="username" type="text" placeholder="Enter ID">
                                        </input>
                                    </div>
                                    <div className="mb-4">
                                        {/* <label className="block text-gray-700 text-sm font-bold mb-2">
                                Password
                              </label> */}
                                        <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight" id="password" type="password" placeholder="Enter Password">
                                        </input>
                                    </div>
                                    <div className="">
                                        <button className="bg-gsblue60 hover:bg-gsblue70 text-white font-bold w-30 py-2 px-2 rounded mx-2" type="button">
                                            User Login
                                        </button>

                                        <button className="bg-gsblue60 hover:bg-gsblue70 text-white font-bold w-30 py-2 px-2 rounded mx-2" type="button">
                                            Admin Login
                                        </button>
                                    </div>
                                    <a className="inline-block align-baseline font-bold text-xs mt-2 text-gsblue60 hover:bg-gsblue50" href="#">
                                        Forgot Password?
                                    </a>
                                </form>
                            </div>
                        </CSSTransition>
                    ) : null}
                </div>
            </div>
            <Footer></Footer>
        </div>
    )
}

export default Home;