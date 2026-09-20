import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, Users, ShieldAlert, Settings, FileText, Activity } from 'lucide-react';

export default function SuperAdminDashboard() {
  return (
    <div>
      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fce4ec', color: '#c2185b' }}>
            <ShieldAlert />
          </div>
          <h3>System Security Level</h3>
          <div className="stat-value">Tier 1</div>
          <div className="stat-change positive">Spring Security & JWT Active</div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#e3f2fd', color: '#1565c0' }}>
            <Users />
          </div>
          <h3>Registered Users</h3>
          <div className="stat-value">1,284</div>
          <div className="stat-change positive">+28 registered this week</div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#e8f5e9', color: '#2e7d32' }}>
            <Activity />
          </div>
          <h3>System Health</h3>
          <div className="stat-value">99.98%</div>
          <div className="stat-change positive">All Microservices Operational</div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fff3e0', color: '#e65100' }}>
            <FileText />
          </div>
          <h3>Total Submissions</h3>
          <div className="stat-value">842</div>
          <div className="stat-change">Active scholarship cycle</div>
        </div>
      </div>

      {/* Quick Navigation Cards */}
      <div className="content-section">
        <div className="section-header">
          <h2>Super Administrator System Operations</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Elevated privileges: Role permissions, system parameters, and immutable security audit logs.
          </p>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '16px' }}>
          <Link
            to="/super-admin/roles"
            style={{ textDecoration: 'none', border: '1px solid #eee', borderRadius: '8px', padding: '20px', background: '#fafafa', display: 'block', transition: 'transform 0.2s' }}
          >
            <ShieldCheck style={{ width: '28px', height: '28px', color: '#d32f2f', marginBottom: '10px' }} />
            <h3 style={{ fontSize: '15px', color: '#222', margin: '0 0 6px 0', fontWeight: '700' }}>
              Roles & Permission Matrix
            </h3>
            <p style={{ fontSize: '12.5px', color: '#666', margin: 0, lineHeight: 1.4 }}>
              Configure granular permission sets (Review, Approve, Disburse, Rebalance) mapped to roles.
            </p>
          </Link>

          <Link
            to="/super-admin/users"
            style={{ textDecoration: 'none', border: '1px solid #eee', borderRadius: '8px', padding: '20px', background: '#fafafa', display: 'block' }}
          >
            <Users style={{ width: '28px', height: '28px', color: '#1976d2', marginBottom: '10px' }} />
            <h3 style={{ fontSize: '15px', color: '#222', margin: '0 0 6px 0', fontWeight: '700' }}>
              User Directory & Roles
            </h3>
            <p style={{ fontSize: '12.5px', color: '#666', margin: 0, lineHeight: 1.4 }}>
              Assign system roles: Student, SAG Officer, Finance Officer, or Administrator.
            </p>
          </Link>

          <Link
            to="/super-admin/settings"
            style={{ textDecoration: 'none', border: '1px solid #eee', borderRadius: '8px', padding: '20px', background: '#fafafa', display: 'block' }}
          >
            <Settings style={{ width: '28px', height: '28px', color: '#388e3c', marginBottom: '10px' }} />
            <h3 style={{ fontSize: '15px', color: '#222', margin: '0 0 6px 0', fontWeight: '700' }}>
              System Configuration
            </h3>
            <p style={{ fontSize: '12.5px', color: '#666', margin: 0, lineHeight: 1.4 }}>
              Configure SLA thresholds, maximum officer workload, and notification channel switches.
            </p>
          </Link>

          <Link
            to="/super-admin/audit-logs"
            style={{ textDecoration: 'none', border: '1px solid #eee', borderRadius: '8px', padding: '20px', background: '#fafafa', display: 'block' }}
          >
            <Activity style={{ width: '28px', height: '28px', color: '#f57c00', marginBottom: '10px' }} />
            <h3 style={{ fontSize: '15px', color: '#222', margin: '0 0 6px 0', fontWeight: '700' }}>
              Immutable Audit Logs
            </h3>
            <p style={{ fontSize: '12.5px', color: '#666', margin: 0, lineHeight: 1.4 }}>
              View tamper-proof audit trails of all logins, assignments, approvals, and disbursements.
            </p>
          </Link>
        </div>
      </div>
    </div>
  );
}
