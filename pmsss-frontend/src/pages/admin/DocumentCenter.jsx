import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { UploadCloud, CheckCircle, AlertTriangle, Eye } from 'lucide-react';

export default function DocumentCenter() {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDocuments() {
      try {
        setLoading(true);
        const res = await adminApi.getDocuments();
        const data = res?.data || res;
        setDocuments(Array.isArray(data) ? data : []);
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
          <h2>System-wide Document Intelligence</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Auditing OCR parsing pipeline, document-matching confidence, and document tamper checks.
          </p>
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          Loading documents...
        </div>
      ) : documents.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          No documents found in the system.
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>Doc ID</th>
              <th>Application ID</th>
              <th>Document Type</th>
              <th>OCR Intelligence Status</th>
              <th>Consistency Match</th>
              <th>Upload Date</th>
            </tr>
          </thead>
          <tbody>
            {documents.map((doc) => {
              const matchScore = doc.ocrConfidence || 0;
              const ocrStatus = doc.verificationStatus === 'VERIFIED' || matchScore >= 80 ? 'PARSED' : 'ANOMALY';
              return (
                <tr key={doc.id || doc.uniqueId}>
                  <td><strong>#{doc.id}</strong></td>
                  <td><strong>{doc.applicationId || 'N/A'}</strong></td>
                  <td>{doc.documentType}</td>
                  <td>
                    <span className={`status-badge ${ocrStatus === 'PARSED' ? 'status-approved' : 'status-rejected'}`}>
                      {ocrStatus}
                    </span>
                  </td>
                  <td>
                    <strong style={{ color: matchScore > 80 ? '#2e7d32' : '#c62828' }}>
                      {matchScore}% Match
                    </strong>
                  </td>
                  <td style={{ fontSize: '12px' }}>{doc.uploadedAt ? new Date(doc.uploadedAt).toLocaleDateString() : 'N/A'}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}
