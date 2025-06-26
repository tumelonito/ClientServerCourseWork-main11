import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { getAccounts, getPortfolioSummary, getAssets } from '../api/investfolio';
import AccountList from '../components/AccountList';
import PortfolioView from '../components/PortfolioView';
import Modal from '../components/Modal';
import AddAccountForm from '../components/AddAccountForm';
import AddTransactionForm from '../components/AddTransactionForm';
import PortfolioChart from '../components/PortfolioChart';

const styles = {
    container: { maxWidth: '1200px', margin: '0 auto', padding: '1rem' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    controls: { display: 'flex', gap: '1rem', marginBottom: '1rem' },
    main: { display: 'grid', gridTemplateColumns: '1fr 3fr', gap: '2rem', marginTop: '1rem' },
    rightColumn: { display: 'flex', flexDirection: 'column', gap: '1rem' },
    button: { padding: '0.5rem 1rem', cursor: 'pointer', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px' },
    txButton: { padding: '0.5rem 1rem', cursor: 'pointer', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px' }
};

function DashboardPage() {
    const { logout } = useAuth();
    const navigate = useNavigate();

    const [accounts, setAccounts] = useState([]);
    const [selectedAccountId, setSelectedAccountId] = useState(null);
    const [portfolioSummary, setPortfolioSummary] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [isAccountModalOpen, setIsAccountModalOpen] = useState(false);
    const [isTxModalOpen, setIsTxModalOpen] = useState(false);
    const [allAssets, setAllAssets] = useState([]);

    const refreshPortfolio = useCallback(() => {
        if (selectedAccountId) {
            setIsLoading(true);
            getPortfolioSummary(selectedAccountId).then(summaryData => {
                setPortfolioSummary(summaryData);
                setIsLoading(false);
            });
        } else {
            setPortfolioSummary(null);
        }
    }, [selectedAccountId]);

    const fetchAccounts = useCallback(async (selectLast = false) => {
        const userAccounts = await getAccounts();
        setAccounts(userAccounts);
        if (userAccounts.length > 0) {
            if (selectLast) {
                setSelectedAccountId(userAccounts[userAccounts.length - 1].id);
            } else if (!selectedAccountId) {
                setSelectedAccountId(userAccounts[0].id);
            }
        }
    }, [selectedAccountId]);

    useEffect(() => {
        fetchAccounts();
        getAssets().then(assets => setAllAssets(assets));
    }, [fetchAccounts]);

    useEffect(() => {
        refreshPortfolio();
    }, [selectedAccountId, refreshPortfolio]);

    const handleLogout = () => { logout(); navigate('/login'); };
    const handleAccountAdded = () => { fetchAccounts(true); };
    const handleTransactionAdded = () => { refreshPortfolio(); };

    return (
        <div style={styles.container}>
            <header style={styles.header}>
                <h1>InvestFolio Dashboard</h1>
                <button onClick={handleLogout}>Logout</button>
            </header>
            <div style={styles.controls}>
                <button style={styles.button} onClick={() => setIsAccountModalOpen(true)}>+ Add Account</button>
                <button style={styles.txButton} onClick={() => setIsTxModalOpen(true)} disabled={!selectedAccountId}>
                    + Add Transaction
                </button>
            </div>
            <main style={styles.main}>
                <div>
                    <AccountList accounts={accounts} onSelect={setSelectedAccountId} selectedAccountId={selectedAccountId} />
                </div>
                <div style={styles.rightColumn}>
                    {isLoading ? (<p>Loading portfolio...</p>) : (<>
                        <PortfolioView summary={portfolioSummary} />
                        <PortfolioChart data={portfolioSummary?.assetsBySector} />
                    </>)}
                </div>
            </main>
            <Modal isOpen={isAccountModalOpen} onClose={() => setIsAccountModalOpen(false)} title="Create New Account">
                <AddAccountForm onAccountAdded={handleAccountAdded} onClose={() => setIsAccountModalOpen(false)} />
            </Modal>
            <Modal isOpen={isTxModalOpen} onClose={() => setIsTxModalOpen(false)} title="Add New Transaction">
                <AddTransactionForm accountId={selectedAccountId} assets={allAssets} onTransactionAdded={handleTransactionAdded} onClose={() => setIsTxModalOpen(false)} />
            </Modal>
        </div>
    );
}
export default DashboardPage;