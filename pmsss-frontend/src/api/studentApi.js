import axiosClient from './axiosClient';

export const studentApi = {
  getProfile: () => axiosClient.get('/student/profile'),
  updateProfile: (profileData) => axiosClient.put('/student/profile', profileData),
  getMyPayments: () => axiosClient.get('/payment/my'),
  getPayments: () => axiosClient.get('/payment/my'),
  getActiveApplication: async () => {
    const res = await axiosClient.get('/applications/my');
    const apps = res?.data || res;
    if (Array.isArray(apps) && apps.length > 0) {
      return { data: apps[0] };
    }
    return { data: null };
  },
  getCorrectionRequests: async () => {
    const res = await axiosClient.get('/student/corrections');
    return { data: res?.data || [] };
  },
};

export default studentApi;
