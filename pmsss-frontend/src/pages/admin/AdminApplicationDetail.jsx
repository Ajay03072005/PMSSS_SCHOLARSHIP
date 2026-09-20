import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { applicationApi } from '../../api/applicationApi';
import { ArrowLeft, UserCheck, ShieldCheck, Clock, FileText } from 'lucide-react';

export default function AdminApplicationDetail() {
  const { id } = useParams();
  const [app, setApp] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadApp() {
      try {
        setLoading(true);
        const res = await applicationApi.getById(id);
        setApp(res.data?.data || null);
      } catch (err) {
        console.error('Error fetching application detail:', err);
      } finally {
        setLoading(false);
      }
    }
    if (id) loadApp();
  }, [id]);

  if (loading) {
    return (
      <div className="content-section" style={{ textAlign: 'center', padding: '40px' }}>
        Loading administrative application details...
      </div>
    );
  }

  const applicant = app?.user || {};

  return (
    <div>
      <div style={{ marginBottom: '16px' }}>
        <Link
          to="/admin/applications"
          style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: '#555', textDecoration: 'none', fontSize: '13px', fontWeight: '600' }}
        >
          <ArrowLeft style={{ width: '16px', height: '16px' }} />
          Back to Applications Manager
        </Link>
      </div>

      <div className="content-section">
        <div className="section-header">
          <div>
            <h2>Application Administrative Record: {id}</h2>
            <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
              Detailed lifecycle tracking, assignment status, and verification history.
            </p>
          </div>
          <span className="status-badge status-under-review">
            {app?.status || 'SUBMITTED'}
          </span>
        </div>

        <table className="applications-table" style={{ marginBottom: '24px' }}>
          <tbody>
            <tr>
              <td style={{ width: '25%', fontWeight: '600', background: '#fcfcfc' }}>Candidate Name:</td>
              <td style={{ width: '25%' }}>{applicant.name || `${applicant.firstName || ''} ${applicant.lastName || ''}` || 'Ajay Kumar'}</td>
              <td style={{ width: '25%', fontWeight: '600', background: '#fcfcfc' }}>Aadhaar Number:</td>
              <td style={{ width: '25%' }}>{applicant.aadhar ? `XXXXXXXX${applicant.aadhar.slice(-4)}` : 'XXXXXXXX4912'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Mobile / Email:</td>
              <td>{applicant.mobile || '+91 9876543210'} / {applicant.email || 'student@pmsss.gov.in'}</td>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Category:</td>
              <td>{app?.category || 'General'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>12th Marks:</td>
              <td><strong>{app?.twelfthMarksPercentage || '86.2%'}</strong> ({app?.twelfthBoard || 'JKBOSE'})</td>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Family Income:</td>
              <td>₹{app?.familyAnnualIncome || '2,40,000'} / Year</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Assigned SAG Officer:</td>
              <td><span style={{ fontWeight: '600', color: '#1565c0' }}>Sunil Verma (SAG Officer)</span></td>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>AI Priority Score:</td>
              <td><strong style={{ color: '#d32f2f' }}>P85 (High Priority)</strong></td>
            </tr>
          </tbody>
        </table>

        {/* Administrative Actions */}
        <div style={{ background: '#fafafa', border: '1px solid #eee', borderRadius: '8px', padding: '16px', display: 'flex', gap: '12px', alignItems: 'center' }}>
          <Link to={`/student/application/${id}`} className="action-btn">
            <FileText style={{ width: '15px', height: '15px' }} />
            View Official Student Copy
          </Link>
          <Link to="/admin/audit-logs" className="filter-btn">
            <ShieldCheck style={{ width: '15px', height: '15px', display: 'inline', verticalAlign: 'middle', marginRight: '4px' }} />
            Audit Trail
          </Link>
        </div>
      </div>
    </div>
  );
}
