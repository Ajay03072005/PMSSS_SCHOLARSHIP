import React from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getDashboardPath = () => {
    if (!user) return '/login';
    switch (user.role) {
      case 'ROLE_STUDENT': return '/student/dashboard';
      case 'ROLE_SAG_OFFICER': return '/sag/dashboard';
      case 'ROLE_FINANCE_OFFICER': return '/finance/dashboard';
      case 'ROLE_ADMIN': return '/admin/dashboard';
      case 'ROLE_SUPER_ADMIN': return '/super-admin/dashboard';
      default: return '/';
    }
  };

  return (
    <nav className="pmsss-public-nav">
      <Link to="/" className="pmsss-logo">
        <span className="logo-p">P</span>
        <span className="logo-m">M</span>
        <span className="logo-s">SSS</span>
        <span className="logo-subtitle">Scholarship Portal</span>
      </Link>

      <ul style={{ display: 'flex', listStyle: 'none', gap: '36px', alignItems: 'center', margin: 0, padding: 0 }}>
        <li>
          <NavLink
            to="/"
            style={({ isActive }) => ({
              color: isActive ? '#ffffff' : 'rgba(255, 255, 255, 0.85)',
              textDecoration: 'none',
              fontSize: '13px',
              fontWeight: '600',
              letterSpacing: '1px'
            })}
          >
            HOME
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/student/application"
            style={({ isActive }) => ({
              color: isActive ? '#ffffff' : 'rgba(255, 255, 255, 0.85)',
              textDecoration: 'none',
              fontSize: '13px',
              fontWeight: '600',
              letterSpacing: '1px'
            })}
          >
            APPLY
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/status"
            style={({ isActive }) => ({
              color: isActive ? '#ffffff' : 'rgba(255, 255, 255, 0.85)',
              textDecoration: 'none',
              fontSize: '13px',
              fontWeight: '600',
              letterSpacing: '1px'
            })}
          >
            STATUS
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/about"
            style={({ isActive }) => ({
              color: isActive ? '#ffffff' : 'rgba(255, 255, 255, 0.85)',
              textDecoration: 'none',
              fontSize: '13px',
              fontWeight: '600',
              letterSpacing: '1px'
            })}
          >
            ABOUT
          </NavLink>
        </li>
        <li>
          {user ? (
            <Link
              to={getDashboardPath()}
              style={{
                backgroundColor: '#d32f2f',
                color: 'white',
                padding: '9px 20px',
                borderRadius: '6px',
                textDecoration: 'none',
                fontSize: '13px',
                fontWeight: '600',
                letterSpacing: '1px'
              }}
            >
              MY DASHBOARD
            </Link>
          ) : (
            <Link
              to="/login"
              style={{
                backgroundColor: 'rgba(211, 47, 47, 0.15)',
                color: 'white',
                padding: '9px 22px',
                borderRadius: '6px',
                border: '1px solid #d32f2f',
                textDecoration: 'none',
                fontSize: '13px',
                fontWeight: '600',
                letterSpacing: '1px'
              }}
            >
              LOGIN
            </Link>
          )}
        </li>
      </ul>
    </nav>
  );
}
