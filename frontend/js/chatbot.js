// Chatbot functionality
const chatbotToggle = document.getElementById('chatbotToggle');
const chatbot = document.getElementById('chatbot');
const chatbotClose = document.getElementById('chatbotClose');
const chatbotMessages = document.getElementById('chatbotMessages');
const chatbotInput = document.getElementById('chatbotInput');
const chatbotSend = document.getElementById('chatbotSend');
const notificationBadge = document.querySelector('.notification-badge');

// Knowledge base for chatbot responses
const responses = {
    'documents': {
        keywords: ['document', 'documents', 'required', 'upload', 'need', 'file'],
        answer: `📄 <strong>Required Documents:</strong><br><br>
        1. Passport size photograph (Max 500KB)<br>
        2. Aadhar Card (PDF/JPG, Max 2MB)<br>
        3. Domicile Certificate of J&K/Ladakh<br>
        4. Income Certificate (Family income < ₹8 Lakhs)<br>
        5. 10th Class Marksheet<br>
        6. 12th Class Marksheet<br>
        7. College Admission Letter<br>
        8. Bank Account Passbook/Statement<br><br>
        All documents should be clear and readable!`
    },
    'eligibility': {
        keywords: ['eligibility', 'eligible', 'qualify', 'criteria', 'requirements'],
        answer: `✅ <strong>Eligibility Criteria:</strong><br><br>
        ✓ Must be a domicile of J&K or Ladakh<br>
        ✓ Passed 12th with minimum 60% marks<br>
        ✓ Family income less than ₹8 Lakhs per annum<br>
        ✓ Admission in AICTE/UGC approved institutions<br>
        ✓ General Degree, Engineering, Medical, or Professional courses<br>
        ✓ Age limit: Generally up to 25 years<br><br>
        You must meet ALL criteria to apply!`
    },
    'bank': {
        keywords: ['bank', 'account', 'ifsc', 'passbook', 'branch'],
        answer: `🏦 <strong>Bank Details Help:</strong><br><br>
        You need to provide:<br>
        • Account Holder Name (preferably student's name)<br>
        • Bank Account Number<br>
        • IFSC Code (11 characters, e.g., SBIN0001234)<br>
        • Bank Name<br>
        • Branch Name<br><br>
        💡 Tip: The scholarship will be directly transferred to this account. Ensure all details are correct!`
    },
    'amount': {
        keywords: ['amount', 'scholarship', 'money', 'financial', 'funding', 'how much'],
        answer: `💰 <strong>Scholarship Amount:</strong><br><br>
        • General Degree: ₹30,000 per annum<br>
        • Engineering: ₹1,25,000 per annum<br>
        • Medical: ₹3,00,000 per annum<br>
        • Professional Courses: Varies<br><br>
        Plus additional maintenance allowance for hostel, books, and other academic expenses!`
    },
    'filling': {
        keywords: ['fill', 'filling', 'how to', 'complete', 'form'],
        answer: `📝 <strong>How to Fill the Form:</strong><br><br>
        1. Fill all sections step by step<br>
        2. Fields marked with * are mandatory<br>
        3. The form auto-saves every 30 seconds<br>
        4. You can go back and edit any section<br>
        5. Review all details in the final step<br>
        6. Accept declaration before submitting<br><br>
        Take your time and fill carefully!`
    },
    'percentage': {
        keywords: ['percentage', 'marks', 'cgpa', 'grade', '12th'],
        answer: `📊 <strong>Marks/Percentage Info:</strong><br><br>
        • Minimum 60% in 12th class required<br>
        • If you have CGPA, convert to percentage<br>
        • Enter exact percentage as on marksheet<br>
        • For example: 75.5% or 8.5 CGPA<br><br>
        Both 10th and 12th marks are required!`
    },
    'course': {
        keywords: ['course', 'degree', 'engineering', 'medical', 'btech', 'mbbs'],
        answer: `🎓 <strong>Course Information:</strong><br><br>
        Covered Courses:<br>
        • General Degree (BA, BSc, BCom, etc.)<br>
        • Engineering (BTech, BE)<br>
        • Medical (MBBS, BDS)<br>
        • Professional (Law, Architecture, etc.)<br><br>
        College must be AICTE/UGC/MCI approved and outside J&K/Ladakh!`
    },
    'contact': {
        keywords: ['contact', 'help', 'support', 'call', 'email', 'helpline'],
        answer: `📞 <strong>Contact Support:</strong><br><br>
        Email: support@pmsss.gov.in<br>
        Grievance: grievance@pmsss.gov.in<br>
        Helpline: 1800-XXX-XXXX<br>
        Timing: 9:00 AM - 6:00 PM (Mon-Fri)<br><br>
        For urgent queries, please call the helpline!`
    },
    'status': {
        keywords: ['status', 'track', 'check', 'application status'],
        answer: `🔍 <strong>Check Application Status:</strong><br><br>
        After submission:<br>
        1. Note your Application ID<br>
        2. Visit the Status page<br>
        3. Enter Application ID and Date of Birth<br>
        4. You'll see current status<br><br>
        You'll also get SMS/Email updates at each stage!`
    },
    'renewal': {
        keywords: ['renewal', 'renew', 'continue', 'next year'],
        answer: `🔄 <strong>Scholarship Renewal:</strong><br><br>
        • Renewable annually for course duration<br>
        • Must maintain satisfactory academic performance<br>
        • Submit renewal application before deadline<br>
        • Provide previous year marksheet<br>
        • No need to upload all documents again<br><br>
        Renewal is subject to compliance with guidelines!`
    },
    'greeting': {
        keywords: ['hi', 'hello', 'hey', 'good morning', 'good evening'],
        answer: `👋 Hello! Welcome to PMSSS Application Portal!<br><br>
        I'm here to help you with the scholarship application process. You can ask me about:<br>
        • Eligibility criteria<br>
        • Required documents<br>
        • How to fill the form<br>
        • Scholarship amounts<br>
        • Any other questions!<br><br>
        What would you like to know?`
    }
};

