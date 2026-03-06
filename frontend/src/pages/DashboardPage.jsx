import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { LogOut, Plus, Search, Car, Clock, CheckCircle, AlertCircle, X, User, QrCode, Users, BarChart3, Bell } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function DashboardPage() {
    const [parkings, setParkings] = useState([]);
    const [loading, setLoading] = useState(true);
    const { user, logout } = useAuth();
    const [showRegisterModal, setShowRegisterModal] = useState(false);
    const [newTicket, setNewTicket] = useState({ carNumber: '', customerName: '', phoneNumber: '', parkingArea: '' });
    const [searchTerm, setSearchTerm] = useState('');
    const [foundCustomers, setFoundCustomers] = useState([]);
    const [showCustomerSelection, setShowCustomerSelection] = useState(false);
    const [processingId, setProcessingId] = useState(null);
    const [exitNotification, setExitNotification] = useState(null);
    const [registering, setRegistering] = useState(false);

    useEffect(() => {
        fetchParkings();

        const token = localStorage.getItem('token');
        if (!token) return;

        const apiUrl = import.meta.env.VITE_API_URL || '';
        const eventSource = new EventSource(`${apiUrl}/api/sse/parking-updates?token=${token}`);
        eventSource.addEventListener('parking-update', () => {
            fetchParkings();
        });
        eventSource.addEventListener('exit-notification', (event) => {
            try {
                const data = JSON.parse(event.data);
                setExitNotification(data);
                setTimeout(() => setExitNotification(prev => prev === data ? null : prev), 30000);
            } catch (e) {
                console.error('Failed to parse exit-notification', e);
            }
        });
        eventSource.onerror = () => {
            eventSource.close();
        };
        return () => eventSource.close();
    }, []);

    const fetchParkings = async () => {
        try {
            const response = await api.get('/parking');
            setParkings(response.data);
        } catch (error) {
            console.error('Failed to fetch parkings', error);
        } finally {
            setLoading(false);
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setRegistering(true);
        try {
            await api.post('/parking', newTicket);
            setShowRegisterModal(false);
            setNewTicket({ carNumber: '', customerName: '', phoneNumber: '', parkingArea: '' });
            setFoundCustomers([]);
            setShowCustomerSelection(false);
            fetchParkings();
        } catch (error) {
            alert(error.response?.data?.message || 'Failed to register ticket');
        } finally {
            setRegistering(false);
        }
    };

    useEffect(() => {
        const timer = setTimeout(() => {
            if (newTicket.carNumber.length >= 2) {
                searchCustomers('carNumber', newTicket.carNumber);
            } else if (newTicket.phoneNumber.length >= 4) {
                searchCustomers('phoneNumber', newTicket.phoneNumber);
            } else {
                setFoundCustomers([]);
                setShowCustomerSelection(false);
            }
        }, 300);

        return () => clearTimeout(timer);
    }, [newTicket.carNumber, newTicket.phoneNumber]);

    const searchCustomers = async (field, value) => {
        try {
            const response = await api.get(`/customers/search?${field}=${value}`);
            const customers = response.data;
            if (customers.length > 0) {
                setFoundCustomers(customers);
                setShowCustomerSelection(field);
            } else {
                setFoundCustomers([]);
                setShowCustomerSelection(false);
            }
        } catch (error) {
            console.error('Failed to search customers', error);
        }
    };

    const selectCustomer = (customer) => {
        setNewTicket({
            ...newTicket,
            customerName: customer.name,
            phoneNumber: customer.phoneNumber
        });
        setFoundCustomers([]);
        setShowCustomerSelection(false);
    };

    const requestExit = async (id) => {
        setProcessingId(id);
        try {
            console.log(`Requesting exit for parking ID: ${id}`);
            await api.post(`/parking/${id}/request-exit`);
            await fetchParkings();
        } catch (error) {
            console.error('Failed to request exit:', error);
            alert('Failed to request exit');
        } finally {
            setProcessingId(null);
        }
    };

    const completeExit = async (id) => {
        setProcessingId(id);
        try {
            console.log(`Completing exit for parking ID: ${id}`);
            await api.post(`/parking/${id}/complete-exit`);
            await fetchParkings();
        } catch (error) {
            console.error('Failed to complete exit:', error);
            alert('Failed to complete exit');
        } finally {
            setProcessingId(null);
        }
    };

    const acceptExit = async (id) => {
        setProcessingId(id);
        try {
            console.log(`Accepting exit for parking ID: ${id}`);
            await api.post(`/parking/${id}/accept-exit`);
            await fetchParkings();
        } catch (error) {
            console.error('Failed to accept exit:', error);
            alert('Failed to accept exit');
        } finally {
            setProcessingId(null);
        }
    };

    const filteredParkings = parkings.filter(p =>
        p.carNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.customerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.ticketNumber.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const stats = {
        total: parkings.length,
        parked: parkings.filter(p => p.status === 'PARKED').length,
        requested: parkings.filter(p => p.status === 'REQUESTED').length,
    };

    return (
        <div className="min-h-screen bg-slate-50">
            {/* Navbar */}
            <nav className="bg-white border-b border-slate-200 sticky top-0 z-30">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex items-center gap-3">
                            <div className="bg-indigo-600 p-2 rounded-lg">
                                <Car className="text-white w-5 h-5" />
                            </div>
                            <h1 className="text-xl font-bold text-slate-900 tracking-tight">Easy Valet</h1>
                        </div>
                        <div className="flex items-center space-x-4">
                            {user?.role === 'ADMIN' && (
                                <>
                                    <Link
                                        to="/admin/staff"
                                        className="inline-flex items-center px-4 py-2 border border-slate-200 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-slate-900 transition-colors"
                                    >
                                        <Users className="w-4 h-4 mr-2" />
                                        Staff
                                    </Link>
                                    <Link
                                        to="/analytics"
                                        className="inline-flex items-center px-4 py-2 border border-slate-200 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-slate-900 transition-colors"
                                    >
                                        <BarChart3 className="w-4 h-4 mr-2" />
                                        Analytics
                                    </Link>
                                </>
                            )}
                            <Link
                                to="/profile"
                                className="inline-flex items-center px-4 py-2 border border-slate-200 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-slate-900 transition-colors"
                            >
                                <User className="w-4 h-4 mr-2" />
                                Profile
                            </Link>
                            <button
                                onClick={logout}
                                className="inline-flex items-center px-4 py-2 border border-slate-200 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-slate-900 transition-colors"
                            >
                                <LogOut className="w-4 h-4 mr-2" />
                                Sign Out
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            <main className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
                {/* Stats Overview */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                    <div className="card p-6 flex items-center gap-4">
                        <div className="p-3 bg-blue-100 rounded-xl text-blue-600">
                            <Car className="w-6 h-6" />
                        </div>
                        <div>
                            <p className="text-sm font-medium text-slate-500">Total Parked</p>
                            <p className="text-2xl font-bold text-slate-900">{stats.parked}</p>
                        </div>
                    </div>
                    <div className="card p-6 flex items-center gap-4">
                        <div className="p-3 bg-yellow-100 rounded-xl text-yellow-600">
                            <AlertCircle className="w-6 h-6" />
                        </div>
                        <div>
                            <p className="text-sm font-medium text-slate-500">Exit Requests</p>
                            <p className="text-2xl font-bold text-slate-900">{stats.requested}</p>
                        </div>
                    </div>
                    <div className="card p-6 flex items-center gap-4">
                        <div className="p-3 bg-green-100 rounded-xl text-green-600">
                            <CheckCircle className="w-6 h-6" />
                        </div>
                        <div>
                            <p className="text-sm font-medium text-slate-500">Total Records</p>
                            <p className="text-2xl font-bold text-slate-900">{stats.total}</p>
                        </div>
                    </div>
                </div>

                {/* Action Bar */}
                <div className="flex flex-col sm:flex-row justify-between items-center mb-6 gap-4">
                    <div className="relative w-full sm:w-96">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <Search className="h-5 w-5 text-slate-400" />
                        </div>
                        <input
                            type="text"
                            className="input-field pl-10 py-2.5"
                            placeholder="Search by car, name, or ticket..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                    <button
                        onClick={() => setShowRegisterModal(true)}
                        className="w-full sm:w-auto btn-primary"
                    >
                        <Plus className="w-5 h-5 mr-2" />
                        Register Vehicle
                    </button>
                </div>

                {/* Parking List */}
                <div className="space-y-4">
                    {/* Mobile Card View */}
                    <div className="lg:hidden space-y-4">
                        {filteredParkings.map((parking) => (
                            <div key={parking.id} className="card p-4">
                                {/* Header */}
                                <div className="flex items-center justify-between mb-3">
                                    <div className="flex items-center gap-3">
                                        <div className="h-10 w-10 bg-slate-100 rounded-full flex items-center justify-center text-slate-500">
                                            <Car className="w-5 h-5" />
                                        </div>
                                        <div>
                                            <div className="text-sm font-bold text-slate-900">{parking.carNumber}</div>
                                            <div className="text-xs text-slate-500">#{parking.ticketNumber}</div>
                                        </div>
                                    </div>
                                    <span className={`px-3 py-1 text-xs font-semibold rounded-full ${parking.status === 'PARKED' ? 'bg-blue-50 text-blue-700 border border-blue-100' :
                                        parking.status === 'REQUESTED' ? 'bg-yellow-50 text-yellow-700 border border-yellow-100 animate-pulse' :
                                            'bg-slate-100 text-slate-800'
                                        }`}>
                                        {parking.status === 'PARKED' ? '주차중' : parking.status === 'REQUESTED' ? '출차요청' : '출차완료'}
                                    </span>
                                </div>

                                {/* Info Grid */}
                                <div className="grid grid-cols-2 gap-3 text-sm mb-4">
                                    <div>
                                        <span className="text-slate-400 text-xs">고객</span>
                                        <p className="font-medium text-slate-900">{parking.customerName}</p>
                                    </div>
                                    <div>
                                        <span className="text-slate-400 text-xs">위치</span>
                                        <p className="font-medium text-slate-900">{parking.parkingArea}</p>
                                    </div>
                                    <div>
                                        <span className="text-slate-400 text-xs">입차</span>
                                        <p className="flex items-center gap-1 text-slate-600">
                                            <Clock className="w-3 h-3" />
                                            {new Date(parking.entryTime).toLocaleString([], { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })}
                                        </p>
                                    </div>
                                    <div>
                                        <span className="text-slate-400 text-xs">출차</span>
                                        {parking.exitTime ? (
                                            <p className="flex items-center gap-1 text-slate-600">
                                                <Clock className="w-3 h-3" />
                                                {new Date(parking.exitTime).toLocaleString([], { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })}
                                            </p>
                                        ) : (
                                            <p className="text-slate-400">-</p>
                                        )}
                                    </div>
                                </div>

                                {/* Exit Staff Info */}
                                {(parking.exitRequestedBy || parking.exitAssignedTo) && (
                                    <div className="flex gap-4 text-xs mb-4 p-2 bg-slate-50 rounded-lg">
                                        {parking.exitRequestedBy && (
                                            <div className="flex items-center gap-1 text-yellow-600">
                                                <span className="text-slate-400">요청:</span>
                                                {parking.exitRequestedBy}
                                            </div>
                                        )}
                                        {parking.exitAssignedTo && (
                                            <div className="flex items-center gap-1 text-green-600">
                                                <span className="text-slate-400">담당:</span>
                                                {parking.exitAssignedTo}
                                            </div>
                                        )}
                                    </div>
                                )}

                                {/* Actions */}
                                <div className="flex gap-2">
                                    {parking.status === 'PARKED' && (
                                        <button
                                            onClick={() => requestExit(parking.id)}
                                            disabled={processingId === parking.id}
                                            className="flex-1 text-yellow-600 bg-yellow-50 hover:bg-yellow-100 px-4 py-2.5 rounded-lg transition-colors font-medium text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            {processingId === parking.id ? 'Processing...' : '출차요청'}
                                        </button>
                                    )}
                                    {parking.status === 'REQUESTED' && !parking.exitAssignedTo && (
                                        <button
                                            onClick={() => acceptExit(parking.id)}
                                            disabled={processingId === parking.id}
                                            className="flex-1 text-indigo-600 bg-indigo-50 hover:bg-indigo-100 px-4 py-2.5 rounded-lg transition-colors font-medium text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            {processingId === parking.id ? 'Processing...' : '수락'}
                                        </button>
                                    )}
                                    {parking.status === 'REQUESTED' && parking.exitAssignedTo && (
                                        <button
                                            onClick={() => completeExit(parking.id)}
                                            disabled={processingId === parking.id}
                                            className="flex-1 text-green-600 bg-green-50 hover:bg-green-100 px-4 py-2.5 rounded-lg transition-colors font-medium text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            {processingId === parking.id ? 'Processing...' : '출차완료'}
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))}
                        {filteredParkings.length === 0 && (
                            <div className="card p-12 text-center text-slate-500">
                                <Car className="mx-auto h-12 w-12 text-slate-300 mb-3" />
                                <p>검색 결과가 없습니다.</p>
                            </div>
                        )}
                    </div>

                    {/* Desktop Table View */}
                    <div className="card overflow-hidden hidden lg:block">
                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-slate-200">
                                <thead className="bg-slate-50">
                                    <tr>
                                        <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Vehicle Info</th>
                                        <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Customer</th>
                                        <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Location</th>
                                        <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
                                        <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">등록/출차요청</th>
                                        <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">출차담당</th>
                                        <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">입차/출차</th>
                                        <th scope="col" className="px-6 py-3 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-slate-200">
                                    {filteredParkings.map((parking) => (
                                        <tr key={parking.id} className="hover:bg-slate-50 transition-colors">
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="flex items-center">
                                                    <div className="flex-shrink-0 h-10 w-10 bg-slate-100 rounded-full flex items-center justify-center text-slate-500">
                                                        <Car className="w-5 h-5" />
                                                    </div>
                                                    <div className="ml-4">
                                                        <div className="text-sm font-bold text-slate-900">{parking.carNumber}</div>
                                                        <div className="text-sm text-slate-500">#{parking.ticketNumber}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="text-sm font-medium text-slate-900">{parking.customerName}</div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-800">
                                                    {parking.parkingArea}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className={`px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${parking.status === 'PARKED' ? 'bg-blue-50 text-blue-700 border border-blue-100' :
                                                    parking.status === 'REQUESTED' ? 'bg-yellow-50 text-yellow-700 border border-yellow-100 animate-pulse' :
                                                        'bg-slate-100 text-slate-800'
                                                    }`}>
                                                    {parking.status}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="flex flex-col text-sm">
                                                    <div className="flex items-center text-slate-600">
                                                        <User className="w-3.5 h-3.5 mr-1.5 text-slate-400" />
                                                        <span className="text-xs text-slate-400">등록:</span>&nbsp;{parking.registeredBy}
                                                    </div>
                                                    {parking.exitRequestedBy && (
                                                        <div className="flex items-center text-yellow-600 mt-1">
                                                            <span className="text-xs">요청:</span>&nbsp;{parking.exitRequestedBy}
                                                        </div>
                                                    )}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm">
                                                {parking.exitAssignedTo ? (
                                                    <div className="flex items-center text-green-600">
                                                        <User className="w-3.5 h-3.5 mr-1.5" />
                                                        {parking.exitAssignedTo}
                                                    </div>
                                                ) : parking.status === 'REQUESTED' ? (
                                                    <span className="text-xs text-slate-400 italic">미배정</span>
                                                ) : (
                                                    <span className="text-xs text-slate-400">-</span>
                                                )}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">
                                                <div className="flex flex-col gap-1">
                                                    <div className="flex items-center gap-1">
                                                        <Clock className="w-3 h-3 text-blue-400" />
                                                        <span className="text-xs text-slate-400">입차:</span>
                                                        {new Date(parking.entryTime).toLocaleString([], { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })}
                                                    </div>
                                                    {parking.exitTime && (
                                                        <div className="flex items-center gap-1">
                                                            <Clock className="w-3 h-3 text-green-400" />
                                                            <span className="text-xs text-slate-400">출차:</span>
                                                            {new Date(parking.exitTime).toLocaleString([], { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })}
                                                        </div>
                                                    )}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                <div className="flex gap-2 justify-end">
                                                    {parking.status === 'PARKED' && (
                                                        <button
                                                            onClick={() => requestExit(parking.id)}
                                                            disabled={processingId === parking.id}
                                                            className="text-yellow-600 hover:text-yellow-900 bg-yellow-50 hover:bg-yellow-100 px-3 py-1.5 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                                        >
                                                            {processingId === parking.id ? 'Processing...' : '출차요청'}
                                                        </button>
                                                    )}
                                                    {parking.status === 'REQUESTED' && !parking.exitAssignedTo && (
                                                        <button
                                                            onClick={() => acceptExit(parking.id)}
                                                            disabled={processingId === parking.id}
                                                            className="text-indigo-600 hover:text-indigo-900 bg-indigo-50 hover:bg-indigo-100 px-3 py-1.5 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                                        >
                                                            {processingId === parking.id ? 'Processing...' : '수락'}
                                                        </button>
                                                    )}
                                                    {parking.status === 'REQUESTED' && parking.exitAssignedTo && (
                                                        <button
                                                            onClick={() => completeExit(parking.id)}
                                                            disabled={processingId === parking.id}
                                                            className="text-green-600 hover:text-green-900 bg-green-50 hover:bg-green-100 px-3 py-1.5 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                                        >
                                                            {processingId === parking.id ? 'Processing...' : '출차완료'}
                                                        </button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                    {filteredParkings.length === 0 && (
                                        <tr>
                                            <td colSpan="8" className="px-6 py-12 text-center text-slate-500">
                                                <Car className="mx-auto h-12 w-12 text-slate-300 mb-3" />
                                                <p>No parking records found matching your search.</p>
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </main>

            {/* Exit Notification Toast */}
            {exitNotification && (
                <div className="fixed bottom-6 right-6 z-50 bg-white rounded-2xl shadow-2xl border border-slate-200 p-5 max-w-sm animate-in fade-in slide-in-from-bottom-4 duration-300">
                    <div className="flex items-start gap-3">
                        <div className="p-2 bg-yellow-100 rounded-xl text-yellow-600 flex-shrink-0">
                            <Bell className="w-5 h-5" />
                        </div>
                        <div className="flex-1">
                            <p className="text-sm font-bold text-slate-900 mb-1">출차 요청</p>
                            <p className="text-sm text-slate-600 mb-3">
                                {exitNotification.requestedBy}님이 {exitNotification.carNumber} 출차를 요청했습니다
                            </p>
                            <div className="flex gap-2">
                                <button
                                    onClick={async () => {
                                        await acceptExit(exitNotification.parkingId);
                                        setExitNotification(null);
                                    }}
                                    className="flex-1 btn-primary text-sm py-2"
                                >
                                    출차 받기
                                </button>
                                <button
                                    onClick={() => setExitNotification(null)}
                                    className="px-3 py-2 text-sm text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
                                >
                                    닫기
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal */}
            {showRegisterModal && (
                <div className="fixed z-50 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
                    <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
                        <div className="fixed inset-0 bg-slate-900 bg-opacity-75 transition-opacity backdrop-blur-sm" aria-hidden="true" onClick={() => setShowRegisterModal(false)}></div>
                        <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
                        <div className="inline-block align-bottom bg-white rounded-2xl text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
                            <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                                <div className="flex justify-between items-center mb-5">
                                    <h3 className="text-lg leading-6 font-bold text-slate-900" id="modal-title">Register New Vehicle</h3>
                                    <button onClick={() => setShowRegisterModal(false)} className="text-slate-400 hover:text-slate-500">
                                        <X className="w-6 h-6" />
                                    </button>
                                </div>
                                <form onSubmit={handleRegister} className="space-y-4">
                                    <div className="relative">
                                        <label className="block text-sm font-medium text-slate-700 mb-1">Car Number</label>
                                        <input
                                            type="text"
                                            value={newTicket.carNumber}
                                            onChange={(e) => setNewTicket({ ...newTicket, carNumber: e.target.value })}
                                            className="input-field"
                                            placeholder="e.g. 12AB 3456"
                                            required
                                            autoComplete="off"
                                        />
                                        {showCustomerSelection === 'carNumber' && foundCustomers.length > 0 && (
                                            <div className="absolute z-[60] left-0 right-0 mt-1 bg-white rounded-xl shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-200">
                                                <div className="bg-slate-50 px-3 py-2 border-b border-slate-100">
                                                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Existing Customers Found</p>
                                                </div>
                                                <div className="max-h-48 overflow-y-auto">
                                                    {foundCustomers.map((c) => (
                                                        <button
                                                            key={c.id}
                                                            type="button"
                                                            onClick={() => selectCustomer(c)}
                                                            className="w-full flex justify-between items-center px-4 py-3 hover:bg-indigo-50 transition-colors border-b border-slate-50 last:border-0 group"
                                                        >
                                                            <div className="flex flex-col items-start">
                                                                <span className="text-sm font-bold text-slate-900 group-hover:text-indigo-600">{c.name}</span>
                                                                <span className="text-xs text-slate-500">{c.phoneNumber}</span>
                                                            </div>
                                                            <div className="bg-slate-100 group-hover:bg-indigo-100 p-1.5 rounded-lg transition-colors">
                                                                <Plus className="w-3.5 h-3.5 text-slate-400 group-hover:text-indigo-600" />
                                                            </div>
                                                        </button>
                                                    ))}
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div>
                                            <label className="block text-sm font-medium text-slate-700 mb-1">Customer Name</label>
                                            <input
                                                type="text"
                                                value={newTicket.customerName}
                                                onChange={(e) => setNewTicket({ ...newTicket, customerName: e.target.value })}
                                                className="input-field"
                                                placeholder="John Doe"
                                                required
                                            />
                                        </div>
                                        <div className="relative">
                                            <label className="block text-sm font-medium text-slate-700 mb-1">Phone Number</label>
                                            <input
                                                type="tel"
                                                value={newTicket.phoneNumber}
                                                onChange={(e) => setNewTicket({ ...newTicket, phoneNumber: e.target.value })}
                                                className="input-field"
                                                placeholder="010-1234-5678"
                                                required
                                                autoComplete="off"
                                            />
                                            {showCustomerSelection === 'phoneNumber' && foundCustomers.length > 0 && (
                                                <div className="absolute z-[60] left-0 right-0 mt-1 bg-white rounded-xl shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-200">
                                                    <div className="bg-slate-50 px-3 py-2 border-b border-slate-100">
                                                        <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Existing Customers Found</p>
                                                    </div>
                                                    <div className="max-h-48 overflow-y-auto">
                                                        {foundCustomers.map((c) => (
                                                            <button
                                                                key={c.id}
                                                                type="button"
                                                                onClick={() => selectCustomer(c)}
                                                                className="w-full flex justify-between items-center px-4 py-3 hover:bg-indigo-50 transition-colors border-b border-slate-50 last:border-0 group"
                                                            >
                                                                <div className="flex flex-col items-start">
                                                                    <span className="text-sm font-bold text-slate-900 group-hover:text-indigo-600">{c.name}</span>
                                                                    <span className="text-xs text-slate-500">{c.phoneNumber}</span>
                                                                </div>
                                                                <div className="bg-slate-100 group-hover:bg-indigo-100 p-1.5 rounded-lg transition-colors">
                                                                    <Plus className="w-3.5 h-3.5 text-slate-400 group-hover:text-indigo-600" />
                                                                </div>
                                                            </button>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-slate-700 mb-1">Parking Area</label>
                                        <input
                                            type="text"
                                            value={newTicket.parkingArea}
                                            onChange={(e) => setNewTicket({ ...newTicket, parkingArea: e.target.value })}
                                            className="input-field"
                                            placeholder="Zone A"
                                            required
                                        />
                                    </div>
                                    <div className="mt-5 sm:mt-6 flex gap-3">
                                        <button
                                            type="button"
                                            onClick={() => setShowRegisterModal(false)}
                                            className="w-full inline-flex justify-center rounded-xl border border-slate-300 shadow-sm px-4 py-3 bg-white text-base font-medium text-slate-700 hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:text-sm"
                                        >
                                            Cancel
                                        </button>
                                        <button
                                            type="submit"
                                            disabled={registering}
                                            className="w-full btn-primary py-3 disabled:opacity-50"
                                        >
                                            {registering ? 'Registering...' : 'Register Vehicle'}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
