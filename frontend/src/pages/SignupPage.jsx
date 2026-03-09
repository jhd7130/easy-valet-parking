import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { Car, ArrowRight, Mail, Lock, User, Building2, Plus } from 'lucide-react';
import api from '../api/axios';

export default function SignupPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [affiliationId, setAffiliationId] = useState('');
    const [affiliations, setAffiliations] = useState([]);
    const [isCreatingNew, setIsCreatingNew] = useState(false);
    const [newAffiliationName, setNewAffiliationName] = useState('');
    const { signup } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        const fetchAffiliations = async () => {
            try {
                const response = await api.get('/affiliations');
                setAffiliations(response.data);
            } catch (err) {
                console.error('Failed to fetch affiliations', err);
            }
        };
        fetchAffiliations();
    }, []);

    const handleAffiliationChange = (e) => {
        const value = e.target.value;
        if (value === '__new__') {
            setIsCreatingNew(true);
            setAffiliationId('');
        } else {
            setIsCreatingNew(false);
            setNewAffiliationName('');
            setAffiliationId(value);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        try {
            let finalAffiliationId = affiliationId ? parseInt(affiliationId) : null;

            if (isCreatingNew && newAffiliationName.trim()) {
                const res = await api.post('/affiliations', { name: newAffiliationName.trim() });
                finalAffiliationId = res.data.id;
            }

            const data = await signup(email, password, nickname, finalAffiliationId);
            setSuccessMessage(data.message || '가입이 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다.');
            setTimeout(() => navigate('/login'), 3000);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create account');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-3xl shadow-xl w-full max-w-4xl overflow-hidden flex flex-col md:flex-row-reverse">
                {/* Left Side - Form (Now on Right for Signup) */}
                <div className="w-full md:w-1/2 p-8 md:p-12">
                    <div className="mb-10">
                        <div className="w-12 h-12 bg-indigo-600 rounded-xl flex items-center justify-center mb-4 shadow-lg shadow-indigo-200">
                            <Car className="text-white w-6 h-6" />
                        </div>
                        <h2 className="text-3xl font-bold text-slate-900 mb-2">Create Account</h2>
                        <p className="text-slate-500">Join us to manage parking efficiently.</p>
                    </div>

                    {successMessage && (
                        <div className="bg-green-50 text-green-700 p-4 rounded-xl mb-6 text-sm font-medium border border-green-100 animate-fade-in">
                            {successMessage}
                        </div>
                    )}

                    {error && (
                        <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6 text-sm font-medium border border-red-100 animate-fade-in">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-semibold text-slate-700 mb-2">Email Address</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-slate-400" />
                                </div>
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="input-field pl-11"
                                    placeholder="Enter your email"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-slate-700 mb-2">Nickname</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <User className="h-5 w-5 text-slate-400" />
                                </div>
                                <input
                                    type="text"
                                    value={nickname}
                                    onChange={(e) => setNickname(e.target.value)}
                                    className="input-field pl-11"
                                    placeholder="Choose a nickname"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-slate-700 mb-2">Password</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <Lock className="h-5 w-5 text-slate-400" />
                                </div>
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="input-field pl-11"
                                    placeholder="Create a password"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-slate-700 mb-2">소속 (선택)</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <Building2 className="h-5 w-5 text-slate-400" />
                                </div>
                                <select
                                    value={isCreatingNew ? '__new__' : affiliationId}
                                    onChange={handleAffiliationChange}
                                    className="input-field pl-11 appearance-none"
                                >
                                    <option value="">소속 없이 가입</option>
                                    {affiliations.map((aff) => (
                                        <option key={aff.id} value={aff.id}>
                                            {aff.name}
                                        </option>
                                    ))}
                                    <option value="__new__">+ 새 소속 만들기</option>
                                </select>
                            </div>
                            {isCreatingNew && (
                                <div className="relative mt-2">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <Plus className="h-5 w-5 text-slate-400" />
                                    </div>
                                    <input
                                        type="text"
                                        value={newAffiliationName}
                                        onChange={(e) => setNewAffiliationName(e.target.value)}
                                        className="input-field pl-11"
                                        placeholder="새 소속 이름 입력"
                                        required
                                    />
                                </div>
                            )}
                        </div>

                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full btn-primary h-12 text-base mt-2"
                        >
                            {isLoading ? (
                                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                            ) : (
                                <>
                                    Sign Up <ArrowRight className="ml-2 w-4 h-4" />
                                </>
                            )}
                        </button>
                    </form>

                    <div className="mt-8 text-center">
                        <p className="text-slate-600">
                            Already have an account?{' '}
                            <Link to="/login" className="font-semibold text-indigo-600 hover:text-indigo-500 transition-colors">
                                Sign in
                            </Link>
                        </p>
                    </div>
                </div>

                {/* Right Side - Image/Decoration */}
                <div className="hidden md:block w-1/2 bg-slate-900 relative overflow-hidden">
                    <div className="absolute inset-0 bg-gradient-to-br from-slate-800 to-slate-900"></div>
                    <div className="absolute inset-0 flex flex-col justify-center p-12 text-white z-10">
                        <h3 className="text-3xl font-bold mb-6">Join the Revolution</h3>
                        <p className="text-slate-300 text-lg leading-relaxed">
                            Streamline your valet operations with real-time tracking, digital ticketing, and seamless customer experiences.
                        </p>
                    </div>
                    {/* Abstract Circles */}
                    <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] bg-indigo-500 opacity-10 rounded-full blur-3xl"></div>
                </div>
            </div>
        </div>
    );
}
