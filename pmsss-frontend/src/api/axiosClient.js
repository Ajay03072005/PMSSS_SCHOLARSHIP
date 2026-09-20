import axios from 'axios';

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1';

const axiosClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

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
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('pmsss_token');
      localStorage.removeItem('pmsss_user');
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
