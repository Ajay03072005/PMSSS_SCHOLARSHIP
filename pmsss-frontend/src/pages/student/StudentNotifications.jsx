import React, { useState, useEffect } from 'react';
import { useNotifications } from '../../context/NotificationContext';
import { notificationApi } from '../../api/notificationApi';
import { Bell, Check, Mail, MessageSquare, Shield, CheckCircle2 } from 'lucide-react';

export default function StudentNotifications() {
  const { notifications, unreadCount, markAsRead, markAllAsRead, fetchNotifications } = useNotifications();
  const [preferences, setPreferences] = useState({
    emailApplicationUpdates: true,
    emailDocumentUpdates: true,
    emailPaymentUpdates: true,
    smsApplicationUpdates: true,
    smsPaymentUpdates: true
  });
  const [prefLoading, setPrefLoading] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);

  useEffect(() => {
    async function loadPreferences() {
      try {
        const res = await notificationApi.getPreferences();
        if (res.data?.data) {
          setPreferences(res.data.data);
        }
      } catch (err) {
        console.error('Error fetching preferences:', err);
      }
    }
    loadPreferences();
  }, []);

  const handleToggle = (key) => {
    setPreferences((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  const handleSavePreferences = async () => {
    try {
      setPrefLoading(true);
      await notificationApi.updatePreferences(preferences);
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 3000);
    } catch (err) {
      console.error('Failed to save preferences:', err);
    } finally {
      setPrefLoading(false);
    }
  };

  return (
    <div>
      {/* Notifications History Section */}
      <div className="content-section">
        <div className="section-header">
          <div>
            <h2>My Notifications & Alerts</h2>
            <span style={{ fontSize: '13px', color: '#666' }}>You have {unreadCount} unread message(s)</span>
          </div>
          {unreadCount > 0 && (
            <button onClick={markAllAsRead} className="action-btn">
              <Check style={{ width: '14px', height: '14px' }} />
              Mark All as Read
            </button>
          )}
        </div>

        {notifications.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#777' }}>
            <Bell style={{ width: '36px', height: '36px', margin: '0 auto 10px', color: '#ccc' }} />
            <p>No notifications received yet. Important status updates will appear here.</p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {notifications.map((item) => (
              <div
                key={item.id}
                onClick={() => markAsRead(item.id)}
                style={{
                  padding: '16px 20px',
                  borderRadius: '8px',
                  border: '1px solid',
                  borderColor: item.isRead ? '#eee' : '#ffcdd2',
                  backgroundColor: item.isRead ? '#ffffff' : '#fff9f9',
                  cursor: 'pointer',
                  transition: 'background 0.2s',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'flex-start'
                }}
              >
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                    <strong style={{ fontSize: '14px', color: '#222' }}>{item.title}</strong>
                    {!item.isRead && (
                      <span className="status-badge status-pending" style={{ padding: '2px 8px', fontSize: '10px' }}>
                        NEW
                      </span>
                    )}
                  </div>
                  <p style={{ margin: 0, fontSize: '13px', color: '#555', lineHeight: '1.4' }}>{item.message}</p>
                </div>
                <div style={{ fontSize: '11px', color: '#999', whiteSpace: 'nowrap', marginLeft: '16px' }}>
                  {item.createdAt ? new Date(item.createdAt).toLocaleDateString() : 'Recent'}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Notification Preferences Section (Section 19 of prompt) */}
      <div className="content-section">
        <div className="section-header">
          <h2>Notification Delivery Preferences</h2>
          <span style={{ fontSize: '12px', color: '#888' }}>
            Transactional alerts are always sent. Configure optional non-critical updates below.
          </span>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '24px', margin: '20px 0' }}>
          {/* Email Preferences */}
          <div style={{ border: '1px solid #eee', borderRadius: '8px', padding: '18px', background: '#fafafa' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '14px', color: '#d32f2f' }}>
              <Mail style={{ width: '18px', height: '18px' }} />
              <strong style={{ fontSize: '14px', color: '#333' }}>Email Notifications</strong>
            </div>
            <label style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', marginBottom: '10px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={preferences.emailApplicationUpdates}
                onChange={() => handleToggle('emailApplicationUpdates')}
              />
              Application Status Updates
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', marginBottom: '10px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={preferences.emailDocumentUpdates}
                onChange={() => handleToggle('emailDocumentUpdates')}
              />
              Document Verification Updates
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={preferences.emailPaymentUpdates}
                onChange={() => handleToggle('emailPaymentUpdates')}
              />
              DBT Payment Updates
            </label>
          </div>

          {/* SMS Preferences */}
          <div style={{ border: '1px solid #eee', borderRadius: '8px', padding: '18px', background: '#fafafa' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '14px', color: '#d32f2f' }}>
              <MessageSquare style={{ width: '18px', height: '18px' }} />
              <strong style={{ fontSize: '14px', color: '#333' }}>SMS Notifications</strong>
            </div>
            <label style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', marginBottom: '10px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={preferences.smsApplicationUpdates}
                onChange={() => handleToggle('smsApplicationUpdates')}
              />
              Application Status Alerts
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={preferences.smsPaymentUpdates}
                onChange={() => handleToggle('smsPaymentUpdates')}
              />
              Payment Release Alerts
            </label>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <button onClick={handleSavePreferences} disabled={prefLoading} className="action-btn">
            {prefLoading ? 'Saving...' : 'Save Preferences'}
          </button>
          {saveSuccess && (
            <span style={{ fontSize: '13px', color: '#2e7d32', display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
              <CheckCircle2 style={{ width: '16px', height: '16px' }} /> Preferences saved successfully!
            </span>
          )}
        </div>
      </div>
    </div>
  );
}
