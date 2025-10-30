// View Application JavaScript
let applicationData = null;

document.addEventListener('DOMContentLoaded', function() {
    loadApplication();
});

async function loadApplication() {
    try {
        // Get application ID from URL parameter
        const urlParams = new URLSearchParams(window.location.search);
        const appId = urlParams.get('id');
        
        // Check if user is logged in
        const token = getToken();
        if (!token) {
            alert('Please login to view your application');
            window.location.href = 'login.html';
            return;
        }
        
        if (!appId) {
            // If no ID in URL, get the latest application for logged-in user
            const response = await apiCall(API_CONFIG.ENDPOINTS.GET_MY_APPLICATIONS, { method: 'GET' });
            
            if (response.success && response.applications && response.applications.length > 0) {
                // Get the most recent submitted application
                const submittedApps = response.applications.filter(app => app.status === 'submitted');
                if (submittedApps.length > 0) {
                    applicationData = submittedApps[0];
                } else {
                    applicationData = response.applications[0];
                }
            } else {
                alert('No application found');
                window.location.href = 'application-form.html';
                return;
            }
        } else {
            // Fetch specific application by ID
            const response = await apiCall(`${API_CONFIG.ENDPOINTS.GET_APPLICATION}?action=view&id=${appId}`, { method: 'GET' });
            
            if (response.success && response.application) {
                applicationData = response.application;
            } else {
                alert('Application not found');
                window.location.href = 'status.html';
                return;
            }
        }
        
        displayApplication();
        
    } catch (error) {
        console.error('Error loading application:', error);
        alert('Failed to load application: ' + error.message);
        window.location.href = 'status.html';
    }
}

