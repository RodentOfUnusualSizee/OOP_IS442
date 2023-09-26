import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';

function Registration() {
    return (
        <div className="Registration">
            <Header management={false} userType="admin" login={false}></Header>
            <div className="container mx-auto max-w-screen-xl h-screen grid place-items-center">
            <form className="w-full max-w-lg">
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        Email Address
                    </label>
                    <input className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white" id="grid-first-name" type="text" placeholder="example@gmail.com">
                    </input>
                    </div>
                    <div className="w-full md:w-1/2 px-3">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        User ID
                    </label>
                    <input className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500" id="grid-last-name" type="text" placeholder="User ID">
                    </input>
                    <span className="text-xs text-gsgrey40 font-light">• You can use your email as your ID </span>
                    </div>
                </div>
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        Password
                    </label>
                    <input maxLength= {25} className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white focus:border-gray-500" id="grid-password" type="password" placeholder="******************">
                    </input>
                    <ul className="">
                        <li className="text-xs font-thin">• Your password can be 8-25 characters long</li>
                        <li className="text-xs font-thin">• Include at least one Uppercase and Lowercase Character</li>
                        <li className="text-xs font-thin" >• At least 1 symbol used</li>
                        <li className="text-xs font-thin">• At least 1 number used</li>
                    </ul>
                    </div>
                </div>
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                    <label className="block uppercase tracking-wide text-gsblue60 text-xs font-bold mb-2">
                        Confirm Password
                    </label>
                    <input maxLength= {25} className="appearance-none block w-full bg-gsgray20 text-gsgrey70 border border-gsgray40 rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white focus:border-gray-500" id="password" type="password" placeholder="******************">
                    </input>
                    </div>
                </div>
                <div className="flex flex-wrap -mx-3 mb-6">
                    <div className="w-full px-3">
                        <button className="px-2 py-2 w-24 rounded-sm text-gswhite bg-gsgreen50 hover:bg-gsgreen60">Sign Up</button>
                    </div>
                </div>
            </form>
                {/*}
                <form className="">
                        <div className="mx-auto max-w-lg">
                            <div className="py-1">
                                <span className="px-1 text-sm text-gray-600">Username</span>
                                <input placeholder="" type="text" className="text-md block px-3 py-2 rounded-lg w-full bg-white border-2 border-gray-300 placeholder-gray-600 shadow-md focus:placeholder-gray-500 focus:bg-white focus:border-gray-600 focus:outline-none">
                            </input>
                            </div>
                            <div className="py-1">
                                <span className="px-1 text-sm text-gray-600">Email</span>
                                <input placeholder="" type="email" className="text-md block px-3 py-2 rounded-lg w-full bg-white border-2 border-gray-300 placeholder-gray-600 shadow-md focus:placeholder-gray-500 focus:bg-white focus:border-gray-600 focus:outline-none">
                            </input>
                            </div>
                            <div className="py-1">
                                <span className="px-1 text-sm text-gray-600">Password</span>
                                <input placeholder="" type="password" x-model="password" className="text-md block px-3 py-2 rounded-lg w-full bg-white border-2 border-gray-300 placeholder-gray-600 shadow-md focus:placeholder-gray-500 focus:bg-white focus:border-gray-600 focus:outline-none">
                            </input>
                            </div>
                            <div className="py-1">
                                <span className="px-1 text-sm text-gray-600">Password Confirm</span>
                                <input placeholder="" type="password" x-model="password_confirm" className="text-md block px-3 py-2 rounded-lg w-full bg-white border-2 border-gray-300 placeholder-gray-600 shadow-md focus:placeholder-gray-500 focus:bg-white focus:border-gray-600 focus:outline-none">
                            </input>
                            </div>

                        </div>
                    </form>
                    */}
            </div>
            <Footer></Footer>
        </div>
    );
}

export default Registration;