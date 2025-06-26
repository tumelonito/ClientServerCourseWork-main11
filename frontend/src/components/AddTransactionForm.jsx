import React, { useState } from 'react';
import { createTransaction } from '../api/investfolio';

const styles = {
    form: { display: 'flex', flexDirection: 'column', gap: '1rem' },
    input: { padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' },
    button: { padding: '0.7rem', borderRadius: '4px', border: 'none', backgroundColor: '#007bff', color: 'white', cursor: 'pointer' },
    radioGroup: { display: 'flex', gap: '1rem', alignItems: 'center' },
};

function AddTransactionForm({ accountId, assets, onTransactionAdded, onClose }) {
    const [assetId, setAssetId] = useState(assets[0]?.id || '');
    const [type, setType] = useState('BUY'); // BUY or SELL
    const [quantity, setQuantity] = useState('');
    const [pricePerUnit, setPricePerUnit] = useState('');
    const [transactionDate, setTransactionDate] = useState(new Date().toISOString().slice(0, 16)); // Формат для datetime-local
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!assetId || !quantity || !pricePerUnit) {
            setError('Please fill in all required fields.');
            return;
        }

        const transactionData = {
            accountId,
            assetId: parseInt(assetId, 10),
            type,
            quantity: parseFloat(quantity),
            pricePerUnit: parseFloat(pricePerUnit),
            transactionDate: new Date(transactionDate).toISOString(),
        };

        const result = await createTransaction(transactionData);

        if (result.success) {
            onTransactionAdded();
            onClose();
        } else {
            setError('Failed to add transaction. Please try again.');
        }
    };

    return (
        <form onSubmit={handleSubmit} style={styles.form}>
            <div>
                <label>Asset</label>
                <select value={assetId} onChange={(e) => setAssetId(e.target.value)} style={styles.input} required>
                    <option value="" disabled>Select an asset</option>
                    {assets.map(asset => (
                        <option key={asset.id} value={asset.id}>{asset.name} ({asset.ticker})</option>
                    ))}
                </select>
            </div>

            <div style={styles.radioGroup}>
                <label>
                    <input type="radio" value="BUY" checked={type === 'BUY'} onChange={(e) => setType(e.target.value)} />
                    Buy
                </label>
                <label>
                    <input type="radio" value="SELL" checked={type === 'SELL'} onChange={(e) => setType(e.target.value)} />
                    Sell
                </label>
            </div>

            <div>
                <label>Quantity</label>
                <input type="number" step="any" value={quantity} onChange={(e) => setQuantity(e.target.value)} style={styles.input} required />
            </div>

            <div>
                <label>Price per Unit</label>
                <input type="number" step="any" value={pricePerUnit} onChange={(e) => setPricePerUnit(e.target.value)} style={styles.input} required />
            </div>

            <div>
                <label>Date & Time</label>
                <input type="datetime-local" value={transactionDate} onChange={(e) => setTransactionDate(e.target.value)} style={styles.input} required />
            </div>

            {error && <p style={{ color: 'red' }}>{error}</p>}
            <button type="submit" style={styles.button}>Add Transaction</button>
        </form>
    );
}

export default AddTransactionForm;