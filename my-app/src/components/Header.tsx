import React, { useState } from 'react';
import { Link } from "react-router-dom";


interface HeaderProps {
    management: boolean;
    userType: String;
    login: boolean;
}

const Header = ({ management, userType, login }: HeaderProps) => {
    const userLinks = [
        {
            name: "Portfolio",
            link: "userhome"
        },
        {
            name: "Stocks",
            link: "stockhome"
        }
    ];

    const adminLinks = [
        {
            name: "User Management",
            link: "adminhome"
        },
        {
            name: "Portfolio Watch",
            link: "audit"
        },
        {
            name: "Data Management",
            link: "datamanagement"

        }
    ];

    const [isManagement, getManagement] = useState<boolean>(management);
    const [user, getUser] = useState<String>(userType);
    const [isLogin, getLogin] = useState<boolean>(login);


    return (
        <header className="bg-gsgray90">
            <div className="mx-auto flex h-16 max-w-screen-xl items-center gap-8 px-4 sm:px-6 lg:px-8">
                <Link to={"/"} className="block text-white-600">
                    <span className="sr-only">Home</span>
                    <img src='/images/gs-white.png' className='h-8 w-auto' />
                </Link>

                <div className="flex flex-1 items-center justify-end md:justify-between">
                    <nav aria-label="Global" className="hidden md:block">
                        {isManagement ? (
                            user === "user" ? (
                                <ul className="flex items-center gap-6 text-sm">
                                    {userLinks.map(({ name, link }) => (
                                        <li key={link}>
                                            <Link to={link} className="text-white hover:text-gray-500/75">
                                                {name}
                                            </Link>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <ul className="flex items-center gap-6 text-sm">
                                    {adminLinks.map(({ name, link }) => (
                                        <li key={link}>
                                            <Link to={link} className="text-white hover:text-gray-500/75">
                                                {name}
                                            </Link>
                                        </li>
                                    ))}
                                </ul>
                            )
                        ) : null}
                    </nav>
                    <div className="flex items-center gap-4">
                        {!isLogin ? (
                            <div className="sm:flex sm:gap-4">
                                <Link to={"/"} className="block rounded-md bg-gswhite px-5 py-2.5 text-sm font-medium text-gsgray90 transition hover:bg-gray-200" >Login</Link>
                                <Link to={"registration"} className="hidden rounded-md bg-gswhite px-5 py-2.5 text-sm font-medium text-gsgray90 transition hover:bg-gray-200 sm:block" >Register</Link>
                            </div>
                        ) : (
                            <div className="sm:flex sm:gap-4">
                                <Link to={"/"} className="hidden rounded-md bg-gswhite px-5 py-2.5 text-sm font-medium text-gsgray90 transition hover:bg-gray-200 sm:block" >Log Out</Link>
                            </div>
                        )}

                        <button className="block rounded bg-wgswhite p-2.5 text-gsgray90 md:hidden">
                            <span className="sr-only">Toggle menu</span>
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
        </header>
    );
}

export default Header;