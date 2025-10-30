// Multi-step form handling
let currentSection = 1;
const totalSections = 5;
let applicationData = {};

// Show specific section
function showSection(n) {
    const sections = document.getElementsByClassName('form-section');
    const progressSteps = document.getElementsByClassName('progress-step');
    
    // Hide all sections
    for (let i = 0; i < sections.length; i++) {
        sections[i].classList.remove('active');
    }
    
    // Remove active from all progress steps
    for (let i = 0; i < progressSteps.length; i++) {
        progressSteps[i].classList.remove('active');
        progressSteps[i].classList.remove('completed');
    }
    
    // Show current section
    sections[n - 1].classList.add('active');
    progressSteps[n - 1].classList.add('active');
    
    // Mark completed steps
    for (let i = 0; i < n - 1; i++) {
        progressSteps[i].classList.add('completed');
    }
    
    // Handle button visibility
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const submitBtn = document.getElementById('submitBtn');
    
    if (n === 1) {
        prevBtn.style.display = 'none';
    } else {
        prevBtn.style.display = 'inline-block';
    }
    
    if (n === totalSections) {
        nextBtn.style.display = 'none';
        submitBtn.style.display = 'inline-block';
        updateReviewSection();
    } else {
        nextBtn.style.display = 'inline-block';
        submitBtn.style.display = 'none';
    }
    
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Change section
function changeSection(direction) {
    // Validate current section before moving forward
    if (direction === 1 && !validateSection(currentSection)) {
        alert('Please fill all required fields in this section.');
        return;
    }
    
    currentSection += direction;
    
    if (currentSection < 1) {
        currentSection = 1;
    }
    if (currentSection > totalSections) {
        currentSection = totalSections;
    }
    
    showSection(currentSection);
}

// Validate section
function validateSection(section) {
    const currentSectionElement = document.getElementById('section' + section);
    const requiredInputs = currentSectionElement.querySelectorAll('[required]');
    
    for (let input of requiredInputs) {
        if (!input.value.trim()) {
            input.focus();
            return false;
        }
    }
    
    return true;
}

// Update review section with form data
function updateReviewSection() {
    // Personal Information Summary
    const personalSummary = document.getElementById('summary-personal');
    personalSummary.innerHTML = `
        <p><strong>Name:</strong> ${document.getElementById('firstName').value} ${document.getElementById('middleName').value} ${document.getElementById('lastName').value}</p>
        <p><strong>Date of Birth:</strong> ${document.getElementById('dob').value}</p>
        <p><strong>Gender:</strong> ${document.getElementById('gender').value}</p>
        <p><strong>Category:</strong> ${document.getElementById('category').value}</p>
        <p><strong>Mobile:</strong> ${document.getElementById('mobile').value}</p>
        <p><strong>Email:</strong> ${document.getElementById('email').value}</p>
        <p><strong>Address:</strong> ${document.getElementById('address').value}, ${document.getElementById('district').value}, ${document.getElementById('state').value} - ${document.getElementById('pincode').value}</p>
    `;
    
    // Academic Information Summary
    const academicSummary = document.getElementById('summary-academic');
    academicSummary.innerHTML = `
        <p><strong>12th Board:</strong> ${document.getElementById('twelfth-board').value}</p>
        <p><strong>12th Marks:</strong> ${document.getElementById('twelfth-marks').value} (${document.getElementById('twelfth-year').value})</p>
        <p><strong>Course:</strong> ${document.getElementById('course-name').value} (${document.getElementById('course-type').value})</p>
        <p><strong>College:</strong> ${document.getElementById('college-name').value}, ${document.getElementById('college-state').value}</p>
        <p><strong>Admission Year:</strong> ${document.getElementById('admission-year').value}</p>
    `;
    
    // Family Information Summary
    const familySummary = document.getElementById('summary-family');
    familySummary.innerHTML = `
        <p><strong>Father's Name:</strong> ${document.getElementById('father-name').value}</p>
        <p><strong>Mother's Name:</strong> ${document.getElementById('mother-name').value}</p>
        <p><strong>Annual Income:</strong> ₹${document.getElementById('annual-income').value}</p>
        <p><strong>Bank Account:</strong> ${document.getElementById('account-number').value} (${document.getElementById('bank-name').value})</p>
        <p><strong>IFSC Code:</strong> ${document.getElementById('ifsc-code').value}</p>
    `;
    
    // Documents Summary
    const documentsSummary = document.getElementById('summary-documents');
    const documents = [
        { id: 'doc-photo', name: 'Passport Photo' },
        { id: 'doc-aadhar', name: 'Aadhar Card' },
        { id: 'doc-domicile', name: 'Domicile Certificate' },
        { id: 'doc-income', name: 'Income Certificate' },
        { id: 'doc-tenth', name: '10th Marksheet' },
        { id: 'doc-twelfth', name: '12th Marksheet' },
        { id: 'doc-admission', name: 'Admission Letter' },
        { id: 'doc-bank', name: 'Bank Passbook' }
    ];
    
    let docsHtml = '<ul class="doc-list">';
    documents.forEach(doc => {
        const file = document.getElementById(doc.id).files[0];
        const status = file ? '✅' : '❌';
        const fileName = file ? file.name : 'Not uploaded';
        docsHtml += `<li>${status} ${doc.name}: <span class="file-name">${fileName}</span></li>`;
    });
    docsHtml += '</ul>';
    documentsSummary.innerHTML = docsHtml;
}

// Form submission
document.getElementById('applicationForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    // Call the actual submit function
    submitApplication();
});

