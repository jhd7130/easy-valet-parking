import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function OAuthCallbackPage() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { loginWithToken } = useAuth();

    useEffect(() => {
        const token = searchParams.get('token');
        if (token) {
            loginWithToken(token)
                .then(() => navigate('/'))
                .catch(() => navigate('/login'));
        } else {
            navigate('/login');
        }
    }, [searchParams, navigate, loginWithToken]);

    return (
        <div className="min-h-screen bg-slate-50 flex items-center justify-center">
            <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
        </div>
    );
}
