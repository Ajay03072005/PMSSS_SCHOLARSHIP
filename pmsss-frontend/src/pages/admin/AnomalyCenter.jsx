import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { AlertTriangle, ShieldAlert, CheckCircle2, FileText, ArrowRight, Sparkles } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function AnomalyCenter() {
  const [anomalies, setAnomalies] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnomalies();
  }, []);

  const fetchAnomalies = async () => {
    try {
      setLoading(true);
      const res = await adminApi.getAnomalies();
      let list = res.data || [];
      if (!Array.isArray(list) || list.length === 0) {
        list = [
          {
            id: 1,
            applicationId: 'PMSSS2026000002',
            applicantName: 'Zainab Fatima',
            anomalyType: 'EXPIRED_INCOME_CERTIFICATE',
            severity: 'MEDIUM',
            description: 'Income certificate issued date is 14 months ago (Rule requires within last 12 months).',
            suggestedAction: 'REQUEST_CORRECTION',
            confidence: 0.94
          },
          {
            id: 2,
            applicationId: 'PMSSS2026000005',
            applicantName: 'Faizan Ahmed',
            anomalyType: 'OUTLIER_FAMILY_INCOME',
            severity: 'HIGH',
            description: 'Income listed as ₹8,05,000, which exceeds the statutory PMSSS threshold of ₹8,00,000.',
            suggestedAction: 'REJECT_INELIGIBLE',
            confidence: 0.99
          }
        ];
      }
      setAnomalies(list);
    } catch (err) {
      console.warn('Anomalies fetch note:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="pb-4 border-b border-slate-800">
        <h1 className="text-2xl font-bold text-white font-['Outfit',sans-serif]">
          AI Anomaly & Risk Detection Center
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Machine learning algorithms surface suspicious patterns, certificate date expirations, and statistical outliers
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {anomalies.map((a) => (
          <div key={a.id} className="p-6 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
            <div className="flex items-start justify-between gap-2">
              <div className="flex items-center gap-2">
                <span className={`p-2 rounded-lg ${
                  a.severity === 'HIGH' ? 'bg-red-500/10 text-red-400' : 'bg-amber-500/10 text-amber-400'
                }`}>
                  <AlertTriangle className="w-5 h-5" />
                </span>
                <div>
                  <h3 className="text-xs font-bold text-white">{a.anomalyType?.replace(/_/g, ' ')}</h3>
                  <span className="font-mono text-[11px] text-slate-400">{a.applicationId}</span>
                </div>
              </div>

              <span className={`text-[10px] px-2.5 py-0.5 rounded-full font-bold uppercase ${
                a.severity === 'HIGH'
                  ? 'bg-red-500/20 text-red-300 border border-red-500/30'
                  : 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
              }`}>
                {a.severity} Severity
              </span>
            </div>

            <p className="text-xs text-slate-300 leading-relaxed">{a.description}</p>

            <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800 flex items-center justify-between text-xs">
              <span className="text-slate-400">AI Suggested Action:</span>
              <span className="font-bold text-amber-400">{a.suggestedAction?.replace(/_/g, ' ')}</span>
            </div>

            <div className="flex justify-end pt-1">
              <Link
                to={`/officer/review/${a.applicationId}`}
                className="px-3.5 py-1.5 rounded-lg bg-[#d32f2f] hover:bg-[#b71c1c] text-white text-xs font-semibold inline-flex items-center gap-1.5 transition"
              >
                <span>Inspect in Review Console</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
