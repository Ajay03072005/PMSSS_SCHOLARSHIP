# PMSSS Scholarship - Backend

## Overview
This is the PHP backend API for the PMSSS (Prime Minister's Special Scholarship Scheme) portal.

## Requirements
- PHP 8.2+ (with mysqli extension)
- MySQL 5.7+ or MariaDB 10.3+
- Composer (optional, for future dependencies)

## Structure
```
backend/
├── api/
│   ├── auth.php                 # Authentication endpoints
│   ├── applications.php         # Application management
│   └── applications-debug.php   # Debug version with enhanced logging
│
├── config/
│   ├── config.php              # Main configuration
│   ├── database.php            # Database connection
│   ├── cors.php                # CORS headers
│   ├── email.php               # Email configuration
│   ├── jwt.php                 # JWT token handling
│   └── helpers.php             # Helper functions
│
├── models/                     # Database models (future)
├── uploads/                    # Uploaded documents
├── logs/                       # Application logs
│
├── database.sql               # Database schema
├── setup-database.bat         # Database setup script
├── START-SERVER.bat           # Quick start script
├── test-connection.php        # Database connection test
└── test-db-connection.php     # Alternative DB test
```

## Setup Instructions

### 1. Database Setup
Run the database setup script:
```bash
cd backend
setup-database.bat
```

Or manually:
```bash
mysql -u root -p -P 3307 < database.sql
```

### 2. Configuration
Edit `config/config.php` and update:
```php
// Database settings
define('DB_HOST', 'localhost');
define('DB_PORT', '3307');
define('DB_NAME', 'pmsss_scholarship');
define('DB_USER', 'root');
define('DB_PASS', 'your_password');

// JWT Secret (change this!)
define('JWT_SECRET', 'your-secret-key-change-this');
```

### 3. File Permissions
Ensure these directories are writable:
```bash
chmod 755 uploads/
chmod 755 logs/
```

### 4. Start Server
Run the server:
```bash
cd backend
START-SERVER.bat
```

Or manually:
```bash
cd backend
php -S localhost:8000
```

## API Endpoints

### Authentication
- `POST /api/auth.php?action=register` - User registration
- `POST /api/auth.php?action=login` - User login
- `GET /api/auth.php?action=me` - Get current user (requires JWT)

### Applications
- `POST /api/applications.php` - Submit new application (requires JWT + files)
- `GET /api/applications.php?action=my` - Get user's applications (requires JWT)
- `GET /api/applications.php?action=view&id=XXX` - Get single application (requires JWT)
- `GET /api/applications.php?action=track&id=XXX` - Track application status

## Database Schema
Main tables:
- `users` - User accounts
- `applications` - Scholarship applications
- `application_status_history` - Status change tracking

## Security Features
- ✅ JWT Authentication
- ✅ Password hashing (bcrypt)
- ✅ CORS protection
- ✅ SQL injection prevention (prepared statements)
- ✅ File upload validation
- ✅ User ownership verification

## File Upload Rules
- **Photo**: JPG/PNG, max 2MB, 3x4 ratio
- **Aadhar**: PDF/JPG, max 5MB
- **Income Certificate**: PDF, max 5MB
- **Domicile**: PDF, max 5MB
- **Class 10 Marksheet**: PDF, max 5MB
- **Class 12 Marksheet**: PDF, max 5MB
- **JEE Scorecard**: PDF, max 5MB

## Testing

### Test Database Connection
```bash
php test-connection.php
```

### Test API Endpoints
Open `test-backend.html` in browser or use tools like:
- Postman
- Thunder Client (VS Code)
- curl

Example curl command:
```bash
# Register
curl -X POST http://localhost:8000/api/auth.php?action=register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","password":"pass123","phone":"1234567890"}'

# Login
curl -X POST http://localhost:8000/api/auth.php?action=login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"pass123"}'
```

## Troubleshooting

### Database Connection Issues
- Check MySQL is running on port 3307
- Verify credentials in `config/config.php`
- Run `test-connection.php`

### CORS Errors
- Check `config/cors.php` includes your frontend URL
- Ensure proper headers are sent

### File Upload Errors
- Check `uploads/` directory permissions
- Verify PHP upload settings in php.ini:
  ```ini
  upload_max_filesize = 10M
  post_max_size = 10M
  ```

### JWT Issues
- Ensure JWT_SECRET is set in config.php
- Check token is sent in Authorization header
- Verify token hasn't expired (24hr default)

## Logging
Logs are stored in `logs/` directory:
- Application logs
- Error logs
- Debug logs (when debug mode enabled)

## Production Deployment
Before deploying to production:
1. Change JWT_SECRET to a strong random key
2. Set error reporting to production mode
3. Disable debug endpoints
4. Use environment variables for sensitive config
5. Enable HTTPS
6. Set proper file permissions
7. Configure proper backup strategy

## Support
For issues, check:
1. Server logs
2. Browser console (frontend)
3. PHP error logs
4. Database logs
