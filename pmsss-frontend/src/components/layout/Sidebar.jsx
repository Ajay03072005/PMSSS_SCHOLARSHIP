import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  User,
  FileText,
  UploadCloud,
  CheckSquare,
  Bot,
  CreditCard,
  Bell,
  LogOut,
  Briefcase,
  Layers,
  AlertTriangle,
  ArrowUpRight,
  Users,
  UserCheck,
  ClipboardList,
  Scale,
  Copy,
  ShieldCheck,
  Settings,
  ShieldAlert
} from 'lucide-react';

export default function Sidebar({ isOpen, onClose }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const role = user.role || '';

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // 1. Student Sidebar
  const studentLinks = [
    { to: '/student/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/student/profile', label: 'My Profile', icon: User },
    { to: '/student/application', label: 'My Application', icon: FileText },
    { to: '/student/documents', label: 'Documents', icon: UploadCloud },
    { to: '/student/eligibility', label: 'Eligibility', icon: CheckSquare },
    { to: '/student/chat', label: 'AI Assistant', icon: Bot },
    { to: '/student/payment', label: 'Payment', icon: CreditCard },
    { to: '/student/notifications', label: 'Notifications', icon: Bell }
  ];

  // 2. SAG Officer Sidebar
  const sagLinks = [
    { to: '/sag/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/sag/applications', label: 'My Applications', icon: FileText },
    { to: '/sag/documents', label: 'Documents', icon: UploadCloud },
    { to: '/sag/corrections', label: 'Corrections', icon: AlertTriangle },
    { to: '/sag/escalations', label: 'Escalations', icon: ArrowUpRight },
    { to: '/sag/notifications', label: 'Notifications', icon: Bell }
  ];

  // 3. Finance Sidebar
  const financeLinks = [
    { to: '/finance/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/finance/applications', label: 'Finance Applications', icon: Layers },
    { to: '/finance/payments', label: 'Payments', icon: CreditCard },
    { to: '/finance/notifications', label: 'Notifications', icon: Bell }
  ];

  // 4. Admin Sidebar
  const adminLinks = [
    { to: '/admin/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/admin/applications', label: 'Applications', icon: FileText },
    { to: '/admin/users', label: 'Users', icon: Users },
    { to: '/admin/officers', label: 'Officers', icon: UserCheck },
    { to: '/admin/assignments', label: 'Assignments', icon: ClipboardList },
    { to: '/admin/workload', label: 'Workload', icon: Scale },
    { to: '/admin/documents', label: 'Documents', icon: UploadCloud },
    { to: '/admin/duplicates', label: 'Duplicates', icon: Copy },
    { to: '/admin/anomalies', label: 'Anomalies', icon: AlertTriangle },
    { to: '/admin/notifications', label: 'Notifications', icon: Bell },
    { to: '/admin/audit-logs', label: 'Audit Logs', icon: ShieldCheck },
    { to: '/admin/settings', label: 'Settings', icon: Settings }
  ];

  // 5. Super Admin Sidebar
  const superAdminLinks = [
    { to: '/super-admin/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/super-admin/users', label: 'Users', icon: Users },
    { to: '/super-admin/roles', label: 'Roles & Permissions', icon: ShieldAlert },
    { to: '/super-admin/settings', label: 'System Settings', icon: Settings },
    { to: '/super-admin/audit-logs', label: 'Audit Logs', icon: ShieldCheck }
  ];

  let links = [];
  let portalTitle = 'PMSSS Portal';
  let portalSubtitle = 'Government of India';

  if (role === 'ROLE_STUDENT') {
    links = studentLinks;
    portalTitle = 'PMSSS Student';
    portalSubtitle = 'Student Portal';
  } else if (role === 'ROLE_SAG_OFFICER') {
    links = sagLinks;
    portalTitle = 'PMSSS SAG';
    portalSubtitle = 'Verification Officer';
  } else if (role === 'ROLE_FINANCE_OFFICER') {
    links = financeLinks;
    portalTitle = 'PMSSS Finance';
    portalSubtitle = 'DBT & Disbursement';
  } else if (role === 'ROLE_ADMIN') {
    links = adminLinks;
    portalTitle = 'PMSSS Admin';
    portalSubtitle = 'Administrator Console';
  } else if (role === 'ROLE_SUPER_ADMIN') {
    links = superAdminLinks;
    portalTitle = 'PMSSS Super Admin';
    portalSubtitle = 'System Controller';
  }

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div
          onClick={onClose}
          className="fixed inset-0 bg-black/60 z-40 lg:hidden"
        />
      )}

      <aside
        className={`admin-sidebar transition-transform duration-200 ease-in-out ${
          isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        {/* Sidebar Header from Existing UI */}
        <div className="sidebar-header">
          <h2>{portalTitle}</h2>
          <p>{portalSubtitle}</p>
        </div>

        {/* Sidebar Menu */}
        <nav className="sidebar-menu">
          {links.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={onClose}
                className={({ isActive }) =>
                  `menu-item ${isActive ? 'active' : ''}`
                }
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
                  <Icon style={{ width: '18px', height: '18px', opacity: 0.9 }} />
                  <span>{item.label}</span>
                </div>
              </NavLink>
            );
          })}
        </nav>

        {/* Pinned Logout Button from Existing UI */}
        <button onClick={handleLogout} className="logout-btn">
          <LogOut style={{ width: '16px', height: '16px' }} />
          <span>Logout</span>
        </button>
      </aside>
    </>
  );
}
