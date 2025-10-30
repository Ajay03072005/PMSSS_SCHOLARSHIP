// Admin Login Handler
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('adminLoginForm');
    const loginBtn = document.getElementById('loginBtn');
    const alertDiv = document.getElementById('alert');

    // Check if already logged in
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken) {
        // Verify token is still valid
        verifyAdminToken(adminToken);
    }

    // Handle form submission
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;
        const rememberMe = document.getElementById('rememberMe').checked;

        if (!username || !password) {
            showAlert('Please enter both username and password', 'error');
            return;
        }

        // Disable button and show loading
        loginBtn.disabled = true;
        loginBtn.textContent = 'Logging in...';

        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}/admin/auth.php?action=login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    password: password,
                    remember: rememberMe
                })
            });

            const data = await response.json();

            if (data.success) {
                // Store admin token
                localStorage.setItem('adminToken', data.token);
                localStorage.setItem('adminUser', JSON.stringify(data.admin));
                
                if (rememberMe) {
                    localStorage.setItem('adminRemember', 'true');
                }

                showAlert('Login successful! Redirecting...', 'success');
                
                // Redirect to admin dashboard
                setTimeout(() => {
                    window.location.href = 'admin-dashboard.html';
                }, 1000);
            } else {
                showAlert(data.message || 'Invalid credentials', 'error');
                loginBtn.disabled = false;
                loginBtn.textContent = 'Login to Admin Panel';
            }
        } catch (error) {
            console.error('Login error:', error);
            showAlert('Login failed. Please check your connection and try again.', 'error');
            loginBtn.disabled = false;
            loginBtn.textContent = 'Login to Admin Panel';
        }
    });

    // Verify admin token
    async function verifyAdminToken(token) {
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}/admin/auth.php?action=verify`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            const data = await response.json();

            if (data.success && data.valid) {
                // Token is valid, redirect to dashboard
                window.location.href = 'admin-dashboard.html';
            } else {
                // Token invalid, clear storage
                localStorage.removeItem('adminToken');
                localStorage.removeItem('adminUser');
            }
        } catch (error) {
            console.error('Token verification error:', error);
        }
    }

    // Show alert message
    function showAlert(message, type) {
        alertDiv.textContent = message;
        alertDiv.className = `alert ${type}`;
        alertDiv.style.display = 'block';

        if (type === 'success') {
            setTimeout(() => {
                alertDiv.style.display = 'none';
            }, 3000);
        }
    }
});
