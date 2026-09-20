import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { financeApi } from '../../api/financeApi';
import { applicationApi } from '../../api/applicationApi';
import { Eye, CreditCard, Search, ArrowRight, CheckCircle2 } from 'lucide-react';

export default function FinanceApplications() {
  const [apps, setApps] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadApps() {
      try {
        setLoading(true);
        const res = await applicationApi.getAllApplications({ status: 'APPROVED', page: 0, size: 50 });
        const data = res?.data?.content || res?.data || res;
        setApps(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error('Error fetching finance applications:', err);
      } finally {
        setLoading(false);
      }
    }
    loadApps();
  }, []);

  const filtered = apps.filter((a) => {
    if (!search.trim()) return true;
    const q = search.toLowerCase();
    return (
      (a.applicationId || '').toLowerCase().includes(q) ||
      (a.applicantName || a.firstName || '').toLowerCase().includes(q)
    );
  });

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Finance-Ready Applications</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Applications approved by SAG Officers ready for DBT bank account verification and disbursement.
          </p>
        </div>
        <Link to="/finance/payments" className="action-btn">
          <CreditCard style={{ width: '15px', height: '15px' }} />
          Payment Processing Queue
        </Link>
      </div>

      <div className="search-box" style={{ marginBottom: '16px' }}>
        <input
          type="text"
          placeholder="Search by Application ID or Student Name..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ width: '100%', maxWidth: '380px', padding: '9px 14px', border: '1px solid #ddd', borderRadius: '6px', fontSize: '13.5px' }}
        />
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          Loading finance-ready applications...
        </div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          No approved applications found for finance processing.
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>Application ID</th>
              <th>Candidate Name</th>
              <th>Status</th>
              <th>Submitted Date</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((item) => (
              <tr key={item.applicationId || item.id}>
                <td><strong>{item.applicationId}</strong></td>
                <td>{item.applicantName || `${item.firstName || ''} ${item.lastName || ''}`.trim() || 'N/A'}</td>
                <td>
                  <span className="status-badge status-approved">
                    {item.status || 'APPROVED'}
                  </span>
                </td>
                <td>{item.createdAt ? new Date(item.createdAt).toLocaleDateString() : 'N/A'}</td>
                <td>
                  <Link to={`/finance/applications/${item.applicationId}`} className="btn-view">
                    <Eye style={{ width: '13px', height: '13px' }} />
                    Details & Disburse
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
