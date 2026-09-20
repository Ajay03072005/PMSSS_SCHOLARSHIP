import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import AppLayout from '../components/layout/AppLayout';
import RoleBasedRoute from './RoleBasedRoute';

// Public & Auth
import Home from '../pages/public/Home';
import TrackStatus from '../pages/public/TrackStatus';
import About from '../pages/public/About';
import Login from '../pages/auth/Login';
import Register from '../pages/auth/Register';

// Student Portal
import StudentDashboard from '../pages/student/StudentDashboard';
import StudentProfile from '../pages/student/StudentProfile';
import ApplicationWizard from '../pages/student/ApplicationWizard';
import ApplicationView from '../pages/student/ApplicationView';
import DocumentManager from '../pages/student/DocumentManager';
import EligibilityAdvisor from '../pages/student/EligibilityAdvisor';
import StudentChatbot from '../pages/student/StudentChatbot';
import StudentPayments from '../pages/student/StudentPayments';
import StudentNotifications from '../pages/student/StudentNotifications';

// SAG Officer Portal
import SagDashboard from '../pages/sag/SagDashboard';
import SagApplications from '../pages/sag/SagApplications';
import ApplicationReview from '../pages/officer/ApplicationReview';
import SagDocuments from '../pages/sag/SagDocuments';
import SagCorrections from '../pages/sag/SagCorrections';
import SagEscalations from '../pages/sag/SagEscalations';
import SagNotifications from '../pages/sag/SagNotifications';

// Finance Officer Portal
import FinanceDashboard from '../pages/finance/FinanceDashboard';
import FinanceApplications from '../pages/finance/FinanceApplications';
import FinanceApplicationDetail from '../pages/finance/FinanceApplicationDetail';
import PaymentProcessing from '../pages/finance/PaymentProcessing';
import PaymentDetail from '../pages/finance/PaymentDetail';
import FinanceNotifications from '../pages/finance/FinanceNotifications';

// Admin Portal
import AdminDashboard from '../pages/admin/AdminDashboard';
import ApplicationManager from '../pages/admin/ApplicationManager';
import AdminApplicationDetail from '../pages/admin/AdminApplicationDetail';
import UserManager from '../pages/admin/UserManager';
import OfficerManager from '../pages/admin/OfficerManager';
import AssignmentManager from '../pages/admin/AssignmentManager';
import WorkloadCenter from '../pages/admin/WorkloadCenter';
import DocumentCenter from '../pages/admin/DocumentCenter';
import DuplicateReview from '../pages/admin/DuplicateReview';
import AnomalyCenter from '../pages/admin/AnomalyCenter';
import NotificationHistory from '../pages/admin/NotificationHistory';
import AuditLogViewer from '../pages/admin/AuditLogViewer';
import SystemSettings from '../pages/admin/SystemSettings';

// Super Admin Portal
import SuperAdminDashboard from '../pages/superadmin/SuperAdminDashboard';
import RoleManager from '../pages/superadmin/RoleManager';

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        {/* ================= PUBLIC ROUTES ================= */}
        <Route path="/" element={<Home />} />
        <Route path="/status" element={<TrackStatus />} />
        <Route path="/track-status" element={<TrackStatus />} />
        <Route path="/about" element={<About />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/apply" element={<Navigate to="/student/application" replace />} />

        {/* ================= STUDENT PORTAL ================= */}
        <Route
          path="/student/dashboard"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT']}>
              <StudentDashboard />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/student/profile"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT']}>
              <StudentProfile />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/student/application"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT']}>
              <ApplicationWizard />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/student/apply"
          element={<Navigate to="/student/application" replace />}
        />
        <Route
          path="/student/application/:id"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <ApplicationView />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/student/documents"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT']}>
              <DocumentManager />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/student/eligibility"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT']}>
              <EligibilityAdvisor />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/student/chat"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT']}>
              <StudentChatbot />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/student/payment"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT']}>
              <StudentPayments />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/student/notifications"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_STUDENT']}>
              <StudentNotifications />
            </RoleBasedRoute>
          }
        />

        {/* ================= SAG OFFICER PORTAL ================= */}
        <Route
          path="/sag/dashboard"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SAG_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <SagDashboard />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/sag/applications"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SAG_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <SagApplications />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/sag/applications/:id"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SAG_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <ApplicationReview />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/sag/documents"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SAG_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <SagDocuments />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/sag/corrections"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SAG_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <SagCorrections />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/sag/escalations"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SAG_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <SagEscalations />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/sag/notifications"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SAG_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <SagNotifications />
            </RoleBasedRoute>
          }
        />

        {/* Officer legacy redirects to SAG portal */}
        <Route path="/officer/dashboard" element={<Navigate to="/sag/dashboard" replace />} />
        <Route path="/officer/queue" element={<Navigate to="/sag/applications" replace />} />
        <Route path="/officer/review/:id" element={<Navigate to="/sag/applications/:id" replace />} />

        {/* ================= FINANCE OFFICER PORTAL ================= */}
        <Route
          path="/finance/dashboard"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_FINANCE_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <FinanceDashboard />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/finance/applications"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_FINANCE_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <FinanceApplications />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/finance/applications/:id"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_FINANCE_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <FinanceApplicationDetail />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/finance/payments"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_FINANCE_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <PaymentProcessing />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/finance/payments/:id"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_FINANCE_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <PaymentDetail />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/finance/notifications"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_FINANCE_OFFICER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <FinanceNotifications />
            </RoleBasedRoute>
          }
        />

        {/* ================= ADMIN PORTAL ================= */}
        <Route
          path="/admin/dashboard"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <AdminDashboard />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/applications"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <ApplicationManager />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/applications/:id"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <AdminApplicationDetail />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/users"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <UserManager />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/officers"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <OfficerManager />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/assignments"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <AssignmentManager />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/workload"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <WorkloadCenter />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/documents"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <DocumentCenter />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/duplicates"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <DuplicateReview />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/anomalies"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <AnomalyCenter />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/notifications"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <NotificationHistory />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/admin/audit-logs"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <AuditLogViewer />
            </RoleBasedRoute>
          }
        />
        <Route path="/admin/audit" element={<Navigate to="/admin/audit-logs" replace />} />
        <Route
          path="/admin/settings"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}>
              <SystemSettings />
            </RoleBasedRoute>
          }
        />

        {/* ================= SUPER ADMIN PORTAL ================= */}
        <Route
          path="/super-admin/dashboard"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SUPER_ADMIN']}>
              <SuperAdminDashboard />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/super-admin/users"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SUPER_ADMIN']}>
              <UserManager />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/super-admin/roles"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SUPER_ADMIN']}>
              <RoleManager />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/super-admin/settings"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SUPER_ADMIN']}>
              <SystemSettings />
            </RoleBasedRoute>
          }
        />
        <Route
          path="/super-admin/audit-logs"
          element={
            <RoleBasedRoute allowedRoles={['ROLE_SUPER_ADMIN']}>
              <AuditLogViewer />
            </RoleBasedRoute>
          }
        />

        {/* Catch-all */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
