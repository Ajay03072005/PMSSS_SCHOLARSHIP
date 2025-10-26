// Multi-step form handling
let currentSection = 1;
const totalSections = 5;

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
    
    // Check declaration
    if (!document.getElementById('declaration').checked) {
        alert('Please accept the declaration to proceed.');
        return;
    }
    
    // Show confirmation
    if (confirm('Are you sure you want to submit this application? You cannot edit it after submission.')) {
        // Here you would normally send the data to the server
        alert('Application submitted successfully! Your application ID is: PMSSS' + Math.floor(100000 + Math.random() * 900000) + '\n\nYou will receive a confirmation email shortly.');
        
        // Redirect to status page
        setTimeout(() => {
            window.location.href = 'status.html';
        }, 2000);
    }
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
                if (element && !(data[key] instanceof File)) {
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