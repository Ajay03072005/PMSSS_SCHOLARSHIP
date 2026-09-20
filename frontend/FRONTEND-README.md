# PMSSS Scholarship - Frontend

## Overview
This is the frontend application for the PMSSS (Prime Minister's Special Scholarship Scheme) portal.

## Structure
```
frontend/
├── index.html              # Home page
├── about.html             # About PMSSS
├── apply.html             # Scholarship application form
├── login.html             # User login
├── register.html          # User registration
├── status.html            # Application status tracking
├── view-application.html  # View & download submitted application
│
├── css/
│   └── styles.css         # Global styles
│
├── js/
│   ├── config.js          # API configuration
│   ├── application.js     # Application form logic
│   ├── login.js           # Login logic
│   ├── register.js        # Registration logic
│   ├── status.js          # Status tracking logic
│   ├── view-application.js # View application logic
│   ├── chatbot.js         # Chatbot functionality
│   └── emailService.js    # Email service integration
│
├── assets/                # Static assets
└── images/                # Images
```

## How to Run

### Option 1: Using Live Server (Recommended)
1. Install [Live Server](https://marketplace.visualstudio.com/items?itemName=ritwickdey.LiveServer) extension in VS Code
2. Right-click on `index.html`
3. Select "Open with Live Server"

### Option 2: Using Python
```bash
cd frontend
python -m http.server 3000
```
Then open: http://localhost:3000

### Option 3: Using Node.js
```bash
cd frontend
npx http-server -p 3000
```
Then open: http://localhost:3000

## Backend Connection
The frontend connects to the backend API at `http://localhost:8000/api`

Make sure the backend server is running before using the application.

## Features
- ✅ User Registration & Login (JWT Authentication)
- ✅ Application Form with File Uploads
- ✅ Application Status Tracking
- ✅ View & Download Application as PDF
- ✅ Professional Government Form Layout
- ✅ Responsive Design
- ✅ EmailJS Integration for Notifications
- ✅ Chatbot Assistance

## API Configuration
Edit `js/config.js` to change the backend URL if needed:
```javascript
const API_CONFIG = {
    BASE_URL: 'http://localhost:8000/api',
    ...
};
```

## Browser Support
- Chrome (Recommended)
- Firefox
- Edge
- Safari

## Notes
- Make sure backend is running on port 8000
- Use hard refresh (Ctrl+Shift+R) after updates
- Check browser console for any errors
