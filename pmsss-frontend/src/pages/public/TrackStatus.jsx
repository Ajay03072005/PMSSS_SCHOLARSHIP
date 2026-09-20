import React, { useState } from 'react';
import { applicationApi } from '../../api/applicationApi';
import StatusBadge from '../../components/common/StatusBadge';
import Timeline from '../../components/common/Timeline';
import { Search, AlertCircle, CheckCircle2, FileText, Calendar, Building, Award } from 'lucide-react';

export default function TrackStatus() {
  const [appId, setAppId] = useState('PMSSS2026000001');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!appId.trim()) return;

    setError('');
    setData(null);
    setLoading(true);

    try {
      const res = await applicationApi.trackStatus(appId.trim());
      setData(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Application not found. Please verify the Application ID.');
    } finally {
      setLoading(false);
    }
  };

  const formatTimelineEvents = (events) => {
    if (!events || !events.length) {
      return [
        { title: 'Application Registered', description: 'Student submitted application form', time: 'Initial Step' },
        { title: 'Automated Pre-Validation', description: 'System executed rule validation and document checks', time: 'Completed' },
        { title: 'Under Processing', description: 'Pending officer evaluation / review', time: 'Current Stage' }
      ];
    }
    return events.map((ev) => ({
      title: ev.status || ev.stage || 'Status Update',
      description: ev.remarks || ev.description || 'Processed by system',
      time: ev.timestamp ? new Date(ev.timestamp).toLocaleDateString() : 'Recorded'
    }));
  };

  return (
    <div className="py-8 max-w-4xl mx-auto px-4">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-extrabold text-white font-['Outfit',sans-serif]">
          Track Application Status
        </h1>
        <p className="mt-2 text-sm text-slate-400">
          Enter your unique PMSSS Application Number to see live progress and timeline
        </p>
      </div>

      {/* Search Bar */}
      <form onSubmit={handleSearch} className="flex gap-2 max-w-xl mx-auto mb-8">
        <div className="relative flex-1">
          <Search className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
          <input
            type="text"
            required
            value={appId}
            onChange={(e) => setAppId(e.target.value)}
            placeholder="e.g. PMSSS2026000001"
            className="w-full pl-10 pr-4 py-3 bg-slate-900 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-[#d32f2f] focus:ring-1 focus:ring-[#d32f2f] text-sm font-mono uppercase"
          />
        </div>
        <button
          type="submit"
          disabled={loading}
          style={{ backgroundColor: '#d32f2f', boxShadow: '0 4px 15px rgba(211, 47, 47, 0.4)' }}
          className="px-6 py-3 hover:bg-[#b71c1c] text-white font-semibold rounded-xl text-sm transition disabled:opacity-50 flex items-center gap-2 shrink-0"
        >
          {loading ? (
            <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
          ) : (
            <span>Search</span>
          )}
        </button>
      </form>

      {error && (
        <div className="p-4 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm flex items-center gap-3 mb-6 max-w-xl mx-auto">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {data && (
        <div className="space-y-6">
          {/* Main Info Card */}
          <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-800">
              <div>
                <div className="flex items-center gap-3">
                  <h2 className="text-xl font-bold text-white font-mono">
                    {data.applicationId || data.id || appId}
                  </h2>
                  <StatusBadge status={data.status || 'SUBMITTED'} />
                </div>
                <p className="text-xs text-slate-400 mt-1">
                  Applicant: <span className="text-slate-200 font-medium">{data.applicantName || 'Applicant'}</span>
                </p>
              </div>

              {data.academicYear && (
                <div className="text-right">
                  <span className="text-xs text-slate-400">Academic Year</span>
                  <p className="text-sm font-semibold text-slate-200">{data.academicYear}</p>
                </div>
              )}
            </div>

            {/* Quick Metadata */}
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-6">
              <div className="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80">
                <span className="text-[11px] text-slate-400 block mb-1">Scheme Category</span>
                <span className="text-xs font-semibold text-slate-200">
                  {data.category || data.stream || 'General Degree'}
                </span>
              </div>
              <div className="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80">
                <span className="text-[11px] text-slate-400 block mb-1">State / UT</span>
                <span className="text-xs font-semibold text-slate-200">Jammu & Kashmir</span>
              </div>
              <div className="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80">
                <span className="text-[11px] text-slate-400 block mb-1">Verification Status</span>
                <span className="text-xs font-semibold text-[#d32f2f]">
                  {data.validationStatus || 'In Progress'}
                </span>
              </div>
              <div className="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80">
                <span className="text-[11px] text-slate-400 block mb-1">Submission Date</span>
                <span className="text-xs font-semibold text-slate-200">
                  {data.createdAt ? new Date(data.createdAt).toLocaleDateString() : 'Active'}
                </span>
              </div>
            </div>

            {data.remarks && (
              <div className="mt-6 p-4 rounded-xl bg-[#d32f2f]/10 border border-[#d32f2f]/20 text-slate-200 text-xs">
                <span className="font-bold">Officer Remarks: </span>
                {data.remarks}
              </div>
            )}
          </div>

          {/* Timeline */}
          <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
            <h3 className="text-base font-bold text-white mb-6 font-['Outfit',sans-serif]">
              Processing Lifecycle Timeline
            </h3>
            <Timeline events={formatTimelineEvents(data.timeline || data.history)} />
          </div>
        </div>
      )}
    </div>
  );
}
