import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { applicationApi } from '../../api/applicationApi';
import { ArrowLeft, Printer, Download, CheckCircle, Clock } from 'lucide-react';

export default function ApplicationView() {
  const { id } = useParams();
  const [app, setApp] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const res = await applicationApi.getById(id);
        setApp(res.data?.data || null);
      } catch (err) {
        console.error('Error loading application:', err);
      } finally {
        setLoading(false);
      }
    }
    if (id) loadData();
  }, [id]);

  const handlePrint = () => {
    window.print();
  };

  if (loading) {
    return (
      <div className="content-section" style={{ textAlign: 'center', padding: '50px' }}>
        <div style={{ color: '#666', fontSize: '15px' }}>Loading official application record...</div>
      </div>
    );
  }

  const applicant = app?.user || {};
  const status = app?.status || 'SUBMITTED';

  return (
    <div>
      {/* Top Action Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <Link
          to="/student/dashboard"
          style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: '#555', textDecoration: 'none', fontSize: '13px', fontWeight: '600' }}
        >
          <ArrowLeft style={{ width: '16px', height: '16px' }} />
          Back to Dashboard
        </Link>
        <button onClick={handlePrint} className="action-btn">
          <Printer style={{ width: '15px', height: '15px' }} />
          Print / Save PDF
        </button>
      </div>

      {/* Official Government Printable Document Format */}
      <div
        className="content-section"
        style={{
          border: '2px solid #222',
          padding: '36px',
          maxWidth: '850px',
          margin: '0 auto',
          background: '#ffffff'
        }}
      >
        {/* Document Header */}
        <div style={{ textAlign: 'center', borderBottom: '2px solid #d32f2f', paddingBottom: '16px', marginBottom: '24px' }}>
          <div style={{ fontSize: '12px', fontWeight: '700', letterSpacing: '1px', color: '#666', textTransform: 'uppercase' }}>
            Government of India • All India Council for Technical Education (AICTE)
          </div>
          <h2 style={{ fontSize: '22px', fontWeight: '900', color: '#d32f2f', margin: '6px 0', letterSpacing: '0.5px' }}>
            PRIME MINISTER'S SPECIAL SCHOLARSHIP SCHEME (PMSSS)
          </h2>
          <div style={{ fontSize: '13px', fontWeight: '600', color: '#333' }}>
            Official Application Acknowledgement • Academic Year 2025-2026
          </div>
        </div>

        {/* Application Identification Bar */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            backgroundColor: '#f9f9f9',
            padding: '12px 18px',
            borderRadius: '6px',
            border: '1px solid #eee',
            marginBottom: '24px'
          }}
        >
          <div>
            <span style={{ fontSize: '12px', color: '#666', textTransform: 'uppercase', fontWeight: '600' }}>Application ID: </span>
            <strong style={{ fontSize: '14px', color: '#d32f2f' }}>{app?.applicationId || id}</strong>
          </div>
          <div>
            <span style={{ fontSize: '12px', color: '#666', textTransform: 'uppercase', fontWeight: '600' }}>Status: </span>
            <span className={`status-badge status-${status.toLowerCase().includes('approved') ? 'approved' : status.toLowerCase().includes('reject') ? 'rejected' : 'under-review'}`}>
              {status}
            </span>
          </div>
        </div>

        {/* Personal Details Table */}
        <h3 style={{ fontSize: '15px', fontWeight: '700', color: '#222', borderBottom: '1px solid #ddd', paddingBottom: '6px', marginBottom: '12px' }}>
          1. Personal Information
        </h3>
        <table className="applications-table" style={{ marginTop: 0, marginBottom: '24px' }}>
          <tbody>
            <tr>
              <td style={{ width: '25%', fontWeight: '600', background: '#fcfcfc' }}>Full Name:</td>
              <td style={{ width: '25%' }}>{applicant.name || `${applicant.firstName || ''} ${applicant.lastName || ''}` || 'Ajay Kumar'}</td>
              <td style={{ width: '25%', fontWeight: '600', background: '#fcfcfc' }}>Father's Name:</td>
              <td style={{ width: '25%' }}>{app?.fatherName || 'Ramesh Kumar'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Date of Birth:</td>
              <td>{applicant.dateOfBirth || '2005-07-03'}</td>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Gender:</td>
              <td>{app?.gender || 'Male'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Aadhaar Number:</td>
              <td>{applicant.aadhar ? `XXXXXXXX${applicant.aadhar.slice(-4)}` : 'XXXXXXXX4912'}</td>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Category:</td>
              <td>{app?.category || 'General'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Mobile Number:</td>
              <td>{applicant.mobile || '+91 9876543210'}</td>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Email Address:</td>
              <td>{applicant.email || 'student@pmsss.gov.in'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Domicile State/UT:</td>
              <td>Jammu & Kashmir</td>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>District:</td>
              <td>{app?.district || 'Jammu'}</td>
            </tr>
          </tbody>
        </table>

        {/* Academic Details Table */}
        <h3 style={{ fontSize: '15px', fontWeight: '700', color: '#222', borderBottom: '1px solid #ddd', paddingBottom: '6px', marginBottom: '12px' }}>
          2. Academic Qualifications
        </h3>
        <table className="applications-table" style={{ marginTop: 0, marginBottom: '24px' }}>
          <thead>
            <tr>
              <th>Qualification</th>
              <th>Board / University</th>
              <th>Passing Year</th>
              <th>Roll Number</th>
              <th>Percentage / CGPA</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style={{ fontWeight: '600' }}>Class 10th</td>
              <td>{app?.tenthBoard || 'JKBOSE'}</td>
              <td>{app?.tenthPassingYear || '2021'}</td>
              <td>{app?.tenthRollNumber || '1049281'}</td>
              <td><strong>{app?.tenthMarksPercentage || '88.4%'}</strong></td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600' }}>Class 12th</td>
              <td>{app?.twelfthBoard || 'JKBOSE'}</td>
              <td>{app?.twelfthPassingYear || '2023'}</td>
              <td>{app?.twelfthRollNumber || '1284912'}</td>
              <td><strong>{app?.twelfthMarksPercentage || '86.2%'}</strong></td>
            </tr>
          </tbody>
        </table>

        {/* Financial & DBT Bank Details */}
        <h3 style={{ fontSize: '15px', fontWeight: '700', color: '#222', borderBottom: '1px solid #ddd', paddingBottom: '6px', marginBottom: '12px' }}>
          3. Bank Account & DBT Information
        </h3>
        <table className="applications-table" style={{ marginTop: 0, marginBottom: '24px' }}>
          <tbody>
            <tr>
              <td style={{ width: '25%', fontWeight: '600', background: '#fcfcfc' }}>Bank Name:</td>
              <td style={{ width: '25%' }}>{app?.bankName || 'State Bank of India'}</td>
              <td style={{ width: '25%', fontWeight: '600', background: '#fcfcfc' }}>Account Number:</td>
              <td style={{ width: '25%' }}>{app?.bankAccountNumber ? `XXXXXXXX${app.bankAccountNumber.slice(-4)}` : 'XXXXXXXX9814'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>IFSC Code:</td>
              <td>{app?.bankIfsc || 'SBIN0001234'}</td>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Family Annual Income:</td>
              <td>₹{app?.familyAnnualIncome || '2,40,000'} / Year</td>
            </tr>
          </tbody>
        </table>

        {/* Declaration & Signatures */}
        <div style={{ marginTop: '30px', paddingTop: '20px', borderTop: '1px dashed #bbb', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
          <div style={{ fontSize: '11px', color: '#666', maxWidth: '400px', lineHeight: '1.5' }}>
            * This is a system-generated document submitted via the official PMSSS Portal. All entries are subject to physical/digital verification by AICTE officers.
          </div>
          <div style={{ textAlign: 'center' }}>
            <div style={{ borderBottom: '1px solid #333', width: '180px', marginBottom: '4px' }} />
            <span style={{ fontSize: '12px', fontWeight: '600', color: '#333' }}>Applicant Signature / Stamp</span>
          </div>
        </div>
      </div>
    </div>
  );
}
