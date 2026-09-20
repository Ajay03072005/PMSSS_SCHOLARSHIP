import axiosClient from './axiosClient';

export const notificationApi = {
  getMyNotifications: () => axiosClient.get('/notifications'),
  getUnreadCount: () => axiosClient.get('/notifications/unread-count'),
  markAsRead: (id) => axiosClient.put(`/notifications/${id}/read`),
  markAllAsRead: () => axiosClient.put('/notifications/read-all'),
  getPreferences: () => axiosClient.get('/notifications/preferences'),
  updatePreferences: (data) => axiosClient.put('/notifications/preferences', data),
  getAdminLogs: (params) => axiosClient.get('/admin/notifications', { params }),
  retryLog: (id) => axiosClient.post(`/admin/notifications/${id}/retry`),
};

export default notificationApi;
