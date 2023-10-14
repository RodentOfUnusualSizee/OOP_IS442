import React, { useEffect } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { CSSTransition } from 'react-transition-group';
import '../styles/home.css';
import axios from 'axios';

function Home() {

    const [loginClicked, setLoginClicked] = React.useState<boolean>(false);
    // const [homePage, setHomePage] = React.useState<boolean>(false);
    let homePage = false; // cause setHomePage is async, doesn't update immediately, so prints false and doesn't redirect to homepage

    // const [username, setUsername] = React.useState<string>("");
    const [email, setEmail] = React.useState<string>("");
    const [password, setPassword] = React.useState<string>("");

    const submitLogin = (e: React.FormEvent<HTMLButtonElement>) => {
        e.preventDefault();
        // Name of button clicked - which kind of login to use
        const buttonName = e.currentTarget.name;
        //Can use the below to check consts
        // alert("Username: " + username + ", Password: " + password + ", Button: " + buttonName);
        alert("Email: " + email + ", Password: " + password + ", Button: " + buttonName);


        if (buttonName == "userLogin") {
            //User Login - Add API call below to verify username and password for USER
            axios.get("http://localhost:8080/api/user/getUserByEmail/" + email)
                .then(function (response) {
                    console.log(response);
                    if (response["data"]["password"] == password){
                        // setHomePage(true);
                        homePage = true
                        localStorage.setItem("userId", response["data"]["id"]);

                        if(homePage){
                            window.location.href = "/UserHome";
                        }
                        else{
                            alert("Invalid username or password");
                        }

                    }
                })
                .catch(function (error) {
                    console.log(error);
                });


        //     // Change true condition below to API call result
        //     if(homePage){
        //         // window.location.href = "/Portfolio";
        //         window.location.href = "/UserHome";
        //     }
        //     else{
        //         // simple alert for now
        //         alert("Invalid username or password");
        //     }
        // } else if (buttonName == "adminLogin") {
        //     //User Login - Add API call below to verify username and password for ADMIN


        //     // Change true condition below to API call result
        //     if(homePage){
        //         // window.location.href = "/Portfolio";
        //         window.location.href = "/UserHome";
        //     }
        //     else{
        //         alert("Invalid username or password");
        //     }
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
                            onExit={()=>setLoginClicked(false)}
                    >
                        <div>
                            <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
                            <p className="text-4xl text-gray-700 font-bold mb-5">
                                Portfolio Performance Analyser App
                            </p>
                            <form className="bg-white shadow-md rounded p-8 m-10">
                                <button onClick={()=>setLoginClicked(false)} className="font-bold py-2 px-4 rounded inline-flex place-items-start">
                                <svg xmlns="http://www.w3.org/2000/svg" height="10px" viewBox="0 0 448 512"><path d="M9.4 233.4c-12.5 12.5-12.5 32.8 0 45.3l160 160c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L109.2 288 416 288c17.7 0 32-14.3 32-32s-14.3-32-32-32l-306.7 0L214.6 118.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0l-160 160z"/></svg>
                                </button>
                                <div className="mb-4">
                                {/*
                                <label className="block text-gray-700 text-sm font-bold mb- 2" >
                                User ID
                                </label>
                              */}
                                    {/* <input className="shadow appearance-none border rounded w-full py-2 px-3 my-2 text-gsgray70 leading-tight"
                                    id="username" 
                                    type="text" 
                                    placeholder="User ID"
                                    value = {username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                    >
                                    </input> */}
                                    <input className="shadow appearance-none border rounded w-full py-2 px-3 my-2 text-gsgray70 leading-tight"
                                        id="email" 
                                        type="text" 
                                        placeholder="Email"
                                        value = {email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                    >
                                    </input>
                                </div>
                                <div className="mb-4">
                                {/* <label className="block text-gray-700 text-sm font-bold mb-2">
                                Password
                                </label> 
                                */}
                                    <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                    id="password" 
                                    type="password" 
                                    placeholder="Password"
                                    value = {password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required>
                                    </input>
                                </div>
                                <div id="loginButtons">
                                    <button className="bg-gsblue60 hover:bg-gsblue70 text-white font-light w-30 py-2 px-2 rounded-sm mx-2" type="submit" onClick={submitLogin} name="userLogin">
                                        User Login
                                    </button>

                                    <button className="bg-gsblue60 hover:bg-gsblue70 text-white font-light w-30 py-2 px-2 rounded-sm mx-2" type="submit" onClick={submitLogin} name="adminLogin">
                                        Admin Login
                                    </button>
                                </div>
                                <div>
                                    <a className="inline-block align-baseline font-bold text-xs text-gsblue60 hover:text-gsblue50" href="#">
                                        Forgot Password?
                                    </a>
                                </div>
                                <div>
                                    <span className="text-xs mx-1">Not a user yet?</span>                            
                                    <a className="inline-block align-baseline font-bold text-xs text-gsblue60 hover:text-gsblue50" href="registration">
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