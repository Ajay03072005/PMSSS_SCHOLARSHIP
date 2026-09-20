import React, { useState, useEffect } from 'react';
import { financeApi } from '../../api/financeApi';
import StatusBadge from '../../components/common/StatusBadge';
import Modal from '../../components/common/Modal';
import { CreditCard, CheckCircle2, AlertCircle, RefreshCw, Send, ShieldCheck, IndianRupee } from 'lucide-react';

export default function PaymentProcessing() {
  const [payments, setPayments] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeModal, setActiveModal] = useState(null);
  const [utrNumber, setUtrNumber] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    fetchPayments();
  }, []);

  const fetchPayments = async () => {
    try {
      setLoading(true);
      const res = await financeApi.getPayments();
      let list = res.data?.content || res.data || [];
      if (!Array.isArray(list) || list.length === 0) {
        list = [
          { id: 101, applicationId: 'PMSSS2026000001', applicantName: 'Aarav Sharma', amount: 50000, paymentType: 'MAINTENANCE_ALLOWANCE', status: 'INITIATED', bankAccount: '••••••••1012', ifsc: 'SBIN0001234' },
          { id: 102, applicationId: 'PMSSS2026000003', applicantName: 'Rohit Verma', amount: 125000, paymentType: 'ACADEMIC_FEE', status: 'INITIATED', bankAccount: '••••••••8912', ifsc: 'JAKA0MAIN01' },
          { id: 103, applicationId: 'PMSSS2026000004', applicantName: 'Iqra Mir', amount: 50000, paymentType: 'MAINTENANCE_ALLOWANCE', status: 'PENDING_APPROVAL', bankAccount: '••••••••7721', ifsc: 'SBIN0004567' }
        ];
      }
      setPayments(list);
    } catch (err) {
      console.warn('Failed to load payments:', err);
    } finally {
      setLoading(false);
    }
  };

  const toggleSelect = (id) => {
    setSelectedIds(prev => 
      prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
    );
  };

  const handleDisburseSingle = async () => {
    if (!activeModal) return;
    setActionLoading(true);
    try {
      await financeApi.disbursePayment(activeModal.id, {
        utrNumber: utrNumber || `DBT${Date.now().toString().slice(-8)}`
      });
      setMessage({ type: 'success', text: `Payment of ₹${activeModal.amount} disbursed successfully!` });
      setActiveModal(null);
      setUtrNumber('');
      fetchPayments();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Disbursement failed.' });
    } finally {
      setActionLoading(false);
    }
  };

  const handleBatchDisburse = async () => {
    if (selectedIds.length === 0) return;
    setActionLoading(true);
    try {
      await financeApi.batchDisburse(selectedIds);
      setMessage({ type: 'success', text: `Batch disbursement initiated for ${selectedIds.length} payments!` });
      setSelectedIds([]);
      fetchPayments();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Batch disbursement failed.' });
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="pb-4 border-b border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
            DBT Payment Authorization & Settlement
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Authorize electronic funds transfer for SAG Officer approved applicants via PFMS / NPCI
          </p>
        </div>

        <div className="flex items-center gap-2">
          {selectedIds.length > 0 && (
            <button
              onClick={handleBatchDisburse}
              disabled={actionLoading}
              className="px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold flex items-center gap-2 shadow-lg shadow-emerald-500/20"
            >
              <Send className="w-4 h-4" />
              <span>Batch Disburse ({selectedIds.length})</span>
            </button>
          )}

          <button
            onClick={fetchPayments}
            className="px-3.5 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-300 border border-slate-800 text-xs font-medium flex items-center gap-1.5"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Refresh</span>
          </button>
        </div>
      </div>

      {message && (
        <div className={`p-4 rounded-xl text-xs flex items-center justify-between border ${
          message.type === 'error'
            ? 'bg-red-500/10 border-red-500/20 text-red-400'
            : 'bg-emerald-500/10 border-emerald-500/20 text-emerald-400'
        }`}>
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4" />
            <span>{message.text}</span>
          </div>
          <button onClick={() => setMessage(null)} className="underline ml-4">Dismiss</button>
        </div>
      )}

      {/* Payment List Table */}
      <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
        <div className="overflow-x-auto">
          <table className="w-full text-xs text-left">
            <thead className="bg-slate-950/70 text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="py-3 px-3">
                  <input
                    type="checkbox"
                    checked={selectedIds.length === payments.length && payments.length > 0}
                    onChange={(e) => {
                      if (e.target.checked) setSelectedIds(payments.map(p => p.id));
                      else setSelectedIds([]);
                    }}
                    className="rounded text-[#d32f2f]"
                  />
                </th>
                <th className="py-3 px-4">Application ID</th>
                <th className="py-3 px-4">Beneficiary</th>
                <th className="py-3 px-4">Type</th>
                <th className="py-3 px-4">Amount</th>
                <th className="py-3 px-4">Bank Details</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {payments.map((p) => (
                <tr key={p.id} className="hover:bg-slate-800/40 transition">
                  <td className="py-3 px-3">
                    <input
                      type="checkbox"
                      checked={selectedIds.includes(p.id)}
                      onChange={() => toggleSelect(p.id)}
                      className="rounded text-[#d32f2f]"
                    />
                  </td>
                  <td className="py-3 px-4 font-mono font-bold text-white">{p.applicationId}</td>
                  <td className="py-3 px-4 text-slate-300">{p.applicantName}</td>
                  <td className="py-3 px-4 text-slate-400">{p.paymentType?.replace(/_/g, ' ')}</td>
                  <td className="py-3 px-4 font-bold text-emerald-400">₹{Number(p.amount).toLocaleString('en-IN')}</td>
                  <td className="py-3 px-4 text-slate-400 text-[11px]">
                    <div>{p.bankAccount}</div>
                    <span className="text-slate-500">{p.ifsc}</span>
                  </td>
                  <td className="py-3 px-4"><StatusBadge status={p.status} /></td>
                  <td className="py-3 px-4 text-right">
                    <button
                      onClick={() => {
                        setActiveModal(p);
                        setUtrNumber(`DBT${Date.now().toString().slice(-8)}`);
                      }}
                      className="px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold transition shadow-sm"
                    >
                      Disburse
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Disburse Modal */}
      {activeModal && (
        <Modal
          isOpen={true}
          title="Authorize Direct Benefit Transfer (DBT)"
          onClose={() => setActiveModal(null)}
        >
          <div className="space-y-4 text-xs">
            <div className="p-3 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
              <div className="flex justify-between">
                <span className="text-slate-400">Beneficiary:</span>
                <span className="text-white font-semibold">{activeModal.applicantName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Application:</span>
                <span className="text-white font-mono">{activeModal.applicationId}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Amount:</span>
                <span className="text-emerald-400 font-bold">₹{Number(activeModal.amount).toLocaleString('en-IN')}</span>
              </div>
            </div>

            <div>
              <label className="block text-slate-400 font-medium mb-1">Electronic UTR Reference Number</label>
              <input
                type="text"
                value={utrNumber}
                onChange={(e) => setUtrNumber(e.target.value)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white font-mono text-xs focus:border-[#d32f2f]"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setActiveModal(null)}
                className="px-4 py-2 rounded-xl bg-slate-800 text-slate-300 text-xs font-medium"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={actionLoading}
                onClick={handleDisburseSingle}
                className="px-5 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold flex items-center gap-1.5"
              >
                {actionLoading && <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />}
                <span>Authorize Transfer</span>
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
