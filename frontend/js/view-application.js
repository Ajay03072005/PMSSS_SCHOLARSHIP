// View Application JavaScript - Professional Form Layout
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
    
    console.log('Application Data:', applicationData); // Debug log
    
    // Parse academic info if it's a string
    let academicInfo = applicationData.academic_info;
    if (typeof academicInfo === 'string') {
        academicInfo = JSON.parse(academicInfo);
    }
    
    // Hide loading, show content
    document.getElementById('loading').style.display = 'none';
    document.getElementById('applicationContent').style.display = 'block';
    
    // Application ID and Date
    document.getElementById('applicationId').textContent = applicationData.application_id;
    if (applicationData.submitted_at) {
        const date = new Date(applicationData.submitted_at);
        document.getElementById('submissionDate').textContent = `Submitted: ${date.toLocaleDateString('en-IN', {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        })}`;
    }
    
    // Photo
    const photoContainer = document.getElementById('photoContainer');
    if (applicationData.photo && applicationData.photo !== null && applicationData.photo !== '') {
        photoContainer.innerHTML = `<img src="http://localhost:8000/php-backend/${applicationData.photo}" alt="Photo">`;
    } else {
        photoContainer.innerHTML = 'Passport Size<br>Photo<br>(Not Uploaded)';
    }
    
    // Personal Information Table
    const personalRows = [
        ['Full Name', `${applicationData.first_name} ${applicationData.middle_name || ''} ${applicationData.last_name}`.trim()],
        ['Date of Birth', formatDate(applicationData.date_of_birth)],
        ['Gender', capitalizeFirst(applicationData.gender)],
        ['Category', applicationData.category.toUpperCase()],
        ['Aadhar Number', applicationData.aadhar],
        ['Mobile Number', applicationData.mobile],
        ['Email Address', applicationData.email],
        ['Permanent Address', applicationData.address],
        ['District', applicationData.district],
        ['State', applicationData.state],
        ['Pincode', applicationData.pincode]
    ];
    
    document.getElementById('personalInfo').innerHTML = personalRows.map(row => `
        <tr>
            <td>${row[0]}</td>
            <td>${row[1] || 'N/A'}</td>
        </tr>
    `).join('');
    
    // Academic Information
    let academicHTML = '';
    
    // 10th Standard
    if (academicInfo && academicInfo.tenth) {
        academicHTML += `
            <h4 style="color: #000; font-size: 14px; margin: 15px 0 10px 0; font-weight: bold;">10th Standard Details</h4>
            <table class="form-table-2col">
                <tr>
                    <td>Board</td>
                    <td>${academicInfo.tenth.board || 'N/A'}</td>
                    <td>Year of Passing</td>
                    <td>${academicInfo.tenth.yearOfPassing || 'N/A'}</td>
                </tr>
                <tr>
                    <td>Percentage</td>
                    <td>${academicInfo.tenth.percentage || 'N/A'}%</td>
                    <td>Roll Number</td>
                    <td>${academicInfo.tenth.rollNumber || 'N/A'}</td>
                </tr>
                <tr>
                    <td colspan="1">School Name</td>
                    <td colspan="3">${academicInfo.tenth.schoolName || 'N/A'}</td>
                </tr>
            </table>
        `;
    }
    
    // 12th Standard
    if (academicInfo && academicInfo.twelfth) {
        academicHTML += `
            <h4 style="color: #000; font-size: 14px; margin: 15px 0 10px 0; font-weight: bold;">12th Standard Details</h4>
            <table class="form-table-2col">
                <tr>
                    <td>Board</td>
                    <td>${academicInfo.twelfth.board || 'N/A'}</td>
                    <td>Stream</td>
                    <td>${capitalizeFirst(academicInfo.twelfth.stream) || 'N/A'}</td>
                </tr>
                <tr>
                    <td>Year of Passing</td>
                    <td>${academicInfo.twelfth.yearOfPassing || 'N/A'}</td>
                    <td>Percentage</td>
                    <td>${academicInfo.twelfth.percentage || 'N/A'}%</td>
                </tr>
                <tr>
                    <td>Roll Number</td>
                    <td>${academicInfo.twelfth.rollNumber || 'N/A'}</td>
                    <td colspan="2"></td>
                </tr>
                <tr>
                    <td>School Name</td>
                    <td colspan="3">${academicInfo.twelfth.schoolName || 'N/A'}</td>
                </tr>
            </table>
        `;
    }
    
    // Course Information
    if (academicInfo && academicInfo.course) {
        academicHTML += `
            <h4 style="color: #000; font-size: 14px; margin: 15px 0 10px 0; font-weight: bold;">Current Course Details</h4>
            <table class="form-table-2col">
                <tr>
                    <td>Course Type</td>
                    <td>${capitalizeFirst(academicInfo.course.type) || 'N/A'}</td>
                    <td>Course Name</td>
                    <td>${academicInfo.course.name || 'N/A'}</td>
                </tr>
                <tr>
                    <td>Duration</td>
                    <td>${academicInfo.course.duration || 'N/A'} years</td>
                    <td>Admission Year</td>
                    <td>${academicInfo.course.admissionYear || 'N/A'}</td>
                </tr>
                <tr>
                    <td>College Name</td>
                    <td colspan="3">${academicInfo.course.collegeName || 'N/A'}</td>
                </tr>
                <tr>
                    <td>College State</td>
                    <td>${academicInfo.course.collegeState || 'N/A'}</td>
                    <td>Admission Status</td>
                    <td>${capitalizeFirst(academicInfo.course.admissionStatus) || 'N/A'}</td>
                </tr>
            </table>
        `;
    }
    
    document.getElementById('academicInfo').innerHTML = academicHTML;
    
    // Family Information Table
    const familyRows = [
        ["Father's Name", applicationData.father_name],
        ["Father's Occupation", applicationData.father_occupation],
        ["Father's Mobile", applicationData.father_mobile],
        ["Mother's Name", applicationData.mother_name],
        ["Mother's Occupation", applicationData.mother_occupation],
        ["Mother's Mobile", applicationData.mother_mobile],
        ['Annual Family Income', applicationData.annual_income ? `₹ ${applicationData.annual_income}` : 'N/A'],
        ['Source of Income', applicationData.income_source]
    ];
    
    document.getElementById('familyInfo').innerHTML = familyRows.map(row => `
        <tr>
            <td>${row[0]}</td>
            <td>${row[1] || 'N/A'}</td>
        </tr>
    `).join('');
    
    // Bank Details Table
    const bankRows = [
        ['Account Holder Name', applicationData.account_holder_name],
        ['Bank Account Number', applicationData.account_number],
        ['IFSC Code', applicationData.ifsc_code],
        ['Bank Name', applicationData.bank_name],
        ['Branch Name', applicationData.branch_name]
    ];
    
    document.getElementById('bankInfo').innerHTML = bankRows.map(row => `
        <tr>
            <td>${row[0]}</td>
            <td>${row[1] || 'N/A'}</td>
        </tr>
    `).join('');
    
    // Documents - Check for actual file paths, not just truthy values
    const documents = [
        { name: 'Passport Size Photograph', field: 'photo' },
        { name: 'Aadhar Card', field: 'aadhar_doc' },
        { name: 'Domicile Certificate', field: 'domicile' },
        { name: 'Income Certificate', field: 'income_cert' },
        { name: '10th Standard Marksheet', field: 'tenth_marksheet' },
        { name: '12th Standard Marksheet', field: 'twelfth_marksheet' },
        { name: 'College Admission Letter', field: 'admission_letter' },
        { name: 'Bank Passbook / Cancelled Cheque', field: 'bank_passbook' }
    ];
    
    console.log('Document fields check:'); // Debug log
    documents.forEach(doc => {
        const value = applicationData[doc.field];
        console.log(`${doc.name} (${doc.field}):`, value, typeof value, value !== null, value !== '');
    });
    
    document.getElementById('documentsInfo').innerHTML = documents.map(doc => {
        const value = applicationData[doc.field];
        // Check if value exists, is not null, and is not an empty string
        const isUploaded = value && value !== null && value !== '' && value !== 'null';
        return `
            <div class="doc-item">
                <span>${doc.name}</span>
                <span class="doc-status ${isUploaded ? 'uploaded' : 'missing'}">
                    ${isUploaded ? '✓ Uploaded' : '✗ Not Uploaded'}
                </span>
            </div>
        `;
    }).join('');
}

