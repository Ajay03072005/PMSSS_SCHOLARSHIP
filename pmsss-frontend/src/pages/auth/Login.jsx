import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { GraduationCap, ShieldCheck, Mail, Lock, ArrowRight, Sparkles, AlertCircle } from 'lucide-react';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const user = await login(email, password);
      const from = location.state?.from?.pathname;
      if (from) {
        navigate(from, { replace: true });
        return;
      }

      // Default role landing
      if (user.role === 'ROLE_STUDENT') navigate('/student/dashboard');
      else if (user.role === 'ROLE_SAG_OFFICER') navigate('/sag/dashboard');
      else if (user.role === 'ROLE_FINANCE_OFFICER') navigate('/finance/dashboard');
      else if (user.role === 'ROLE_ADMIN') navigate('/admin/dashboard');
      else if (user.role === 'ROLE_SUPER_ADMIN') navigate('/super-admin/dashboard');
      else navigate('/');
    } catch (err) {
      setError(err.message || 'Invalid credentials or connection error');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickLogin = (demoEmail, demoPass) => {
    setEmail(demoEmail);
    setPassword(demoPass);
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md">
        {/* Header */}
        <div className="text-center mb-8">
          <div
            style={{
              width: '64px',
              height: '64px',
              borderRadius: '16px',
              background: 'linear-gradient(135deg, #d32f2f, #b71c1c)',
              boxShadow: '0 8px 25px rgba(211, 47, 47, 0.35)',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: '16px'
            }}
          >
            <GraduationCap className="w-9 h-9 text-white" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-white font-['Poppins',sans-serif]">
            Sign In to PMSSS
          </h1>
          <p className="mt-2 text-sm text-slate-300">
            Prime Minister's Special Scholarship Scheme Portal
          </p>
        </div>

        {/* Card */}
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 sm:p-8 shadow-2xl backdrop-blur-md">
          {error && (
            <div className="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-400 text-sm flex items-start gap-3">
              <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5 uppercase tracking-wide">
                Email / Registration ID
              </label>
              <div className="relative">
                <Mail className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@pmsss.gov.in"
                  className="w-full pl-10 pr-4 py-2.5 bg-slate-950 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-[#d32f2f] focus:ring-1 focus:ring-[#d32f2f] text-sm"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5 uppercase tracking-wide">
                Password
              </label>
              <div className="relative">
                <Lock className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-4 py-2.5 bg-slate-950 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-[#d32f2f] focus:ring-1 focus:ring-[#d32f2f] text-sm"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              style={{
                backgroundColor: '#d32f2f',
                boxShadow: '0 4px 15px rgba(211, 47, 47, 0.4)'
              }}
              className="w-full mt-3 py-3 px-4 hover:bg-[#b71c1c] text-white font-semibold rounded-xl text-sm transition-all duration-150 flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                <>
                  <span>Sign In</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          {/* Quick Demo Logins */}
          <div className="mt-6 pt-6 border-t border-slate-800">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                Quick Role Logins (Demo)
              </span>
              <Sparkles className="w-3.5 h-3.5 text-[#d32f2f]" />
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 text-xs">
              <button
                type="button"
                onClick={() => handleQuickLogin('student@pmsss.gov.in', 'Student@123')}
                className="p-2.5 rounded-lg bg-slate-800 hover:bg-slate-700/80 text-slate-200 text-left border border-slate-700 transition"
              >
                <span className="font-semibold block text-red-300">🎓 Student</span>
                <span className="text-[11px] text-slate-400">student@pmsss...</span>
              </button>
              <button
                type="button"
                onClick={() => handleQuickLogin('sag.officer@pmsss.gov.in', 'Officer@123')}
                className="p-2.5 rounded-lg bg-slate-800 hover:bg-slate-700/80 text-slate-200 text-left border border-slate-700 transition"
              >
                <span className="font-semibold block text-red-300">📋 SAG Officer</span>
                <span className="text-[11px] text-slate-400">sag.officer@...</span>
              </button>
              <button
                type="button"
                onClick={() => handleQuickLogin('finance.officer@pmsss.gov.in', 'Finance@123')}
                className="p-2.5 rounded-lg bg-slate-800 hover:bg-slate-700/80 text-slate-200 text-left border border-slate-700 transition"
              >
                <span className="font-semibold block text-red-300">💳 Finance</span>
                <span className="text-[11px] text-slate-400">finance.officer@...</span>
              </button>
              <button
                type="button"
                onClick={() => handleQuickLogin('admin@pmsss.gov.in', 'Admin@123')}
                className="p-2.5 rounded-lg bg-slate-800 hover:bg-slate-700/80 text-slate-200 text-left border border-slate-700 transition"
              >
                <span className="font-semibold block text-red-300">⚡ Admin</span>
                <span className="text-[11px] text-slate-400">admin@pmsss...</span>
              </button>
              <button
                type="button"
                onClick={() => handleQuickLogin('superadmin@pmsss.gov.in', 'SuperAdmin@123')}
                className="p-2.5 rounded-lg bg-slate-800 hover:bg-slate-700/80 text-slate-200 text-left border border-slate-700 transition"
              >
                <span className="font-semibold block text-red-300">👑 Super Admin</span>
                <span className="text-[11px] text-slate-400">superadmin@...</span>
              </button>
            </div>
          </div>

          <div className="mt-6 text-center text-xs text-slate-400">
            Don't have an account yet?{' '}
            <Link to="/register" style={{ color: '#d32f2f' }} className="font-semibold hover:underline">
              Register as Student
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
