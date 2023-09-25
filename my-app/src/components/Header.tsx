import React, { useState } from 'react';
import '../tailwind.css';


const Header = () => {
    const userLinks = [
        {
            name: "Portfolio",
            link: "/portfolio"
        },
        {
            name: "Stocks",
            link: "/stocks"
        },
        {
            name: "Invest",
            link: "/invest"
        }
    ];

    const adminLinks = [
        {
            name: "User Management",
            link: "/user-management"
        },
        {
            name: "Portfolio Watch",
            link: "/portfolio-watch"
        },
        {
            name: "Data Management",
            link: "/data-management"

        }
    ];

    const [user, getUser] = useState<String>("user");
    const [isLogin, getLogin] = useState<Boolean>(false);


    return (
        <header className="bg-blue">
            <div className="mx-auto flex h-16 max-w-screen-xl items-center gap-8 px-4 sm:px-6 lg:px-8">
                <a className="block text-t  eal-600" href="/">
                    <span className="sr-only">Home</span>
                </a>

                <div className="flex flex-1 items-center justify-end md:justify-between">
                    <nav aria-label="Global" className="hidden md:block">
                        {user === "user" ? (
                            <ul className="flex items-center gap-6 text-sm">
                                {userLinks.map(({ name, link }) => (
                                    <li key={link}>
                                        <a className="text-gray-500 transition hover:text-gray-500/75" href="/">
                                            {name}
                                        </a>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <ul className="flex items-center gap-6 text-sm">
                                {adminLinks.map(({ name, link }) => (
                                    <li key={link}>
                                        <a className="text-gray-500 transition hover:text-gray-500/75" href="/">
                                            {name}
                                        </a>
                                    </li>
                                ))}
                            </ul>
                        )
                        }
                    </nav>

                    <div className="flex items-center gap-4">
                        {!isLogin ? (
                            <div className="sm:flex sm:gap-4">
                                <a className="block rounded-md bg-teal-600 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-teal-700" href="/">Login</a>
                                <a className="hidden rounded-md bg-gray-100 px-5 py-2.5 text-sm font-medium text-teal-600 transition hover:text-teal-600/75 sm:block" href="/">Register</a>
                            </div>
                        ) : (
                            <div className="sm:flex sm:gap-4">
                                <a className="hidden rounded-md bg-gray-100 px-5 py-2.5 text-sm font-medium text-teal-600 transition hover:text-teal-600/75 sm:block" href="/">Log Out</a>
                            </div>
                        )}

                        <button className="block rounded bg-gray-100 p-2.5 text-gray-600 transition hover:text-gray-600/75 md:hidden">
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