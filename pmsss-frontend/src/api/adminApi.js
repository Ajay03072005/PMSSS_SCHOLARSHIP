import axiosClient from './axiosClient';

export const adminApi = {
  getWorkloads: () => axiosClient.get('/admin/workload'),
  getWorkload: () => axiosClient.get('/admin/workload'),
  rebalanceWorkload: () => axiosClient.post('/admin/workload/rebalance'),
  getEscalations: (status = 'OPEN') => axiosClient.get(`/admin/escalations?status=${status}`),
  getProcessingMetrics: () => axiosClient.get('/admin/processing-metrics'),
  getAuditLogs: (params = {}) => axiosClient.get('/audit', { params }),
  getDashboardStats: () => axiosClient.get('/dashboard/admin'),
  getUsers: (params = {}) => axiosClient.get('/admin/users', { params }),
  updateUserRole: (id, role) => axiosClient.put(`/admin/users/${id}/role`, null, { params: { role } }),
  toggleUserStatus: (id, active) => axiosClient.put(`/admin/users/${id}/status`, null, { params: { active } }),
  getOfficers: () => axiosClient.get('/admin/officers'),
  getSettings: () => axiosClient.get('/admin/settings'),
  updateSettings: (data) => axiosClient.put('/admin/settings', data),
  getApplications: (params = {}) => axiosClient.get('/applications', { params }),
  getAssignments: () => axiosClient.get('/admin/assignments'),
  getDocuments: () => axiosClient.get('/admin/documents'),
  getCorrections: () => axiosClient.get('/admin/corrections'),
};

export default adminApi;