// File input styling and validation
document.querySelectorAll('input[type="file"]').forEach(input => {
    input.addEventListener('change', function() {
        const file = this.files[0];
        if (file) {
            // Check file size (2MB = 2097152 bytes, 500KB = 512000 bytes)
            const maxSize = this.id === 'doc-photo' ? 512000 : 2097152;
            if (file.size > maxSize) {
                alert('File size exceeds the limit. Please upload a smaller file.');
                this.value = '';
                return;
            }
            
            // Update UI to show file is selected
            const label = this.previousElementSibling;
            label.style.borderColor = '#e91e63';
            label.style.background = 'rgba(233, 30, 99, 0.1)';
        }
    });
});

// Initialize form
showSection(currentSection);

// Auto-save functionality (optional)
let autoSaveInterval;
function enableAutoSave() {
    autoSaveInterval = setInterval(() => {
        const formData = new FormData(document.getElementById('applicationForm'));
        const data = {};
        formData.forEach((value, key) => {
            if (value instanceof File) {
                data[key] = value.name;
            } else {
                data[key] = value;
            }
        });
        localStorage.setItem('pmsss_application_draft', JSON.stringify(data));
        console.log('Form auto-saved');
    }, 30000); // Save every 30 seconds
}

// Load saved data on page load
window.addEventListener('load', () => {
    const savedData = localStorage.getItem('pmsss_application_draft');
    if (savedData) {
        const shouldLoad = confirm('We found a saved draft of your application. Would you like to continue from where you left?');
        if (shouldLoad) {
            const data = JSON.parse(savedData);
            Object.keys(data).forEach(key => {
                const element = document.getElementById(key) || document.querySelector(`[name="${key}"]`);
                // Skip file inputs - they cannot be programmatically set for security reasons
                if (element && element.type !== 'file' && !(data[key] instanceof File)) {
                    element.value = data[key];
                }
            });
        }
    }
    
    // Enable auto-save
    enableAutoSave();
});

// Clear saved data on successful submission
function clearDraft() {
    localStorage.removeItem('pmsss_application_draft');
    clearInterval(autoSaveInterval);
}

// Helper functions to safely get values
const getValue = (id, defaultValue = '') => {
    const element = document.getElementById(id);
    return element ? element.value.trim() : defaultValue;
};

const getNumberValue = (id, defaultValue = 0) => {
    const element = document.getElementById(id);
    return element ? (parseFloat(element.value) || defaultValue) : defaultValue;
};

