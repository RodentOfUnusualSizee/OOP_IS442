import React, { useEffect } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { CSSTransition } from 'react-transition-group';
import '../styles/home.css';
import { loginUser } from '../utils/api';


import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';


function Home() {

    const {setAuthUser, setIsLoggedIn} = useAuth();
    const navigate = useNavigate();

    const [loginClicked, setLoginClicked] = React.useState<boolean>(false);
    let homePage = false; // cause setHomePage is async, doesn't update immediately, so prints false and doesn't redirect to homepage

    // const [username, setUsername] = React.useState<string>("");
    const [email, setEmail] = React.useState<string>("");
    const [password, setPassword] = React.useState<string>("");

    const submitLogin = (e: React.FormEvent<HTMLButtonElement>) => {
        e.preventDefault();
        // Name of button clicked - which kind of login to use
        const buttonName = e.currentTarget.name;
        //Can use the below to check consts
        alert("Email: " + email + ", Password: " + password + ", Button: " + buttonName);


        if (buttonName == "userLogin") {
            //User Login - Add API call below to verify username and password for USER
            let data = {
                "email": email,
                "password": password
            }
            const loginAPI = loginUser(data);
            loginAPI.then((response) => {
                console.log(response);

                if (response["success"]) {
                    setAuthUser(response["data"]);
                    setIsLoggedIn(true);
                    navigate("/UserHome");
                } else {
                    alert("Invalid username or password");
                }
            }).catch((error) => {
                console.log(error);
            });
        }
    }



    return (
        <div>
            <Header management={false} userType="admin" login={false}></Header>
            <div className="container mx-auto max-w-screen-xl h-screen rounded-l p-8 grid place-items-center">
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
                                    <input className="shadow appearance-none border rounded w-full py-2 px-3 my-2 text-gsgray70 leading-tight"
                                        id="email"
                                        type="text"
                                        placeholder="Email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                    >
                                    </input>
                                </div>
                                <div className="mb-4">
                                    <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                        id="password"
                                        type="password"
                                        placeholder="Password"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required>
                                    </input>
                                </div>
                                <div id="loginButtons">
                                    <button className="bg-gsblue60 hover:bg-gsblue70 text-white font-light w-30 py-2 px-2 rounded-sm mx-2" type="submit" onClick={submitLogin} name="userLogin">
                                        User Login
                                    </button>

                                    <button className="bg-gsblue60 hover:bg-gsblue70 text-white font-light w-30 py-2 px-2 rounded-sm mx-2 my-2" type="submit" onClick={submitLogin} name="adminLogin">
                                        Admin Login
                                    </button>
                                </div>
                                <div>
                                    <a className="inline-block align-baseline font-bold text-xs text-gsblue60 hover:text-gsblue50 my-2" href="#">
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
                        <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
                        <p className="text-4xl text-gray-700 font-bold mb-5">
                            Portfolio Performance Analyser App
                        </p>
                        <p className="container mx-auto text-sm">
                            <button className=" bg-gsblue60 hover:bg-gsblue70 focus:outline-none focus:ring-2 focus:ring-black w-20 mx-2 px-2 py-2 text-white rounded-sm font-light" onClick={() => setLoginClicked(true)} >Login</button>
                            <button className=" bg-gsblue60 hover:bg-gsblue70 focus:outline-none focus:ring-2 focus:ring-black w-20 mx-2 px-2 py-2 text-white rounded-sm font-light"><a href="registration">Register</a></button>
                        </p>
                    </div>
                }
            </div>
            <Footer></Footer>
        </div>
    )
}

export default Home;