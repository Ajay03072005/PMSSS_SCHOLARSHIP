// Status Tracking Form Handler
document.addEventListener('DOMContentLoaded', function() {
    const trackForm = document.querySelector('.status-form');
    
    if (trackForm) {
        trackForm.addEventListener('submit', handleTrackApplication);
    }
});

async function handleTrackApplication(e) {
    e.preventDefault();
    
    // Get form values
    const applicationId = document.getElementById('appId').value.trim();
    const dateOfBirth = document.getElementById('dob').value;
    
    // Validation
    if (!applicationId || !dateOfBirth) {
        showMessage('Please enter both Application ID and Date of Birth', 'error');
        return;
    }
    
    // Show loading state
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Tracking...';
    submitBtn.disabled = true;
    
    try {
        // Make API call
        const response = await apiCall(API_CONFIG.ENDPOINTS.TRACK_APPLICATION, {
            method: 'POST',
            body: JSON.stringify({
                applicationId,
                dateOfBirth
            })
        });
        
        if (response.success) {
            displayApplicationStatus(response.application);
        }
        
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        
    } catch (error) {
        showMessage(error.message || 'Unable to track application. Please check your details.', 'error');
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

function displayApplicationStatus(application) {
    const statusResult = document.getElementById('statusResult');
    
    if (!statusResult) {
        // Create status result section if it doesn't exist
        const statusSection = document.querySelector('.status-form-card');
        const resultDiv = document.createElement('div');
        resultDiv.id = 'statusResult';
        resultDiv.className = 'status-result';
        
        if (statusSection && statusSection.parentElement) {
            statusSection.parentElement.appendChild(resultDiv);
        } else {
            console.error('Could not find status section to append result');
            return;
        }
    }
    
    const resultDiv = document.getElementById('statusResult');
    resultDiv.innerHTML = `
        <div class="status-card">
            <div class="status-header">
                <h3>Application Status</h3>
                <span class="status-badge status-${application.status}">${formatStatus(application.status)}</span>
            </div>
            
            <div class="applicant-info">
                <h4>Applicant Details</h4>
                <p><strong>Name:</strong> ${application.personalInfo.firstName} ${application.personalInfo.lastName}</p>
                <p><strong>Application ID:</strong> ${application.applicationId}</p>
                <p><strong>Submitted:</strong> ${application.submittedAt ? new Date(application.submittedAt).toLocaleDateString() : 'Not submitted'}</p>
            </div>
            
            <div style="text-align: center; margin: 20px 0;">
                <button onclick="window.location.href='view-application.html?id=${application.applicationId}'" 
                        style="background: linear-gradient(135deg, #e91e63 0%, #c2185b 100%); 
                               color: white; 
                               padding: 12px 30px; 
                               border: none; 
                               border-radius: 25px; 
                               font-size: 16px; 
                               font-weight: 600; 
                               cursor: pointer;
                               transition: transform 0.2s;">
                    📄 View Full Application
                </button>
            </div>
            
            <div class="status-timeline">
                <h4>Application Timeline</h4>
                ${generateTimeline(application.statusHistory)}
            </div>
        </div>
    `;
    
    resultDiv.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function generateTimeline(statusHistory) {
    if (!statusHistory || statusHistory.length === 0) {
        return '<p>No status updates available</p>';
    }
    
    return statusHistory.map((item, index) => `
        <div class="timeline-item ${index === statusHistory.length - 1 ? 'active' : ''}">
            <div class="timeline-dot"></div>
            <div class="timeline-content">
                <h5>${formatStatus(item.status)}</h5>
                <p class="timeline-date">${new Date(item.date).toLocaleString()}</p>
                ${item.remarks ? `<p class="timeline-remarks">${item.remarks}</p>` : ''}
            </div>
        </div>
    `).join('');
}

function formatStatus(status) {
    const statusMap = {
        'draft': 'Draft',
        'submitted': 'Submitted',
        'under_review': 'Under Review',
        'verified': 'Verified',
        'approved': 'Approved',
        'rejected': 'Rejected',
        'disbursed': 'Disbursed'
    };
    return statusMap[status] || status;
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
    
    // Insert at top of status form
    const statusForm = document.querySelector('.status-form');
    if (statusForm) {
        statusForm.parentElement.insertBefore(messageDiv, statusForm);
    }
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        messageDiv.remove();
    }, 5000);
}
