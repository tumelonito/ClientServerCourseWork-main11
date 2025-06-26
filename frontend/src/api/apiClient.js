import axios from 'axios';

export const apiClient = axios.create({
    baseURL: 'http://localhost:8080', // Your Java server URL
});

// Interceptor to automatically add the auth token to every request
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers['Token'] = token;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);