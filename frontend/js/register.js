// Registration Form Handler
document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.querySelector('.register-form');
    
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
});

async function handleRegister(e) {
    e.preventDefault();
    
    // Get form values
    const firstName = document.getElementById('firstName').value.trim();
    const lastName = document.getElementById('lastName').value.trim();
    const email = document.getElementById('email').value.trim();
    const mobile = document.getElementById('mobile').value.trim();
    const dateOfBirth = document.getElementById('dob').value;
    const aadhar = document.getElementById('aadhar').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const termsAccepted = document.getElementById('terms').checked;
    
    // Validation
    if (!firstName || !lastName || !email || !mobile || !dateOfBirth || !aadhar || !password) {
        showMessage('Please fill all required fields', 'error');
        return;
    }
    
    // Mobile validation
    if (!/^[0-9]{10}$/.test(mobile)) {
        showMessage('Please enter a valid 10-digit mobile number', 'error');
        return;
    }
    
    // Aadhar validation
    if (!/^[0-9]{12}$/.test(aadhar)) {
        showMessage('Please enter a valid 12-digit Aadhar number', 'error');
        return;
    }
    
    // Password validation
    if (password.length < 8) {
        showMessage('Password must be at least 8 characters long', 'error');
        return;
    }
    
    if (password !== confirmPassword) {
        showMessage('Passwords do not match', 'error');
        return;
    }
    
    if (!termsAccepted) {
        showMessage('Please accept the terms and conditions', 'error');
        return;
    }
    
    // Show loading state
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Registering...';
    submitBtn.disabled = true;
    
    try {
        // Make API call
        const response = await apiCall(API_CONFIG.ENDPOINTS.REGISTER, {
            method: 'POST',
            body: JSON.stringify({
                firstName,
                lastName,
                email,
                mobile,
                dateOfBirth,
                aadhar,
                password
            })
        });
        
        if (response.success) {
            // Save token and user data
            setToken(response.token);
            setUser(response.user);
            
            // Send welcome email
            const emailData = {
                email: email,
                fullName: `${firstName} ${lastName}`
            };
            
            // Send email asynchronously (don't wait for it)
            sendRegistrationEmail(emailData).then(result => {
                if (result.success) {
                    console.log('Welcome email sent successfully');
                } else {
                    console.warn('Failed to send welcome email, but registration successful');
                }
            }).catch(err => {
                console.warn('Email sending error:', err);
            });
            
            showMessage('Registration successful! Welcome email sent. Redirecting...', 'success');
            
            // Redirect to application page after 2 seconds
            setTimeout(() => {
                window.location.href = 'application-form.html';
            }, 2000);
        }
        
    } catch (error) {
        showMessage(error.message || 'Registration failed. Please try again.', 'error');
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
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
    
    // Insert at top of register card
    const registerCard = document.querySelector('.register-card');
    registerCard.insertBefore(messageDiv, registerCard.firstChild);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        messageDiv.remove();
    }, 5000);
}
