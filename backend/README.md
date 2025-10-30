# PMSSS PHP Backend

PHP + MySQL backend for PMSSS Scholarship Portal

## 🚀 Setup Instructions

### Prerequisites
- XAMPP / WAMP / LAMP (Apache + MySQL + PHP)
- PHP 7.4 or higher
- MySQL 5.7 or higher

### Installation Steps

1. **Install XAMPP** (if not already installed)
   - Download from: https://www.apachefriends.org/
   - Install to default location (C:\xampp)

2. **Move Project to htdocs**
   - Copy entire `Scholarship` folder to `C:\xampp\htdocs\`
   - Final path should be: `C:\xampp\htdocs\Scholarship\`

3. **Start Apache and MySQL**
   - Open XAMPP Control Panel
   - Click "Start" for Apache
   - Click "Start" for MySQL

4. **Create Database**
   - Open browser and go to: http://localhost/phpmyadmin
   - Click "New" to create database
   - Name it: `pmsss_scholarship`
   - Go to "Import" tab
   - Select file: `php-backend/database.sql`
   - Click "Go" to import

5. **Configure Database Connection**
   - Open `php-backend/config/config.php`
   - Update if needed:
     ```php
     define('DB_HOST', 'localhost');
     define('DB_USER', 'root');
     define('DB_PASS', '');  // Your MySQL password
     define('DB_NAME', 'pmsss_scholarship');
     ```

6. **Update Frontend API Configuration**
   - Already done! File: `js/config.js`
   - API URL: `http://localhost/Scholarship/php-backend/api`

7. **Test the Setup**
   - Open browser
   - Go to: `http://localhost/Scholarship/index.html`
   - Try registering a new user
   - Test login and application submission

## 📡 API Endpoints

### Authentication
- **Register:** `POST /api/auth.php?action=register`
- **Login:** `POST /api/auth.php?action=login`
- **Get User:** `GET /api/auth.php?action=me` (requires token)

### Applications
- **Submit:** `POST /api/applications.php`
- **Track:** `POST /api/applications.php?action=track`

## 🗄️ Database Structure

### Tables
1. **users** - User accounts
2. **applications** - Scholarship applications
3. **application_history** - Status change history
4. **notifications** - User notifications

## 📁 File Structure

```
php-backend/
├── api/
│   ├── auth.php           # Authentication endpoints
│   └── applications.php   # Application endpoints
├── config/
│   └── config.php        # Database & configuration
├── uploads/              # Uploaded documents
└── database.sql          # Database schema
```

## 🔐 Security Features

- ✅ Password hashing (bcrypt)
- ✅ JWT authentication
- ✅ SQL injection prevention (PDO prepared statements)
- ✅ File upload validation
- ✅ CORS headers
- ✅ Input validation

## 🧪 Testing

1. **Test Health:**
   - http://localhost/Scholarship/php-backend/api/auth.php

2. **Register User:**
   ```json
   POST /api/auth.php?action=register
   {
     "firstName": "Test",
     "lastName": "User",
     "email": "test@example.com",
     "password": "Test@12345",
     "mobile": "9876543210",
     "aadhar": "123456789012",
     "dateOfBirth": "2000-01-01"
   }
   ```

3. **Login:**
   ```json
   POST /api/auth.php?action=login
   {
     "email": "test@example.com",
     "password": "Test@12345"
   }
   ```

## 📊 View Database

**Option 1: phpMyAdmin**
- Go to: http://localhost/phpmyadmin
- Select database: `pmsss_scholarship`
- Click on tables to view data

**Option 2: SQL Query**
```sql
-- View all users
SELECT * FROM users;

-- View all applications
SELECT * FROM applications;

-- Count applications by status
SELECT status, COUNT(*) as count FROM applications GROUP BY status;
```

## 🔧 Troubleshooting

**Issue: CORS Error**
- Check that CORS headers are in config.php
- Make sure Apache is running

**Issue: Database Connection Failed**
- Check MySQL is running in XAMPP
- Verify credentials in config.php

**Issue: File Upload Failed**
- Check `uploads/` folder exists and has write permissions
- Verify file size < 2MB
- Check allowed file types (PDF, JPG, PNG)

**Issue: 404 Not Found**
- Verify project is in `C:\xampp\htdocs\Scholarship\`
- Check Apache is running
- URL should be: `http://localhost/Scholarship/...`

## ✅ Differences from Node.js

| Feature | Node.js | PHP |
|---------|---------|-----|
| Server | Express (port 5000) | Apache (port 80) |
| Database | MongoDB | MySQL |
| Auth | JWT (Node) | JWT (PHP) |
| URL | localhost:5000/api | localhost/Scholarship/php-backend/api |

## 🎉 You're Done!

Your PHP backend is now ready! The Node.js backend is no longer needed. Everything now runs through:
- **Frontend:** http://localhost/Scholarship/
- **Backend:** Apache + PHP + MySQL

Enjoy your new PHP-powered PMSSS portal! 🚀
