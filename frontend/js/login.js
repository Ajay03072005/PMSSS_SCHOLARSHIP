// Login Form Handler
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('.login-form');
    
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    // Check if user is already logged in
    const token = getToken();
    if (token) {
        // Verify token is valid
        verifyToken();
    }
});

async function handleLogin(e) {
    e.preventDefault();
    
    // Get form values
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const rememberMe = document.getElementById('remember')?.checked;
    
    // Validation
    if (!email || !password) {
        showMessage('Please enter both email and password', 'error');
        return;
    }
    
    if (!/^\S+@\S+\.\S+$/.test(email)) {
        showMessage('Please enter a valid email address', 'error');
        return;
    }
    
    // Show loading state
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Logging in...';
    submitBtn.disabled = true;
    
    try {
        // Make API call
        const response = await apiCall(API_CONFIG.ENDPOINTS.LOGIN, {
            method: 'POST',
            body: JSON.stringify({
                email,
                password
            })
        });
        
        if (response.success) {
            // Save token and user data
            setToken(response.token);
            setUser(response.user);
            
            showMessage('Login successful! Redirecting...', 'success');
            
            // Redirect based on user role
            setTimeout(() => {
                if (response.user.role === 'admin' || response.user.role === 'reviewer') {
                    window.location.href = 'admin-dashboard.html';
                } else {
                    window.location.href = 'application-form.html';
                }
            }, 1500);
        }
        
    } catch (error) {
        showMessage(error.message || 'Login failed. Please check your credentials.', 'error');
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

async function verifyToken() {
    try {
        const response = await apiCall(API_CONFIG.ENDPOINTS.GET_USER, {
            method: 'GET'
        });
        
        if (response.success) {
            setUser(response.user);
        }
    } catch (error) {
        // Token is invalid, clear it
        removeToken();
        removeUser();
    }
}

function logout() {
    removeToken();
    removeUser();
    showMessage('Logged out successfully', 'success');
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 1000);
}

function showMessage(message, type) {
    // Remove existing message if any
    const existingMsg = document.querySelector('.alert-message');
    if (existingMsg) {
        existingMsg.remove();
    }
    
    // Create message element
    const messageDiv = document.createElement('div');
    messageDiv.className = `alert-message alert-${type}`;
    messageDiv.innerHTML = `
        <span>${message}</span>
        <button onclick="this.parentElement.remove()" style="background: none; border: none; color: inherit; font-size: 20px; cursor: pointer; margin-left: 10px;">&times;</button>
    `;
    
    // Insert at top of login card
    const loginCard = document.querySelector('.login-card');
    if (loginCard) {
        loginCard.insertBefore(messageDiv, loginCard.firstChild);
    }
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        messageDiv.remove();
    }, 5000);
}
