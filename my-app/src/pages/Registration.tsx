import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { registerUser } from '../utils/api';
import { EyeIcon } from '@heroicons/react/20/solid';
import { EyeSlashIcon } from '@heroicons/react/24/solid';
import { ToastContainer, toast, Slide } from 'react-toastify';

function Registration() {

    const [email, setEmail] = React.useState<string>("");
    const [firstName, setFirstName] = React.useState<string>("");
    const [lastName, setLastName] = React.useState<string>("");
    const [password, setPassword] = React.useState<string>("");
    const [confirmPassword, setConfirmPassword] = React.useState<string>("");

    {/* Registration Handler - Check if password meets requirements*/}
    const submitRegistration = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if(password != confirmPassword){
            passwordNoMatch();
            return;
        }

        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        // Regex Explanation:
        // ^ - start of string
        // (?=.*[a-z]) - at least one lowercase letter  
        // (?=.*[A-Z]) - at least one uppercase letter
        // (?=.*\d\s) - at least one number
        // (?=.*[@$!%*?&]) - at least one special character
        // [A-Za-z\d@$!%*?&]{8,} - 8 or more characters, input already limits length to 25
        if(passwordRegex.test(password)){
            registerSuccess();

            let data = {
                "email": email,
                "password": password,
                "firstName": firstName,
                "lastName": lastName,
                "role": "user"
            }
            
            const register = registerUser(data);
            register.then((response) => {
                console.log(response);
                if (response.success){
                    window.location.href = '/';
                }

            }).catch((error) => {
                console.log(error);

            });
        }
        else{
            passwordInvalid();
        }
    }

    {/* Password Requirements */}
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

    {/* Password Reveal Handler */}
    const [showPassword, setShowPassword] = React.useState<boolean>(false);

    {/* Toastify Notification Handler */}
    const registerSuccess = () => toast.success(
        "Registration Successful!", 
        {
            theme: "colored",
            position: "top-center",
            autoClose: 3000,
            pauseOnHover: false,
        }
    );

    const passwordNoMatch= () => toast.error(
        "Passwords do not match!", 
        {
            theme: "colored",
            position: "top-center",
            autoClose: 3000,
            pauseOnHover: false,
        }
    );

    const passwordInvalid= () => toast.error(
        "The password you've entered is invalid!", 
        {
            theme: "colored",
            position: "top-center",
            autoClose: 3000,
            pauseOnHover: false,
        }
    );



    return (
        <div className="Registration">
            <Header management={false} userType="admin" login={false}></Header>
            <div className="container mx-auto max-w-screen-xl h-screen grid place-items-center">
            <form className="w-full max-w-lg" onSubmit={submitRegistration}>

                <div className="flex flex-wrap -mx-3 mb-6">
                        <div className="w-full md:w-1/2 px-3">
                            <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                                First Name
                            </label>
                            <input className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 leading-tight focus:bg-white focus:border-gsgray-70" 
                            id="firstName" 
                            type="text" 
                            placeholder="John"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                            required
                            >
                            </input>
                        </div>

                        <div className="w-full md:w-1/2 px-3">
                            <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                                Last Name
                            </label>
                            <input className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 leading-tight focus:bg-white focus:border-gsgray-70" 
                            id="lastName" 
                            type="text" 
                            placeholder="Doe"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                            required
                            >
                            </input>
                        </div>
                </div>

                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        Email Address
                    </label>
                    <input className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:bg-white focus:border-gsgray-70" 
                    id="email" 
                    type="text" 
                    placeholder="example@gmail.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    >
                    </input>
                    </div>
                </div>

                {/* Password Input #1*/}
                <div className="flex flex-wrap -mx-3">
                    <div className="w-full px-3">
                        <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                            Password
                        </label>
                        <div className="relative mt-2 rounded-md shadow-sm">
                            <input
                            maxLength= {25} 
                            className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:bg-white focus:border-gsgray-70" 
                            id="password" 
                            type= {showPassword ? "text" : "password"}
                            placeholder="******************"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            onKeyUp={passwordReqs}
                            required
                            />
                            <div className="absolute inset-y-0 right-0 flex items-center pr-3">
                                {showPassword ?
                                (<EyeIcon className="h-5 w-5 text-gsgray60" aria-hidden="true" onClick={() => setShowPassword(prevState => !prevState)}/>):
                                (<EyeSlashIcon className="h-5 w-5 text-gsgray60" aria-hidden="true" onClick={() => setShowPassword(prevState => !prevState)}/>)} 
                            </div>
                        </div>
                    </div>
                </div>
                {/* Password Requirements */}
                <div className= "mb-6">
                    <ul className="">
                        <li className="text-xs font-thin" id="rq1">• Your password can be 8-25 characters long</li>
                        <li className="text-xs font-thin" id="rq2">• Include at least one Uppercase and Lowercase Character</li>
                        <li className="text-xs font-thin" id="rq3">• At least 1 valid symbol used (@$!%*?&) </li>
                        <li className="text-xs font-thin" id="rq4">• At least 1 number used</li>
                    </ul>
                </div>

                {/* Password Input #2 - Confirm Match*/}
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        Confirm Password
                    </label>
                    <input maxLength= {25} className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:bg-white focus:border-gsgray-70" 
                    id="confirmPassword" 
                    type="password" 
                    placeholder="******************"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    />
                    </div>
                </div>
                
                {/* Sign Up / Submit Button */}
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                        <button className="px-2 py-2 w-32 rounded-sm text-gswhite bg-gsgreen50 hover:bg-gsgreen60 focus:border-gsgreen70" type="submit">Sign Up</button>
                    </div>
                </div>
            </form>
            </div>
            <Footer></Footer>
            <ToastContainer transition={Slide} />
        </div>
    );
}

export default Registration;