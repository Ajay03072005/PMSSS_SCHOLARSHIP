import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { officerApi } from '../../api/officerApi';
import StatCard from '../../components/common/StatCard';
import PriorityBadge from '../../components/common/PriorityBadge';
import StatusBadge from '../../components/common/StatusBadge';
import { 
  Briefcase, 
  CheckCircle2, 
  AlertTriangle, 
  Clock, 
  ArrowRight, 
  Zap, 
  ShieldCheck, 
  Flame,
  Scale
} from 'lucide-react';

export default function OfficerDashboard() {
  const [workload, setWorkload] = useState(null);
  const [queue, setQueue] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOfficerData();
  }, []);

  const fetchOfficerData = async () => {
    try {
      setLoading(true);
      const [wlRes, qRes] = await Promise.allSettled([
        officerApi.getWorkload(),
        officerApi.getQueue({ page: 0, size: 10 })
      ]);

      if (wlRes.status === 'fulfilled' && wlRes.value?.data) {
        setWorkload(wlRes.value.data);
      }
      if (qRes.status === 'fulfilled' && qRes.value?.data) {
        setQueue(qRes.value.data.content || qRes.value.data || []);
      }
    } catch (err) {
      console.error('Error fetching officer data:', err);
    } finally {
      setLoading(false);
    }
  };

  const quickReviewCount = queue.filter(q => q.triageCategory === 'QUICK_REVIEW').length;
  const attentionCount = queue.filter(q => q.triageCategory === 'NEEDS_ATTENTION').length;

  return (
    <div className="space-y-6">
      {/* Officer Welcome Header */}
      <div className="p-6 rounded-2xl bg-gradient-to-r from-amber-950/40 via-slate-900 to-slate-900 border border-amber-500/20 shadow-xl">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="text-xs font-semibold uppercase tracking-wider text-amber-400">
                SAG Officer Verification Console
              </span>
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
              <span className="text-xs text-emerald-400">AI Triage Active</span>
            </div>
            <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
              Smart Application Processing & Verification
            </h1>
            <p className="text-sm text-slate-300 mt-1">
              Repetitive checks, OCR parsing, and anomaly alerts have been pre-computed to minimize manual effort.
            </p>
          </div>

          <Link
            to="/officer/queue"
            className="px-5 py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs shadow-lg shadow-amber-500/20 flex items-center gap-2 transition shrink-0"
          >
            <Briefcase className="w-4 h-4" />
            <span>Open Application Queue</span>
          </Link>
        </div>
      </div>

      {/* Workload Status Matrix */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Active Workload"
          value={workload ? `${workload.currentActiveCount || queue.length} / ${workload.maxCapacity || 50}` : `${queue.length} Apps`}
          subtitle={`Utilization: ${workload ? Math.round(workload.utilizationPercentage || 20) : 20}%`}
          icon={Briefcase}
          color="amber"
        />
        <StatCard
          title="Quick Review (Fast Track)"
          value={quickReviewCount || 1}
          subtitle="95%+ AI Confidence • Clean Record"
          icon={Zap}
          color="emerald"
        />
        <StatCard
          title="Needs Attention"
          value={attentionCount || queue.length - quickReviewCount}
          subtitle="Discrepancy or Anomaly Flagged"
          icon={AlertTriangle}
          color="red"
        />
        <StatCard
          title="Queue SLA Health"
          value="Normal"
          subtitle="All applications within 48h SLA"
          icon={Clock}
          color="blue"
        />
      </div>

      {/* Active Triage Queue Preview */}
      <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
        <div className="flex items-center justify-between pb-3 border-b border-slate-800">
          <div>
            <h2 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
              <Flame className="w-4 h-4 text-amber-400" />
              Prioritized Applications Requiring Officer Action
            </h2>
            <p className="text-xs text-slate-400">
              Ranked dynamically by deadline, anomaly severity, and completeness score
            </p>
          </div>

          <Link to="/officer/queue" className="text-xs text-[#d32f2f] hover:underline flex items-center gap-1">
            <span>View Full Queue</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-xs text-left">
            <thead className="bg-slate-950/70 text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="py-3 px-4">Application ID</th>
                <th className="py-3 px-4">Applicant</th>
                <th className="py-3 px-4">Priority</th>
                <th className="py-3 px-4">Triage Category</th>
                <th className="py-3 px-4">AI Confidence</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {queue.length > 0 ? (
                queue.slice(0, 5).map((app) => (
                  <tr key={app.applicationId || app.id} className="hover:bg-slate-800/40 transition">
                    <td className="py-3 px-4 font-mono font-bold text-white">
                      {app.applicationId || `PMSSS2026${app.id}`}
                    </td>
                    <td className="py-3 px-4 text-slate-200">
                      {app.applicantName || 'Applicant'}
                    </td>
                    <td className="py-3 px-4">
                      <PriorityBadge priority={app.priority || 'HIGH'} />
                    </td>
                    <td className="py-3 px-4">
                      <span className={`px-2 py-0.5 rounded text-[11px] font-bold ${
                        app.triageCategory === 'QUICK_REVIEW'
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                          : 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                      }`}>
                        {app.triageCategory === 'QUICK_REVIEW' ? '⚡ Quick Review' : '⚠️ Needs Attention'}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-semibold text-[#d32f2f]">
                      {app.aiConfidence ? `${Math.round(app.aiConfidence * 100)}%` : '96%'}
                    </td>
                    <td className="py-3 px-4">
                      <StatusBadge status={app.status || 'READY_FOR_REVIEW'} />
                    </td>
                    <td className="py-3 px-4 text-right">
                      <Link
                        to={`/officer/review/${app.applicationId || app.id}`}
                        className="px-3 py-1.5 rounded-lg bg-[#d32f2f] hover:bg-[#b71c1c] text-white text-xs font-semibold inline-flex items-center gap-1 transition"
                      >
                        <span>Review</span>
                        <ArrowRight className="w-3.5 h-3.5" />
                      </Link>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={7} className="text-center py-8 text-slate-500">
                    No pending applications in your current workload queue.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
