import React from 'react';

export default function StatCard({ title, value, subtitle, icon: Icon, color = 'red', trend }) {
  const colorMap = {
    red: 'bg-red-50 text-red-700 border-red-200',
    blue: 'bg-red-50 text-red-700 border-red-200',
    emerald: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    amber: 'bg-amber-50 text-amber-700 border-amber-200',
    rose: 'bg-rose-50 text-rose-700 border-rose-200',
    indigo: 'bg-red-50 text-red-700 border-red-200',
    purple: 'bg-purple-50 text-purple-700 border-purple-200',
  };

  const iconBgMap = {
    red: 'bg-[#d32f2f] text-white',
    blue: 'bg-[#d32f2f] text-white',
    emerald: 'bg-emerald-600 text-white',
    amber: 'bg-amber-500 text-white',
    rose: 'bg-[#d32f2f] text-white',
    indigo: 'bg-[#d32f2f] text-white',
    purple: 'bg-purple-600 text-white',
  };

  return (
    <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">{title}</p>
          <p className="text-2xl font-bold text-slate-900 mt-1">{value ?? '—'}</p>
          {subtitle && <p className="text-xs text-slate-500 mt-0.5">{subtitle}</p>}
        </div>
        {Icon && (
          <div className={`w-12 h-12 rounded-xl flex items-center justify-center shadow-inner ${iconBgMap[color] || iconBgMap.red}`}>
            <Icon className="w-6 h-6" />
          </div>
        )}
      </div>
      {trend && (
        <div className="mt-3 pt-3 border-t border-slate-100 flex items-center text-xs text-slate-600">
          <span>{trend}</span>
        </div>
      )}
    </div>
  );
}
