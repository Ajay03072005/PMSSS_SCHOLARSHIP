import React from 'react';
import { Sparkles, AlertTriangle, ShieldCheck, CopyCheck, FileText } from 'lucide-react';

export default function AiSummaryCard({ summary }) {
  if (!summary) return null;

  const isClean = summary.overallMatchScore >= 80 && !summary.duplicateSuspected && summary.activeAnomaliesCount === 0;

  return (
    <div className="bg-slate-900 text-white rounded-2xl p-6 shadow-xl border border-slate-800">
      <div className="flex items-center justify-between border-b border-slate-800 pb-4">
        <div className="flex items-center gap-2.5">
          <div className="w-9 h-9 rounded-lg bg-[#d32f2f]/10 flex items-center justify-center border border-[#d32f2f]/30">
            <Sparkles className="w-5 h-5 text-[#d32f2f]" />
          </div>
          <div>
            <h3 className="text-base font-bold tracking-tight font-['Outfit',sans-serif]">AI Pre-Verification Summary</h3>
            <p className="text-xs text-slate-400">Application: {summary.applicationId}</p>
          </div>
        </div>
        <span className={`px-3 py-1 rounded-full text-xs font-bold border ${
          isClean 
            ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' 
            : 'bg-amber-500/20 text-amber-300 border-amber-500/40'
        }`}>
          {isClean ? 'QUICK REVIEW' : 'ATTENTION REQUIRED'}
        </span>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 my-5 text-xs">
        <div className="bg-slate-950/60 border border-slate-800 rounded-xl p-3">
          <span className="text-slate-400 text-[11px] block">Document Match Score</span>
          <span className="text-xl font-bold text-white mt-0.5 block">{summary.overallMatchScore}%</span>
        </div>
        <div className="bg-slate-950/60 border border-slate-800 rounded-xl p-3">
          <span className="text-slate-400 text-[11px] block">Uploaded Documents</span>
          <span className="text-xl font-bold text-white mt-0.5 block">{summary.documentsCount} Files</span>
        </div>
        <div className="bg-slate-950/60 border border-slate-800 rounded-xl p-3">
          <span className="text-slate-400 text-[11px] block">Duplicate Check</span>
          <span className="text-xl font-bold text-white mt-0.5 block">
            {summary.duplicateSuspected ? `${summary.duplicateMatchPercentage}% Match` : 'Clean'}
          </span>
        </div>
        <div className="bg-slate-950/60 border border-slate-800 rounded-xl p-3">
          <span className="text-slate-400 text-[11px] block">Anomaly Alerts</span>
          <span className="text-xl font-bold text-white mt-0.5 block">
            {summary.activeAnomaliesCount > 0 ? `${summary.activeAnomaliesCount} Flagged` : '0 Alerts'}
          </span>
        </div>
      </div>

      {/* Recommended Action callout */}
      <div className={`p-4 rounded-xl border text-xs leading-relaxed flex items-start gap-3 ${
        isClean 
          ? 'bg-emerald-950/40 border-emerald-800/50 text-emerald-200' 
          : 'bg-amber-950/40 border-amber-800/50 text-amber-200'
      }`}>
        {isClean ? (
          <ShieldCheck className="w-5 h-5 text-emerald-400 flex-shrink-0 mt-0.5" />
        ) : (
          <AlertTriangle className="w-5 h-5 text-amber-400 flex-shrink-0 mt-0.5" />
        )}
        <div>
          <p className="font-bold uppercase tracking-wider text-[11px] mb-0.5">Triage Recommendation</p>
          <p>{summary.recommendedAction}</p>
        </div>
      </div>
    </div>
  );
}
