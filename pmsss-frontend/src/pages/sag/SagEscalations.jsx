import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { officerApi } from '../../api/officerApi';
import { ArrowUpRight, Eye, ShieldAlert, Loader2 } from 'lucide-react';

export default function SagEscalations() {
  const [escalations, setEscalations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadEscalations() {
      try {
        setLoading(true);
        const res = await officerApi.getEscalations('OPEN');
        const data = res?.data || res;
        setEscalations(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error('Error fetching escalations:', err);
      } finally {
        setLoading(false);
      }
    }
    loadEscalations();
  }, []);

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Escalated Applications</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Complex boundary cases and policy exceptions referred to senior administrative supervisors.
          </p>
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          Loading escalations...
        </div>
      ) : escalations.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          No active escalations in your queue.
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>Application ID</th>
              <th>Applicant Name</th>
              <th>Escalation Reason</th>
              <th>Status</th>
              <th>Escalated On</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {escalations.map((item) => (
              <tr key={item.id}>
                <td><strong>{item.applicationId}</strong></td>
                <td>{item.applicantName || item.officerName || 'N/A'}</td>
                <td><span style={{ fontSize: '12.5px', color: '#555' }}>{item.reason}</span></td>
                <td>
                  <span className="status-badge status-pending">
                    {item.status}
                  </span>
                </td>
                <td>{item.escalatedAt ? new Date(item.escalatedAt).toLocaleString() : 'N/A'}</td>
                <td>
                  <Link to={`/sag/applications/${item.applicationId}`} className="btn-view">
                    <Eye style={{ width: '13px', height: '13px' }} />
                    View Details
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
