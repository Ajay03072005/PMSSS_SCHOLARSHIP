import axiosClient from './axiosClient';

export const aiApi = {
  checkCompleteness: (applicationId) => axiosClient.get(`/ai/completeness/${applicationId}`),
  checkConsistency: (applicationId) => axiosClient.get(`/ai/consistency/${applicationId}`),
  checkDuplicates: (applicationId) => axiosClient.get(`/ai/duplicates/${applicationId}`),
  getAnomalies: (applicationId) => axiosClient.get(`/ai/anomalies/${applicationId}`),
  checkEligibility: (data) => axiosClient.post('/ai/eligibility/check', data),
  sendChatMessage: (message) => axiosClient.post('/ai/chatbot/message', { message }),
  getReviewQueue: () => axiosClient.get('/ai/review-queue'),
  runNaturalLanguageAnalytics: (query) => axiosClient.post('/ai/analytics/query', { query }),
};