// Submit application to backend
async function submitApplication() {
    try {
        // Check if user is logged in
        const token = getToken();
        if (!token) {
            alert('Please login to submit your application');
            window.location.href = 'login.html';
            return;
        }

        // Validate final section
        if (!validateSection(currentSection)) {
            alert('Please fill all required fields');
            return;
        }

        // Check declaration
        const declaration = document.getElementById('declaration');
        if (!declaration || !declaration.checked) {
            alert('Please accept the declaration to proceed');
            return;
        }

        // Show loading state
        const submitBtn = document.getElementById('submitBtn');
        if (!submitBtn) {
            alert('Submit button not found');
            return;
        }
        const originalText = submitBtn.textContent;
        submitBtn.textContent = 'Submitting...';
        submitBtn.disabled = true;

        // Prepare form data with safe field access
        const formData = new FormData();
        
        // Collect all form data
        const applicationFormData = {
            personalInfo: {
                firstName: getValue('firstName'),
                middleName: getValue('middleName'),
                lastName: getValue('lastName'),
                dateOfBirth: getValue('dob'),
                gender: getValue('gender'),
                category: getValue('category'),
                aadhar: getValue('aadhar'),
                mobile: getValue('mobile'),
                email: getValue('email'),
                address: getValue('address'),
                district: getValue('district'),
                state: getValue('state'),
                pincode: getValue('pincode')
            },
            academicInfo: {
                tenth: {
                    board: getValue('tenth-board'),
                    yearOfPassing: getNumberValue('tenth-year'),
                    percentage: getValue('tenth-marks'),
                    rollNumber: getValue('tenth-roll'),
                    schoolName: getValue('tenth-school')
                },
                twelfth: {
                    board: getValue('twelfth-board'),
                    yearOfPassing: getNumberValue('twelfth-year'),
                    percentage: getValue('twelfth-marks'),
                    rollNumber: getValue('twelfth-roll'),
                    schoolName: getValue('twelfth-school'),
                    stream: getValue('twelfth-stream')
                },
                course: {
                    type: getValue('course-type'),
                    name: getValue('course-name'),
                    duration: getNumberValue('course-duration'),
                    collegeName: getValue('college-name'),
                    collegeState: getValue('college-state'),
                    admissionYear: getNumberValue('admission-year'),
                    admissionStatus: getValue('admission-status')
                }
            },
            familyInfo: {
                father: {
                    name: getValue('father-name'),
                    occupation: getValue('father-occupation'),
                    mobile: getValue('father-mobile')
                },
                mother: {
                    name: getValue('mother-name'),
                    occupation: getValue('mother-occupation'),
                    mobile: getValue('mother-mobile')
                },
                annualIncome: getNumberValue('income'),
                incomeSource: getValue('income-source')
            },
            bankDetails: {
                accountHolderName: getValue('account-holder'),
                accountNumber: getValue('account-number'),
                ifscCode: getValue('ifsc-code'),
                bankName: getValue('bank-name'),
                branchName: getValue('branch-name')
            },
            declaration: true,
            submit: true
        };

        // Add application data as JSON
        formData.append('data', JSON.stringify(applicationFormData));

        // Add file uploads - using correct HTML IDs
        const fileFieldsMap = {
            'doc-photo': 'photo',
            'doc-aadhar': 'aadhar',
            'doc-domicile': 'domicile',
            'doc-income': 'income',
            'doc-tenth': 'tenthMarksheet',
            'doc-twelfth': 'twelfthMarksheet',
            'doc-admission': 'admissionLetter',
            'doc-bank': 'bankPassbook'
        };
        
        Object.keys(fileFieldsMap).forEach(htmlId => {
            const fileInput = document.getElementById(htmlId);
            const backendField = fileFieldsMap[htmlId];
            
            if (fileInput && fileInput.files.length > 0) {
                console.log(`Appending file: ${backendField} from input ${htmlId}`);
                formData.append(backendField, fileInput.files[0]);
            } else {
                console.log(`No file selected for: ${htmlId}`);
            }
        });

        // Submit to backend
        console.log('Submitting application with data:', applicationFormData);
        console.log('FormData entries:', Array.from(formData.entries()).map(([k,v]) => `${k}: ${v instanceof File ? v.name : 'data'}`));
        
        const response = await apiCallWithFiles(API_CONFIG.ENDPOINTS.SUBMIT_APPLICATION, formData);
        
        console.log('Server response:', response);

        if (response.success) {
            // Send confirmation email using EmailJS
            const emailData = {
                email: applicationFormData.personalInfo.email,
                firstName: applicationFormData.personalInfo.firstName,
                lastName: applicationFormData.personalInfo.lastName,
                applicationId: response.application.applicationId
            };
            
            // Send email (don't wait for it, send in background)
            if (typeof sendApplicationConfirmationEmail === 'function') {
                sendApplicationConfirmationEmail(emailData).then(result => {
                    if (result.success) {
                        console.log('Confirmation email sent successfully');
                    } else {
                        console.warn('Email sending failed, but application was submitted');
                    }
                });
            }
            
            // Clear draft
            clearDraft();
            
            // Show success message
            alert(`Application submitted successfully!\n\nYour Application ID: ${response.application.applicationId}\n\nA confirmation email has been sent to ${emailData.email}\n\nRedirecting to your application...`);
            
            // Redirect to view application page
            setTimeout(() => {
                window.location.href = `view-application.html?id=${response.application.applicationId}`;
            }, 1000);
        }

    } catch (error) {
        console.error('Submission error:', error);
        console.error('Error stack:', error.stack);
        alert(error.message || 'Failed to submit application. Please try again.');
        const submitBtn = document.getElementById('submitBtn');
        submitBtn.textContent = 'Submit Application';
        submitBtn.disabled = false;
    }
}

