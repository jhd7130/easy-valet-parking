import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { ArrowLeft, User, Mail, Shield, Calendar } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function ProfilePage() {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const { logout } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            const response = await api.get('/users/me');
            setProfile(response.data);
        } catch (error) {
            console.error('Failed to fetch profile', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-slate-50 flex items-center justify-center">
                <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-50">
            {/* Header */}
            <div className="bg-white border-b border-slate-200">
                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                    <button
                        onClick={() => navigate('/')}
                        className="inline-flex items-center text-slate-600 hover:text-slate-900 mb-4 transition-colors"
                    >
                        <ArrowLeft className="w-4 h-4 mr-2" />
                        Back to Dashboard
                    </button>
                    <h1 className="text-3xl font-bold text-slate-900">My Profile</h1>
                </div>
            </div>

            {/* Profile Content */}
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="card p-8">
                    {/* Avatar Section */}
                    <div className="flex items-center gap-6 mb-8 pb-8 border-b border-slate-200">
                        <div className="w-24 h-24 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-full flex items-center justify-center text-white text-3xl font-bold shadow-lg">
                            {profile?.nickname?.charAt(0).toUpperCase() || 'U'}
                        </div>
                        <div>
                            <h2 className="text-2xl font-bold text-slate-900 mb-1">{profile?.nickname}</h2>
                            <p className="text-slate-500">{profile?.email}</p>
                        </div>
                    </div>

                    {/* Profile Details */}
                    <div className="space-y-6">
                        <div className="flex items-start gap-4">
                            <div className="p-3 bg-indigo-50 rounded-xl text-indigo-600">
                                <Mail className="w-5 h-5" />
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-medium text-slate-500 mb-1">Email Address</p>
                                <p className="text-base font-semibold text-slate-900">{profile?.email}</p>
                            </div>
                        </div>

                        <div className="flex items-start gap-4">
                            <div className="p-3 bg-purple-50 rounded-xl text-purple-600">
                                <User className="w-5 h-5" />
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-medium text-slate-500 mb-1">Nickname</p>
                                <p className="text-base font-semibold text-slate-900">{profile?.nickname}</p>
                            </div>
                        </div>

                        <div className="flex items-start gap-4">
                            <div className="p-3 bg-blue-50 rounded-xl text-blue-600">
                                <Shield className="w-5 h-5" />
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-medium text-slate-500 mb-1">Role</p>
                                <span className={`inline-flex px-3 py-1 rounded-full text-sm font-semibold ${profile?.role === 'ADMIN'
                                        ? 'bg-red-100 text-red-700'
                                        : 'bg-green-100 text-green-700'
                                    }`}>
                                    {profile?.role}
                                </span>
                            </div>
                        </div>

                        <div className="flex items-start gap-4">
                            <div className="p-3 bg-slate-50 rounded-xl text-slate-600">
                                <Calendar className="w-5 h-5" />
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-medium text-slate-500 mb-1">Member Since</p>
                                <p className="text-base font-semibold text-slate-900">
                                    {profile?.createdAt ? new Date(profile.createdAt).toLocaleDateString('en-US', {
                                        year: 'numeric',
                                        month: 'long',
                                        day: 'numeric'
                                    }) : 'N/A'}
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Actions */}
                    <div className="mt-8 pt-8 border-t border-slate-200 flex gap-3">
                        <button
                            onClick={logout}
                            className="px-6 py-2.5 border border-red-200 rounded-xl text-sm font-medium text-red-600 hover:bg-red-50 transition-colors"
                        >
                            Sign Out
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