function downloadPDF() {
    const element = document.getElementById('printableArea');
    
    // Create a clone for PDF generation with optimizations
    const clone = element.cloneNode(true);
    
    const opt = {
        margin: 10,
        filename: `PMSSS_Application_${applicationData.application_id}.pdf`,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { 
            scale: 3,
            useCORS: true,
            logging: true,
            letterRendering: true,
            allowTaint: false,
            backgroundColor: '#ffffff'
        },
        jsPDF: { 
            unit: 'mm', 
            format: 'a4', 
            orientation: 'portrait',
            compress: true
        },
        pagebreak: { 
            mode: ['avoid-all', 'css', 'legacy']
        }
    };
    
    // Show loading message
    const downloadBtn = document.querySelector('.btn-download');
    const originalText = downloadBtn.innerHTML;
    downloadBtn.innerHTML = '<span>⏳</span> Generating PDF...';
    downloadBtn.disabled = true;
    
    html2pdf().set(opt).from(element).save().then(() => {
        downloadBtn.innerHTML = originalText;
        downloadBtn.disabled = false;
    }).catch(err => {
        console.error('PDF generation error:', err);
        downloadBtn.innerHTML = originalText;
        downloadBtn.disabled = false;
        alert('Failed to generate PDF. Please try printing instead.');
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
