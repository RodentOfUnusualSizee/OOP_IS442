import React, { useEffect } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { CSSTransition } from 'react-transition-group';
import '../styles/home.css';

import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

import { EyeIcon } from '@heroicons/react/20/solid';
import { EyeSlashIcon } from '@heroicons/react/24/solid';

function Home() {

    interface authToken {
        id: number;
        email: string;
        role: string;
    }

    const { login, setAuthUser, setIsLoggedIn } = useAuth();
    const navigate = useNavigate();

    const [loginClicked, setLoginClicked] = React.useState<boolean>(false);
    let homePage = false;

    const [email, setEmail] = React.useState<string>("");
    const [password, setPassword] = React.useState<string>("");
    const [error, setError] = React.useState<string>("");

    const submitLogin = async (e: React.FormEvent<HTMLButtonElement>) => {
        e.preventDefault();
        // alert("Email: " + email + ", Password: " + password );
        try {
            const userRole = (await login(email, password)) as unknown as string;
            console.log("User Role:", userRole);
            let role = userRole;
            if (role === "user") {
                navigate("/UserHome");
            }
            else {
                navigate("/AdminHome");
            }
        } catch (error : unknown) {
            if (error instanceof Error) {
                console.error("An error occurred during login", error);
                alert(error.cause);
                setError(error.message);
            } else {
                console.error("An error occurred during login", error);
                setError("An error occurred during login");
            }
        }
    }

    const [showPassword, setShowPassword] = React.useState<boolean>(false);

    return (
        <div>
            <Header management={false} userType="admin" login={false}></Header>
            <div className="container mx-auto max-w-screen-xl h-screen rounded-l p-8 grid place-items-center">
                {/* Login Modal */}
                {loginClicked ? (
                    <CSSTransition
                        in={loginClicked}
                        timeout={300}   
                        classNames="fade"
                        unmountOnExit
                        onExit={() => setLoginClicked(false)}
                    >
                        <div>
                            <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
                            <p className="text-4xl text-gray-700 font-bold mb-5">
                                Portfolio Performance Analyser App
                            </p>
                            <form className="bg-white shadow-md rounded p-8 m-10">
                                <div className="mb-4">
                                    <input className="appearance-none block w-full bg-gswhite text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:bg-white focus:border-gsgray-70"
                                        id="email"
                                        type="text"
                                        placeholder="Email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                    >
                                    </input>
                                </div>
                                <div className="relative mt-2 rounded-md shadow-sm">
                                    <input className="sappearance-none block w-full bg-gswhite text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:bg-white focus:border-gsgray-70"
                                        id="password"
                                        type= {showPassword ? "text" : "password"}
                                        placeholder="Password"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required>
                                    </input>

                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3">
                                        {showPassword ?
                                        (<EyeIcon className="h-5 w-5 text-gsgray60" aria-hidden="true" onClick={() => setShowPassword(prevState => !prevState)}/>):
                                        (<EyeSlashIcon className="h-5 w-5 text-gsgray60" aria-hidden="true" onClick={() => setShowPassword(prevState => !prevState)}/>)} 
                                    </div>
                                </div>
                                <div id="errorMessage" className="text-gsred60 text-sm font-medium w-full pb-4 px-2 place-items-center">
                                    {error}
                                </div>
                                <div id="loginButtons">
                                    <button className="bg-gsblue60 hover:bg-gsblue70 text-gswhite font-medium w-28 py-2 px-2 rounded-sm mx-2" type="submit" onClick={submitLogin} name="Login">
                                        Login
                                    </button>

                                </div>
                                <div>
                                    <a className="inline-block align-baseline font-bold text-xs text-gsblue60 hover:text-gsblue50 my-2" href="/resetpassword
                                    ">
                                        Forgot Password?
                                    </a>
                                </div>
                                <div>
                                    <span className="text-xs mx-1">Not a user yet?</span>
                                    <a className="inline-block align-baseline font-bold text-xs text-gsblue60 hover:text-gsblue50 my-2" href="registration">
                                        Register here
                                    </a>
                                </div>

                            </form>
                        </div>
                    </CSSTransition>
                ) :
                    <div>
                        {/* Initial Landing Page */}
                        <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
                        <p className="text-4xl text-gray-700 font-bold mb-5">
                            Portfolio Performance Analyser App
                        </p>
                        {/* Login and Register buttons - out of modal */}
                        <p className="container mx-auto text-sm">
                            <button className=" bg-gsblue60 hover:bg-gsblue70 focus:outline-none focus:ring-2 focus:ring-black w-20 mx-2 px-2 py-2 text-gswhite rounded-sm font-light" onClick={() => setLoginClicked(true)} >Login</button>
                            <button className=" bg-gsblue60 hover:bg-gsblue70 focus:outline-none focus:ring-2 focus:ring-black w-20 mx-2 px-2 py-2 text-gswhite rounded-sm font-light"><a href="registration">Register</a></button>
                        </p>
                    </div>
                }
            </div>
            <Footer></Footer>
        </div>
    )
}

export default Home;