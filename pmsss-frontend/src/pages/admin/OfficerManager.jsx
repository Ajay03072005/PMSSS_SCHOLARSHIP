import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { UserCheck, Shield, Scale, Mail, Phone } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function OfficerManager() {
  const [officers, setOfficers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadOfficers() {
      try {
        setLoading(true);
        const res = await adminApi.getOfficers();
        const data = res?.data || res;
        setOfficers(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error('Error fetching officers:', err);
      } finally {
        setLoading(false);
      }
    }
    loadOfficers();
  }, []);

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Officer Directory & Workload Overview</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Active verification and finance officers with current queue allocations and processing capacity.
          </p>
        </div>
        <Link to="/admin/workload" className="action-btn">
          <Scale style={{ width: '15px', height: '15px' }} />
          Workload Rebalancer
        </Link>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          Loading officers...
        </div>
      ) : officers.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          No officers found.
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>Officer Name</th>
              <th>Role Designation</th>
              <th>Email</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {officers.map((off) => {
              const roleName = (off.role || '').replace('ROLE_', '');
              return (
                <tr key={off.id}>
                  <td><strong>{off.fullName || off.name || 'N/A'}</strong></td>
                  <td>
                    <span className="status-badge" style={{ background: '#e3f2fd', color: '#1565c0' }}>
                      {roleName}
                    </span>
                  </td>
                  <td>{off.email}</td>
                  <td>
                    <span className={`status-badge ${off.isActive ? 'status-approved' : 'status-rejected'}`}>
                      {off.isActive ? 'AVAILABLE' : 'INACTIVE'}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}
