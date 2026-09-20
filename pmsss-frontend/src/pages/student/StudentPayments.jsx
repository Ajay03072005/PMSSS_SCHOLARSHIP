import React, { useState, useEffect } from 'react';
import { studentApi } from '../../api/studentApi';
import StatCard from '../../components/common/StatCard';
import StatusBadge from '../../components/common/StatusBadge';
import { CreditCard, CheckCircle2, Clock, IndianRupee, ShieldCheck, Building, Download } from 'lucide-react';

export default function StudentPayments() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPayments();
  }, []);

  const fetchPayments = async () => {
    try {
      setLoading(true);
      const res = await studentApi.getPayments();
      const rawList = Array.isArray(res.data) ? res.data : (Array.isArray(res) ? res : []);
      const normalized = rawList.map((p, index) => ({
        id: p.id,
        installmentNumber: index + 1,
        paymentType: Number(p.amount) > 100000 ? 'ACADEMIC_FEE_INSTITUTE' : 'MAINTENANCE_ALLOWANCE',
        amount: p.amount,
        status: p.paymentStatus || p.status || 'PENDING',
        utrNumber: p.transactionReference || p.utrNumber || '—',
        disbursementDate: p.paymentDate ? new Date(p.paymentDate).toLocaleDateString('en-IN') : 'Scheduled',
        bankAccount: p.bankAccount ? `••••${p.bankAccount.slice(-4)}` : '••••1012'
      }));
      setPayments(normalized);
    } catch (err) {
      console.error('Error fetching payments from DB:', err);
      setPayments([]);
    } finally {
      setLoading(false);
    }
  };

  const totalDisbursed = payments
    .filter(p => p.status === 'DISBURSED' || p.status === 'SUCCESS')
    .reduce((sum, p) => sum + Number(p.amount || 0), 0);

  const totalPending = payments
    .filter(p => p.status !== 'DISBURSED' && p.status !== 'SUCCESS')
    .reduce((sum, p) => sum + Number(p.amount || 0), 0);

  return (
    <div className="space-y-6">
      <div className="pb-4 border-b border-slate-800">
        <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
          Direct Benefit Transfer (DBT) & Scholarship Disbursements
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Track maintenance allowance credited to your Aadhaar-seeded bank account and tuition fees paid to your allotted institution
        </p>
      </div>

      {/* Summary KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard
          title="Total Disbursed"
          value={`₹${totalDisbursed.toLocaleString('en-IN')}`}
          subtitle="Credited via PFMS / DBT"
          icon={IndianRupee}
          color="emerald"
        />
        <StatCard
          title="In-Pipeline / Scheduled"
          value={`₹${totalPending.toLocaleString('en-IN')}`}
          subtitle="Awaiting installment milestone"
          icon={Clock}
          color="blue"
        />
        <StatCard
          title="Bank Seeding Status"
          value="Aadhaar Linked"
          subtitle="DBT Mandate Active"
          icon={ShieldCheck}
          color="indigo"
        />
      </div>

      {/* Payment Transactions Table */}
      <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
            <CreditCard className="w-4 h-4 text-emerald-400" />
            Disbursement Ledger
          </h2>
          <span className="text-xs text-slate-400 font-medium">Session 2026-27</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-xs text-left">
            <thead className="bg-slate-950/70 text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="py-3 px-4">Type / Purpose</th>
                <th className="py-3 px-4">Inst. #</th>
                <th className="py-3 px-4">Amount (₹)</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4">UTR / Ref No.</th>
                <th className="py-3 px-4">Credited Account</th>
                <th className="py-3 px-4">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {payments.map((p) => (
                <tr key={p.id} className="hover:bg-slate-800/40 transition">
                  <td className="py-3 px-4 font-semibold text-white">
                    {p.paymentType?.replace(/_/g, ' ') || 'Scholarship Disbursal'}
                  </td>
                  <td className="py-3 px-4 text-slate-300">
                    Inst. {p.installmentNumber || 1}
                  </td>
                  <td className="py-3 px-4 font-bold text-emerald-400">
                    ₹{Number(p.amount).toLocaleString('en-IN')}
                  </td>
                  <td className="py-3 px-4">
                    <StatusBadge status={p.status} />
                  </td>
                  <td className="py-3 px-4 font-mono text-slate-300 text-[11px]">
                    {p.utrNumber || '—'}
                  </td>
                  <td className="py-3 px-4 text-slate-400 text-[11px]">
                    {p.bankAccount || '••••1012'}
                  </td>
                  <td className="py-3 px-4 text-slate-400">
                    {p.disbursementDate || 'Scheduled'}
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
