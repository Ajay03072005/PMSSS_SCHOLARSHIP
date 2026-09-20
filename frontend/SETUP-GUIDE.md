# 🎓 PMSSS Portal - Complete Setup Guide

## ⚡ Quick Start (5 Minutes)

### 1️⃣ Install XAMPP
- Download: https://www.apachefriends.org/
- Install to: `C:\xampp`

### 2️⃣ Move Project
- Copy `Scholarship` folder to: `C:\xampp\htdocs\`

### 3️⃣ Start Services
- Open **XAMPP Control Panel**
- Start **Apache** ✅
- Start **MySQL** ✅

### 4️⃣ Create Database
1. Go to: http://localhost/phpmyadmin
2. Click **"New"**
3. Database name: `pmsss_scholarship`
4. Click **"Create"**
5. Click **"Import"** tab
6. Choose file: `php-backend/database.sql`
7. Click **"Go"**

### 5️⃣ Open Portal
- Visit: **http://localhost/Scholarship/index.html**
- Done! 🎉

---

## ✅ Test It

1. Click **"Apply Now"**
2. Register with your details
3. Login
4. Fill application form
5. Check data in: http://localhost/phpmyadmin

---

## 📊 View Your Data

**phpMyAdmin:** http://localhost/phpmyadmin
- Select `pmsss_scholarship` database
- Click on tables: users, applications, etc.

---

## ❌ Problems?

### Apache won't start
- **Cause:** Port 80 is used by another program (Skype, IIS, etc.)
- **Fix:** Stop other programs or change Apache port in XAMPP

### MySQL won't start
- **Cause:** Port 3306 is in use
- **Fix:** Stop other MySQL services or change port

### Database connection failed
- **Fix:** Check MySQL is running (green in XAMPP)
- Verify database name: `pmsss_scholarship`

### Page not found (404)
- **Fix:** Use `http://localhost/Scholarship/` NOT `file:///`
- Apache must be running

---

## 📝 Default Login

**Admin:**
- Email: admin@pmsss.gov.in
- Password: Admin@123

**Test Student:**
- Email: test@example.com
- Password: Test@123

---

## 🎯 Features Working

✅ User Registration  
✅ User Login  
✅ Application Submission  
✅ Application Tracking  
✅ File Upload  
✅ Status History  
✅ Notifications  
✅ JWT Authentication  
✅ Password Encryption  
✅ Database Storage  

---

## 📁 Important Files

- **Frontend:** All HTML files in root
- **Backend API:** `php-backend/api/`
- **Database:** `php-backend/database.sql`
- **Config:** `php-backend/config/`
- **Uploads:** `php-backend/uploads/`

---

## 🔄 Start Fresh

If you need to reset everything:

1. Open phpMyAdmin
2. Drop `pmsss_scholarship` database
3. Create new database
4. Import `database.sql` again

---

**Need more details?** Check `php-backend/README.md`

**Happy Coding! 🚀**
