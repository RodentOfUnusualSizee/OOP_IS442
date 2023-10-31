import React, { useState, useEffect, useContext, useCallback } from "react";
import axios from 'axios';

// Interface
export interface AuthContextInterface {
    authUser: any;
    setAuthUser: React.Dispatch<React.SetStateAction<any>>;
    isLoggedIn: boolean;
    setIsLoggedIn: React.Dispatch<React.SetStateAction<boolean>>;
    login: (username: string, password: string) => void;
    logout: () => void;

    // portfolioId: any;
    // setPortfolioId: React.Dispatch<React.SetStateAction<any>>;
}

const AuthContext = React.createContext<AuthContextInterface | undefined>(undefined);

export function useAuth(): AuthContextInterface {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
}

export function AuthProvider(props: { children: React.ReactNode }) {
    const [authUser, setAuthUser] = useState<any | null>(null);
    const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        if (token) {
            setIsLoggedIn(true);
            setAuthUser(JSON.parse(token));
        }
    }, []);

    const login = async (email: string, password: string): Promise<String> => {
        try {
            const response = await axios.post('http://localhost:8080/api/user/login', {
                "email": email,
                "password": password
            });
            console.log("This");
            console.log(response.data.data);
            setAuthUser(response.data.data)
            setIsLoggedIn(true);
            localStorage.setItem('authToken', JSON.stringify(response.data.data));
            return response.data.data.role;
        } catch (error) {
            console.error(error);
            throw error; // this will allow you to use .catch when calling login
        }
    };

    const logout = useCallback(() => {
        setAuthUser(null);
        setIsLoggedIn(false);
        localStorage.removeItem('authToken');
    }, []);



    const value: AuthContextInterface = {
        authUser,
        setAuthUser,
        isLoggedIn,
        setIsLoggedIn,
        login,
        logout,
    };

    return (
        <AuthContext.Provider value={value}>
            {props.children}
        </AuthContext.Provider>
    );
}