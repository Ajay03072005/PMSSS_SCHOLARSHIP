// API Configuration
const API_CONFIG = {
    BASE_URL: 'http://localhost:8000/api',
    ENDPOINTS: {
        // Auth
        REGISTER: '/auth.php?action=register',
        LOGIN: '/auth.php?action=login',
        GET_USER: '/auth.php?action=me',
        
        // Applications - Using debug version temporarily
        SUBMIT_APPLICATION: '/applications-debug.php',
        GET_MY_APPLICATIONS: '/applications.php?action=my',
        GET_APPLICATION: '/applications.php',
        GET_APPLICATIONS: '/applications.php',
        TRACK_APPLICATION: '/applications.php?action=track',
        
        // Users
        GET_PROFILE: '/users/profile',
        UPDATE_PROFILE: '/users/profile',
        GET_NOTIFICATIONS: '/users/notifications',
        MARK_READ: '/users/notifications',
        
        // Admin
        GET_ALL_APPLICATIONS: '/admin/applications',
        UPDATE_STATUS: '/admin/applications',
        GET_STATISTICS: '/admin/statistics'
    }
};

// Helper function to get token
const getToken = () => localStorage.getItem('token');

// Helper function to set token
const setToken = (token) => localStorage.setItem('token', token);

// Helper function to remove token
const removeToken = () => localStorage.removeItem('token');

// Helper function to get user
const getUser = () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
};

// Helper function to set user
const setUser = (user) => localStorage.setItem('user', JSON.stringify(user));

// Helper function to remove user
const removeUser = () => localStorage.removeItem('user');

// API call helper with authentication
async function apiCall(endpoint, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    const config = {
        method: 'GET',
        ...options,
        headers
    };
    
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${endpoint}`, config);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || 'API request failed');
        }
        
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// API call helper for file uploads
async function apiCallWithFiles(endpoint, formData) {
    const token = getToken();
    const headers = {};
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${endpoint}`, {
            method: 'POST',
            headers,
            body: formData
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || 'File upload failed');
        }
        
        return data;
    } catch (error) {
        console.error('Upload Error:', error);
        throw error;
    }
}
