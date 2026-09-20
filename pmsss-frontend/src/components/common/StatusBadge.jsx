import React from 'react';
import { 
  CheckCircle2, 
  Clock, 
  AlertTriangle, 
  XCircle, 
  FileText, 
  Send, 
  RotateCcw, 
  DollarSign, 
  ShieldAlert 
} from 'lucide-react';

const statusConfig = {
  DRAFT: { label: 'Draft', bg: 'bg-slate-100 text-slate-700 border-slate-300', icon: FileText },
  SUBMITTED: { label: 'Submitted', bg: 'bg-blue-100 text-blue-800 border-blue-300', icon: Send },
  VALIDATING: { label: 'Auto Validating', bg: 'bg-purple-100 text-purple-800 border-purple-300', icon: Clock },
  VALIDATION_FAILED: { label: 'Validation Issues', bg: 'bg-rose-100 text-rose-800 border-rose-300', icon: AlertTriangle },
  DOCUMENT_PROCESSING: { label: 'OCR Processing', bg: 'bg-indigo-100 text-indigo-800 border-indigo-300', icon: Clock },
  DOCUMENT_VERIFICATION: { label: 'Document Review', bg: 'bg-sky-100 text-sky-800 border-sky-300', icon: Clock },
  NEEDS_CORRECTION: { label: 'Correction Requested', bg: 'bg-amber-100 text-amber-800 border-amber-300', icon: RotateCcw },
  ASSIGNED: { label: 'Assigned to Officer', bg: 'bg-blue-100 text-blue-800 border-blue-300', icon: Clock },
  UNDER_REVIEW: { label: 'Under Review', bg: 'bg-cyan-100 text-cyan-800 border-cyan-300', icon: Clock },
  SAG_REVIEW: { label: 'SAG Review', bg: 'bg-indigo-100 text-indigo-800 border-indigo-300', icon: Clock },
  SAG_APPROVED: { label: 'SAG Approved', bg: 'bg-emerald-100 text-emerald-800 border-emerald-300', icon: CheckCircle2 },
  SAG_REJECTED: { label: 'Rejected', bg: 'bg-rose-100 text-rose-800 border-rose-300', icon: XCircle },
  ESCALATED: { label: 'Escalated to Supervisor', bg: 'bg-orange-100 text-orange-800 border-orange-300', icon: ShieldAlert },
  SENT_TO_FINANCE: { label: 'Sent to Finance', bg: 'bg-teal-100 text-teal-800 border-teal-300', icon: Clock },
  PAYMENT_PROCESSING: { label: 'DBT Processing', bg: 'bg-violet-100 text-violet-800 border-violet-300', icon: DollarSign },
  PAYMENT_COMPLETED: { label: 'Disbursed', bg: 'bg-emerald-100 text-emerald-800 border-emerald-300', icon: CheckCircle2 },
  COMPLETED: { label: 'Completed', bg: 'bg-emerald-100 text-emerald-800 border-emerald-300', icon: CheckCircle2 },
};

export default function StatusBadge({ status, className = '' }) {
  const config = statusConfig[status] || { 
    label: status || 'Unknown', 
    bg: 'bg-slate-100 text-slate-700 border-slate-300', 
    icon: Clock 
  };
  const Icon = config.icon;

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold border ${config.bg} ${className}`}>
      <Icon className="w-3.5 h-3.5" />
      <span>{config.label}</span>
    </span>
  );
}
