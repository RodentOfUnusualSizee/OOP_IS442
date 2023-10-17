import React, { useState, useEffect, useContext } from "react";

// Interface
export interface AuthContextInterface {
    authUser: any;
    setAuthUser: React.Dispatch<React.SetStateAction<any>>;
    isLoggedIn: boolean;
    setIsLoggedIn: React.Dispatch<React.SetStateAction<boolean>>;
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
        // You can add authentication logic here if needed
    }, []);

    const value: AuthContextInterface = {
        authUser,
        setAuthUser,
        isLoggedIn,
        setIsLoggedIn,
    };

    return (
        <AuthContext.Provider value={value}>
            {props.children}
        </AuthContext.Provider>
    );
}