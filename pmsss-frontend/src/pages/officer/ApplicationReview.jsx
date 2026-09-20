import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { officerApi } from '../../api/officerApi';
import AiSummaryCard from '../../components/ai/AiSummaryCard';
import ComparisonTable from '../../components/ai/ComparisonTable';
import StatusBadge from '../../components/common/StatusBadge';
import PriorityBadge from '../../components/common/PriorityBadge';
import Modal from '../../components/common/Modal';
import { 
  ShieldCheck, 
  CheckCircle2, 
  XCircle, 
  RotateCcw, 
  AlertTriangle, 
  ArrowLeft, 
  FileText, 
  Eye, 
  Sparkles,
  Check,
  AlertCircle
} from 'lucide-react';

export default function ApplicationReview() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [summaryData, setSummaryData] = useState(null);
  const [activeActionModal, setActiveActionModal] = useState(null); // 'APPROVE', 'REJECT', 'CORRECTION', 'ESCALATE'
  const [remarks, setRemarks] = useState('');
  const [correctionField, setCorrectionField] = useState('INCOME_CERTIFICATE');
  const [actionLoading, setActionLoading] = useState(false);
  const [actionSuccess, setActionSuccess] = useState('');
  const [actionError, setActionError] = useState('');

  useEffect(() => {
    loadSummary();
  }, [id]);

  const loadSummary = async () => {
    try {
      setLoading(true);
      const res = await officerApi.getApplicationSummary(id);
      const data = res.data || res;
      setSummaryData(data);
    } catch (err) {
      console.error('Backend summary fetch error:', err);
      setSummaryData(null);
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async () => {
    setActionLoading(true);
    setActionError('');
    setActionSuccess('');

    try {
      if (activeActionModal === 'APPROVE') {
        await officerApi.approve(id, remarks || 'Application verified and approved by SAG Officer.');
        setActionSuccess('Application Approved successfully! Dispatched to Finance Queue.');
      } else if (activeActionModal === 'REJECT') {
        await officerApi.reject(id, remarks || 'Criteria not met.');
        setActionSuccess('Application Rejected.');
      } else if (activeActionModal === 'CORRECTION') {
        await officerApi.requestCorrection(id, {
          fieldOrDocument: correctionField,
          comments: remarks || 'Please re-upload a clear copy.'
        });
        setActionSuccess('Correction Request sent to Student.');
      } else if (activeActionModal === 'ESCALATE') {
        await officerApi.escalate(id, {
          reason: remarks || 'Discrepancy requires Admin/Director review.'
        });
        setActionSuccess('Application Escalated to Administrator.');
      }

      setTimeout(() => {
        setActiveActionModal(null);
        navigate('/officer/queue');
      }, 1500);
    } catch (err) {
      setActionError(err.response?.data?.message || 'Action failed. Please try again.');
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header with Navigation */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate('/officer/queue')}
            className="p-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-400 hover:text-white border border-slate-800 transition"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-xl font-bold text-white font-mono">{id}</h1>
              <StatusBadge status={summaryData?.status || 'READY_FOR_REVIEW'} />
              <PriorityBadge priority={summaryData?.priority || 'HIGH'} />
            </div>
            <p className="text-xs text-slate-400 mt-0.5">
              Applicant: <span className="text-slate-200 font-semibold">{summaryData?.applicantName || 'Applicant'}</span>
            </p>
          </div>
        </div>

        {/* Action Buttons Bar */}
        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={() => {
              setRemarks('Documents and pre-validation verified. Recommending for scholarship allotment.');
              setActiveActionModal('APPROVE');
            }}
            className="px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold shadow-lg shadow-emerald-500/20 flex items-center gap-1.5 transition"
          >
            <CheckCircle2 className="w-4 h-4" />
            <span>1-Click Approve</span>
          </button>

          <button
            onClick={() => {
              setRemarks('');
              setActiveActionModal('CORRECTION');
            }}
            className="px-3.5 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 text-xs font-bold shadow-lg shadow-amber-500/20 flex items-center gap-1.5 transition"
          >
            <RotateCcw className="w-4 h-4" />
            <span>Request Correction</span>
          </button>

          <button
            onClick={() => {
              setRemarks('');
              setActiveActionModal('ESCALATE');
            }}
            className="px-3.5 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-semibold flex items-center gap-1.5 transition"
          >
            <AlertTriangle className="w-4 h-4" />
            <span>Escalate</span>
          </button>

          <button
            onClick={() => {
              setRemarks('');
              setActiveActionModal('REJECT');
            }}
            className="px-3.5 py-2 rounded-xl bg-red-600/20 hover:bg-red-600 text-red-300 hover:text-white border border-red-500/30 text-xs font-semibold flex items-center gap-1.5 transition"
          >
            <XCircle className="w-4 h-4" />
            <span>Reject</span>
          </button>
        </div>
      </div>

      {/* AI Summary Card (Pre-Validation, Anomaly, Suggestions) */}
      <AiSummaryCard summary={summaryData} />

      {/* Side-by-Side OCR Comparison Table */}
      <ComparisonTable items={summaryData?.comparisonItems || summaryData?.comparisons} />

      {/* Attached Documents Quick Drawer */}
      <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
          <FileText className="w-4 h-4 text-[#d32f2f]" />
          Submitted Verification Documents
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          {(summaryData?.documents || []).map((doc, idx) => (
            <div key={idx} className="p-3 rounded-xl bg-slate-950/70 border border-slate-800 flex items-center justify-between">
              <div className="truncate mr-2">
                <p className="text-xs font-semibold text-white truncate">{doc.name}</p>
                <span className="text-[10px] text-slate-400">{doc.type}</span>
              </div>
              <span className="text-[10px] px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-semibold shrink-0">
                Verified
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* Action Modal */}
      {activeActionModal && (
        <Modal
          isOpen={true}
          title={
            activeActionModal === 'APPROVE' ? 'Confirm Scholarship Approval' :
            activeActionModal === 'REJECT' ? 'Confirm Application Rejection' :
            activeActionModal === 'CORRECTION' ? 'Request Candidate Correction' :
            'Escalate Application to Admin'
          }
          onClose={() => !actionLoading && setActiveActionModal(null)}
        >
          <div className="space-y-4 text-xs">
            {actionSuccess ? (
              <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center gap-2">
                <Check className="w-4 h-4" />
                <span>{actionSuccess}</span>
              </div>
            ) : (
              <>
                {actionError && (
                  <div className="p-3 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 flex items-center gap-2">
                    <AlertCircle className="w-4 h-4" />
                    <span>{actionError}</span>
                  </div>
                )}

                {activeActionModal === 'CORRECTION' && (
                  <div>
                    <label className="block font-medium text-slate-400 mb-1">Target Document / Field</label>
                    <select
                      value={correctionField}
                      onChange={(e) => setCorrectionField(e.target.value)}
                      className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs"
                    >
                      <option value="INCOME_CERTIFICATE">Income Certificate (Recent Tehsildar Copy)</option>
                      <option value="TWELFTH_MARKSHEET">Class 12th Marks Card</option>
                      <option value="DOMICILE_CERTIFICATE">Domicile Certificate</option>
                      <option value="BANK_PASSBOOK">Bank Passbook / IFSC</option>
                    </select>
                  </div>
                )}

                <div>
                  <label className="block font-medium text-slate-400 mb-1">
                    Officer Remarks & Justification
                  </label>
                  <textarea
                    rows={3}
                    value={remarks}
                    onChange={(e) => setRemarks(e.target.value)}
                    placeholder="Enter formal officer notes..."
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
                  />
                </div>

                <div className="flex justify-end gap-2 pt-2">
                  <button
                    type="button"
                    disabled={actionLoading}
                    onClick={() => setActiveActionModal(null)}
                    className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-medium"
                  >
                    Cancel
                  </button>
                  <button
                    type="button"
                    disabled={actionLoading}
                    onClick={handleAction}
                    className={`px-4 py-2 rounded-xl text-white text-xs font-bold flex items-center gap-1.5 ${
                      activeActionModal === 'APPROVE' ? 'bg-emerald-600 hover:bg-emerald-500' :
                      activeActionModal === 'REJECT' ? 'bg-red-600 hover:bg-red-500' :
                      activeActionModal === 'CORRECTION' ? 'bg-amber-600 hover:bg-amber-500' :
                      'bg-purple-600 hover:bg-purple-500'
                    }`}
                  >
                    {actionLoading && <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />}
                    <span>Confirm Decision</span>
                  </button>
                </div>
              </>
            )}
          </div>
        </Modal>
      )}
    </div>
  );
}