function displayApplication() {
    if (!applicationData) {
        document.getElementById('loading').textContent = 'No application data found';
        return;
    }
    
    // Parse academic info if it's a string
    let academicInfo = applicationData.academic_info;
    if (typeof academicInfo === 'string') {
        academicInfo = JSON.parse(academicInfo);
    }
    
    // Hide loading, show content
    document.getElementById('loading').style.display = 'none';
    document.getElementById('applicationContent').style.display = 'block';
    
    // Application ID and Date
    document.getElementById('applicationId').textContent = `Application ID: ${applicationData.application_id}`;
    if (applicationData.submitted_at) {
        const date = new Date(applicationData.submitted_at);
        document.getElementById('submissionDate').textContent = `Submitted on: ${date.toLocaleDateString('en-IN', {
            day: '2-digit',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        })}`;
    }
    
    // Personal Information
    const personalInfo = [
        { label: 'Full Name', value: `${applicationData.first_name} ${applicationData.middle_name || ''} ${applicationData.last_name}`.trim() },
        { label: 'Date of Birth', value: formatDate(applicationData.date_of_birth) },
        { label: 'Gender', value: capitalizeFirst(applicationData.gender) },
        { label: 'Category', value: applicationData.category.toUpperCase() },
        { label: 'Aadhar Number', value: applicationData.aadhar },
        { label: 'Mobile Number', value: applicationData.mobile },
        { label: 'Email', value: applicationData.email },
        { label: 'Address', value: applicationData.address },
        { label: 'District', value: applicationData.district },
        { label: 'State', value: applicationData.state },
        { label: 'Pincode', value: applicationData.pincode }
    ];
    
    document.getElementById('personalInfo').innerHTML = personalInfo.map(item => `
        <div class="info-item">
            <div class="info-label">${item.label}</div>
            <div class="info-value">${item.value || 'N/A'}</div>
        </div>
    `).join('');
    
    // Academic Information
    let academicHTML = '';
    
    // 10th Standard
    if (academicInfo.tenth) {
        academicHTML += `
            <h3 style="color: white; font-size: 16px; margin: 20px 0 10px 0;">10th Standard</h3>
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Board</div>
                    <div class="info-value">${academicInfo.tenth.board || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Year of Passing</div>
                    <div class="info-value">${academicInfo.tenth.yearOfPassing || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Percentage</div>
                    <div class="info-value">${academicInfo.tenth.percentage || 'N/A'}%</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Roll Number</div>
                    <div class="info-value">${academicInfo.tenth.rollNumber || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">School Name</div>
                    <div class="info-value">${academicInfo.tenth.schoolName || 'N/A'}</div>
                </div>
            </div>
        `;
    }
    
    // 12th Standard
    if (academicInfo.twelfth) {
        academicHTML += `
            <h3 style="color: white; font-size: 16px; margin: 20px 0 10px 0;">12th Standard</h3>
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Board</div>
                    <div class="info-value">${academicInfo.twelfth.board || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Stream</div>
                    <div class="info-value">${capitalizeFirst(academicInfo.twelfth.stream) || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Year of Passing</div>
                    <div class="info-value">${academicInfo.twelfth.yearOfPassing || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Percentage</div>
                    <div class="info-value">${academicInfo.twelfth.percentage || 'N/A'}%</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Roll Number</div>
                    <div class="info-value">${academicInfo.twelfth.rollNumber || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">School Name</div>
                    <div class="info-value">${academicInfo.twelfth.schoolName || 'N/A'}</div>
                </div>
            </div>
        `;
    }
    
    // Course Information
    if (academicInfo.course) {
        academicHTML += `
            <h3 style="color: white; font-size: 16px; margin: 20px 0 10px 0;">Current Course</h3>
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Course Type</div>
                    <div class="info-value">${capitalizeFirst(academicInfo.course.type) || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Course Name</div>
                    <div class="info-value">${academicInfo.course.name || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Duration</div>
                    <div class="info-value">${academicInfo.course.duration || 'N/A'} years</div>
                </div>
                <div class="info-item">
                    <div class="info-label">College Name</div>
                    <div class="info-value">${academicInfo.course.collegeName || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">College State</div>
                    <div class="info-value">${academicInfo.course.collegeState || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Admission Year</div>
                    <div class="info-value">${academicInfo.course.admissionYear || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Admission Status</div>
                    <div class="info-value">${capitalizeFirst(academicInfo.course.admissionStatus) || 'N/A'}</div>
                </div>
            </div>
        `;
    }
    
    document.getElementById('academicInfo').innerHTML = academicHTML;
    
    // Family Information
    const familyInfo = [
        { label: "Father's Name", value: applicationData.father_name },
        { label: "Father's Occupation", value: applicationData.father_occupation },
        { label: "Father's Mobile", value: applicationData.father_mobile },
        { label: "Mother's Name", value: applicationData.mother_name },
        { label: "Mother's Occupation", value: applicationData.mother_occupation },
        { label: "Mother's Mobile", value: applicationData.mother_mobile },
        { label: 'Annual Income', value: applicationData.annual_income ? `₹${applicationData.annual_income}` : 'N/A' },
        { label: 'Income Source', value: applicationData.income_source }
    ];
    
    document.getElementById('familyInfo').innerHTML = familyInfo.map(item => `
        <div class="info-item">
            <div class="info-label">${item.label}</div>
            <div class="info-value">${item.value || 'N/A'}</div>
        </div>
    `).join('');
    
    // Bank Details
    const bankInfo = [
        { label: 'Account Holder Name', value: applicationData.account_holder_name },
        { label: 'Account Number', value: applicationData.account_number },
        { label: 'IFSC Code', value: applicationData.ifsc_code },
        { label: 'Bank Name', value: applicationData.bank_name },
        { label: 'Branch Name', value: applicationData.branch_name }
    ];
    
    document.getElementById('bankInfo').innerHTML = bankInfo.map(item => `
        <div class="info-item">
            <div class="info-label">${item.label}</div>
            <div class="info-value">${item.value || 'N/A'}</div>
        </div>
    `).join('');
    
    // Documents
    const documents = [
        { name: 'Passport Photo', field: 'photo', icon: '📷' },
        { name: 'Aadhar Card', field: 'aadhar_doc', icon: '🆔' },
        { name: 'Domicile Certificate', field: 'domicile', icon: '📄' },
        { name: 'Income Certificate', field: 'income_cert', icon: '💰' },
        { name: '10th Marksheet', field: 'tenth_marksheet', icon: '📊' },
        { name: '12th Marksheet', field: 'twelfth_marksheet', icon: '📈' },
        { name: 'Admission Letter', field: 'admission_letter', icon: '✉️' },
        { name: 'Bank Passbook', field: 'bank_passbook', icon: '🏦' }
    ];
    
    document.getElementById('documentsInfo').innerHTML = documents.map(doc => {
        const isUploaded = applicationData[doc.field] && applicationData[doc.field] !== null;
        return `
            <div class="doc-item ${isUploaded ? 'uploaded' : 'missing'}">
                <div class="doc-icon">${doc.icon}</div>
                <div class="doc-name">${doc.name}</div>
                <div class="doc-status ${isUploaded ? 'uploaded' : 'missing'}">
                    ${isUploaded ? '✓ Uploaded' : '✗ Not Uploaded'}
                </div>
            </div>
        `;
    }).join('');
}

function downloadPDF() {
    const element = document.getElementById('printableArea');
    const opt = {
        margin: 10,
        filename: `PMSSS_Application_${applicationData.application_id}.pdf`,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2, backgroundColor: '#2c3e50' },
        jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
    };
    
    // Show loading message
    const downloadBtn = document.querySelector('.btn-download');
    const originalText = downloadBtn.innerHTML;
    downloadBtn.innerHTML = '<span>⏳</span> Generating PDF...';
    downloadBtn.disabled = true;
    
    html2pdf().set(opt).from(element).save().then(() => {
        downloadBtn.innerHTML = originalText;
        downloadBtn.disabled = false;
    });
}

// Helper functions
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        day: '2-digit',
        month: 'long',
        year: 'numeric'
    });
}

function capitalizeFirst(str) {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1);
}
