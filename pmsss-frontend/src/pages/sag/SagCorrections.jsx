import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { officerApi } from '../../api/officerApi';
import { AlertTriangle, Clock, Eye, CheckCircle2 } from 'lucide-react';

export default function SagCorrections() {
  const [corrections, setCorrections] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadCorrections() {
      try {
        setLoading(true);
        const res = await officerApi.getCorrections();
        const data = res?.data || res;
        setCorrections(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error('Error fetching corrections:', err);
      } finally {
        setLoading(false);
      }
    }
    loadCorrections();
  }, []);

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Application Correction Queue</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Targeted correction requests sent to students to prevent unnecessary rejections.
          </p>
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          Loading corrections...
        </div>
      ) : corrections.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          No active correction requests.
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>Application ID</th>
              <th>Field / Document</th>
              <th>Correction Reason</th>
              <th>Status</th>
              <th>Requested By</th>
              <th>Requested At</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {corrections.map((item) => (
              <tr key={item.id}>
                <td><strong>{item.applicationId}</strong></td>
                <td><span className="status-badge" style={{ background: '#f5f5f5', color: '#333' }}>{item.documentType || item.fieldKey || 'General'}</span></td>
                <td><span style={{ fontSize: '12.5px', color: '#555' }}>{item.reason}</span></td>
                <td>
                  <span className={`status-badge ${item.isResolved ? 'status-approved' : 'status-pending'}`}>
                    {item.isResolved ? 'Resolved' : 'Pending Student'}
                  </span>
                </td>
                <td>{item.requestedBy || 'SAG Officer'}</td>
                <td>{item.requestedAt ? new Date(item.requestedAt).toLocaleString() : 'N/A'}</td>
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
  );
}
