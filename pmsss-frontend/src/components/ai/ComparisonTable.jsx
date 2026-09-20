import React from 'react';
import { CheckCircle2, AlertTriangle, HelpCircle } from 'lucide-react';

export default function ComparisonTable({ items = [] }) {
  if (!items || items.length === 0) {
    return (
      <div className="p-6 text-center text-slate-400 text-xs bg-slate-50 border border-slate-200 rounded-xl">
        No document comparison items available. Trigger OCR verification to compare.
      </div>
    );
  }

  return (
    <div className="overflow-x-auto border border-slate-200 rounded-xl bg-white shadow-sm">
      <table className="w-full text-left text-xs">
        <thead className="bg-slate-50 border-b border-slate-200 text-slate-600 font-semibold uppercase tracking-wider text-[10px]">
          <tr>
            <th className="py-3 px-4">Field</th>
            <th className="py-3 px-4">Application Form Value</th>
            <th className="py-3 px-4">Extracted Document Data</th>
            <th className="py-3 px-4">Match Status</th>
            <th className="py-3 px-4 text-right">Confidence</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {items.map((item, idx) => {
            const isMatch = item.matchStatus === 'MATCH';
            const isMismatch = item.matchStatus === 'POTENTIAL_MISMATCH' || item.matchStatus === 'MISMATCH';

            return (
              <tr key={idx} className="hover:bg-slate-50/60 transition-colors">
                <td className="py-3 px-4 font-semibold text-slate-800">{item.applicationField}</td>
                <td className="py-3 px-4 text-slate-700">{item.applicationValue || '—'}</td>
                <td className="py-3 px-4 font-mono text-slate-900 bg-slate-50/50 px-2 rounded">
                  {item.documentExtractedValue || '—'}
                </td>
                <td className="py-3 px-4">
                  {isMatch ? (
                    <span className="inline-flex items-center gap-1 text-emerald-700 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full font-bold text-[10px]">
                      <CheckCircle2 className="w-3 h-3" /> MATCH
                    </span>
                  ) : isMismatch ? (
                    <span className="inline-flex items-center gap-1 text-rose-700 bg-rose-50 border border-rose-200 px-2 py-0.5 rounded-full font-bold text-[10px]">
                      <AlertTriangle className="w-3 h-3" /> MISMATCH
                    </span>
                  ) : (
                    <span className="inline-flex items-center gap-1 text-slate-600 bg-slate-100 border border-slate-200 px-2 py-0.5 rounded-full font-medium text-[10px]">
                      <HelpCircle className="w-3 h-3" /> PENDING
                    </span>
                  )}
                </td>
                <td className="py-3 px-4 text-right font-medium text-slate-600">
                  {item.confidence ? `${Math.round(item.confidence * 100)}%` : '—'}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
