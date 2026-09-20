import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ProtectedRoute from './ProtectedRoute';

export default function RoleBasedRoute({ allowedRoles, children }) {
  const { user } = useAuth();

  return (
    <ProtectedRoute>
      {(() => {
        if (!user) return null;
        const userRole = user.role;
        const hasAccess = allowedRoles.includes(userRole);

        if (!hasAccess) {
          // Redirect to appropriate landing based on user's actual role
          if (userRole === 'ROLE_STUDENT') {
            return <Navigate to="/student/dashboard" replace />;
          } else if (userRole === 'ROLE_SAG_OFFICER') {
            return <Navigate to="/sag/dashboard" replace />;
          } else if (userRole === 'ROLE_FINANCE_OFFICER') {
            return <Navigate to="/finance/dashboard" replace />;
          } else if (userRole === 'ROLE_ADMIN') {
            return <Navigate to="/admin/dashboard" replace />;
          } else if (userRole === 'ROLE_SUPER_ADMIN') {
            return <Navigate to="/super-admin/dashboard" replace />;
          }
          return <Navigate to="/" replace />;
        }

        return children;
      })()}
    </ProtectedRoute>
  );
}
