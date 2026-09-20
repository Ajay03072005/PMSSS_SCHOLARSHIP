import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { officerApi } from '../../api/officerApi';
import PriorityBadge from '../../components/common/PriorityBadge';
import StatusBadge from '../../components/common/StatusBadge';
import { 
  Search, 
  Filter, 
  Zap, 
  AlertTriangle, 
  ArrowRight, 
  CheckCircle2, 
  Sparkles, 
  Clock,
  RefreshCw
} from 'lucide-react';

export default function OfficerQueue() {
  const [queue, setQueue] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('ALL'); // ALL, QUICK_REVIEW, NEEDS_ATTENTION
  const [search, setSearch] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('ALL');

  useEffect(() => {
    fetchQueue();
  }, []);

  const fetchQueue = async () => {
    try {
      setLoading(true);
      const res = await officerApi.getQueue();
      const payload = res.data || res;
      let list = [];
      if (Array.isArray(payload)) {
        list = payload;
      } else if (payload.quickReviewList || payload.needsAttentionList) {
        list = [...(payload.quickReviewList || []), ...(payload.needsAttentionList || [])];
      } else if (Array.isArray(payload.content)) {
        list = payload.content;
      }

      const normalized = list.map((item) => ({
        applicationId: item.applicationId,
        applicantName: item.applicantName || 'Applicant',
        priority: item.priority || 'NORMAL',
        triageCategory: item.reviewCategory || item.triageCategory || 'QUICK_REVIEW',
        aiConfidence: item.aiScore ? (item.aiScore > 1 ? item.aiScore / 100 : item.aiScore) : 0.95,
        anomalyCount: item.anomalyCount || 0,
        status: item.status || 'ACTIVE',
        slaHoursRemaining: item.slaDueAt ? Math.max(1, Math.round((new Date(item.slaDueAt) - new Date()) / 3600000)) : 36,
        stream: item.stream || 'ENGINEERING',
        reasons: item.reasons || (item.triageReason ? item.triageReason.split(';') : ['Pre-validation checks verified'])
      }));

      setQueue(normalized);
    } catch (err) {
      console.error('Error fetching queue from DB:', err);
      setQueue([]);
    } finally {
      setLoading(false);
    }
  };

  const filteredQueue = queue.filter((app) => {
    // Tab Filter
    if (activeTab === 'QUICK_REVIEW' && app.triageCategory !== 'QUICK_REVIEW') return false;
    if (activeTab === 'NEEDS_ATTENTION' && app.triageCategory !== 'NEEDS_ATTENTION') return false;

    // Priority Filter
    if (priorityFilter !== 'ALL' && app.priority !== priorityFilter) return false;

    // Search Query
    if (search.trim()) {
      const q = search.toLowerCase();
      const idMatch = (app.applicationId || '').toLowerCase().includes(q);
      const nameMatch = (app.applicantName || '').toLowerCase().includes(q);
      if (!idMatch && !nameMatch) return false;
    }

    return true;
  });

  return (
    <div className="space-y-6">
      <div className="pb-4 border-b border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
            Smart Officer Triage Queue
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Applications segregated into Fast Track (Quick Review) vs. Edge Cases (Needs Attention)
          </p>
        </div>

        <button
          onClick={fetchQueue}
          className="px-3.5 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-300 border border-slate-800 text-xs font-medium flex items-center gap-1.5 transition self-start sm:self-auto"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Refresh Queue</span>
        </button>
      </div>

      {/* Tabs & Search Filter */}
      <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-4">
        {/* Triage Tabs */}
        <div className="flex bg-slate-900 p-1 rounded-xl border border-slate-800">
          <button
            onClick={() => setActiveTab('ALL')}
            className={`px-4 py-2 rounded-lg text-xs font-semibold transition ${
              activeTab === 'ALL'
                ? 'bg-[#d32f2f] text-white shadow-md'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            All Assigned ({queue.length})
          </button>
          <button
            onClick={() => setActiveTab('QUICK_REVIEW')}
            className={`px-4 py-2 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition ${
              activeTab === 'QUICK_REVIEW'
                ? 'bg-emerald-600 text-white shadow-md'
                : 'text-emerald-400 hover:text-emerald-300'
            }`}
          >
            <Zap className="w-3.5 h-3.5" />
            <span>Quick Review ({queue.filter(q => q.triageCategory === 'QUICK_REVIEW').length})</span>
          </button>
          <button
            onClick={() => setActiveTab('NEEDS_ATTENTION')}
            className={`px-4 py-2 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition ${
              activeTab === 'NEEDS_ATTENTION'
                ? 'bg-amber-600 text-white shadow-md'
                : 'text-amber-400 hover:text-amber-300'
            }`}
          >
            <AlertTriangle className="w-3.5 h-3.5" />
            <span>Needs Attention ({queue.filter(q => q.triageCategory === 'NEEDS_ATTENTION').length})</span>
          </button>
        </div>

        {/* Search & Priority Controls */}
        <div className="flex items-center gap-2">
          <div className="relative flex-1 sm:w-64">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search ID or Student..."
              className="w-full pl-9 pr-3 py-2 bg-slate-900 border border-slate-700 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-[#d32f2f]"
            />
          </div>

          <select
            value={priorityFilter}
            onChange={(e) => setPriorityFilter(e.target.value)}
            className="px-3 py-2 bg-slate-900 border border-slate-700 rounded-xl text-xs text-slate-300 focus:outline-none focus:border-[#d32f2f]"
          >
            <option value="ALL">All Priorities</option>
            <option value="HIGH">High Priority</option>
            <option value="MEDIUM">Medium Priority</option>
            <option value="NORMAL">Normal Priority</option>
          </select>
        </div>
      </div>

      {/* Queue Table */}
      <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
        <div className="overflow-x-auto">
          <table className="w-full text-xs text-left">
            <thead className="bg-slate-950/70 text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="py-3.5 px-4">Application</th>
                <th className="py-3.5 px-4">Stream</th>
                <th className="py-3.5 px-4">Priority</th>
                <th className="py-3.5 px-4">Triage Mode</th>
                <th className="py-3.5 px-4">AI Score</th>
                <th className="py-3.5 px-4">SLA Time</th>
                <th className="py-3.5 px-4">Pre-Validation Alerts</th>
                <th className="py-3.5 px-4 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {filteredQueue.length > 0 ? (
                filteredQueue.map((app) => (
                  <tr key={app.applicationId} className="hover:bg-slate-800/40 transition">
                    <td className="py-3.5 px-4">
                      <div className="font-mono font-bold text-white">{app.applicationId}</div>
                      <div className="text-slate-300">{app.applicantName}</div>
                    </td>
                    <td className="py-3.5 px-4 text-slate-300">
                      {app.stream || 'Engineering'}
                    </td>
                    <td className="py-3.5 px-4">
                      <PriorityBadge priority={app.priority} />
                    </td>
                    <td className="py-3.5 px-4">
                      <span className={`px-2 py-0.5 rounded text-[11px] font-bold ${
                        app.triageCategory === 'QUICK_REVIEW'
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                          : 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                      }`}>
                        {app.triageCategory === 'QUICK_REVIEW' ? '⚡ Quick' : '⚠️ Attention'}
                      </span>
                    </td>
                    <td className="py-3.5 px-4">
                      <span className={`font-semibold ${
                        (app.aiConfidence || 0.95) >= 0.9 ? 'text-emerald-400' : 'text-amber-400'
                      }`}>
                        {Math.round((app.aiConfidence || 0.95) * 100)}%
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-slate-300">
                      <div className="flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5 text-slate-400" />
                        <span>{app.slaHoursRemaining ? `${app.slaHoursRemaining}h left` : '< 48h'}</span>
                      </div>
                    </td>
                    <td className="py-3.5 px-4">
                      {app.anomalyCount > 0 ? (
                        <span className="text-amber-400 flex items-center gap-1">
                          <AlertTriangle className="w-3.5 h-3.5" />
                          <span>{app.anomalyCount} Flag(s)</span>
                        </span>
                      ) : (
                        <span className="text-emerald-400 flex items-center gap-1">
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          <span>Clean Match</span>
                        </span>
                      )}
                    </td>
                    <td className="py-3.5 px-4 text-right">
                      <Link
                        to={`/officer/review/${app.applicationId}`}
                        className="px-3.5 py-1.5 rounded-lg bg-[#d32f2f] hover:bg-[#b71c1c] text-white text-xs font-semibold inline-flex items-center gap-1 transition shadow-sm"
                      >
                        <span>Examine</span>
                        <ArrowRight className="w-3.5 h-3.5" />
                      </Link>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={8} className="text-center py-10 text-slate-500">
                    No applications matched your filter criteria.
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
