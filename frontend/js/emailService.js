
const EMAILJS_CONFIG = {
    PUBLIC_KEY: 'NbZFpYNdF9Re8w0IG',     
    SERVICE_ID: 'service_7qu3rhd',       
    TEMPLATE_ID_REGISTRATION: 'template_b0gsudp',
    TEMPLATE_ID_CONFIRMATION: 'template_sxp0z93',
    TEMPLATE_ID_STATUS_UPDATE: 'YOUR_STATUS_TEMPLATE_ID' // Optional
};


function initEmailJS() {
    if (typeof emailjs !== 'undefined') {
        emailjs.init(EMAILJS_CONFIG.PUBLIC_KEY);
        console.log('EmailJS initialized');
    } else {
        console.error('EmailJS library not loaded');
    }
}

/**
 * Send registration welcome email
 * @param {Object} userData - User registration details
 * @returns {Promise}
 */
async function sendRegistrationEmail(userData) {
    const templateParams = {
        to_email: userData.email,
        to_name: userData.fullName,
        user_email: userData.email,
        registration_date: new Date().toLocaleDateString('en-IN', {
            day: '2-digit',
            month: 'short',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        }),
        login_url: `${window.location.origin}/login.html`,
        apply_url: `${window.location.origin}/apply.html`,
        portal_url: window.location.origin
    };

    try {
        const response = await emailjs.send(
            EMAILJS_CONFIG.SERVICE_ID,
            EMAILJS_CONFIG.TEMPLATE_ID_REGISTRATION,
            templateParams
        );
        
        console.log('Registration email sent successfully!', response);
        return { success: true, response };
    } catch (error) {
        console.error('Failed to send registration email:', error);
        return { success: false, error };
    }
}

/**
 * Send application confirmation email
 * @param {Object} applicationData - Application details
 * @returns {Promise}
 */
async function sendApplicationConfirmationEmail(applicationData) {
    const templateParams = {
        to_email: applicationData.email,
        to_name: `${applicationData.firstName} ${applicationData.lastName}`,
        application_id: applicationData.applicationId,
        submission_date: new Date().toLocaleDateString('en-IN', {
            day: '2-digit',
            month: 'short',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        }),
        status: 'Submitted',
        track_url: `${window.location.origin}/status.html`
    };

    try {
        const response = await emailjs.send(
            EMAILJS_CONFIG.SERVICE_ID,
            EMAILJS_CONFIG.TEMPLATE_ID_CONFIRMATION,
            templateParams
        );
        
        console.log('Confirmation email sent successfully!', response);
        return { success: true, response };
    } catch (error) {
        console.error('Failed to send confirmation email:', error);
        return { success: false, error };
    }
}

/**
 * Send status update email
 * @param {Object} statusData - Status update details
 * @returns {Promise}
 */
async function sendStatusUpdateEmail(statusData) {
    const templateParams = {
        to_email: statusData.email,
        to_name: statusData.name,
        application_id: statusData.applicationId,
        status: statusData.status,
        remarks: statusData.remarks || 'Please check the portal for more details',
        update_date: new Date().toLocaleDateString('en-IN', {
            day: '2-digit',
            month: 'short',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        }),
        portal_url: `${window.location.origin}/status.html`
    };

    try {
        const response = await emailjs.send(
            EMAILJS_CONFIG.SERVICE_ID,
            EMAILJS_CONFIG.TEMPLATE_ID_STATUS_UPDATE,
            templateParams
        );
        
        console.log('Status update email sent successfully!', response);
        return { success: true, response };
    } catch (error) {
        console.error('Failed to send status update email:', error);
        return { success: false, error };
    }
}

// Initialize EmailJS when the script loads
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initEmailJS);
} else {
    initEmailJS();
}
