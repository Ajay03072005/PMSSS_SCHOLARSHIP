import axiosClient from './axiosClient';

export const documentApi = {
  uploadDocument: (applicationUniqueId, documentType, file) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('documentType', documentType);
    if (applicationUniqueId) {
      formData.append('applicationUniqueId', applicationUniqueId);
      formData.append('applicationId', applicationUniqueId);
    }
    return axiosClient.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  replaceDocument: (documentUniqueId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return axiosClient.put(`/documents/${documentUniqueId}/replace`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  getMyDocuments: () => axiosClient.get('/applications/my/documents').catch(() => axiosClient.get('/documents/application/my')),
  getDocuments: (applicationUniqueId) => axiosClient.get(`/applications/${applicationUniqueId}/documents`),
  viewDocumentUrl: (documentUniqueId) => `/api/v1/documents/${documentUniqueId}/view`,
  downloadDocumentUrl: (documentUniqueId) => `/api/v1/documents/${documentUniqueId}/download`,
  deleteDocument: (documentUniqueId) => axiosClient.delete(`/documents/${documentUniqueId}`),
  getExtractedData: (documentId) => axiosClient.get(`/documents/${documentId}/extracted-data`),
};

