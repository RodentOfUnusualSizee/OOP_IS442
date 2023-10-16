import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { registerUser } from '../utils/api';

function Registration() {

    const [email, setEmail] = React.useState<string>("");
    const [firstName, setFirstName] = React.useState<string>("");
    const [lastName, setLastName] = React.useState<string>("");
    const [password, setPassword] = React.useState<string>("");
    const [confirmPassword, setConfirmPassword] = React.useState<string>("");

    const submitRegistration = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        // can use the below to check consts
        // alert("Email: " + email + ", User ID: " + userID + ", Password: " + password);
        alert("Email: " + email + ", First Name: " + firstName +  + ", Last Name: " + lastName + ", Password: " + password);
        alert(password == confirmPassword);

        if(password != confirmPassword){
            alert("Passwords do not match");
            return; // skip below code
        }

        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        // password regex to check if it clears all the requirements
        // comment out the checkers if you dont need them
        if(passwordRegex.test(password)){
            alert("Password is good");
            window.location.href = '/';
        }
        else{
            alert("Password is bad");
        }
        
        // api call to register user 
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
        }).catch((error) => {
            console.log(error);
        });

        // axios.post("http://localhost:8080/api/user/create", {
        //     "email": email,
        //     "password": password,
        //     "firstName": firstName,
        //     "lastName": lastName,
        //     "role": "user"
        // })
        // .then(function (response) {
        //     console.log(response);
        // })
        // .catch(function (error) {
        //     console.log(error);
        // });
    }

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
                            <input className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500" id="firstName" 
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
                            <input className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500" id="lastName" 
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
                    {/* <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0"> */}
                    <div className="w-full px-3">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        Email Address
                    </label>
                    <input className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white" 
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
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        Password
                    </label>
                    <input maxLength= {25} className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white focus:border-gray-500" 
                    id="password" 
                    type="password" 
                    placeholder="******************"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    onKeyUp={passwordReqs}
                    required
                    >
                    </input>
                    <ul className="">
                        <li className="text-xs font-thin" id="rq1">• Your password can be 8-25 characters long</li>
                        <li className="text-xs font-thin" id="rq2">• Include at least one Uppercase and Lowercase Character</li>
                        <li className="text-xs font-thin" id="rq3">• At least 1 symbol used</li>
                        <li className="text-xs font-thin" id="rq4">• At least 1 number used</li>
                    </ul>
                    </div>
                </div>
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        Confirm Password
                    </label>
                    <input maxLength= {25} className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white focus:border-gray-500" 
                    id="confirmPassword" 
                    type="password" 
                    placeholder="******************"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    >
                    </input>
                    </div>
                </div>
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                        <button className="px-2 py-2 w-24 rounded-sm text-gswhite bg-gsgreen50 hover:bg-gsgreen60" type="submit">Sign Up</button>
                    </div>
                </div>
            </form>
            </div>
            <Footer></Footer>
        </div>
    );
}

export default Registration;