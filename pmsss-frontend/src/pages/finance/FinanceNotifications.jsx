import React from 'react';
import { useNotifications } from '../../context/NotificationContext';
import { Bell, Check, CreditCard, Clock } from 'lucide-react';

export default function FinanceNotifications() {
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications();

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Finance & Disbursement Alerts</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Alerts for approved scholarships arriving from SAG, PFMS reconciliation reports, and disbursement tasks.
          </p>
        </div>
        {unreadCount > 0 && (
          <button onClick={markAllAsRead} className="action-btn">
            <Check style={{ width: '14px', height: '14px' }} />
            Mark All Read
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          <Bell style={{ width: '36px', height: '36px', margin: '0 auto 10px', color: '#ccc' }} />
          No finance notifications yet.
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
                borderColor: item.isRead ? '#eee' : '#c8e6c9',
                backgroundColor: item.isRead ? '#ffffff' : '#f9fdf9',
                cursor: 'pointer',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'flex-start'
              }}
            >
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                  <strong style={{ fontSize: '14px', color: '#222' }}>{item.title}</strong>
                  {!item.isRead && (
                    <span className="status-badge status-approved" style={{ padding: '2px 8px', fontSize: '10px' }}>
                      NEW
                    </span>
                  )}
                </div>
                <p style={{ margin: 0, fontSize: '13px', color: '#555' }}>{item.message}</p>
              </div>
              <div style={{ fontSize: '11px', color: '#999', whiteSpace: 'nowrap', marginLeft: '16px' }}>
                {item.createdAt ? new Date(item.createdAt).toLocaleDateString() : 'Recent'}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
