import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Car, Clock, MapPin, CheckCircle, Loader2 } from 'lucide-react';
import api from '../api/axios';

export default function GuestExitPage() {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const [parking, setParking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [requesting, setRequesting] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!token) {
            setError('Invalid link');
            setLoading(false);
            return;
        }
        fetchStatus();
    }, [token]);

    const fetchStatus = async () => {
        try {
            const response = await api.get(`/guest/status?token=${token}`);
            setParking(response.data);
            setError(null);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to load parking status');
        } finally {
            setLoading(false);
        }
    };

    const handleRequestExit = async () => {
        setRequesting(true);
        try {
            const response = await api.post(`/guest/request-exit?token=${token}`);
            setParking(response.data);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to request exit');
        } finally {
            setRequesting(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-slate-50 flex items-center justify-center">
                <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
                <div className="bg-white rounded-2xl shadow-lg p-8 max-w-sm w-full text-center">
                    <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Car className="w-8 h-8 text-red-500" />
                    </div>
                    <h2 className="text-xl font-bold text-slate-900 mb-2">Oops!</h2>
                    <p className="text-slate-500">{error}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-b from-indigo-50 to-slate-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl shadow-lg p-8 max-w-sm w-full">
                {/* Header */}
                <div className="text-center mb-6">
                    <div className="w-16 h-16 bg-indigo-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Car className="w-8 h-8 text-indigo-600" />
                    </div>
                    <h1 className="text-2xl font-bold text-slate-900">Easy Valet</h1>
                    <p className="text-slate-500 text-sm mt-1">Parking Status</p>
                </div>

                {/* Car Info */}
                <div className="space-y-4 mb-6">
                    <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-xl">
                        <Car className="w-5 h-5 text-slate-400" />
                        <div>
                            <p className="text-xs text-slate-400">Vehicle</p>
                            <p className="font-semibold text-slate-900">{parking.maskedCarNumber}</p>
                        </div>
                    </div>
                    <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-xl">
                        <MapPin className="w-5 h-5 text-slate-400" />
                        <div>
                            <p className="text-xs text-slate-400">Location</p>
                            <p className="font-semibold text-slate-900">{parking.parkingArea}</p>
                        </div>
                    </div>
                    <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-xl">
                        <Clock className="w-5 h-5 text-slate-400" />
                        <div>
                            <p className="text-xs text-slate-400">Parked Since</p>
                            <p className="font-semibold text-slate-900">
                                {new Date(parking.entryTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </p>
                        </div>
                    </div>
                </div>

                {/* Status-specific UI */}
                {parking.status === 'PARKED' && (
                    <button
                        onClick={handleRequestExit}
                        disabled={requesting}
                        className="w-full bg-indigo-600 text-white py-4 rounded-xl font-semibold text-lg hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                    >
                        {requesting ? (
                            <>
                                <Loader2 className="w-5 h-5 animate-spin" />
                                Processing...
                            </>
                        ) : (
                            'Request Vehicle Exit'
                        )}
                    </button>
                )}

                {parking.status === 'REQUESTED' && (
                    <div className="text-center p-6 bg-yellow-50 rounded-xl border border-yellow-100">
                        <div className="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-3">
                            <Loader2 className="w-6 h-6 text-yellow-600 animate-spin" />
                        </div>
                        <h3 className="font-bold text-yellow-800 text-lg">Preparing Your Vehicle</h3>
                        <p className="text-yellow-600 text-sm mt-1">Our staff is bringing your car now</p>
                    </div>
                )}

                {parking.status === 'EXITED' && (
                    <div className="text-center p-6 bg-green-50 rounded-xl border border-green-100">
                        <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
                            <CheckCircle className="w-6 h-6 text-green-600" />
                        </div>
                        <h3 className="font-bold text-green-800 text-lg">Vehicle Ready!</h3>
                        <p className="text-green-600 text-sm mt-1">Your car is waiting for you</p>
                    </div>
                )}
            </div>
        </div>
    );
}
