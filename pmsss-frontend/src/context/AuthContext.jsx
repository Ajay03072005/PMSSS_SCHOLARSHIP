import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('pmsss_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('pmsss_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      authApi.getMe()
        .then((res) => {
          const userData = res?.data || res;
          if (userData) {
            if (userData.role && !userData.role.startsWith('ROLE_')) {
              userData.role = `ROLE_${userData.role.toUpperCase()}`;
            }
            setUser(userData);
            localStorage.setItem('pmsss_user', JSON.stringify(userData));
          }
        })
        .catch(() => {
          logout();
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const res = await authApi.login(email, password);
    const payload = res.data?.data || res.data || res;
    const jwtToken = payload.token || payload.jwt;
    const userData = payload.user || payload;
    if (userData && userData.role) {
      userData.role = userData.role.startsWith('ROLE_') ? userData.role : `ROLE_${userData.role.toUpperCase()}`;
    }
    setToken(jwtToken);
    setUser(userData);
    localStorage.setItem('pmsss_token', jwtToken);
    if (payload.refreshToken) {
      localStorage.setItem('pmsss_refresh_token', payload.refreshToken);
    }
    localStorage.setItem('pmsss_user', JSON.stringify(userData));
    return userData;
  };

  const register = async (data) => {
    const res = await authApi.register(data);
    return res;
  };

  const logout = async () => {
    const refreshToken = localStorage.getItem('pmsss_refresh_token');
    if (refreshToken) {
      try {
        await authApi.logout(refreshToken);
      } catch {
        // Clear local state even if the server is unavailable.
      }
    }
    setToken(null);
    setUser(null);
    localStorage.removeItem('pmsss_token');
    localStorage.removeItem('pmsss_refresh_token');
    localStorage.removeItem('pmsss_user');
  };

  const hasRole = (role) => {
    if (!user) return false;
    const cleanRole = role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    return user.role === cleanRole;
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, isAuthenticated: !!user, login, register, logout, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
