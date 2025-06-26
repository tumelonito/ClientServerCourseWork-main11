import React from 'react';

const styles = {
    container: {
        padding: '1rem',
        border: '1px solid #ccc',
        borderRadius: '8px',
        marginBottom: '1rem',
    },
    list: {
        listStyle: 'none',
        padding: 0,
    },
    listItem: {
        padding: '0.5rem',
        cursor: 'pointer',
        borderRadius: '4px',
    },
    activeItem: {
        backgroundColor: '#e0e0e0',
    }
};

function AccountList({ accounts, onSelect, selectedAccountId }) {
    if (!accounts || accounts.length === 0) {
        return <p>You don't have any accounts yet.</p>;
    }

    return (
        <div style={styles.container}>
            <h3>Your Accounts</h3>
            <ul style={styles.list}>
                {accounts.map((account) => (
                    <li
                        key={account.id}
                        onClick={() => onSelect(account.id)}
                        style={{
                            ...styles.listItem,
                            ...(account.id === selectedAccountId ? styles.activeItem : {}),
                        }}
                    >
                        {account.name} ({account.currency})
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default AccountList;