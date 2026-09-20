import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNotifications } from '../../context/NotificationContext';
import { Bell, Menu, CheckCircle, ExternalLink } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function TopBar({ onToggleSidebar, title = 'Dashboard' }) {
  const { user } = useAuth();
  const { unreadCount, notifications, markAsRead, markAllAsRead } = useNotifications();
  const [showNotifs, setShowNotifs] = useState(false);

  const getInitials = (name) => {
    if (!name) return 'U';
    return name
      .split(' ')
      .map((n) => n[0])
      .join('')
      .substring(0, 2)
      .toUpperCase();
  };

  const getRoleLabel = (role) => {
    switch (role) {
      case 'ROLE_SUPER_ADMIN': return 'Super Admin';
      case 'ROLE_ADMIN': return 'Admin';
      case 'ROLE_SAG_OFFICER': return 'SAG Officer';
      case 'ROLE_FINANCE_OFFICER': return 'Finance Officer';
      case 'ROLE_STUDENT': return 'Student';
      default: return 'User';
    }
  };

  const displayName = user ? (user.name || `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email) : 'User';

  return (
    <header className="top-bar">
      <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
        {onToggleSidebar && (
          <button
            onClick={onToggleSidebar}
            className="lg:hidden p-2 rounded-lg text-gray-600 hover:bg-gray-100"
            title="Toggle Sidebar"
          >
            <Menu style={{ width: '22px', height: '22px' }} />
          </button>
        )}
        <h1>{title}</h1>
      </div>

      <div className="admin-info">
        {/* Notification Bell Dropdown */}
        <div className="relative">
          <button
            onClick={() => setShowNotifs(!showNotifs)}
            className="p-2 rounded-full hover:bg-gray-100 relative transition-colors"
            title="Notifications"
          >
            <Bell style={{ width: '20px', height: '20px', color: '#555' }} />
            {unreadCount > 0 && (
              <span
                style={{
                  position: 'absolute',
                  top: '2px',
                  right: '2px',
                  width: '18px',
                  height: '18px',
                  backgroundColor: '#d32f2f',
                  color: 'white',
                  borderRadius: '50%',
                  fontSize: '10px',
                  fontWeight: '700',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }}
              >
                {unreadCount > 9 ? '9+' : unreadCount}
              </span>
            )}
          </button>

          {/* Notifications Dropdown Panel */}
          {showNotifs && (
            <div
              className="absolute right-0 mt-2 w-80 bg-white rounded-xl shadow-xl border border-gray-200 z-50 overflow-hidden"
              style={{ minWidth: '300px' }}
            >
              <div
                style={{
                  padding: '12px 16px',
                  borderBottom: '1px solid #eee',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  background: '#fafafa'
                }}
              >
                <span style={{ fontSize: '13px', fontWeight: '700', color: '#333' }}>
                  Notifications ({unreadCount})
                </span>
                {unreadCount > 0 && (
                  <button
                    onClick={markAllAsRead}
                    style={{ fontSize: '11px', color: '#d32f2f', fontWeight: '600', cursor: 'pointer', background: 'none', border: 'none' }}
                  >
                    Mark all read
                  </button>
                )}
              </div>

              <div style={{ maxHeight: '300px', overflowY: 'auto' }}>
                {notifications.length === 0 ? (
                  <div style={{ padding: '24px', textAlign: 'center', color: '#888', fontSize: '12px' }}>
                    No notifications yet
                  </div>
                ) : (
                  notifications.slice(0, 5).map((n) => (
                    <div
                      key={n.id}
                      onClick={() => markAsRead(n.id)}
                      style={{
                        padding: '12px 16px',
                        borderBottom: '1px solid #f5f5f5',
                        background: n.isRead ? '#ffffff' : '#fff8f8',
                        cursor: 'pointer'
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                        <span style={{ fontSize: '12px', fontWeight: '600', color: '#222' }}>{n.title}</span>
                        {!n.isRead && <span style={{ width: '6px', height: '6px', backgroundColor: '#d32f2f', borderRadius: '50%' }} />}
                      </div>
                      <p style={{ fontSize: '11.5px', color: '#666', margin: 0, lineHeight: 1.4 }}>{n.message}</p>
                    </div>
                  ))
                )}
              </div>

              <div style={{ padding: '10px 16px', borderTop: '1px solid #eee', textAlign: 'center', background: '#fafafa' }}>
                <Link
                  to={user?.role === 'ROLE_STUDENT' ? '/student/notifications' : '/admin/notifications'}
                  onClick={() => setShowNotifs(false)}
                  style={{ fontSize: '12px', color: '#d32f2f', fontWeight: '600', textDecoration: 'none' }}
                >
                  View all notifications →
                </Link>
              </div>
            </div>
          )}
        </div>

        {/* User Info & Avatar */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div className="admin-avatar">
            {getInitials(displayName)}
          </div>
          <div className="hidden sm:block">
            <div style={{ fontSize: '13.5px', fontWeight: '700', color: '#222' }}>{displayName}</div>
            <div style={{ fontSize: '11px', color: '#777', fontWeight: '500' }}>{getRoleLabel(user?.role)}</div>
          </div>
        </div>
      </div>
    </header>
  );
}
