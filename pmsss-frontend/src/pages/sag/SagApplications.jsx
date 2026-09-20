import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { officerApi } from '../../api/officerApi';
import { Search, Eye, CheckCircle, AlertTriangle } from 'lucide-react';

export default function SagApplications() {
  const [queue, setQueue] = useState({ quickReview: [], needsAttention: [] });
  const [activeTab, setActiveTab] = useState('ALL');
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const res = await officerApi.getQueue();
        setQueue(res.data?.data || { quickReview: [], needsAttention: [] });
      } catch (err) {
        console.error('Error fetching SAG applications:', err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  const allApps = [
    ...(queue.quickReview || []).map((a) => ({ ...a, queueType: 'QUICK' })),
    ...(queue.needsAttention || []).map((a) => ({ ...a, queueType: 'ATTENTION' }))
  ];

  const filtered = allApps.filter((item) => {
    if (activeTab === 'QUICK' && item.queueType !== 'QUICK') return false;
    if (activeTab === 'ATTENTION' && item.queueType !== 'ATTENTION') return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        (item.applicationId && item.applicationId.toLowerCase().includes(q)) ||
        (item.applicantName && item.applicantName.toLowerCase().includes(q))
      );
    }
    return true;
  });

  return (
    <div>
      <div className="content-section">
        {/* Section Header with Tabs */}
        <div className="section-header">
          <div>
            <h2>Assigned Review Queue</h2>
            <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
              Applications pre-processed with OCR consistency checks and anomaly detection.
            </p>
          </div>

          <div className="filters">
            <button
              onClick={() => setActiveTab('ALL')}
              className={`filter-btn ${activeTab === 'ALL' ? 'active' : ''}`}
            >
              All ({allApps.length})
            </button>
            <button
              onClick={() => setActiveTab('QUICK')}
              className={`filter-btn ${activeTab === 'QUICK' ? 'active' : ''}`}
            >
              Quick Review ({queue.quickReview?.length || 0})
            </button>
            <button
              onClick={() => setActiveTab('ATTENTION')}
              className={`filter-btn ${activeTab === 'ATTENTION' ? 'active' : ''}`}
            >
              Needs Attention ({queue.needsAttention?.length || 0})
            </button>
          </div>
        </div>

        {/* Search Box from Existing UI */}
        <div className="search-box" style={{ marginBottom: '16px' }}>
          <input
            type="text"
            placeholder="Search by Application ID or Applicant Name..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ width: '100%', maxWidth: '380px', padding: '9px 14px', border: '1px solid #ddd', borderRadius: '6px', fontSize: '13.5px' }}
          />
        </div>

        {/* Applications Table */}
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
            Loading assigned applications...
          </div>
        ) : filtered.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
            No applications match your filter.
          </div>
        ) : (
          <table className="applications-table">
            <thead>
              <tr>
                <th>App ID</th>
                <th>Candidate Name</th>
                <th>Priority</th>
                <th>Category</th>
                <th>OCR Status</th>
                <th>State</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((item) => (
                <tr key={item.id || item.applicationId}>
                  <td><strong>{item.applicationId}</strong></td>
                  <td>{item.applicantName || 'Applicant'}</td>
                  <td>
                    <span style={{ fontWeight: '700', color: item.priorityScore > 70 ? '#d32f2f' : '#333' }}>
                      {item.priorityScore ? `P${item.priorityScore}` : 'P50'}
                    </span>
                  </td>
                  <td>
                    <span className="status-badge" style={{ background: '#f5f5f5', color: '#444' }}>
                      {item.queueType === 'QUICK' ? 'Auto-Matched' : 'Flagged'}
                    </span>
                  </td>
                  <td>
                    <span style={{ color: item.queueType === 'QUICK' ? '#2e7d32' : '#e65100', fontWeight: '700' }}>
                      {item.ocrMatchScore ? `${item.ocrMatchScore}%` : item.queueType === 'QUICK' ? '92%' : '68%'} Match
                    </span>
                  </td>
                  <td>
                    <span className={`status-badge status-${item.queueType === 'QUICK' ? 'under-review' : 'pending'}`}>
                      {item.status || 'UNDER_REVIEW'}
                    </span>
                  </td>
                  <td>
                    <Link to={`/sag/applications/${item.applicationId}`} className="btn-view">
                      <Eye style={{ width: '13px', height: '13px' }} />
                      Review
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
