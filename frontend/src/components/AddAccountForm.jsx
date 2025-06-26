import React, { useState } from 'react';
import { createAccount } from '../api/investfolio';

const styles = {
    form: { display: 'flex', flexDirection: 'column', gap: '1rem' },
    input: { padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' },
    button: { padding: '0.7rem', borderRadius: '4px', border: 'none', backgroundColor: '#007bff', color: 'white', cursor: 'pointer' },
};

function AddAccountForm({ onAccountAdded, onClose }) {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [currency, setCurrency] = useState('USD');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!name || !currency) {
            setError('Name and currency are required.');
            return;
        }

        const result = await createAccount({ name, description, currency });

        if (result.success) {
            onAccountAdded(); // Повідомляємо батьківський компонент про успіх
            onClose(); // Закриваємо модальне вікно
        } else {
            setError('Failed to create account. Please try again.');
        }
    };

    return (
        <form onSubmit={handleSubmit} style={styles.form}>
            <div>
                <label>Account Name</label>
                <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    style={styles.input}
                    required
                />
            </div>
            <div>
                <label>Description</label>
                <input
                    type="text"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    style={styles.input}
                />
            </div>
            <div>
                <label>Currency</label>
                <select value={currency} onChange={(e) => setCurrency(e.target.value)} style={styles.input}>
                    <option value="USD">USD</option>
                    <option value="EUR">EUR</option>
                    <option value="UAH">UAH</option>
                </select>
            </div>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <button type="submit" style={styles.button}>Create Account</button>
        </form>
    );
}

export default AddAccountForm;