import React, { useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import TopBar from './TopBar';
import Footer from './Footer';
import { useAuth } from '../../context/AuthContext';

export default function AppLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();

  // Public paths should use the public layout even if a token is present
  const isPublicPath = ['/', '/login', '/register', '/status', '/track-status', '/about'].includes(location.pathname);
  const showPortalLayout = isAuthenticated && !isPublicPath;

  // Determine portal page title
  const getPageTitle = (path) => {
    if (path.includes('/dashboard')) return 'Dashboard Overview';
    if (path.includes('/profile')) return 'Student Profile';
    if (path.includes('/application')) return 'Scholarship Application';
    if (path.includes('/documents')) return 'Document Verification & Management';
    if (path.includes('/eligibility')) return 'Eligibility Advisor';
    if (path.includes('/chat')) return 'PMSSS Virtual Assistant';
    if (path.includes('/payment')) return 'Disbursement & Payments';
    if (path.includes('/notifications')) return 'Notifications & Alerts';
    if (path.includes('/corrections')) return 'Application Corrections';
    if (path.includes('/escalations')) return 'Application Escalations';
    if (path.includes('/workload')) return 'Officer Workload & Rebalancing';
    if (path.includes('/users')) return 'User Account Management';
    if (path.includes('/officers')) return 'Officer Directory';
    if (path.includes('/assignments')) return 'Application Assignments';
    if (path.includes('/duplicates')) return 'Duplicate Applications Review';
    if (path.includes('/anomalies')) return 'Anomaly Detection Center';
    if (path.includes('/audit-logs')) return 'System Audit Logs';
    if (path.includes('/settings')) return 'System Configuration';
    if (path.includes('/roles')) return 'Roles & Permission Management';
    return 'PMSSS Scholarship Portal';
  };

  if (showPortalLayout) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
        <div className="portal-main-content">
          <TopBar
            onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
            title={getPageTitle(location.pathname)}
          />
          <Outlet />
        </div>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(135deg, #434343 0%, #262626 100%)', color: 'white', display: 'flex', flexDirection: 'column' }}>
      <Navbar />
      <main style={{ flex: 1 }}>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
