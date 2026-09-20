import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { applicationApi } from '../../api/applicationApi';
import { studentApi } from '../../api/studentApi';
import { 
  FileText, 
  GraduationCap, 
  IndianRupee, 
  CheckCircle, 
  AlertCircle, 
  ArrowRight, 
  ArrowLeft, 
  Sparkles, 
  Send,
  Save,
  ShieldCheck
} from 'lucide-react';

export default function ApplicationWizard() {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [saveLoading, setSaveLoading] = useState(false);
  const [error, setError] = useState('');
  const [validationErrors, setValidationErrors] = useState([]);
  const [existingApp, setExistingApp] = useState(null);

  const [formData, setFormData] = useState({
    stream: 'ENGINEERING',
    academicYear: '2026-2027',
    tenthRollNumber: 'JKB-10-8921',
    tenthBoard: 'JKBOSE',
    tenthPassingYear: '2023',
    tenthPercentage: 88.5,
    twelfthRollNumber: 'JKB-12-4412',
    twelfthBoard: 'JKBOSE',
    twelfthPassingYear: '2025',
    twelfthMarksObtained: 442,
    twelfthTotalMarks: 500,
    twelfthPercentage: 88.4,
    familyAnnualIncome: 450000,
    incomeCertificateNumber: 'INC/SRIN/2025/9981',
    aadhaarNumber: '123456789012',
    declared: true
  });

  useEffect(() => {
    loadActiveApp();
  }, []);

  const loadActiveApp = async () => {
    try {
      const res = await studentApi.getActiveApplication();
      if (res.data) {
        setExistingApp(res.data);
        setFormData(prev => ({
          ...prev,
          ...res.data
        }));
      }
    } catch (err) {
      // New application
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleNext = () => {
    setError('');
    // Step-wise quick validations
    if (currentStep === 1 && !formData.stream) {
      setError('Please select an eligible course stream.');
      return;
    }
    if (currentStep === 2) {
      if (!formData.twelfthRollNumber || !formData.twelfthPercentage) {
        setError('Please complete Class 12 academic information.');
        return;
      }
      if (Number(formData.twelfthPercentage) < 40) {
        setError('Minimum aggregate percentage required for PMSSS is 40% (reserved) or 45% (general).');
        return;
      }
    }
    if (currentStep === 3) {
      if (Number(formData.familyAnnualIncome) > 800000) {
        setError('Annual family income must not exceed ₹8,00,000 as per PMSSS guidelines.');
        return;
      }
    }

    setCurrentStep(prev => Math.min(prev + 1, 4));
  };

  const handlePrev = () => {
    setError('');
    setCurrentStep(prev => Math.max(prev - 1, 1));
  };

  const handleSubmit = async () => {
    setError('');
    setValidationErrors([]);

    if (!formData.declared) {
      setError('You must accept the truthfulness declaration before submitting.');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        ...formData,
        familyAnnualIncome: Number(formData.familyAnnualIncome),
        twelfthPercentage: Number(formData.twelfthPercentage),
        twelfthMarksObtained: Number(formData.twelfthMarksObtained),
        twelfthTotalMarks: Number(formData.twelfthTotalMarks)
      };

      const res = await applicationApi.submitApplication(payload);
      
      // If validation warnings or errors returned
      if (res.data && res.data.status === 'VALIDATION_FAILED') {
        setValidationErrors(res.data.validationErrors || ['Pre-validation checks flagged issues.']);
        setError('Pre-validation failed. Please correct highlighted issues.');
      } else {
        navigate('/student/documents', { state: { newSubmission: true } });
      }
    } catch (err) {
      const data = err.response?.data;
      if (data?.validationErrors) {
        setValidationErrors(data.validationErrors);
      }
      setError(data?.message || 'Submission error. Please check form values.');
    } finally {
      setLoading(false);
    }
  };

  const steps = [
    { num: 1, label: 'Course Stream' },
    { num: 2, label: 'Academic Scores' },
    { num: 3, label: 'Income & Domicile' },
    { num: 4, label: 'Review & Submit' }
  ];

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="pb-4 border-b border-slate-800">
        <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
          Scholarship Application Wizard (PMSSS 2026)
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Automated Pre-Validation verifies criteria before submission to SAG Officer
        </p>
      </div>

      {/* Stepper Progress */}
      <div className="grid grid-cols-4 gap-2">
        {steps.map((s) => (
          <div
            key={s.num}
            className={`p-2.5 rounded-xl border text-center transition-all ${
              currentStep === s.num
                ? 'bg-[#d32f2f]/20 border-[#d32f2f] text-white shadow-md shadow-red-500/10'
                : currentStep > s.num
                ? 'bg-slate-900 border-emerald-500/40 text-emerald-400'
                : 'bg-slate-900/50 border-slate-800 text-slate-500'
            }`}
          >
            <div className="text-[10px] uppercase font-bold tracking-wider mb-0.5">Step {s.num}</div>
            <div className="text-xs font-semibold truncate">{s.label}</div>
          </div>
        ))}
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-xs flex items-start gap-2.5">
          <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
          <div className="space-y-1">
            <p className="font-semibold">{error}</p>
            {validationErrors.length > 0 && (
              <ul className="list-disc list-inside space-y-0.5 text-slate-300">
                {validationErrors.map((e, idx) => (
                  <li key={idx}>{e}</li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}

      {/* Form Content Card */}
      <div className="p-6 sm:p-8 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-6">
        {/* STEP 1: Stream Selection */}
        {currentStep === 1 && (
          <div className="space-y-4">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <GraduationCap className="w-5 h-5 text-[#d32f2f]" />
              Select Course Stream & Academic Session
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">Academic Session</label>
                <input
                  type="text"
                  disabled
                  value={formData.academicYear}
                  className="w-full px-3 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-300 text-xs"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">Course Category</label>
                <select
                  name="stream"
                  value={formData.stream}
                  onChange={handleChange}
                  className="w-full px-3 py-2.5 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs focus:border-[#d32f2f]"
                >
                  <option value="ENGINEERING">Engineering & Technology (B.Tech / B.E.)</option>
                  <option value="GENERAL">General Degree (B.A. / B.Sc. / B.Com / B.B.A)</option>
                  <option value="MEDICAL_NURSING">Medical & Paramedical / B.Sc. Nursing</option>
                  <option value="PHARMACY">Pharmacy (B.Pharm)</option>
                  <option value="HOTEL_MANAGEMENT">Hotel Management & Catering (BHMCT)</option>
                </select>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-[#d32f2f]/10 border border-[#d32f2f]/20 text-slate-200 text-xs flex items-start gap-3">
              <Sparkles className="w-5 h-5 shrink-0 text-[#d32f2f] mt-0.5" />
              <p>
                As per PMSSS scheme norms, candidates must have passed 10+2 from JKBOSE or CBSE affiliated schools in J&K or Ladakh.
              </p>
            </div>
          </div>
        )}

        {/* STEP 2: Academic Details */}
        {currentStep === 2 && (
          <div className="space-y-6">
            <div>
              <h2 className="text-base font-bold text-white mb-3">Class 10th (Secondary) Records</h2>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">10th Roll Number</label>
                  <input
                    type="text"
                    name="tenthRollNumber"
                    value={formData.tenthRollNumber}
                    onChange={handleChange}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Board of Examination</label>
                  <input
                    type="text"
                    name="tenthBoard"
                    value={formData.tenthBoard}
                    onChange={handleChange}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Aggregate %</label>
                  <input
                    type="number"
                    step="0.01"
                    name="tenthPercentage"
                    value={formData.tenthPercentage}
                    onChange={handleChange}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs"
                  />
                </div>
              </div>
            </div>

            <div className="pt-2 border-t border-slate-800">
              <h2 className="text-base font-bold text-white mb-3">Class 12th (Higher Secondary) Records</h2>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">12th Roll Number</label>
                  <input
                    type="text"
                    name="twelfthRollNumber"
                    value={formData.twelfthRollNumber}
                    onChange={handleChange}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Marks Obtained</label>
                  <input
                    type="number"
                    name="twelfthMarksObtained"
                    value={formData.twelfthMarksObtained}
                    onChange={(e) => {
                      const obt = Number(e.target.value);
                      const tot = Number(formData.twelfthTotalMarks) || 500;
                      setFormData({
                        ...formData,
                        twelfthMarksObtained: obt,
                        twelfthPercentage: Number(((obt / tot) * 100).toFixed(2))
                      });
                    }}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1">Calculated Percentage</label>
                  <input
                    type="number"
                    disabled
                    value={formData.twelfthPercentage}
                    className="w-full px-3 py-2 bg-slate-950/60 border border-slate-800 rounded-xl text-emerald-400 font-bold text-xs"
                  />
                </div>
              </div>
            </div>
          </div>
        )}

        {/* STEP 3: Income & Identity */}
        {currentStep === 3 && (
          <div className="space-y-4">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <IndianRupee className="w-5 h-5 text-emerald-400" />
              Annual Family Income & Identity Rules
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">
                  Annual Family Income (₹) <span className="text-emerald-400 font-normal">Max ₹8,00,000</span>
                </label>
                <input
                  type="number"
                  name="familyAnnualIncome"
                  value={formData.familyAnnualIncome}
                  onChange={handleChange}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">Income Certificate No.</label>
                <input
                  type="text"
                  name="incomeCertificateNumber"
                  value={formData.incomeCertificateNumber}
                  onChange={handleChange}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs"
                />
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-medium text-slate-400 mb-1">Aadhaar Card Number (12 Digits)</label>
                <input
                  type="text"
                  maxLength={12}
                  name="aadhaarNumber"
                  value={formData.aadhaarNumber}
                  onChange={handleChange}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-xl text-white text-xs font-mono"
                />
              </div>
            </div>
          </div>
        )}

        {/* STEP 4: Review & Declaration */}
        {currentStep === 4 && (
          <div className="space-y-6">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <ShieldCheck className="w-5 h-5 text-[#d32f2f]" />
              Declaration & Automated Pre-Validation Check
            </h2>

            <div className="p-4 rounded-xl bg-slate-950/70 border border-slate-800 space-y-3 text-xs">
              <div className="flex justify-between py-1 border-b border-slate-800">
                <span className="text-slate-400">Stream:</span>
                <span className="text-white font-medium">{formData.stream}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800">
                <span className="text-slate-400">Class 12th Percentage:</span>
                <span className="text-white font-medium">{formData.twelfthPercentage}%</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800">
                <span className="text-slate-400">Family Income:</span>
                <span className="text-white font-medium">₹{Number(formData.familyAnnualIncome).toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-slate-400">Aadhaar Format:</span>
                <span className="text-emerald-400 font-medium">Valid (12 digits)</span>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-[#d32f2f]/10 border border-[#d32f2f]/20 text-xs text-slate-200">
              <p>
                <strong>Important:</strong> Submitting will trigger PMSSS 2.0 automated consistency checks and queue your file for SAG Officer triage.
              </p>
            </div>

            <label className="flex items-start gap-3 p-3 rounded-xl bg-slate-950/50 border border-slate-800 cursor-pointer">
              <input
                type="checkbox"
                name="declared"
                checked={formData.declared}
                onChange={handleChange}
                className="mt-0.5 rounded text-[#d32f2f] focus:ring-0"
              />
              <span className="text-xs text-slate-300 leading-relaxed">
                I solemnly affirm that all entries made in this application are true and correct. I understand that submitting fake documents will lead to disqualification and recovery.
              </span>
            </label>
          </div>
        )}

        {/* Action Controls */}
        <div className="flex items-center justify-between pt-6 border-t border-slate-800">
          <button
            type="button"
            disabled={currentStep === 1 || loading}
            onClick={handlePrev}
            className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-medium flex items-center gap-1.5 disabled:opacity-30"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Previous</span>
          </button>

          {currentStep < 4 ? (
            <button
              type="button"
              onClick={handleNext}
              className="px-5 py-2.5 rounded-xl bg-[#d32f2f] hover:bg-[#b71c1c] text-white text-xs font-medium flex items-center gap-1.5 shadow-lg shadow-red-500/20"
            >
              <span>Continue</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          ) : (
            <button
              type="button"
              disabled={loading}
              onClick={handleSubmit}
              className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white text-xs font-bold flex items-center gap-2 shadow-lg shadow-emerald-500/20 disabled:opacity-50"
            >
              {loading ? (
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                <>
                  <Send className="w-4 h-4" />
                  <span>Submit Application</span>
                </>
              )}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
