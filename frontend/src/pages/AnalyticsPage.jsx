import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, BarChart3, Clock, Users, Car } from 'lucide-react';

export default function AnalyticsPage() {
    const [dailySummary, setDailySummary] = useState(null);
    const [avgDuration, setAvgDuration] = useState(null);
    const [staffPerformance, setStaffPerformance] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        Promise.all([
            api.get('/analytics/daily-summary'),
            api.get('/analytics/average-duration'),
            api.get('/analytics/staff-performance'),
        ]).then(([summary, duration, performance]) => {
            setDailySummary(summary.data);
            setAvgDuration(duration.data);
            setStaffPerformance(performance.data);
        }).catch(err => {
            console.error('Failed to fetch analytics', err);
        }).finally(() => setLoading(false));
    }, []);

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
                    <div className="flex items-center gap-3">
                        <BarChart3 className="w-8 h-8 text-indigo-600" />
                        <h1 className="text-3xl font-bold text-slate-900">Analytics</h1>
                    </div>
                </div>
            </div>

            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
                {/* Daily Summary */}
                {dailySummary && (
                    <div>
                        <h2 className="text-lg font-bold text-slate-900 mb-3">Today's Summary ({dailySummary.date})</h2>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                            <div className="card p-5 text-center">
                                <Car className="w-6 h-6 text-slate-400 mx-auto mb-2" />
                                <p className="text-2xl font-bold text-slate-900">{dailySummary.total}</p>
                                <p className="text-xs text-slate-500">Total</p>
                            </div>
                            <div className="card p-5 text-center">
                                <div className="w-6 h-6 bg-blue-100 rounded-full mx-auto mb-2" />
                                <p className="text-2xl font-bold text-blue-600">{dailySummary.parked}</p>
                                <p className="text-xs text-slate-500">Parked</p>
                            </div>
                            <div className="card p-5 text-center">
                                <div className="w-6 h-6 bg-yellow-100 rounded-full mx-auto mb-2" />
                                <p className="text-2xl font-bold text-yellow-600">{dailySummary.requested}</p>
                                <p className="text-xs text-slate-500">Requested</p>
                            </div>
                            <div className="card p-5 text-center">
                                <div className="w-6 h-6 bg-green-100 rounded-full mx-auto mb-2" />
                                <p className="text-2xl font-bold text-green-600">{dailySummary.exited}</p>
                                <p className="text-xs text-slate-500">Exited</p>
                            </div>
                        </div>
                    </div>
                )}

                {/* Average Duration */}
                {avgDuration && (
                    <div className="card p-6">
                        <div className="flex items-center gap-3 mb-2">
                            <Clock className="w-5 h-5 text-indigo-600" />
                            <h2 className="text-lg font-bold text-slate-900">Average Parking Duration</h2>
                        </div>
                        <p className="text-3xl font-bold text-indigo-600">{avgDuration.averageParkingMinutes} min</p>
                        <p className="text-sm text-slate-500">Based on {avgDuration.totalExited} completed exits</p>
                    </div>
                )}

                {/* Staff Performance */}
                {staffPerformance.length > 0 && (
                    <div>
                        <div className="flex items-center gap-3 mb-3">
                            <Users className="w-5 h-5 text-indigo-600" />
                            <h2 className="text-lg font-bold text-slate-900">Staff Performance</h2>
                        </div>
                        <div className="card overflow-hidden">
                            <table className="min-w-full divide-y divide-slate-200">
                                <thead className="bg-slate-50">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Staff</th>
                                        <th className="px-6 py-3 text-center text-xs font-semibold text-slate-500 uppercase">Registered</th>
                                        <th className="px-6 py-3 text-center text-xs font-semibold text-slate-500 uppercase">Exits Completed</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-200">
                                    {staffPerformance.map(s => (
                                        <tr key={s.staffName} className="hover:bg-slate-50">
                                            <td className="px-6 py-4 text-sm font-medium text-slate-900">{s.staffName}</td>
                                            <td className="px-6 py-4 text-sm text-center text-slate-600">{s.registered}</td>
                                            <td className="px-6 py-4 text-sm text-center text-slate-600">{s.exitsCompleted}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
