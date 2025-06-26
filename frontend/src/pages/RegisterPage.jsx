import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { registerUser } from '../api/auth';

function RegisterPage() {
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const auth = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (password.length < 4) {
            setError('Password must be at least 4 characters long.');
            return;
        }

        const result = await registerUser(login, password);

        if (result.success && result.token) {
            auth.login(result.token);
            navigate('/dashboard');
        } else {
            setError('Registration failed. This login might already be taken.');
        }
    };

    return (
        <div>
            <h2>Register for InvestFolio</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Login:</label>
                    <input
                        type="text"
                        value={login}
                        onChange={(e) => setLogin(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <button type="submit">Register</button>
            </form>
            <p>
                Already have an account? <Link to="/login">Login here</Link>
            </p>
        </div>
    );
}

export default RegisterPage;