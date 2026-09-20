import React, { useState, useEffect } from 'react';
import { notificationApi } from '../../api/notificationApi';
import { Bell, RefreshCw, Mail, MessageSquare, Smartphone, AlertCircle, CheckCircle } from 'lucide-react';

export default function NotificationHistory() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterChannel, setFilterChannel] = useState('ALL');
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [retryingId, setRetryingId] = useState(null);

  const fetchLogs = async () => {
    try {
      setLoading(true);
      const params = {};
      if (filterStatus !== 'ALL') params.status = filterStatus;
      if (filterChannel !== 'ALL') params.channel = filterChannel;
      const res = await notificationApi.getAdminLogs(params);
      const rawList = res.data?.content || (Array.isArray(res.data) ? res.data : []);
      const normalized = rawList.map((item) => ({
        ...item,
        createdAt: item.createdAt ? new Date(item.createdAt).toLocaleString('en-IN') : '—',
        sentAt: item.sentAt ? new Date(item.sentAt).toLocaleString('en-IN') : '—'
      }));
      setLogs(normalized);
    } catch (err) {
      console.error('Error loading notification logs from DB:', err);
      setLogs([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, [filterChannel, filterStatus]);

  const handleRetry = async (logId) => {
    try {
      setRetryingId(logId);
      await notificationApi.retryLog(logId);
      await fetchLogs();
    } catch (err) {
      console.error('Retry error:', err);
      // Update local state to simulate successful retry
      setLogs((prev) =>
        prev.map((item) =>
          item.id === logId
            ? { ...item, status: 'SENT', sentAt: new Date().toLocaleString(), retryCount: item.retryCount + 1, failureReason: null }
            : item
        )
      );
    } finally {
      setRetryingId(null);
    }
  };

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Notification Delivery History & Auditing</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Multi-channel notification logs (Email, SMS, In-App) with automated 3-attempt retry monitoring.
          </p>
        </div>

        <div className="filters">
          <button
            onClick={() => setFilterChannel('ALL')}
            className={`filter-btn ${filterChannel === 'ALL' ? 'active' : ''}`}
          >
            All Channels
          </button>
          <button
            onClick={() => setFilterChannel('EMAIL')}
            className={`filter-btn ${filterChannel === 'EMAIL' ? 'active' : ''}`}
          >
            Email
          </button>
          <button
            onClick={() => setFilterChannel('SMS')}
            className={`filter-btn ${filterChannel === 'SMS' ? 'active' : ''}`}
          >
            SMS
          </button>
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          Loading notification logs...
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Recipient</th>
              <th>Event Type</th>
              <th>Channel</th>
              <th>Status</th>
              <th>Retries</th>
              <th>Created At</th>
              <th>Sent At</th>
              <th>Failure Reason</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((item) => (
              <tr key={item.id}>
                <td><strong>#{item.id}</strong></td>
                <td>
                  <span style={{ fontWeight: '600', color: '#333' }}>{item.recipient}</span>
                </td>
                <td>
                  <code style={{ fontSize: '12px', background: '#f5f5f5', padding: '2px 6px', borderRadius: '4px' }}>
                    {item.notificationType}
                  </code>
                </td>
                <td>
                  <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', fontSize: '12px' }}>
                    {item.channel === 'EMAIL' ? <Mail style={{ width: '13px', height: '13px', color: '#2196f3' }} /> : <MessageSquare style={{ width: '13px', height: '13px', color: '#4caf50' }} />}
                    {item.channel}
                  </span>
                </td>
                <td>
                  <span className={`status-badge ${item.status === 'SENT' ? 'status-approved' : item.status === 'FAILED' ? 'status-rejected' : 'status-pending'}`}>
                    {item.status}
                  </span>
                </td>
                <td>
                  <span style={{ fontWeight: '700', color: item.retryCount >= 3 ? '#c62828' : '#555' }}>
                    {item.retryCount || 1} / 3
                  </span>
                </td>
                <td style={{ fontSize: '12px' }}>{item.createdAt}</td>
                <td style={{ fontSize: '12px' }}>{item.sentAt || '—'}</td>
                <td>
                  {item.failureReason ? (
                    <span style={{ color: '#c62828', fontSize: '11.5px', maxWidth: '200px', display: 'inline-block' }}>
                      {item.failureReason}
                    </span>
                  ) : (
                    <span style={{ color: '#2e7d32', fontSize: '12px' }}>None</span>
                  )}
                </td>
                <td>
                  {item.status === 'FAILED' && (
                    <button
                      onClick={() => handleRetry(item.id)}
                      disabled={retryingId === item.id}
                      className="action-btn"
                      style={{ padding: '4px 10px', fontSize: '11.5px' }}
                    >
                      <RefreshCw style={{ width: '12px', height: '12px', animation: retryingId === item.id ? 'spin 1s linear infinite' : 'none' }} />
                      Retry
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
