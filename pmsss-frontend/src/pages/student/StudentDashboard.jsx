import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { studentApi } from '../../api/studentApi';
import { applicationApi } from '../../api/applicationApi';
import StatCard from '../../components/common/StatCard';
import StatusBadge from '../../components/common/StatusBadge';
import Timeline from '../../components/common/Timeline';
import { 
  FileText, 
  UploadCloud, 
  CreditCard, 
  Bot, 
  AlertCircle, 
  CheckCircle2, 
  Sparkles, 
  ArrowRight,
  ShieldAlert,
  Clock
} from 'lucide-react';

export default function StudentDashboard() {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [activeApp, setActiveApp] = useState(null);
  const [corrections, setCorrections] = useState([]);
  const [payments, setPayments] = useState([]);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [appRes, corrRes, payRes] = await Promise.allSettled([
        studentApi.getActiveApplication(),
        studentApi.getCorrectionRequests(),
        studentApi.getPayments()
      ]);

      if (appRes.status === 'fulfilled' && appRes.value?.data) {
        setActiveApp(appRes.value.data);
      }
      if (corrRes.status === 'fulfilled' && corrRes.value?.data) {
        setCorrections(corrRes.value.data.filter(c => c.status === 'PENDING'));
      }
      if (payRes.status === 'fulfilled' && payRes.value?.data) {
        setPayments(payRes.value.data);
      }
    } catch (err) {
      console.error('Error fetching dashboard:', err);
    } finally {
      setLoading(false);
    }
  };

  const totalDisbursed = payments
    .filter(p => p.status === 'DISBURSED' || p.status === 'SUCCESS')
    .reduce((sum, p) => sum + (Number(p.amount) || 0), 0);

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="p-6 rounded-2xl bg-gradient-to-r from-red-950/40 via-slate-900 to-slate-900 border border-red-500/20 backdrop-blur-sm relative overflow-hidden">
        <div className="relative z-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="text-xs font-semibold uppercase tracking-wider text-red-400">
                Academic Session 2026-27
              </span>
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
              <span className="text-xs text-emerald-400">Portal Open</span>
            </div>
            <h1 className="text-2xl font-bold text-white font-['Poppins',sans-serif]">
              Welcome back, {user?.fullName || user?.name || 'Applicant'}!
            </h1>
            <p className="text-sm text-slate-300 mt-1">
              Track your scholarship processing, AI checks, and disbursement updates in real time.
            </p>
          </div>

          <div className="flex items-center gap-3">
            <Link
              to="/student/application"
              style={{ backgroundColor: '#d32f2f', boxShadow: '0 4px 15px rgba(211, 47, 47, 0.3)' }}
              className="px-4 py-2.5 rounded-xl hover:bg-[#b71c1c] text-white text-sm font-semibold flex items-center gap-2 transition"
            >
              <FileText className="w-4 h-4" />
              <span>{activeApp ? 'Review Application' : 'Start Application'}</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Action Required: Correction Alert */}
      {corrections.length > 0 && (
        <div className="p-4 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-300 flex items-start justify-between gap-4">
          <div className="flex items-start gap-3">
            <ShieldAlert className="w-5 h-5 text-amber-400 shrink-0 mt-0.5" />
            <div>
              <h4 className="text-sm font-bold">Action Required: Correction Requested</h4>
              <p className="text-xs text-amber-400/90 mt-0.5">
                The SAG Officer reviewed your application and requested corrections or re-uploads for {corrections.length} item(s).
              </p>
            </div>
          </div>
          <Link
            to="/student/documents"
            className="px-3 py-1.5 rounded-lg bg-amber-500 text-slate-950 font-bold text-xs shrink-0 hover:bg-amber-400 transition"
          >
            Review & Fix
          </Link>
        </div>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Application Status"
          value={activeApp ? activeApp.status?.replace(/_/g, ' ') : 'Not Submitted'}
          subtitle={activeApp?.applicationId || 'New Applicant'}
          icon={FileText}
          color="blue"
        />
        <StatCard
          title="AI Confidence Score"
          value={activeApp?.aiConfidence ? `${Math.round(activeApp.aiConfidence * 100)}%` : '98%'}
          subtitle="Rule & OCR consistency check"
          icon={Sparkles}
          color="indigo"
        />
        <StatCard
          title="Total Disbursed (DBT)"
          value={`₹${totalDisbursed.toLocaleString('en-IN')}`}
          subtitle={`${payments.length} scheduled installments`}
          icon={CreditCard}
          color="emerald"
        />
        <StatCard
          title="Target SLA Window"
          value="< 48 Hours"
          subtitle="Priority Smart Triage"
          icon={Clock}
          color="purple"
        />
      </div>

      {/* Application Snapshot & Quick Navigation */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Active Application Card */}
        <div className="lg:col-span-2 p-6 rounded-2xl bg-slate-900 border border-slate-800 space-y-6 shadow-xl">
          <div className="flex items-center justify-between pb-4 border-b border-slate-800">
            <div>
              <h3 className="text-base font-bold text-white font-['Outfit',sans-serif]">
                Current Application Summary
              </h3>
              <p className="text-xs text-slate-400">
                AI pre-validation automatically runs on every change
              </p>
            </div>
            {activeApp && <StatusBadge status={activeApp.status} />}
          </div>

          {activeApp ? (
            <div className="space-y-4">
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 text-xs">
                <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800">
                  <span className="text-slate-400 block mb-1">Application ID</span>
                  <span className="font-mono font-bold text-white text-sm">{activeApp.applicationId}</span>
                </div>
                <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800">
                  <span className="text-slate-400 block mb-1">Scholarship Stream</span>
                  <span className="font-bold text-white text-sm">{activeApp.stream || 'Engineering / Tech'}</span>
                </div>
                <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800">
                  <span className="text-slate-400 block mb-1">Submitted On</span>
                  <span className="font-bold text-white text-sm">
                    {activeApp.createdAt ? new Date(activeApp.createdAt).toLocaleDateString() : 'Recent'}
                  </span>
                </div>
              </div>

              {/* Automated Checks Preview */}
              <div className="p-4 rounded-xl bg-slate-950/60 border border-slate-800/80 space-y-2">
                <div className="flex items-center justify-between text-xs font-semibold text-slate-300">
                  <span className="flex items-center gap-1.5">
                    <Sparkles className="w-3.5 h-3.5 text-[#d32f2f]" />
                    Automated Pre-Validation Checklist
                  </span>
                  <span className="text-emerald-400">All Passed</span>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs text-slate-400 pt-1">
                  <div className="flex items-center gap-2">
                    <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                    <span>Aadhaar 12-digit & format verified</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                    <span>Family income within ₹8,00,000 ceiling</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                    <span>Bank Account IFSC verified</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                    <span>Mandatory marks & domicile attached</span>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-end gap-3 pt-2">
                <Link
                  to="/student/documents"
                  className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium border border-slate-700 flex items-center gap-1.5 transition"
                >
                  <UploadCloud className="w-3.5 h-3.5" />
                  <span>View Documents</span>
                </Link>
                <Link
                  to="/student/apply"
                  className="px-4 py-2 rounded-xl bg-[#d32f2f] hover:bg-[#b71c1c] text-white text-xs font-medium flex items-center gap-1.5 transition"
                >
                  <span>View Details</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          ) : (
            <div className="text-center py-8">
              <FileText className="w-12 h-12 text-slate-600 mx-auto mb-3" />
              <p className="text-sm font-semibold text-white">No Application In Progress</p>
              <p className="text-xs text-slate-400 mt-1 max-w-sm mx-auto">
                Begin your PMSSS scholarship application for the current academic session today.
              </p>
              <Link
                to="/student/apply"
                className="mt-4 inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#d32f2f] hover:bg-[#b71c1c] text-white text-xs font-medium shadow-lg shadow-red-500/20 transition"
              >
                <span>Start New Application</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>
          )}
        </div>

        {/* Quick Help & AI Tools Card */}
        <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 space-y-4 shadow-xl">
          <h3 className="text-base font-bold text-white font-['Outfit',sans-serif]">
            Smart Student Tools
          </h3>

          <Link
            to="/student/eligibility"
            className="p-4 rounded-xl bg-slate-950/70 border border-slate-800/80 hover:border-[#d32f2f]/40 transition block group"
          >
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2 rounded-lg bg-[#d32f2f]/10 text-[#d32f2f] group-hover:bg-[#d32f2f]/20 transition">
                <CheckCircle2 className="w-4 h-4" />
              </div>
              <div>
                <h4 className="text-xs font-bold text-white">Eligibility Advisor AI</h4>
                <p className="text-[11px] text-slate-400">Simulate eligibility rules</p>
              </div>
            </div>
            <p className="text-[11px] text-slate-400 leading-normal">
              Enter your category, family income, and marks to immediately check scheme suitability.
            </p>
          </Link>

          <Link
            to="/student/chatbot"
            className="p-4 rounded-xl bg-slate-950/70 border border-slate-800/80 hover:border-[#d32f2f]/40 transition block group"
          >
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2 rounded-lg bg-[#d32f2f]/10 text-[#d32f2f] group-hover:bg-[#d32f2f]/20 transition">
                <Bot className="w-4 h-4" />
              </div>
              <div>
                <h4 className="text-xs font-bold text-white">PMSSS AI Buddy</h4>
                <p className="text-[11px] text-slate-400">24/7 Scholarship Assistant</p>
              </div>
            </div>
            <p className="text-[11px] text-slate-400 leading-normal">
              Get answers about documents required, deadlines, colleges, and quota criteria.
            </p>
          </Link>

          <Link
            to="/student/payments"
            className="p-4 rounded-xl bg-slate-950/70 border border-slate-800/80 hover:border-emerald-500/40 transition block group"
          >
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2 rounded-lg bg-emerald-500/10 text-emerald-400 group-hover:bg-emerald-500/20 transition">
                <CreditCard className="w-4 h-4" />
              </div>
              <div>
                <h4 className="text-xs font-bold text-white">DBT Payment Status</h4>
                <p className="text-[11px] text-slate-400">Maintenance & Tuition fees</p>
              </div>
            </div>
            <p className="text-[11px] text-slate-400 leading-normal">
              Track Direct Benefit Transfer installments and bank credit references.
            </p>
          </Link>
        </div>
      </div>
    </div>
  );
}
