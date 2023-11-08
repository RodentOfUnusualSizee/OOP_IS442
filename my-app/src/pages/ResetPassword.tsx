import React, { useRef } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { Slide, ToastContainer } from 'react-toastify';
import { getResetPasswordToken,  sendResetPasswordEmail, checkResetPasswordToken, resetPassword} from '../utils/api';
import { showToastMessage, showToastMessageSuccess } from '../utils/transform';

import { EyeIcon } from '@heroicons/react/20/solid';
import { EyeSlashIcon } from '@heroicons/react/24/solid';

function ResetPassword() {

    // user input
    const [email, setEmail] = React.useState<string>("");
    const [password, setPassword] = React.useState<string>("");


    // token
    const [isValid, setIsValid] = React.useState<boolean>(false);


    // send email
    const form = useRef<HTMLFormElement | null>(null);
    const sendEmail = async (e: { preventDefault: () => void; }) => {
        e.preventDefault();

        try {
            const response1 = await getResetPasswordToken(email);
            const response2 = await sendResetPasswordEmail(email, response1.data.token);
            showToastMessageSuccess(response2)

        } catch (error) {
            showToastMessage("Email does not exist")
            console.log(error);
        }
    };


    // check if email and token are valid
    const searchParams = new URLSearchParams(window.location.href);
    let tmptoken = searchParams.get("token");
    let useremail = searchParams.get("http://localhost:3000/resetpassword?email") || "";

    if (tmptoken !== null && useremail !== "") {
        const resetPasswordToken = checkResetPasswordToken(useremail, tmptoken);

        resetPasswordToken.then((response) => {
            if (response.success){
                setIsValid(true);
                setEmail(useremail);
            }

        }).catch((error) => {
            console.log(error);
        });
    }


    // reset password
    const userResetPassword = (e: { preventDefault: () => void; }) => {
        e.preventDefault();
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

        if(passwordRegex.test(password)){
            const passwordReset = resetPassword(email, password);
            
            passwordReset.then((response) => {
                console.log(response);
                showToastMessageSuccess(response);
                
            }).catch((error) => {
                console.log(error);
            });
        } else {
            showToastMessage("Your password is invalid or Email does not exist. Please try again.")
        }
    };


    // check if password is valid
    const passwordReqs = () => {
        const lengthRegex = /^.{8,25}$/;
        const upperLowerRegex = /^(?=.*[a-z])(?=.*[A-Z])/;
        const symbolRegex = /^(?=.*[@$!%*?&])/;
        const numberRegex = /^(?=.*\d)/;
        
        if(lengthRegex.test(password)){
            document.getElementById("rq1")!.style.color = "green";
        } else{
            document.getElementById("rq1")!.style.color = "red";
        }

        if(upperLowerRegex.test(password)){
            document.getElementById("rq2")!.style.color = "green";
        }else{
            document.getElementById("rq2")!.style.color = "red";
        }

        if(symbolRegex.test(password)){
            document.getElementById("rq3")!.style.color = "green";
        }else{
            document.getElementById("rq3")!.style.color = "red";
        }

        if(numberRegex.test(password)){
            document.getElementById("rq4")!.style.color = "green";
        }else{
            document.getElementById("rq4")!.style.color = "red";
        }
    }

    const [showPassword, setShowPassword] = React.useState<boolean>(false);

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
                        <div className="relative mt-2 rounded-md shadow-sm">
                            <input className="shadow appearance-none border rounded w-full py-2 px-3 my-2 text-gsgray70 leading-tight"
                                id="email"
                                type={showPassword ? "text" : "password"}
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            >
                            </input>
                            <div className="absolute inset-y-0 right-0 flex items-center pr-3">
                                {showPassword ?
                                    (<EyeIcon className="h-5 w-5 text-gsgray60" aria-hidden="true" onClick={() => setShowPassword(prevState => !prevState)} />) :
                                    (<EyeSlashIcon className="h-5 w-5 text-gsgray60" aria-hidden="true" onClick={() => setShowPassword(prevState => !prevState)} />)}
                            </div>
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
                                value={email}
                                readOnly // user not allowed to reset other users passwords
                            >
                            </input>
                        </div>
                        <div className="relative mt-2 rounded-md shadow-sm">
                            <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                id="password"
                                type={showPassword ? "text" : "password"}
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                onKeyUp={() => {
                                    passwordReqs();
                                }}
                                required>
                            </input>
                            <div className="absolute inset-y-0 right-0 flex items-center pr-3">
                                {showPassword ?
                                    (<EyeIcon className="h-5 w-5 text-gsgray60" aria-hidden="true" onClick={() => setShowPassword(prevState => !prevState)} />) :
                                    (<EyeSlashIcon className="h-5 w-5 text-gsgray60" aria-hidden="true" onClick={() => setShowPassword(prevState => !prevState)} />)}
                            </div>
                        </div>
                        <div>
                            <ul className="py-2">
                                <li className="text-xs font-thin" id="rq1">• Your password can be 8-25 characters long</li>
                                <li className="text-xs font-thin" id="rq2">• Include at least one Uppercase and Lowercase Character</li>
                                <li className="text-xs font-thin" id="rq3">• At least 1 symbol used</li>
                                <li className="text-xs font-thin" id="rq4">• At least 1 number used</li>
                            </ul>
                        </div>
                        <div id="loginButtons">
                            <input className="bg-gsblue60 hover:bg-gsblue70 text-white font-light w-30 py-2 px-4 rounded mx-2" type="submit" value="Reset Password" />
                        </div>
                    </form>
                </div>
            }
            </div>
            <ToastContainer transition={Slide} />
            <Footer></Footer>
        </div>
    );
}

export default ResetPassword;
