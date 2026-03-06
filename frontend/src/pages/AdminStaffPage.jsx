import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, UserPlus, UserCheck, UserX, Users } from 'lucide-react';

export default function AdminStaffPage() {
    const [staff, setStaff] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showInvite, setShowInvite] = useState(false);
    const [inviteForm, setInviteForm] = useState({ email: '', nickname: '' });
    const [processing, setProcessing] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        fetchStaff();
    }, []);

    const fetchStaff = async () => {
        try {
            const response = await api.get('/admin/staff');
            setStaff(response.data);
        } catch (error) {
            console.error('Failed to fetch staff', error);
        } finally {
            setLoading(false);
        }
    };

    const handleInvite = async (e) => {
        e.preventDefault();
        try {
            await api.post('/admin/staff/invite', inviteForm);
            setShowInvite(false);
            setInviteForm({ email: '', nickname: '' });
            fetchStaff();
        } catch (error) {
            alert(error.response?.data?.message || 'Failed to invite staff');
        }
    };

    const toggleActive = async (id, currentActive) => {
        setProcessing(id);
        try {
            const action = currentActive ? 'deactivate' : 'activate';
            await api.post(`/admin/staff/${id}/${action}`);
            fetchStaff();
        } catch (error) {
            alert(error.response?.data?.message || 'Failed to update staff');
        } finally {
            setProcessing(null);
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
            <div className="bg-white border-b border-slate-200">
                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                    <button
                        onClick={() => navigate('/')}
                        className="inline-flex items-center text-slate-600 hover:text-slate-900 mb-4 transition-colors"
                    >
                        <ArrowLeft className="w-4 h-4 mr-2" />
                        Back to Dashboard
                    </button>
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <Users className="w-8 h-8 text-indigo-600" />
                            <h1 className="text-3xl font-bold text-slate-900">Staff Management</h1>
                        </div>
                        <button
                            onClick={() => setShowInvite(!showInvite)}
                            className="btn-primary"
                        >
                            <UserPlus className="w-4 h-4 mr-2" />
                            Invite Staff
                        </button>
                    </div>
                </div>
            </div>

            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Invite Form */}
                {showInvite && (
                    <div className="card p-6 mb-6">
                        <h3 className="text-lg font-bold text-slate-900 mb-4">Invite New Staff</h3>
                        <form onSubmit={handleInvite} className="flex flex-col sm:flex-row gap-3">
                            <input
                                type="email"
                                placeholder="Email"
                                value={inviteForm.email}
                                onChange={e => setInviteForm({ ...inviteForm, email: e.target.value })}
                                className="input-field flex-1"
                                required
                            />
                            <input
                                type="text"
                                placeholder="Nickname"
                                value={inviteForm.nickname}
                                onChange={e => setInviteForm({ ...inviteForm, nickname: e.target.value })}
                                className="input-field flex-1"
                                required
                            />
                            <button type="submit" className="btn-primary whitespace-nowrap">
                                Send Invite
                            </button>
                        </form>
                    </div>
                )}

                {/* Staff List */}
                <div className="card overflow-hidden">
                    <table className="min-w-full divide-y divide-slate-200">
                        <thead className="bg-slate-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Name</th>
                                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Email</th>
                                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Role</th>
                                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Status</th>
                                <th className="px-6 py-3 text-right text-xs font-semibold text-slate-500 uppercase">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-200">
                            {staff.map(s => (
                                <tr key={s.id} className="hover:bg-slate-50">
                                    <td className="px-6 py-4 text-sm font-medium text-slate-900">{s.nickname}</td>
                                    <td className="px-6 py-4 text-sm text-slate-500">{s.email}</td>
                                    <td className="px-6 py-4">
                                        <span className={`px-2 py-1 text-xs font-semibold rounded-full ${s.role === 'ADMIN' ? 'bg-red-100 text-red-700' : 'bg-blue-100 text-blue-700'}`}>
                                            {s.role}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className={`px-2 py-1 text-xs font-semibold rounded-full ${s.active ? 'bg-green-100 text-green-700' : !s.invitedBy ? 'bg-yellow-100 text-yellow-700' : 'bg-slate-100 text-slate-500'}`}>
                                            {s.active ? 'Active' : !s.invitedBy ? '승인 대기' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <button
                                            onClick={() => toggleActive(s.id, s.active)}
                                            disabled={processing === s.id}
                                            className={`inline-flex items-center px-3 py-1.5 rounded-lg text-sm font-medium transition-colors disabled:opacity-50 ${s.active
                                                    ? 'text-red-600 bg-red-50 hover:bg-red-100'
                                                    : 'text-green-600 bg-green-50 hover:bg-green-100'
                                                }`}
                                        >
                                            {s.active ? (
                                                <><UserX className="w-3.5 h-3.5 mr-1" /> Deactivate</>
                                            ) : (
                                                <><UserCheck className="w-3.5 h-3.5 mr-1" /> Activate</>
                                            )}
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}
