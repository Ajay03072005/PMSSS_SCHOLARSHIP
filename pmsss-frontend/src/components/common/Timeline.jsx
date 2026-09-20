import React from 'react';
import { Check, Circle } from 'lucide-react';

export default function Timeline({ steps, currentStepIndex }) {
  return (
    <div className="w-full py-4">
      <div className="flex items-center justify-between relative">
        {/* Connecting bar */}
        <div className="absolute top-1/2 left-0 w-full h-1 bg-slate-200 -translate-y-1/2 z-0" />
        <div 
          className="absolute top-1/2 left-0 h-1 bg-emerald-500 -translate-y-1/2 transition-all duration-500 z-0"
          style={{ width: `${(Math.max(0, currentStepIndex) / (steps.length - 1)) * 100}%` }}
        />

        {steps.map((step, idx) => {
          const isCompleted = idx < currentStepIndex;
          const isCurrent = idx === currentStepIndex;

          return (
            <div key={idx} className="flex flex-col items-center relative z-10">
              <div 
                className={`w-9 h-9 rounded-full flex items-center justify-center font-bold text-xs border-2 transition-all shadow-sm ${
                  isCompleted 
                    ? 'bg-emerald-500 border-emerald-600 text-white' 
                    : isCurrent 
                    ? 'bg-[#d32f2f] border-[#b71c1c] text-white ring-4 ring-red-100 animate-pulse' 
                    : 'bg-white border-slate-300 text-slate-400'
                }`}
              >
                {isCompleted ? <Check className="w-4 h-4 stroke-[3]" /> : idx + 1}
              </div>
              <span className={`text-[11px] font-semibold mt-2 text-center max-w-[80px] leading-tight ${
                isCurrent ? 'text-[#d32f2f]' : isCompleted ? 'text-slate-700' : 'text-slate-400'
              }`}>
                {step.label}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
