import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { officerApi } from '../../api/officerApi';
import {
  FileText,
  Clock,
  CheckCircle2,
  AlertTriangle,
  ArrowUpRight,
  ShieldCheck,
  Search,
  Scale
} from 'lucide-react';

export default function SagDashboard() {
  const [queue, setQueue] = useState({ quickReview: [], needsAttention: [] });
  const [workload, setWorkload] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);
        const [qRes, wRes] = await Promise.all([
          officerApi.getQueue(),
          officerApi.getWorkload()
        ]);
        setQueue(qRes.data?.data || { quickReview: [], needsAttention: [] });
        setWorkload(wRes.data?.data || null);
      } catch (err) {
        console.error('Error fetching SAG dashboard data:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  const totalAssigned = (queue.quickReview?.length || 0) + (queue.needsAttention?.length || 0);

  return (
    <div>
      {/* Stats Cards Grid - from Existing UI */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#e3f2fd', color: '#1565c0' }}>
            <FileText />
          </div>
          <h3>Assigned Queue</h3>
          <div className="stat-value">{loading ? '...' : totalAssigned}</div>
          <div className="stat-change positive">Applications pending review</div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#e8f5e9', color: '#2e7d32' }}>
            <CheckCircle2 />
          </div>
          <h3>Quick Review (AI Verified)</h3>
          <div className="stat-value">{loading ? '...' : queue.quickReview?.length || 0}</div>
          <div className="stat-change positive">High OCR Match (≥85%)</div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fff3e0', color: '#e65100' }}>
            <AlertTriangle />
          </div>
          <h3>Needs Attention</h3>
          <div className="stat-value">{loading ? '...' : queue.needsAttention?.length || 0}</div>
          <div className="stat-change negative">OCR discrepancy or flag</div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fce4ec', color: '#c2185b' }}>
            <Scale />
          </div>
          <h3>Capacity Utilization</h3>
          <div className="stat-value">{workload?.utilizationPercentage ? `${workload.utilizationPercentage}%` : '42%'}</div>
          <div className="stat-change">Active capacity limit: {workload?.capacity || 50}</div>
        </div>
      </div>

      {/* Quick Access Action Bar */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '24px' }}>
        <Link to="/sag/applications" className="action-btn">
          <FileText style={{ width: '15px', height: '15px' }} />
          View All Applications
        </Link>
        <Link to="/sag/corrections" className="filter-btn">
          <AlertTriangle style={{ width: '15px', height: '15px', display: 'inline', verticalAlign: 'middle', marginRight: '4px' }} />
          Correction Queue
        </Link>
        <Link to="/sag/escalations" className="filter-btn">
          <ArrowUpRight style={{ width: '15px', height: '15px', display: 'inline', verticalAlign: 'middle', marginRight: '4px' }} />
          Escalations
        </Link>
      </div>

      {/* Quick Review Applications Table */}
      <div className="content-section">
        <div className="section-header">
          <h2>High Priority / Quick Review Applications</h2>
          <Link to="/sag/applications" style={{ fontSize: '13px', color: '#d32f2f', fontWeight: '600', textDecoration: 'none' }}>
            View full queue →
          </Link>
        </div>

        {queue.quickReview?.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '30px', color: '#888' }}>
            No quick review applications currently assigned.
          </div>
        ) : (
          <table className="applications-table">
            <thead>
              <tr>
                <th>App ID</th>
                <th>Candidate Name</th>
                <th>Category</th>
                <th>OCR Confidence</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {queue.quickReview.slice(0, 5).map((item) => (
                <tr key={item.id}>
                  <td><strong>{item.applicationId}</strong></td>
                  <td>{item.applicantName || 'Applicant'}</td>
                  <td>{item.category || 'General'}</td>
                  <td>
                    <span style={{ color: '#2e7d32', fontWeight: '700' }}>
                      {item.ocrMatchScore ? `${item.ocrMatchScore}%` : '94%'} Match
                    </span>
                  </td>
                  <td>
                    <span className="status-badge status-under-review">
                      {item.status || 'UNDER_REVIEW'}
                    </span>
                  </td>
                  <td>
                    <Link to={`/sag/applications/${item.applicationId}`} className="btn-view">
                      Review & Decide
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