// Toggle chatbot
chatbotToggle.addEventListener('click', () => {
    chatbot.classList.toggle('active');
    chatbotToggle.classList.toggle('active');
    
    if (chatbot.classList.contains('active')) {
        notificationBadge.style.display = 'none';
        chatbotInput.focus();
        scrollToBottom();
    }
});

// Close chatbot
chatbotClose.addEventListener('click', () => {
    chatbot.classList.remove('active');
    chatbotToggle.classList.remove('active');
});

// Send message on button click
chatbotSend.addEventListener('click', sendMessage);

// Send message on Enter key
chatbotInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        sendMessage();
    }
});

// Send message function
function sendMessage() {
    const message = chatbotInput.value.trim();
    
    if (message === '') return;
    
    // Add user message
    addMessage(message, 'user');
    chatbotInput.value = '';
    
    // Show typing indicator
    showTypingIndicator();
    
    // Get bot response after a delay
    setTimeout(() => {
        hideTypingIndicator();
        const response = getBotResponse(message);
        addMessage(response, 'bot');
        scrollToBottom();
    }, 1000);
}

// Quick question function
function askQuestion(question) {
    addMessage(question, 'user');
    showTypingIndicator();
    
    setTimeout(() => {
        hideTypingIndicator();
        const response = getBotResponse(question);
        addMessage(response, 'bot');
        scrollToBottom();
    }, 800);
}

// Add message to chat
function addMessage(text, sender) {
    const messageDiv = document.createElement('div');
    messageDiv.className = sender === 'user' ? 'user-message' : 'bot-message';
    
    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';
    contentDiv.innerHTML = `<p>${text}</p>`;
    
    messageDiv.appendChild(contentDiv);
    chatbotMessages.appendChild(messageDiv);
    
    scrollToBottom();
}

// Get bot response based on keywords
function getBotResponse(userMessage) {
    const lowerMessage = userMessage.toLowerCase();
    
    // Check each response category
    for (const [key, data] of Object.entries(responses)) {
        if (data.keywords.some(keyword => lowerMessage.includes(keyword))) {
            return data.answer;
        }
    }
    
    // Default response if no match found
    return `I'm not sure about that specific question. Here are some topics I can help with:<br><br>
    • Eligibility criteria<br>
    • Required documents<br>
    • How to fill the form<br>
    • Bank details<br>
    • Scholarship amounts<br>
    • Application status<br><br>
    Or you can contact our support team:<br>
    📧 support@pmsss.gov.in<br>
    📞 1800-XXX-XXXX`;
}

// Show typing indicator
function showTypingIndicator() {
    const typingDiv = document.createElement('div');
    typingDiv.className = 'bot-message typing-indicator';
    typingDiv.id = 'typingIndicator';
    typingDiv.innerHTML = `
        <div class="message-content">
            <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
            </div>
        </div>
    `;
    chatbotMessages.appendChild(typingDiv);
    scrollToBottom();
}

// Hide typing indicator
function hideTypingIndicator() {
    const typingIndicator = document.getElementById('typingIndicator');
    if (typingIndicator) {
        typingIndicator.remove();
    }
}

// Scroll to bottom of messages
function scrollToBottom() {
    chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
}

// Auto-show chatbot on page load (optional)
setTimeout(() => {
    if (!chatbot.classList.contains('active')) {
        notificationBadge.style.display = 'flex';
    }
}, 3000);

// Contextual help based on current form section
function provideContextualHelp() {
    const activeSection = document.querySelector('.form-section.active');
    if (!activeSection) return;
    
    const sectionId = activeSection.id;
    let helpMessage = '';
    
    switch(sectionId) {
        case 'section1':
            helpMessage = '💡 Filling Personal Information? Make sure your name matches your Aadhar card exactly!';
            break;
        case 'section2':
            helpMessage = '💡 Need help with academic details? Enter marks exactly as shown on your marksheet.';
            break;
        case 'section3':
            helpMessage = '💡 For bank details, ensure the account is active and preferably in your name.';
            break;
        case 'section4':
            helpMessage = '💡 Upload clear, readable scanned copies. Keep file sizes under the specified limits.';
            break;
        case 'section5':
            helpMessage = '💡 Review all details carefully before submitting. You cannot edit after submission!';
            break;
    }
    
    if (helpMessage && !chatbot.classList.contains('active')) {
        // Show notification
        notificationBadge.textContent = '!';
        notificationBadge.style.display = 'flex';
    }
}