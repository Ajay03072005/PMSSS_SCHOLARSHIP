import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { financeApi } from '../../api/financeApi';
import StatCard from '../../components/common/StatCard';
import StatusBadge from '../../components/common/StatusBadge';
import { CreditCard, IndianRupee, Clock, CheckCircle2, AlertCircle, ArrowRight, ShieldCheck, Zap } from 'lucide-react';

export default function FinanceDashboard() {
  const [stats, setStats] = useState(null);
  const [recentPayments, setRecentPayments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchFinanceOverview();
  }, []);

  const fetchFinanceOverview = async () => {
    try {
      setLoading(true);
      const [dashRes, payRes] = await Promise.allSettled([
        financeApi.getDashboard(),
        financeApi.getPayments({ page: 0, size: 5 })
      ]);

      if (dashRes.status === 'fulfilled' && dashRes.value?.data) {
        setStats(dashRes.value.data);
      }
      if (payRes.status === 'fulfilled' && payRes.value?.data) {
        setRecentPayments(payRes.value.data.content || payRes.value.data || []);
      }
    } catch (err) {
      console.warn('Finance overview note:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="p-6 rounded-2xl bg-gradient-to-r from-emerald-950/40 via-slate-900 to-slate-900 border border-emerald-500/20 shadow-xl">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="text-xs font-semibold uppercase tracking-wider text-emerald-400">
                Finance & DBT Management
              </span>
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
              <span className="text-xs text-emerald-400">PFMS Bridge Active</span>
            </div>
            <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
              Scholarship Disbursement & DBT Console
            </h1>
            <p className="text-sm text-slate-300 mt-1">
              Process verified SAG Officer approved scholarships for DBT maintenance allowance and institutional fees.
            </p>
          </div>

          <Link
            to="/finance/payments"
            className="px-5 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs shadow-lg shadow-emerald-500/20 flex items-center gap-2 transition shrink-0"
          >
            <CreditCard className="w-4 h-4" />
            <span>Process Payment Queue</span>
          </Link>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Disbursed (Session)"
          value={`₹${(stats?.totalDisbursed || stats?.totalDisbursedAmount || 0).toLocaleString('en-IN')}`}
          subtitle="Direct Credit to Student & Institute"
          icon={IndianRupee}
          color="emerald"
        />
        <StatCard
          title="Pending Disbursement"
          value={`₹${(stats?.pendingAmount || 0).toLocaleString('en-IN')}`}
          subtitle={`${stats?.pendingCount || 0} Authorized Applications`}
          icon={Clock}
          color="amber"
        />
        <StatCard
          title="Success Rate"
          value={stats?.successRate || '—'}
          subtitle="DBT accuracy"
          icon={CheckCircle2}
          color="blue"
        />
        <StatCard
          title="PFMS Gateway"
          value="Operational"
          subtitle="Bank Webhook Synchronized"
          icon={ShieldCheck}
          color="purple"
        />
      </div>

      {/* Recent Disbursals */}
      <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
        <div className="flex items-center justify-between pb-3 border-b border-slate-800">
          <h2 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
            <CreditCard className="w-4 h-4 text-emerald-400" />
            Recent DBT Disbursements
          </h2>

          <Link to="/finance/payments" className="text-xs text-[#d32f2f] hover:underline flex items-center gap-1">
            <span>View All</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-xs text-left">
            <thead className="bg-slate-950/70 text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="py-3 px-4">Application ID</th>
                <th className="py-3 px-4">Beneficiary</th>
                <th className="py-3 px-4">Amount</th>
                <th className="py-3 px-4">Type</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4">UTR Number</th>
                <th className="py-3 px-4 text-right">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {recentPayments.length === 0 ? (
                <tr>
                  <td colSpan="7" className="py-6 text-center text-slate-500 text-xs">No recent disbursements found.</td>
                </tr>
              ) : recentPayments.map((p, idx) => (
                <tr key={idx} className="hover:bg-slate-800/40 transition">
                  <td className="py-3 px-4 font-mono font-bold text-white">{p.appId || p.applicationId}</td>
                  <td className="py-3 px-4 text-slate-300">{p.name || p.applicantName || 'Beneficiary'}</td>
                  <td className="py-3 px-4 font-bold text-emerald-400">₹{Number(p.amount).toLocaleString('en-IN')}</td>
                  <td className="py-3 px-4 text-slate-400">{(p.type || p.paymentType || '').replace(/_/g, ' ')}</td>
                  <td className="py-3 px-4"><StatusBadge status={p.status} /></td>
                  <td className="py-3 px-4 font-mono text-slate-400 text-[11px]">{p.utr || p.utrNumber || '—'}</td>
                  <td className="py-3 px-4 text-slate-400 text-right">{p.date || (p.createdAt ? new Date(p.createdAt).toLocaleDateString() : '—')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
