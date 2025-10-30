# Quick Setup - You Have MySQL Already! 🎉

Since you have MySQL installed, you just need **Apache + PHP**. Here's the easiest way:

---

## 🚀 Recommended: Install XAMPP

### Why XAMPP?
- ✅ Includes Apache + PHP (you won't use its MySQL)
- ✅ Easy to install and configure
- ✅ Works perfectly with your existing MySQL
- ✅ No conflicts!

### Steps:

1. **Download XAMPP**
   - Go to: https://www.apachefriends.org/
   - Download for Windows
   - Install to default location: `C:\xampp`

2. **Move Your Project**
   ```
   Copy: C:\Users\ajaya\Documents\Scholarship
   To: C:\xampp\htdocs\Scholarship
   ```

3. **Start Only Apache**
   - Open XAMPP Control Panel
   - Click **Start** next to **Apache** ONLY
   - ❌ Do NOT start MySQL in XAMPP (use your system MySQL)

4. **Create Database in Your MySQL**
   
   Open PowerShell or Command Prompt:
   ```bash
   mysql -u root -pAjay
   ```
   
   Then run:
   ```sql
   CREATE DATABASE pmsss_scholarship CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE pmsss_scholarship;
   SOURCE C:/Users/ajaya/Documents/Scholarship/php-backend/database.sql;
   SHOW TABLES;
   EXIT;
   ```

5. **Test Your Portal**
   - Open browser
   - Go to: http://localhost/Scholarship/test-backend.html
   - Click "Test API Connection"
   - All should be green ✅

---

## 📋 Quick Checklist

- [ ] Download & Install XAMPP
- [ ] Copy project to `C:\xampp\htdocs\Scholarship`
- [ ] Start Apache in XAMPP (leave MySQL off)
- [ ] Your system MySQL is already running ✅
- [ ] Run SQL commands to create database
- [ ] Test: http://localhost/Scholarship/test-backend.html

---

## 🔧 Create Database - Simple Commands

**Option 1: Command Line (Fastest)**
```bash
# Login to MySQL
mysql -u root -pAjay

# Copy and paste these commands:
CREATE DATABASE pmsss_scholarship CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pmsss_scholarship;
SOURCE C:/xampp/htdocs/Scholarship/php-backend/database.sql;
SHOW TABLES;
EXIT;
```

**Option 2: If you have MySQL Workbench**
1. Open MySQL Workbench
2. Connect to localhost
3. Create new schema: `pmsss_scholarship`
4. Right-click → Run SQL Script
5. Select: `database.sql`
6. Execute

**Option 3: If you have phpMyAdmin**
1. Go to: http://localhost/phpmyadmin
2. New → Database name: `pmsss_scholarship`
3. Import → Choose `database.sql`
4. Go

---

## ✅ After Setup

Your stack will be:
- **MySQL**: Your existing installation (port 3306) ✅
- **Apache**: XAMPP (port 80)
- **PHP**: XAMPP
- **Project**: `C:\xampp\htdocs\Scholarship`

Access your portal at: **http://localhost/Scholarship/index.html**

---

## 🎯 Why This Works Perfectly

Your `database.php` is already configured:
```php
Host: localhost        ← Your MySQL server
User: root            ← Your MySQL user  
Password: Ajay        ← Your MySQL password ✅
Database: pmsss_scholarship
```

XAMPP's Apache will use PHP to connect to YOUR MySQL (not XAMPP's MySQL). Perfect! 🎉

---

## 💡 What If I Already Have Apache?

If you have Apache installed separately:
1. Make sure PHP is installed and configured
2. Move project to your Apache webroot
3. Update Apache to recognize `.php` files
4. Create the database using commands above

---

**Next Step:** Install XAMPP and create the database! 🚀
