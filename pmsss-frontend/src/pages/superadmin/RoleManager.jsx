import React, { useState } from 'react';
import { ShieldCheck, Check, Lock, Save, CheckCircle2 } from 'lucide-react';

export default function RoleManager() {
  const permissionsList = [
    { key: 'APPLICATION_VIEW', label: 'View Applications', category: 'Applications' },
    { key: 'APPLICATION_REVIEW', label: 'Review & Verify Applications', category: 'Applications' },
    { key: 'APPLICATION_APPROVE', label: 'Approve Scholarship Eligibility', category: 'Applications' },
    { key: 'APPLICATION_REJECT', label: 'Reject Application', category: 'Applications' },
    { key: 'APPLICATION_CORRECTION', label: 'Request Applicant Corrections', category: 'Applications' },
    { key: 'APPLICATION_ASSIGN', label: 'Assign & Reassign Officers', category: 'Applications' },
    { key: 'DOCUMENT_VIEW', label: 'View Submitted Documents', category: 'Documents' },
    { key: 'DOCUMENT_VERIFY', label: 'Perform OCR & Document Verification', category: 'Documents' },
    { key: 'PAYMENT_VIEW', label: 'View Disbursement Records', category: 'Payments' },
    { key: 'PAYMENT_PROCESS', label: 'Initiate DBT Payments', category: 'Payments' },
    { key: 'PAYMENT_UPDATE', label: 'Update Payment Status & Remarks', category: 'Payments' },
    { key: 'USER_VIEW', label: 'View System Users', category: 'Administration' },
    { key: 'USER_UPDATE', label: 'Modify Roles & Permissions', category: 'Administration' },
    { key: 'AUDIT_VIEW', label: 'View System Audit Logs', category: 'Administration' },
    { key: 'WORKLOAD_VIEW', label: 'Monitor Workload & Capacity', category: 'Administration' },
    { key: 'SYSTEM_SETTINGS', label: 'Modify System Configuration', category: 'Administration' }
  ];

  const [rolePermissions, setRolePermissions] = useState({
    ROLE_STUDENT: ['APPLICATION_VIEW'],
    ROLE_SAG_OFFICER: ['APPLICATION_VIEW', 'APPLICATION_REVIEW', 'APPLICATION_APPROVE', 'APPLICATION_REJECT', 'APPLICATION_CORRECTION', 'DOCUMENT_VIEW', 'DOCUMENT_VERIFY'],
    ROLE_FINANCE_OFFICER: ['APPLICATION_VIEW', 'PAYMENT_VIEW', 'PAYMENT_PROCESS', 'PAYMENT_UPDATE', 'AUDIT_VIEW'],
    ROLE_ADMIN: ['APPLICATION_VIEW', 'APPLICATION_REVIEW', 'APPLICATION_ASSIGN', 'DOCUMENT_VIEW', 'PAYMENT_VIEW', 'USER_VIEW', 'USER_UPDATE', 'AUDIT_VIEW', 'WORKLOAD_VIEW', 'SYSTEM_SETTINGS'],
    ROLE_SUPER_ADMIN: permissionsList.map((p) => p.key)
  });

  const [selectedRole, setSelectedRole] = useState('ROLE_SAG_OFFICER');
  const [saved, setSaved] = useState(false);

  const togglePermission = (permKey) => {
    if (selectedRole === 'ROLE_SUPER_ADMIN') return; // Super admin has all permissions
    setRolePermissions((prev) => {
      const current = prev[selectedRole] || [];
      const updated = current.includes(permKey)
        ? current.filter((k) => k !== permKey)
        : [...current, permKey];
      return { ...prev, [selectedRole]: updated };
    });
  };

  const handleSave = () => {
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  const activePerms = rolePermissions[selectedRole] || [];

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>Role-Based Permission Matrix</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Map discrete granular privileges to system roles to enforce least-privilege security.
          </p>
        </div>

        <button onClick={handleSave} className="action-btn">
          <Save style={{ width: '15px', height: '15px' }} />
          Save Permissions
        </button>
      </div>

      {saved && (
        <div style={{ background: '#e8f5e9', border: '1px solid #c8e6c9', borderRadius: '6px', padding: '10px 16px', marginBottom: '16px', color: '#2e7d32', fontSize: '13px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <CheckCircle2 style={{ width: '16px', height: '16px' }} /> Role permissions successfully persisted!
        </div>
      )}

      {/* Role Selection Tabs */}
      <div className="filters" style={{ marginBottom: '20px' }}>
        {[
          { key: 'ROLE_STUDENT', label: 'Student' },
          { key: 'ROLE_SAG_OFFICER', label: 'SAG Officer' },
          { key: 'ROLE_FINANCE_OFFICER', label: 'Finance Officer' },
          { key: 'ROLE_ADMIN', label: 'Administrator' },
          { key: 'ROLE_SUPER_ADMIN', label: 'Super Administrator' }
        ].map((r) => (
          <button
            key={r.key}
            onClick={() => setSelectedRole(r.key)}
            className={`filter-btn ${selectedRole === r.key ? 'active' : ''}`}
          >
            {r.label}
          </button>
        ))}
      </div>

      {/* Permissions Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '16px' }}>
        {permissionsList.map((perm) => {
          const isAssigned = activePerms.includes(perm.key);
          const isLocked = selectedRole === 'ROLE_SUPER_ADMIN';

          return (
            <div
              key={perm.key}
              onClick={() => !isLocked && togglePermission(perm.key)}
              style={{
                border: '1px solid',
                borderColor: isAssigned ? '#d32f2f' : '#e0e0e0',
                backgroundColor: isAssigned ? '#fff8f8' : '#ffffff',
                borderRadius: '8px',
                padding: '14px 18px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                cursor: isLocked ? 'default' : 'pointer',
                transition: 'all 0.15s ease'
              }}
            >
              <div>
                <span style={{ fontSize: '11px', color: '#888', textTransform: 'uppercase', fontWeight: '600' }}>
                  {perm.category}
                </span>
                <div style={{ fontSize: '13.5px', fontWeight: '600', color: '#222', marginTop: '2px' }}>
                  {perm.label}
                </div>
                <code style={{ fontSize: '11px', color: '#666' }}>{perm.key}</code>
              </div>

              <div>
                <input
                  type="checkbox"
                  checked={isAssigned}
                  disabled={isLocked}
                  onChange={() => {}}
                  style={{ width: '18px', height: '18px', accentColor: '#d32f2f', cursor: isLocked ? 'default' : 'pointer' }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
