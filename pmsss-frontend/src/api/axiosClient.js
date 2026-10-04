import axios from 'axios';

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1';

const axiosClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const refreshClient = axios.create({ baseURL });

const clearStoredSession = () => {
  localStorage.removeItem('pmsss_token');
  localStorage.removeItem('pmsss_refresh_token');
  localStorage.removeItem('pmsss_user');
};

// Request interceptor: attach JWT access token
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('pmsss_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: handle 401s and standardize errors
axiosClient.interceptors.response.use(
  (response) => {
    // Return data directly if standard ApiResponse
    return response.data;
  },
  async (error) => {
    const originalRequest = error.config;
    const isAuthRequest = originalRequest?.url?.includes('/auth/login')
      || originalRequest?.url?.includes('/auth/register')
      || originalRequest?.url?.includes('/auth/refresh')
      || originalRequest?.url?.includes('/auth/logout');

    if (error.response?.status === 401 && !originalRequest?._retry && !isAuthRequest) {
      const refreshToken = localStorage.getItem('pmsss_refresh_token');
      if (refreshToken) {
        originalRequest._retry = true;
        try {
          const refreshResponse = await refreshClient.post('/auth/refresh', { refreshToken });
          const refreshed = refreshResponse.data?.data || refreshResponse.data;
          localStorage.setItem('pmsss_token', refreshed.token);
          if (refreshed.refreshToken) {
            localStorage.setItem('pmsss_refresh_token', refreshed.refreshToken);
          }
          originalRequest.headers.Authorization = `Bearer ${refreshed.token}`;
          return axiosClient(originalRequest);
        } catch (refreshError) {
          clearStoredSession();
        }
      } else {
        clearStoredSession();
      }

      const isAuthPage = window.location.pathname.includes('/login') || window.location.pathname.includes('/register');
      if (!isAuthPage) {
        window.location.href = '/login';
      }
    }
    const message = error.response?.data?.message || error.message || 'An unexpected error occurred';
    return Promise.reject(new Error(message));
  }
);

export default axiosClient;
