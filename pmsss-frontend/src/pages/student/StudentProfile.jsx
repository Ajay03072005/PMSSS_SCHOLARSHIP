import React, { useState, useEffect } from 'react';
import { studentApi } from '../../api/studentApi';
import { useAuth } from '../../context/AuthContext';
import { User, Mail, Phone, Calendar, MapPin, Building, CreditCard, Save, CheckCircle, AlertCircle } from 'lucide-react';

export default function StudentProfile() {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    dob: '2004-06-15',
    gender: 'Male',
    category: 'General',
    domicileState: 'Jammu & Kashmir',
    domicileDistrict: 'Srinagar',
    address: 'House No. 42, Rajbagh, Srinagar, J&K',
    bankAccountNo: '987654321012',
    ifscCode: 'SBIN0001234',
    bankName: 'State Bank of India',
    branchName: 'Main Branch Srinagar'
  });

  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      setFetching(true);
      const res = await studentApi.getProfile();
      const p = res.data || res;
      setFormData(prev => ({
        ...prev,
        ...p,
        fullName: user?.fullName || (user?.firstName ? `${user.firstName} ${user.lastName || ''}`.trim() : prev.fullName),
        email: user?.email || prev.email,
        phone: user?.mobile || prev.phone,
        domicileState: p.state || prev.domicileState,
        domicileDistrict: p.domicileDistrict || prev.domicileDistrict,
        address: p.currentAddress || p.permanentAddress || prev.address
      }));
    } catch (err) {
      console.warn('Profile fetch note:', err);
    } finally {
      setFetching(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);
    setLoading(true);

    try {
      await studentApi.updateProfile(formData);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update profile.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="pb-4 border-b border-slate-800">
        <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
          Student Profile & Identity
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Ensure details match your official 10th/12th certificate, Domicile certificate, and Bank passbook
        </p>
      </div>

      {success && (
        <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-sm flex items-center gap-3">
          <CheckCircle className="w-5 h-5 shrink-0" />
          <span>Profile updated and synchronized successfully!</span>
        </div>
      )}

      {error && (
        <div className="p-4 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm flex items-center gap-3">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Section 1: Basic Information */}
        <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <h2 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
            <User className="w-4 h-4 text-[#d32f2f]" />
            1. Personal & Identity Details
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Full Name</label>
              <input
                type="text"
                name="fullName"
                required
                value={formData.fullName}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Email Address</label>
              <input
                type="email"
                name="email"
                disabled
                value={formData.email}
                className="w-full px-3 py-2 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-400 text-xs cursor-not-allowed"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Mobile Number</label>
              <input
                type="tel"
                name="phone"
                required
                value={formData.phone}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Date of Birth</label>
              <input
                type="date"
                name="dob"
                value={formData.dob}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Gender</label>
              <select
                name="gender"
                value={formData.gender}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              >
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Social Category</label>
              <select
                name="category"
                value={formData.category}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              >
                <option value="General">General / Open Merit</option>
                <option value="SC">Scheduled Caste (SC)</option>
                <option value="ST">Scheduled Tribe (ST)</option>
                <option value="SEBC">Socially and Economically Backward Classes</option>
                <option value="PH">Physically Handicapped (PwD)</option>
              </select>
            </div>
          </div>
        </div>

        {/* Section 2: Domicile & Residential Address */}
        <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <h2 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
            <MapPin className="w-4 h-4 text-[#d32f2f]" />
            2. Domicile & Address (UT of J&K / Ladakh)
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">State / UT</label>
              <select
                name="domicileState"
                value={formData.domicileState}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              >
                <option value="Jammu & Kashmir">Jammu & Kashmir</option>
                <option value="Ladakh">Ladakh</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">District</label>
              <input
                type="text"
                name="domicileDistrict"
                value={formData.domicileDistrict}
                onChange={handleChange}
                placeholder="e.g. Srinagar / Jammu / Leh"
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              />
            </div>

            <div className="sm:col-span-2">
              <label className="block text-xs font-medium text-slate-400 mb-1">Complete Postal Address</label>
              <textarea
                name="address"
                rows={2}
                value={formData.address}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              />
            </div>
          </div>
        </div>

        {/* Section 3: Bank Account for DBT */}
        <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
              <CreditCard className="w-4 h-4 text-emerald-400" />
              3. DBT Bank Account (Aadhaar Seeded)
            </h2>
            <span className="text-[11px] text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20 font-medium">
              Must be active student account
            </span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Savings Account Number</label>
              <input
                type="text"
                name="bankAccountNo"
                required
                value={formData.bankAccountNo}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs font-mono focus:outline-none focus:border-[#d32f2f]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Bank IFSC Code</label>
              <input
                type="text"
                name="ifscCode"
                required
                maxLength={11}
                value={formData.ifscCode}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs font-mono uppercase focus:outline-none focus:border-[#d32f2f]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Bank Name</label>
              <input
                type="text"
                name="bankName"
                value={formData.bankName}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Branch Name</label>
              <input
                type="text"
                name="branchName"
                value={formData.branchName}
                onChange={handleChange}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:outline-none focus:border-[#d32f2f]"
              />
            </div>
          </div>
        </div>

        <div className="flex justify-end pt-2">
          <button
            type="submit"
            disabled={loading}
            className="px-6 py-2.5 rounded-xl bg-[#d32f2f] hover:bg-[#b71c1c] text-white font-medium text-xs shadow-lg shadow-red-500/20 flex items-center gap-2 transition disabled:opacity-50"
          >
            {loading ? (
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            ) : (
              <>
                <Save className="w-4 h-4" />
                <span>Save Profile Information</span>
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
}
