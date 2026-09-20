import axiosClient from './axiosClient';

export const officerApi = {
  getMyQueue: () => axiosClient.get('/sag/applications'),
  getQueue: () => axiosClient.get('/sag/applications'),
  getMyWorkload: () => axiosClient.get('/sag/dashboard'),
  getWorkload: () => axiosClient.get('/sag/dashboard'),
  getApplicationSummary: (applicationId) => axiosClient.get(`/sag/applications/${applicationId}`),
  approveApplication: (applicationId, remarks) =>
    axiosClient.post(`/sag/applications/${applicationId}/approve?remarks=${encodeURIComponent(remarks || '')}`),
  rejectApplication: (applicationId, reason) =>
    axiosClient.post(`/sag/applications/${applicationId}/reject?reason=${encodeURIComponent(reason)}`),
  requestCorrection: (applicationId, docType, fieldKey, reason) =>
    axiosClient.post(`/sag/applications/${applicationId}/correction?documentType=${encodeURIComponent(docType || '')}&fieldKey=${encodeURIComponent(fieldKey || '')}&reason=${encodeURIComponent(reason)}`),
  escalateApplication: (applicationId, reason) =>
    axiosClient.post(`/sag/applications/${applicationId}/escalate?reason=${encodeURIComponent(reason)}`),
  getEscalations: (status = 'OPEN') => axiosClient.get(`/admin/escalations?status=${status}`),
  getCorrections: () => axiosClient.get('/admin/corrections'),
};

export default officerApi;
