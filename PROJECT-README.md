# PMSSS Scholarship Portal

A complete web-based scholarship management system for Prime Minister's Special Scholarship Scheme (PMSSS).

## 🎯 Project Overview

This is a full-stack scholarship application portal with separate frontend and backend architecture.

```
PMSSS_SCHOLARSHIP/
├── frontend/          # HTML, CSS, JavaScript (Client-side)
├── backend/           # PHP API Server (Server-side)
└── README.md          # This file
```

## ✨ Features

### User Features
- 🔐 **Secure Authentication** - JWT-based login/registration
- 📝 **Application Form** - Comprehensive scholarship application with file uploads
- 📊 **Status Tracking** - Real-time application status updates
- 📄 **View & Download** - Professional PDF download of submitted applications
- 💬 **Chatbot** - AI-powered assistance
- 📧 **Email Notifications** - Automated status updates

### Technical Features
- ✅ RESTful API architecture
- ✅ JWT authentication
- ✅ File upload handling (Photo, Aadhar, Certificates)
- ✅ MySQL database with prepared statements
- ✅ Professional government-style form layout
- ✅ Responsive design
- ✅ CORS protection
- ✅ Input validation & sanitization

## 🚀 Quick Start

### Prerequisites
- PHP 8.2+ (with mysqli extension)
- MySQL 5.7+ or XAMPP
- Modern web browser (Chrome, Firefox, Edge)
- (Optional) VS Code with Live Server extension

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/Ajay03072005/PMSSS_SCHOLARSHIP.git
cd PMSSS_SCHOLARSHIP
```

#### 2. Setup Backend
```bash
cd backend
# Run database setup
setup-database.bat

# Start PHP server
START-SERVER.bat
```
Backend will run on: **http://localhost:8000**

#### 3. Setup Frontend
Open a new terminal:

**Option A: Using Live Server (Recommended)**
1. Open VS Code in `frontend/` folder
2. Right-click `index.html` → "Open with Live Server"

**Option B: Using Python**
```bash
cd frontend
python -m http.server 3000
```

**Option C: Using Node.js**
```bash
cd frontend
npx http-server -p 3000
```

Frontend will run on: **http://localhost:3000** (or Live Server port)

#### 4. Access the Portal
- Open your browser: `http://localhost:3000` (or your Live Server URL)
- Register a new account
- Apply for scholarship
- Track your application status

## 📁 Project Structure

### Frontend (`/frontend`)
```
frontend/
├── index.html              # Landing page
├── apply.html             # Application form
├── login.html             # User login
├── register.html          # User registration
├── status.html            # Status tracking
├── view-application.html  # View/download application
├── css/
│   └── styles.css         # Global styles
├── js/
│   ├── config.js          # API configuration
│   ├── application.js     # Form logic
│   ├── login.js           # Authentication
│   └── view-application.js # PDF generation
└── assets/                # Images, icons
```

### Backend (`/backend`)
```
backend/
├── api/
│   ├── auth.php           # Login/Register endpoints
│   └── applications.php   # Application CRUD
├── config/
│   ├── database.php       # DB connection
│   ├── jwt.php            # JWT handling
│   └── cors.php           # CORS configuration
├── uploads/               # Uploaded documents
├── logs/                  # Server logs
└── database.sql          # Database schema
```

## 🔧 Configuration

### Backend Configuration
Edit `backend/config/config.php`:
```php
// Database
define('DB_HOST', 'localhost');
define('DB_PORT', '3307');
define('DB_NAME', 'pmsss_scholarship');
define('DB_USER', 'root');
define('DB_PASS', 'your_password');

// JWT Secret (CHANGE THIS!)
define('JWT_SECRET', 'your-secret-key');
```

### Frontend Configuration
Edit `frontend/js/config.js`:
```javascript
const API_CONFIG = {
    BASE_URL: 'http://localhost:8000/api',
    ...
};
```

## 📊 Database Schema

### Main Tables
- `users` - User accounts (id, name, email, password_hash, phone, created_at)
- `applications` - Scholarship applications (id, user_id, application_id, personal details, academic info, documents, status)
- `application_status_history` - Status tracking log

### Default Database Settings
- **Port**: 3307
- **Database Name**: pmsss_scholarship
- **Default User**: root

## 🔐 API Endpoints

### Authentication
```
POST /api/auth.php?action=register
POST /api/auth.php?action=login
GET  /api/auth.php?action=me
```

### Applications
```
POST /api/applications.php              (Submit application)
GET  /api/applications.php?action=my    (Get user's applications)
GET  /api/applications.php?action=view&id=XXX
GET  /api/applications.php?action=track&id=XXX
```

## 🎨 Design Features

- **Professional Government Form** - Red & black color scheme
- **Photo Layout** - Photo positioned on right side (3x2 ratio)
- **PDF Generation** - High-quality PDF download with proper alignment
- **Responsive Design** - Works on desktop, tablet, and mobile
- **Dark Theme** - Modern dark gradient background matching official design

## 🧪 Testing

### Test Backend Connection
```bash
cd backend
php test-connection.php
```

### Test API
- Use `test-backend.html` in frontend
- Or use Postman/Thunder Client

## 📱 Browser Support

| Browser | Supported |
|---------|-----------|
| Chrome  | ✅ Yes     |
| Firefox | ✅ Yes     |
| Edge    | ✅ Yes     |
| Safari  | ✅ Yes     |

## 🛠️ Troubleshooting

### Backend Issues
- **Database connection failed**: Check MySQL is running on port 3307
- **CORS errors**: Verify CORS settings in `config/cors.php`
- **Upload fails**: Check `uploads/` folder permissions

### Frontend Issues
- **API errors**: Ensure backend server is running on port 8000
- **PDF not generating**: Hard refresh browser (Ctrl+Shift+R)
- **Images not loading**: Check file paths in HTML

## 📄 Documentation

Detailed documentation available:
- [`frontend/FRONTEND-README.md`](frontend/FRONTEND-README.md) - Frontend setup & usage
- [`backend/BACKEND-README.md`](backend/BACKEND-README.md) - Backend API documentation
- [`frontend/SETUP-GUIDE.md`](frontend/SETUP-GUIDE.md) - Complete setup guide
- [`frontend/INTEGRATION_GUIDE.md`](frontend/INTEGRATION_GUIDE.md) - Integration details

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Developer

**Ajay**
- GitHub: [@Ajay03072005](https://github.com/Ajay03072005)
- Repository: [PMSSS_SCHOLARSHIP](https://github.com/Ajay03072005/PMSSS_SCHOLARSHIP)

## 🙏 Acknowledgments

- AICTE - Prime Minister's Special Scholarship Scheme
- Government of India
- All contributors and testers

## 📧 Support

For issues and queries:
1. Check the documentation in respective README files
2. Create an issue on GitHub
3. Review troubleshooting guide above

---

**Built with ❤️ for PMSSS Students**
