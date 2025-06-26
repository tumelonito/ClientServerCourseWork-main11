import { apiClient } from './apiClient';

export const loginUser = async (login, password) => {
    try {
        const response = await apiClient.post('/login', { login, password });
        const token = response.headers['token']; // lowercase
        return { success: true, token };
    } catch (error) {
        console.error('Login failed:', error);
        return { success: false, error };
    }
};

export const registerUser = async (login, password) => {
    try {
        const response = await apiClient.put('/login', { login, password });
        const token = response.headers['token'];
        return { success: true, token };
    } catch (error) {
        console.error('Registration failed:', error);
        return { success: false, error };
    }
};
