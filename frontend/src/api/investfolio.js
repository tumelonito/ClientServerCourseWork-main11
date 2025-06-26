import { apiClient } from './apiClient';

export const getAccounts = async () => {
    try {
        const response = await apiClient.get('/api/accounts');
        return response.data;
    } catch (error) {
        console.error('Failed to fetch accounts:', error);
        return [];
    }
};

export const getPortfolioSummary = async (accountId) => {
    try {
        const response = await apiClient.get(`/api/dashboard/account/${accountId}`);
        return response.data;
    } catch (error) {
        console.error(`Failed to fetch summary for account ${accountId}:`, error);
        return null;
    }
};

export const createAccount = async (accountData) => {
    try {
        const response = await apiClient.post('/api/accounts', accountData);
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Failed to create account:', error);
        return { success: false, error };
    }
};

export const getAssets = async () => {
    try {
        const response = await apiClient.get('/api/assets');
        return response.data;
    } catch (error) {
        console.error('Failed to fetch assets:', error);
        return [];
    }
};

export const createTransaction = async (transactionData) => {
    try {
        const response = await apiClient.post('/api/transactions', transactionData);
        return { success: true, data: response.data };
    } catch (error) {
    console.error('Failed to create transaction:', error);
    return { success: false, error };
}
};