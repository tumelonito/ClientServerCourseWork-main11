import React from 'react';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';

// Набір кольорів для секторів діаграми
const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#AF19FF', '#FF1943'];

const styles = {
    container: {
        height: 300, // Задаємо висоту контейнера
        border: '1px solid #ccc',
        borderRadius: '8px',
        padding: '1rem',
        marginTop: '1rem'
    },
    title: {
        textAlign: 'center',
        marginTop: 0
    }
}

function PortfolioChart({ data }) {
    // Перетворюємо дані з нашого API у формат, який розуміє Recharts
    // З { "Crypto": 12000, "Technology": 5000 }
    // у [ { name: "Crypto", value: 12000 }, { name: "Technology", value: 5000 } ]
    const chartData = Object.entries(data || {}).map(([name, value]) => ({
        name,
        value: parseFloat(value.toFixed(2)),
    }));

    if (!chartData || chartData.length === 0) {
        return (
            <div style={styles.container}>
                <h3 style={styles.title}>Asset Distribution by Sector</h3>
                <p style={{textAlign: 'center'}}>No data to display chart.</p>
            </div>
        );
    }

    return (
        <div style={styles.container}>
            <h3 style={styles.title}>Asset Distribution by Sector</h3>
            <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                    <Pie
                        data={chartData}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        outerRadius={80}
                        fill="#8884d8"
                        dataKey="value"
                        nameKey="name"
                        label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    >
                        {chartData.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                    </Pie>
                    <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
                    <Legend />
                </PieChart>
            </ResponsiveContainer>
        </div>
    );
}

export default PortfolioChart;