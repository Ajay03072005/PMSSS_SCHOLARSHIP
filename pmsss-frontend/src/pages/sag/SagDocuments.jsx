import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { FileText, CheckCircle, AlertTriangle, Eye, ShieldCheck } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function SagDocuments() {
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDocuments() {
      try {
        setLoading(true);
        const res = await adminApi.getDocuments();
        const data = res?.data || res;
        setDocs(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error('Error fetching documents:', err);
      } finally {
        setLoading(false);
      }
    }
    loadDocuments();
  }, []);

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Document Verification Center</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Automated OCR extraction and consistency results across submitted applicant documents.
          </p>
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          Loading documents...
        </div>
      ) : docs.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          No documents found.
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>Application ID</th>
              <th>Document Type</th>
              <th>File Name</th>
              <th>OCR Verification</th>
              <th>Confidence</th>
              <th>Submission Date</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {docs.map((doc) => {
              const confidence = doc.ocrConfidence || doc.matchScore || 0;
              const ocrStatus = doc.verificationStatus === 'VERIFIED' || confidence >= 80 ? 'MATCHED' : 'FLAGGED';
              return (
                <tr key={doc.id || doc.uniqueId}>
                  <td><strong>{doc.applicationId || 'N/A'}</strong></td>
                  <td>{doc.documentType}</td>
                  <td><span style={{ color: '#555', fontStyle: 'italic' }}>{doc.fileName || doc.originalFileName || 'N/A'}</span></td>
                  <td>
                    <span className={`status-badge ${ocrStatus === 'MATCHED' ? 'status-approved' : 'status-rejected'}`}>
                      {ocrStatus === 'MATCHED' ? 'Verified Match' : 'Discrepancy'}
                    </span>
                  </td>
                  <td>
                    <strong style={{ color: confidence >= 80 ? '#2e7d32' : '#c62828' }}>
                      {confidence}%
                    </strong>
                  </td>
                  <td>{doc.uploadedAt ? new Date(doc.uploadedAt).toLocaleDateString() : 'N/A'}</td>
                  <td>
                    <Link to={`/sag/applications/${doc.applicationId || ''}`} className="btn-view">
                      <Eye style={{ width: '13px', height: '13px' }} />
                      Inspect
                    </Link>
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