async function saveDraft() {
    try {
        
        const token = getToken();
        if (!token) {
            alert('Please login to save your application');
            window.location.href = 'login.html';
            return;
        }

        const saveBtn = event.target;
        const originalText = saveBtn.textContent;
        saveBtn.textContent = 'Saving...';
        saveBtn.disabled = true;
        const formData = new FormData();
        const applicationFormData = {
            personalInfo: {
                firstName: document.getElementById('firstName').value.trim(),
                middleName: document.getElementById('middleName').value.trim(),
                lastName: document.getElementById('lastName').value.trim(),
                dateOfBirth: document.getElementById('dob').value,
                gender: document.getElementById('gender').value,
                category: document.getElementById('category').value,
                aadhar: document.getElementById('aadhar').value.trim(),
                mobile: document.getElementById('mobile').value.trim(),
                email: document.getElementById('email').value.trim(),
                address: document.getElementById('address').value.trim(),
                district: document.getElementById('district').value,
                state: document.getElementById('state').value,
                pincode: document.getElementById('pincode').value.trim()
            },
            academicInfo: {
                tenth: {
                    board: getValue('tenth-board'),
                    yearOfPassing: getNumberValue('tenth-year'),
                    percentage: getValue('tenth-marks'),
                    rollNumber: getValue('tenth-roll'),
                    schoolName: getValue('tenth-school')
                },
                twelfth: {
                    board: getValue('twelfth-board'),
                    yearOfPassing: getNumberValue('twelfth-year'),
                    percentage: getValue('twelfth-marks'),
                    rollNumber: getValue('twelfth-roll'),
                    schoolName: getValue('twelfth-school'),
                    stream: getValue('twelfth-stream')
                },
                course: {
                    type: getValue('course-type'),
                    name: getValue('course-name'),
                    duration: getNumberValue('course-duration'),
                    collegeName: getValue('college-name'),
                    collegeState: getValue('college-state'),
                    admissionYear: getNumberValue('admission-year'),
                    admissionStatus: getValue('admission-status')
                }
            },
            familyInfo: {
                father: {
                    name: getValue('father-name'),
                    occupation: getValue('father-occupation'),
                    mobile: getValue('father-mobile')
                },
                mother: {
                    name: getValue('mother-name'),
                    occupation: getValue('mother-occupation'),
                    mobile: getValue('mother-mobile')
                },
                annualIncome: getNumberValue('income'),
                incomeSource: getValue('income-source')
            },
            bankDetails: {
                accountHolderName: getValue('account-holder'),
                accountNumber: getValue('account-number'),
                ifscCode: getValue('ifsc-code'),
                bankName: getValue('bank-name'),
                branchName: getValue('branch-name')
            },
            declaration: false,
            submit: false
        };

        formData.append('data', JSON.stringify(applicationFormData));

        // Add files if available
        const fileFields = ['photo', 'aadharDoc', 'domicile', 'income', 'marksheet10', 'marksheet12', 'admissionLetter', 'bankPassbook'];
        fileFields.forEach(field => {
            const fileInput = document.getElementById(field);
            if (fileInput && fileInput.files.length > 0) {
                const fieldMap = {
                    'aadharDoc': 'aadhar',
                    'marksheet10': 'tenthMarksheet',
                    'marksheet12': 'twelfthMarksheet'
                };
                const backendField = fieldMap[field] || field;
                formData.append(backendField, fileInput.files[0]);
            }
        });

        const response = await apiCallWithFiles(API_CONFIG.ENDPOINTS.SUBMIT_APPLICATION, formData);

        if (response.success) {
            alert('Draft saved successfully!');
        }

        saveBtn.textContent = originalText;
        saveBtn.disabled = false;

    } catch (error) {
        alert('Failed to save draft. Please try again.');
        event.target.textContent = 'Save Draft';
        event.target.disabled = false;
    }
}