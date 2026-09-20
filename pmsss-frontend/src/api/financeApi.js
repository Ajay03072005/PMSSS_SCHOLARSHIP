import axiosClient from './axiosClient';

export const financeApi = {
  getPayments: (params = {}) => {
    let status = 'PENDING';
    let page = 0;
    let size = 10;
    if (typeof params === 'string') {
      status = params;
    } else if (params && typeof params === 'object') {
      if (params.status) status = params.status;
      if (params.page !== undefined) page = params.page;
      if (params.size !== undefined) size = params.size;
    }
    return axiosClient.get(`/payment?status=${status}&page=${page}&size=${size}`);
  },
  getDashboard: () => axiosClient.get('/dashboard/finance'),
  getApprovedApplications: (params = {}) =>
    axiosClient.get('/applications', { params: { status: 'APPROVED', page: 0, size: 50, ...params } }),
  initiatePayment: (applicationId, amount) =>
    axiosClient.post(`/payment/initiate?applicationId=${applicationId}&amount=${amount}`),
  disbursePayment: (paymentId) =>
    axiosClient.post(`/payment/disburse/${paymentId}`),
  failPayment: (paymentId, reason) =>
    axiosClient.post(`/payment/fail/${paymentId}?reason=${encodeURIComponent(reason)}`),
  getTransactions: (paymentId) =>
    axiosClient.get(`/payment/${paymentId}/transactions`),
};

export default financeApi;
