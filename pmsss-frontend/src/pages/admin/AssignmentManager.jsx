import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { ClipboardList, CheckCircle2, UserCheck, Search } from 'lucide-react';

export default function AssignmentManager() {
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadAssignments() {
      try {
        setLoading(true);
        const res = await adminApi.getAssignments();
        const data = res?.data || res;
        setAssignments(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error('Error fetching assignments:', err);
      } finally {
        setLoading(false);
      }
    }
    loadAssignments();
  }, []);

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Smart Application Assignments</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Workload-aware automated routing allocating candidates according to officer capacity, region, and priority.
          </p>
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          Loading assignments...
        </div>
      ) : assignments.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          No assignments found.
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>Assignment ID</th>
              <th>Application ID</th>
              <th>Assigned Officer</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Assigned Timestamp</th>
            </tr>
          </thead>
          <tbody>
            {assignments.map((item) => (
              <tr key={item.id}>
                <td><strong>#{item.id}</strong></td>
                <td><strong>{item.applicationId}</strong></td>
                <td>
                  <span style={{ fontWeight: '600', color: '#222' }}>{item.officer?.fullName || item.officerName || 'N/A'}</span>
                  <div style={{ fontSize: '11px', color: '#666' }}>{item.officer?.role || item.officerRole || ''}</div>
                </td>
                <td>
                  <span style={{ fontWeight: '700', color: item.priority === 'HIGH' ? '#d32f2f' : '#333' }}>{item.priority || 'NORMAL'}</span>
                </td>
                <td>
                  <span className="status-badge" style={{ background: '#f5f5f5', color: '#333' }}>
                    {item.status || 'ACTIVE'}
                  </span>
                </td>
                <td style={{ fontSize: '12px' }}>{item.assignedAt ? new Date(item.assignedAt).toLocaleString() : 'N/A'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
