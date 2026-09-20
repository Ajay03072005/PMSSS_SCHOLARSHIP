import React from 'react';
import { AlertCircle, ArrowUpCircle, MinusCircle } from 'lucide-react';

export default function PriorityBadge({ priority, className = '' }) {
  const p = (priority || 'NORMAL').toUpperCase();

  if (p === 'HIGH') {
    return (
      <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-red-100 text-red-800 border border-red-300 ${className}`}>
        <AlertCircle className="w-3.5 h-3.5 text-red-600" />
        HIGH PRIORITY
      </span>
    );
  }

  if (p === 'MEDIUM') {
    return (
      <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-100 text-amber-800 border border-amber-300 ${className}`}>
        <ArrowUpCircle className="w-3.5 h-3.5 text-amber-600" />
        MEDIUM
      </span>
    );
  }

  return (
    <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-700 border border-slate-300 ${className}`}>
      <MinusCircle className="w-3.5 h-3.5 text-slate-500" />
      NORMAL
    </span>
  );
}
