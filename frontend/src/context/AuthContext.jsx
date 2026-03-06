import { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            api.get('/users/me')
                .then(response => {
                    setUser({
                        token,
                        email: response.data.email,
                        nickname: response.data.nickname,
                        role: response.data.role,
                        affiliationId: response.data.affiliationId,
                    });
                })
                .catch(() => {
                    localStorage.removeItem('token');
                    setUser(null);
                })
                .finally(() => setLoading(false));
        } else {
            setLoading(false);
        }
    }, []);

    const login = async (email, password) => {
        const response = await api.post('/auth/login', { email, password });
        const { accessToken, nickname, role, affiliationId } = response.data;
        localStorage.setItem('token', accessToken);
        setUser({ token: accessToken, email, nickname, role, affiliationId });
        return response.data;
    };

    const signup = async (email, password, nickname, affiliationId) => {
        const response = await api.post('/auth/signup', { email, password, nickname, affiliationId });
        return response.data;
    };

    const loginWithToken = async (token) => {
        localStorage.setItem('token', token);
        const response = await api.get('/users/me');
        setUser({
            token,
            email: response.data.email,
            nickname: response.data.nickname,
            role: response.data.role,
            affiliationId: response.data.affiliationId,
        });
    };

    const logout = () => {
        localStorage.removeItem('token');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, loginWithToken, signup, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
