import React, { useState } from 'react';
import { UploadCloud, FileCheck, AlertTriangle, Eye, RefreshCw, CheckCircle, Trash2, Download, X, ArrowUpCircle } from 'lucide-react';
import { documentApi } from '../../api/documentApi';

export default function DocumentUploadBox({
  label,
  documentType,
  required = false,
  existingFile,
  documentUniqueId,
  status = 'NOT_UPLOADED',
  rejectionReason,
  version = 1,
  onUpload,
  onReplace,
  onDelete,
  ocrData,
  disabled = false
}) {
  const [stagedFile, setStagedFile] = useState(null);
  const [uploadState, setUploadState] = useState('IDLE'); // IDLE | STAGED | UPLOADING | SUCCESS | FAILED
  const [error, setError] = useState(null);

  const formatFileSize = (bytes) => {
    if (!bytes) return '';
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
  };

  const handleFileSelect = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // Validate size (max 10MB)
    if (file.size > 10 * 1024 * 1024) {
      setError('File size exceeds 10MB maximum limit.');
      setUploadState('FAILED');
      setStagedFile(null);
      return;
    }

    // Validate MIME type
    const validTypes = ['application/pdf', 'image/jpeg', 'image/png', 'image/jpg'];
    if (!validTypes.includes(file.type)) {
      setError('Only PDF, JPG, or PNG files are permitted.');
      setUploadState('FAILED');
      setStagedFile(null);
      return;
    }

    setError(null);
    setStagedFile(file);
    setUploadState('STAGED');
  };

  const handlePerformUpload = async () => {
    if (!stagedFile) return;

    setUploadState('UPLOADING');
    setError(null);

    try {
      if (documentUniqueId && onReplace) {
        await onReplace(documentUniqueId, stagedFile);
      } else if (onUpload) {
        await onUpload(stagedFile, documentType);
      }
      setUploadState('SUCCESS');
      setStagedFile(null);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Upload failed. Please try again.');
      setUploadState('FAILED');
    }
  };

  const handleCancelStaged = () => {
    setStagedFile(null);
    setUploadState('IDLE');
    setError(null);
  };

  const isVerified = status === 'VERIFIED';
  const isRejected = status === 'REJECTED';
  const isUploaded = (status === 'UPLOADED' || status === 'PROCESSING' || status === 'RESUBMITTED' || isVerified) && !stagedFile;

  return (
    <div className={`border rounded-xl p-4 transition-all ${
      stagedFile 
        ? 'border-blue-500/50 bg-blue-950/20'
        : isRejected 
        ? 'border-rose-300 bg-rose-50/20' 
        : isVerified 
        ? 'border-emerald-300 bg-emerald-50/20' 
        : 'border-slate-800 bg-slate-900/90'
    }`}>
      <div className="flex items-start justify-between">
        <div>
          <h4 className="text-sm font-semibold text-white flex items-center gap-1.5 font-['Poppins',sans-serif]">
            {label}
            {required && <span className="text-rose-500">*</span>}
          </h4>
          <p className="text-[11px] text-slate-400 mt-0.5">
            Accepted: PDF, JPG, PNG (Max 10MB) {version > 1 ? `• Version ${version}` : ''}
          </p>
        </div>

        {isVerified && !stagedFile && (
          <span className="inline-flex items-center gap-1 text-xs font-semibold text-emerald-400 bg-emerald-500/10 border border-emerald-500/30 px-2 py-0.5 rounded-full">
            <CheckCircle className="w-3.5 h-3.5" /> Verified ✓
          </span>
        )}
        {isRejected && !stagedFile && (
          <span className="inline-flex items-center gap-1 text-xs font-semibold text-rose-400 bg-rose-500/10 border border-rose-500/30 px-2 py-0.5 rounded-full">
            <AlertTriangle className="w-3.5 h-3.5" /> Action Required
          </span>
        )}
        {stagedFile && (
          <span className="inline-flex items-center gap-1 text-xs font-semibold text-amber-400 bg-amber-500/10 border border-amber-500/30 px-2 py-0.5 rounded-full animate-pulse">
            Ready to Upload
          </span>
        )}
      </div>

      {/* Upload & Staged Area */}
      <div className="mt-3 space-y-3">
        {/* State 1: User Selected A File (Staged for Upload) */}
        {stagedFile ? (
          <div className="p-3 bg-slate-950 border border-blue-500/40 rounded-xl space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2.5 min-w-0">
                <FileCheck className="w-5 h-5 text-blue-400 shrink-0" />
                <div className="min-w-0">
                  <p className="text-xs font-semibold text-white truncate">{stagedFile.name}</p>
                  <p className="text-[10px] text-slate-400">
                    {formatFileSize(stagedFile.size)} • Staged for storage
                  </p>
                </div>
              </div>
              <button
                type="button"
                onClick={handleCancelStaged}
                disabled={uploadState === 'UPLOADING'}
                className="p-1 text-slate-400 hover:text-rose-400 transition-colors"
                title="Cancel selection"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Explicit Upload Action Button */}
            <div className="flex items-center gap-2 pt-1 border-t border-slate-900">
              <button
                type="button"
                onClick={handlePerformUpload}
                disabled={uploadState === 'UPLOADING' || disabled}
                className="flex-1 inline-flex items-center justify-center gap-2 px-3 py-2 text-xs font-bold text-white bg-gradient-to-r from-[#d32f2f] to-[#b71c1c] hover:from-[#e53935] hover:to-[#c62828] rounded-lg shadow-md hover:shadow-red-500/20 transition-all disabled:opacity-50"
              >
                {uploadState === 'UPLOADING' ? (
                  <>
                    <RefreshCw className="w-4 h-4 animate-spin" />
                    <span>Storing in Cloudinary...</span>
                  </>
                ) : (
                  <>
                    <ArrowUpCircle className="w-4 h-4" />
                    <span>{documentUniqueId ? `Upload New Version (v${version + 1})` : 'Upload Document'}</span>
                  </>
                )}
              </button>
              <button
                type="button"
                onClick={handleCancelStaged}
                disabled={uploadState === 'UPLOADING'}
                className="px-3 py-2 text-xs font-medium text-slate-400 hover:text-white bg-slate-900 border border-slate-800 rounded-lg transition-colors"
              >
                Cancel
              </button>
            </div>
          </div>
        ) : isUploaded || existingFile ? (
          /* State 2: Already Uploaded File */
          <div className="flex items-center justify-between p-3 bg-slate-950 border border-slate-800 rounded-lg">
            <div className="flex items-center gap-2.5 overflow-hidden">
              <FileCheck className="w-5 h-5 text-[#d32f2f] shrink-0" />
              <div className="flex flex-col min-w-0">
                <span className="text-xs font-medium text-white truncate">
                  {existingFile || `${label}.pdf`}
                </span>
                <span className="text-[10px] text-emerald-400 font-medium flex items-center gap-1">
                  Cloudinary Stored ✓ {version > 1 ? `(v${version})` : ''}
                </span>
              </div>
            </div>
            <div className="flex items-center gap-2">
              {documentUniqueId && (
                <>
                  <a
                    href={documentApi.viewDocumentUrl(documentUniqueId)}
                    target="_blank"
                    rel="noreferrer"
                    className="p-1 text-slate-400 hover:text-white transition-colors"
                    title="View Document"
                  >
                    <Eye className="w-4 h-4" />
                  </a>
                  <a
                    href={documentApi.downloadDocumentUrl(documentUniqueId)}
                    download
                    className="p-1 text-slate-400 hover:text-white transition-colors"
                    title="Download Document"
                  >
                    <Download className="w-4 h-4" />
                  </a>
                </>
              )}
              {!disabled && (
                <label className="text-xs font-medium text-[#d32f2f] hover:text-[#b71c1c] cursor-pointer flex items-center gap-1 bg-slate-900 px-2.5 py-1 rounded-md border border-slate-800 transition-colors">
                  <RefreshCw className="w-3.5 h-3.5" />
                  <span>Replace</span>
                  <input type="file" onChange={handleFileSelect} className="hidden" accept=".pdf,.jpg,.jpeg,.png" disabled={uploadState === 'UPLOADING'} />
                </label>
              )}
              {onDelete && documentUniqueId && !disabled && !isVerified && (
                <button
                  type="button"
                  onClick={() => onDelete(documentUniqueId)}
                  className="p-1 text-slate-500 hover:text-rose-400 transition-colors"
                  title="Delete Document"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              )}
            </div>
          </div>
        ) : (
          /* State 3: Empty Dropzone to Choose File */
          <label className={`flex flex-col items-center justify-center p-4 border-2 border-dashed rounded-xl cursor-pointer transition-all ${
            disabled || uploadState === 'UPLOADING'
              ? 'bg-slate-950/50 border-slate-800 cursor-not-allowed opacity-60' 
              : 'border-slate-800 bg-slate-950/70 hover:border-[#d32f2f] hover:bg-red-500/5'
          }`}>
            <UploadCloud className="w-7 h-7 text-slate-400 mb-1" />
            <span className="text-xs font-semibold text-slate-200">
              Select Document File
            </span>
            <span className="text-[10px] text-slate-500 mt-0.5">
              Click to select • You can confirm before uploading
            </span>
            <input 
              type="file" 
              onChange={handleFileSelect} 
              disabled={disabled || uploadState === 'UPLOADING'} 
              className="hidden" 
              accept=".pdf,.jpg,.jpeg,.png" 
            />
          </label>
        )}
      </div>

      {uploadState === 'UPLOADING' && (
        <p className="text-xs text-[#d32f2f] animate-pulse mt-2 flex items-center gap-1.5">
          <RefreshCw className="w-3.5 h-3.5 animate-spin" /> Uploading to Cloudinary & running OCR extraction...
        </p>
      )}

      {error && (
        <p className="text-xs text-rose-400 mt-2 font-medium flex items-center gap-1">
          <AlertTriangle className="w-3.5 h-3.5 shrink-0" /> {error}
        </p>
      )}

      {isRejected && rejectionReason && !stagedFile && (
        <div className="mt-2.5 p-2.5 bg-rose-500/10 border border-rose-500/20 rounded-lg text-xs text-rose-300">
          <p className="font-semibold flex items-center gap-1 text-rose-400">
            <AlertTriangle className="w-3.5 h-3.5" /> Officer Feedback:
          </p>
          <p className="mt-0.5">{rejectionReason}</p>
        </div>
      )}

      {/* OCR Extractions card */}
      {ocrData && (
        <div className="mt-2.5 p-2 bg-slate-950 border border-slate-800 rounded-lg text-[11px] text-slate-300">
          <span className="font-bold text-white">OCR Extracted:</span> {ocrData}
        </div>
      )}
    </div>
  );
}


