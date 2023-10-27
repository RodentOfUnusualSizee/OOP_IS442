import React, { useRef } from 'react';
import emailjs from '@emailjs/browser';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useSearchParams } from "react-router-dom";
import { getResetPasswordToken,  sendResetPasswordEmail, checkResetPasswordToken, resetPassword} from '../utils/api';

function ResetPassword() {

    // user input
    const [email, setEmail] = React.useState<string>("");
    const [password, setPassword] = React.useState<string>("");
    const [msg, setMsg] = React.useState<string>("");


    // token
    const [token, setToken] = useSearchParams();
    const [isValid, setIsValid] = React.useState<boolean>(false);


    // send email
    const form = useRef<HTMLFormElement | null>(null);
    const sendEmail = (e: { preventDefault: () => void; }) => {
        e.preventDefault();

        if (form.current) {
            const resetPasswordToken = getResetPasswordToken(email);
            resetPasswordToken.then((response) => {
                console.log(response);
                setToken(response.data.token)

            }).catch((error) => {
                console.log(error);

            });

            const resetPasswordEmail = sendResetPasswordEmail(email, token);
            resetPasswordEmail.then((response) => {
                console.log(response);
                setMsg(response);
            }).catch((error) => {
                console.log(error);
            });
        }
    };


    // check if email is valid
    const searchParams = new URLSearchParams(window.location.href);
    let tmptoken = searchParams.get("token");
    let useremail = searchParams.get("http://localhost:3000/resetpassword?email");

    if (tmptoken !== null && useremail !== null) {
        const resetPasswordToken = checkResetPasswordToken(useremail, tmptoken);

        resetPasswordToken.then((response) => {
            if (response.success){
                setIsValid(true);
            }

        }).catch((error) => {
            console.log(error);

        });
    }


    // reset password
    const userResetPassword = (e: { preventDefault: () => void; }) => {
        e.preventDefault();
        
        if (form.current) {
            const passwordReset = resetPassword(email, password);
            
            passwordReset
            .then((response) => {
                console.log(response);
                setMsg(response);
            })
            .catch((error) => {
                console.log(error);
            });
        }
    };

    return (
    <div>
            <Header management={false} userType="admin" login={false}></Header>
            <div className="container mx-auto max-w-screen-xl h-screen rounded-l p-8 grid place-items-center">
            {!isValid? (
                <div>
                    <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
                    <p className="text-4xl text-gray-700 font-bold mb-5 mx-20 px-20">
                        Reset Password
                    </p>
                    <form className="bg-white shadow-md rounded p-8 m-10" ref={form} onSubmit={sendEmail}>
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
                        <div id="errorMessage" className="text-gsgreen60 text-sm font-light w-30 pb-2 px-2">
                            {msg}
                        </div>
                        <div id="loginButtons">
                            <input className="bg-gsblue60 hover:bg-gsblue70 text-white font-light w-30 py-2 px-2 rounded mx-2" type="submit" value="Reset Password" />
                        </div>
                    </form>
                </div>
            ) :
                <div>
                    <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
                    <p className="text-4xl text-gray-700 font-bold mb-5 mx-20 px-20">
                        Reset Password
                    </p>
                    <form className="bg-white shadow-md rounded p-8 m-10" ref={form} onSubmit={userResetPassword}>
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
                        <div id="errorMessage" className="text-gsgreen60 text-sm font-light w-30 pb-2 px-2">
                            {msg}
                        </div>
                        <div id="loginButtons">
                            <input className="bg-gsblue60 hover:bg-gsblue70 text-white font-light w-30 py-2 px-2 rounded mx-2" type="submit" value="Reset Password" />
                        </div>
                    </form>
                </div>
            }
            </div>
            <Footer></Footer>
        </div>
    );
}

export default ResetPassword;
