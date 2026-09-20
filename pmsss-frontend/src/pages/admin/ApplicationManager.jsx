import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { adminApi } from '../../api/adminApi';
import StatusBadge from '../../components/common/StatusBadge';
import PriorityBadge from '../../components/common/PriorityBadge';
import { Search, Filter, RefreshCw, ArrowRight, FileText, Download } from 'lucide-react';

export default function ApplicationManager() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      setLoading(true);
      const res = await adminApi.getApplications({ page: 0, size: 50 });
      let list = res.data?.content || res.data || [];
      if (!Array.isArray(list)) {
        list = [];
      }
      setApplications(list);
    } catch (err) {
      console.warn('Failed to fetch admin applications:', err);
    } finally {
      setLoading(false);
    }
  };

  const filtered = applications.filter((app) => {
    if (statusFilter !== 'ALL' && app.status !== statusFilter) return false;
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
            All PMSSS Applications
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Global repository across J&K and Ladakh student applicants
          </p>
        </div>

        <button
          onClick={fetchApplications}
          className="px-3.5 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-300 border border-slate-800 text-xs font-medium flex items-center gap-1.5 self-start sm:self-auto"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Refresh</span>
        </button>
      </div>

      {/* Filter Bar */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by ID or Applicant Name..."
            className="w-full pl-9 pr-3 py-2 bg-slate-900 border border-slate-700 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-[#d32f2f]"
          />
        </div>

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="w-full sm:w-auto px-3 py-2 bg-slate-900 border border-slate-700 rounded-xl text-xs text-slate-300 focus:outline-none focus:border-[#d32f2f]"
        >
          <option value="ALL">All Statuses</option>
          <option value="READY_FOR_REVIEW">Ready For Review</option>
          <option value="UNDER_REVIEW">Under Review</option>
          <option value="APPROVED">Approved</option>
          <option value="NEEDS_CORRECTION">Needs Correction</option>
          <option value="REJECTED">Rejected</option>
        </select>
      </div>

      {/* Applications Table */}
      <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
        <div className="overflow-x-auto">
          <table className="w-full text-xs text-left">
            <thead className="bg-slate-950/70 text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="py-3 px-4">Application ID</th>
                <th className="py-3 px-4">Applicant Name</th>
                <th className="py-3 px-4">Stream</th>
                <th className="py-3 px-4">Assigned SAG Officer</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4">Submitted Date</th>
                <th className="py-3 px-4 text-right">Inspect</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {filtered.map((app) => (
                <tr key={app.applicationId} className="hover:bg-slate-800/40 transition">
                  <td className="py-3.5 px-4 font-mono font-bold text-white">{app.applicationId}</td>
                  <td className="py-3.5 px-4 text-slate-300 font-medium">{app.applicantName}</td>
                  <td className="py-3.5 px-4 text-slate-400">{app.stream}</td>
                  <td className="py-3.5 px-4 text-slate-400 font-mono text-[11px]">{app.officer || 'Auto Assigned'}</td>
                  <td className="py-3.5 px-4"><StatusBadge status={app.status} /></td>
                  <td className="py-3.5 px-4 text-slate-400">{app.createdAt}</td>
                  <td className="py-3.5 px-4 text-right">
                    <Link
                      to={`/officer/review/${app.applicationId}`}
                      className="px-3 py-1.5 rounded-lg bg-[#d32f2f] hover:bg-[#b71c1c] text-white text-xs font-semibold inline-flex items-center gap-1 transition"
                    >
                      <span>View</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </Link>
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
