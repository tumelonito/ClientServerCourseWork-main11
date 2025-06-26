import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // Import Link
import { useAuth } from '../context/AuthContext';
import { loginUser } from '../api/auth';

function LoginPage() {
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const auth = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        const result = await loginUser(login, password);
        if (result.success) {
            auth.login(result.token);
            navigate('/dashboard');
        } else {
            setError('Invalid login or password.');
        }
    };

    return (
        <div>
            <h2>Login to InvestFolio</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Login:</label>
                    <input type="text" value={login} onChange={(e) => setLogin(e.target.value)} required />
                </div>
                <div>
                    <label>Password:</label>
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <button type="submit">Login</button>
            </form>
            <p>
                Don't have an account? <Link to="/register">Register here</Link>
            </p>
        </div>
    );
}

export default LoginPage;