import React from 'react';
import { Shield, Sparkles, CheckCircle2, Heart } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-slate-900 border-t border-slate-800 text-slate-400 py-6 text-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-2">
          <Shield className="w-4 h-4 text-[#d32f2f]" />
          <span>PMSSS 2.0 • AI-Assisted Smart Scholarship Portal</span>
          <span className="text-slate-600">|</span>
          <span className="text-xs text-slate-500">Government of India & AICTE</span>
        </div>

        <div className="flex items-center gap-4 text-xs text-slate-400">
          <span className="flex items-center gap-1.5">
            <Sparkles className="w-3.5 h-3.5 text-amber-400" />
            Zero-Wait OCR & Pre-Validation
          </span>
          <span className="text-slate-600">•</span>
          <span className="flex items-center gap-1.5">
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
            Human-In-The-Loop Approval
          </span>
        </div>
      </div>
    </footer>
  );
}
