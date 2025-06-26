import React, { createContext, useState, useContext, useEffect } from 'react';
import { apiClient } from '../api/apiClient';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem('authToken'));

    useEffect(() => {
        if (token) {
            localStorage.setItem('authToken', token);
            apiClient.defaults.headers.common['Token'] = token;
        } else {
            localStorage.removeItem('authToken');
            delete apiClient.defaults.headers.common['Token'];
        }
    }, [token]);

    const login = (newToken) => setToken(newToken);
    const logout = () => setToken(null);

    const authValue = {
        token,
        isAuthenticated: !!token,
        login,
        logout,
    };

    return <AuthContext.Provider value={authValue}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    return useContext(AuthContext);
};