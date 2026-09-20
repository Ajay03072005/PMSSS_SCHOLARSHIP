import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { adminApi } from '../../api/adminApi';
import StatCard from '../../components/common/StatCard';
import StatusBadge from '../../components/common/StatusBadge';
import { 
  Users, 
  FileText, 
  Scale, 
  AlertTriangle, 
  Copy, 
  BarChart3, 
  ShieldCheck, 
  ArrowRight, 
  Sparkles,
  Zap,
  RotateCcw
} from 'lucide-react';

export default function AdminDashboard() {
  const [metrics, setMetrics] = useState(null);
  const [workloads, setWorkloads] = useState([]);
  const [escalations, setEscalations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAdminOverview();
  }, []);

  const fetchAdminOverview = async () => {
    try {
      setLoading(true);
      const [mRes, wRes, eRes] = await Promise.allSettled([
        adminApi.getProcessingMetrics(),
        adminApi.getWorkload(),
        adminApi.getEscalations()
      ]);

      if (mRes.status === 'fulfilled' && mRes.value?.data) {
        setMetrics(mRes.value.data);
      }
      if (wRes.status === 'fulfilled' && wRes.value?.data) {
        setWorkloads(wRes.value.data);
      }
      if (eRes.status === 'fulfilled' && eRes.value?.data) {
        setEscalations(eRes.value.data);
      }
    } catch (err) {
      console.warn('Admin overview fetch note:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="p-6 rounded-2xl bg-gradient-to-r from-purple-950/40 via-slate-900 to-slate-900 border border-purple-500/20 shadow-xl">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="text-xs font-semibold uppercase tracking-wider text-purple-400">
                PMSSS 2.0 Command Center
              </span>
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
              <span className="text-xs text-emerald-400">All Pipelines Operational</span>
            </div>
            <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
              System Administration & Pipeline Health
            </h1>
            <p className="text-sm text-slate-300 mt-1">
              Oversee automated pre-validation, AI triage accuracy, SAG officer workloads, and fraud anomalies.
            </p>
          </div>

          <div className="flex items-center gap-2">
            <Link
              to="/admin/workload"
              className="px-4 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs shadow-lg shadow-purple-500/20 flex items-center gap-2 transition"
            >
              <Scale className="w-4 h-4" />
              <span>Workload Balancer</span>
            </Link>
          </div>
        </div>
      </div>

      {/* KPI Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Applications"
          value={metrics?.totalApplications || 0}
          subtitle="Session 2026-27"
          icon={FileText}
          color="blue"
        />
        <StatCard
          title="Avg Review Time"
          value={metrics?.avgOfficerReviewHours ? `${metrics.avgOfficerReviewHours} Hours` : '0 Hours'}
          subtitle="AI-assisted triage pipeline"
          icon={Zap}
          color="emerald"
        />
        <StatCard
          title="Active SAG Officers"
          value={workloads.length}
          subtitle="Round-robin & capacity balancing"
          icon={Users}
          color="purple"
        />
        <StatCard
          title="Escalations / Anomalies"
          value={escalations.length}
          subtitle="Requires supervisory review"
          icon={AlertTriangle}
          color="amber"
        />
      </div>

      {/* Quick Navigation Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Link
          to="/admin/workload"
          className="p-4 rounded-xl bg-slate-900 border border-slate-800 hover:border-purple-500/40 transition group"
        >
          <div className="flex items-center gap-3 mb-2">
            <div className="p-2 rounded-lg bg-purple-500/10 text-purple-400 group-hover:bg-purple-500/20">
              <Scale className="w-5 h-5" />
            </div>
            <h3 className="text-xs font-bold text-white">Workload Balancer</h3>
          </div>
          <p className="text-[11px] text-slate-400">1-click rebalance overloaded officer queues</p>
        </Link>

        <Link
          to="/admin/duplicates"
          className="p-4 rounded-xl bg-slate-900 border border-slate-800 hover:border-[#d32f2f]/40 transition group"
        >
          <div className="flex items-center gap-3 mb-2">
            <div className="p-2 rounded-lg bg-[#d32f2f]/10 text-[#d32f2f] group-hover:bg-[#d32f2f]/20">
              <Copy className="w-5 h-5" />
            </div>
            <h3 className="text-xs font-bold text-white">Duplicate Review</h3>
          </div>
          <p className="text-[11px] text-slate-400">Aadhaar, marks card, and name collision resolver</p>
        </Link>

        <Link
          to="/admin/anomalies"
          className="p-4 rounded-xl bg-slate-900 border border-slate-800 hover:border-amber-500/40 transition group"
        >
          <div className="flex items-center gap-3 mb-2">
            <div className="p-2 rounded-lg bg-amber-500/10 text-amber-400 group-hover:bg-amber-500/20">
              <AlertTriangle className="w-5 h-5" />
            </div>
            <h3 className="text-xs font-bold text-white">Anomaly Center</h3>
          </div>
          <p className="text-[11px] text-slate-400">Income ceiling outliers & document discrepancies</p>
        </Link>

        <Link
          to="/admin/metrics"
          className="p-4 rounded-xl bg-slate-900 border border-slate-800 hover:border-emerald-500/40 transition group"
        >
          <div className="flex items-center gap-3 mb-2">
            <div className="p-2 rounded-lg bg-emerald-500/10 text-emerald-400 group-hover:bg-emerald-500/20">
              <BarChart3 className="w-5 h-5" />
            </div>
            <h3 className="text-xs font-bold text-white">Processing Analytics</h3>
          </div>
          <p className="text-[11px] text-slate-400">Throughput, SLA compliance, and bottlenecks</p>
        </Link>
      </div>

      {/* Officer Workload Status Preview */}
      <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
        <div className="flex items-center justify-between pb-3 border-b border-slate-800">
          <div>
            <h2 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
              <Scale className="w-4 h-4 text-purple-400" />
              Officer Capacity & Utilization Status
            </h2>
            <p className="text-xs text-slate-400">Monitors active assignment counts to avoid bottlenecks</p>
          </div>

          <Link to="/admin/workload" className="text-xs text-[#d32f2f] hover:underline flex items-center gap-1">
            <span>Manage Capacity</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {workloads.length === 0 ? (
            <div className="p-4 rounded-xl bg-slate-950/70 border border-slate-800 text-center text-xs text-slate-400 col-span-2">
              No officer workload data available.
            </div>
          ) : workloads.map((w, idx) => {
            const utilization = w.utilization || w.utilizationPercentage || 0;
            return (
              <div key={idx} className="p-4 rounded-xl bg-slate-950/70 border border-slate-800 space-y-2">
                <div className="flex items-center justify-between">
                  <div>
                    <h4 className="text-xs font-bold text-white">{w.officerName || w.user?.fullName || w.email}</h4>
                    <span className="text-[11px] text-slate-400">{w.email || w.user?.email}</span>
                  </div>
                  <span className="text-xs font-mono font-bold text-purple-400">
                    {w.active || w.currentActiveCount || 0} / {w.capacity || w.maxCapacity || 50}
                  </span>
                </div>

                {/* Progress Bar */}
                <div className="w-full h-2 bg-slate-800 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-[#d32f2f] rounded-full transition-all"
                    style={{ width: `${Math.min(utilization, 100)}%` }}
                  />
                </div>

                <div className="flex justify-between text-[11px] text-slate-400">
                  <span>Utilization: {Math.round(utilization)}%</span>
                  <span className={`font-medium ${utilization > 75 ? 'text-amber-400' : 'text-emerald-400'}`}>
                    {utilization > 75 ? 'High Load' : 'Capacity Normal'}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
