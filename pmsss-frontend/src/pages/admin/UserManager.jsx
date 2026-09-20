import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { Users, UserCheck, Shield, CheckCircle, XCircle } from 'lucide-react';

export default function UserManager() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');

  useEffect(() => {
    async function loadUsers() {
      try {
        setLoading(true);
        const res = await adminApi.getUsers({ page: 0, size: 100 });
        const data = res?.data?.content || res?.data || res;
        setUsers(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error('Error fetching users:', err);
      } finally {
        setLoading(false);
      }
    }
    loadUsers();
  }, []);

  const handleRoleChange = async (userId, newRole) => {
    try {
      await adminApi.updateUserRole(userId, newRole);
      setUsers((prev) =>
        prev.map((u) => (u.id === userId ? { ...u, role: newRole } : u))
      );
    } catch (err) {
      console.error('Error updating role:', err);
    }
  };

  const handleToggleStatus = async (userId, currentStatus) => {
    try {
      await adminApi.toggleUserStatus(userId, !currentStatus);
      setUsers((prev) =>
        prev.map((u) => (u.id === userId ? { ...u, isActive: !currentStatus } : u))
      );
    } catch (err) {
      console.error('Error toggling status:', err);
    }
  };

  const filtered = users.filter((u) => {
    const userRole = u.role || '';
    if (roleFilter !== 'ALL' && userRole !== roleFilter) return false;
    if (!search.trim()) return true;
    const q = search.toLowerCase();
    const name = u.fullName || u.name || '';
    return name.toLowerCase().includes(q) || (u.email || '').toLowerCase().includes(q);
  });

  return (
    <div className="content-section">
      <div className="section-header">
        <div>
          <h2>User Account & Access Management</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Manage user directory, role permissions, and active login state.
          </p>
        </div>

        <div className="filters">
          <button
            onClick={() => setRoleFilter('ALL')}
            className={`filter-btn ${roleFilter === 'ALL' ? 'active' : ''}`}
          >
            All Roles
          </button>
          <button
            onClick={() => setRoleFilter('ROLE_STUDENT')}
            className={`filter-btn ${roleFilter === 'ROLE_STUDENT' ? 'active' : ''}`}
          >
            Students
          </button>
          <button
            onClick={() => setRoleFilter('ROLE_SAG_OFFICER')}
            className={`filter-btn ${roleFilter === 'ROLE_SAG_OFFICER' ? 'active' : ''}`}
          >
            SAG Officers
          </button>
          <button
            onClick={() => setRoleFilter('ROLE_FINANCE_OFFICER')}
            className={`filter-btn ${roleFilter === 'ROLE_FINANCE_OFFICER' ? 'active' : ''}`}
          >
            Finance Officers
          </button>
        </div>
      </div>

      <div className="search-box" style={{ marginBottom: '16px' }}>
        <input
          type="text"
          placeholder="Search by User Name or Email..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ width: '100%', maxWidth: '380px', padding: '9px 14px', border: '1px solid #ddd', borderRadius: '6px', fontSize: '13.5px' }}
        />
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
          Loading users...
        </div>
      ) : (
        <table className="applications-table">
          <thead>
            <tr>
              <th>User ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Mobile</th>
              <th>Assigned Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((user) => (
              <tr key={user.id}>
                <td><strong>#{user.id}</strong></td>
                <td><strong>{user.fullName || user.name || 'N/A'}</strong></td>
                <td>{user.email}</td>
                <td>{user.mobile || user.phone || 'N/A'}</td>
                <td>
                  <select
                    value={user.role || 'ROLE_STUDENT'}
                    onChange={(e) => handleRoleChange(user.id, e.target.value)}
                    style={{
                      padding: '6px 10px',
                      border: '1px solid #ddd',
                      borderRadius: '6px',
                      fontSize: '12.5px',
                      fontWeight: '600',
                      background: '#fff'
                    }}
                  >
                    <option value="ROLE_STUDENT">Student</option>
                    <option value="ROLE_SAG_OFFICER">SAG Officer</option>
                    <option value="ROLE_FINANCE_OFFICER">Finance Officer</option>
                    <option value="ROLE_ADMIN">Admin</option>
                    <option value="ROLE_SUPER_ADMIN">Super Admin</option>
                  </select>
                </td>
                <td>
                  <span className={`status-badge ${user.isActive ? 'status-approved' : 'status-rejected'}`}>
                    {user.isActive ? 'Active' : 'Disabled'}
                  </span>
                </td>
                <td>
                  <button
                    onClick={() => handleToggleStatus(user.id, user.isActive)}
                    style={{
                      padding: '5px 10px',
                      border: '1px solid #ddd',
                      background: user.isActive ? '#fff' : '#d32f2f',
                      color: user.isActive ? '#c62828' : '#fff',
                      borderRadius: '5px',
                      cursor: 'pointer',
                      fontSize: '12px',
                      fontWeight: '600'
                    }}
                  >
                    {user.isActive ? 'Disable' : 'Enable'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
