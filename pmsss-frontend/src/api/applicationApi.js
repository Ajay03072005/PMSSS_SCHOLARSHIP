import axiosClient from './axiosClient';

export const applicationApi = {
  submitApplication: (data) => axiosClient.post('/applications', data),
  getMyApplications: () => axiosClient.get('/applications/my'),
  getApplicationById: (id) => axiosClient.get(`/applications/${id}`),
  trackStatus: (applicationId, dob) => axiosClient.get(`/applications/track/${applicationId}?dob=${dob}`),
  getAllApplications: (params = {}) => axiosClient.get('/applications', { params }),
};
