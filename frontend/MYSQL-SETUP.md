# MySQL Setup Guide for PMSSS Portal

You have MySQL installed on your system. Here's how to set it up:

---

## ✅ Current Configuration

Your database settings (in `php-backend/config/database.php`):
- **Host:** localhost
- **User:** root
- **Password:** Ajay
- **Database:** pmsss_scholarship

---

## 🗄️ Create Database (Choose One Method)

### Method 1: Using MySQL Command Line

1. **Open Command Prompt or PowerShell**

2. **Login to MySQL:**
```bash
mysql -u root -p
```
Enter password: `Ajay`

3. **Create Database:**
```sql
CREATE DATABASE pmsss_scholarship CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. **Use the Database:**
```sql
USE pmsss_scholarship;
```

5. **Import Schema:**
```sql
SOURCE C:/Users/ajaya/Documents/Scholarship/php-backend/database.sql;
```

6. **Verify Tables:**
```sql
SHOW TABLES;
```

7. **Exit:**
```sql
EXIT;
```

---

### Method 2: Using phpMyAdmin (if you have it)

1. Go to: http://localhost/phpmyadmin
2. Click **"New"** on the left sidebar
3. Database name: `pmsss_scholarship`
4. Collation: `utf8mb4_unicode_ci`
5. Click **"Create"**
6. Click **"Import"** tab
7. Choose file: `php-backend/database.sql`
8. Click **"Go"**

---

### Method 3: Using MySQL Workbench (if installed)

1. Open MySQL Workbench
2. Connect to your local MySQL server
3. Click **"Create New Schema"** icon
4. Name: `pmsss_scholarship`
5. Charset: `utf8mb4`, Collation: `utf8mb4_unicode_ci`
6. Apply
7. File → Run SQL Script
8. Select: `php-backend/database.sql`
9. Run

---

## 🚀 Setup Apache/PHP

Since you have MySQL separately, you still need Apache + PHP. Choose one:

### Option 1: Install XAMPP (Easiest - Recommended)
- Download: https://www.apachefriends.org/
- Install to: `C:\xampp`
- **Only use Apache from XAMPP** (not MySQL)
- Your system MySQL will work fine!

### Option 2: Install PHP + Apache Separately
- Download PHP: https://windows.php.net/download/
- Download Apache: https://www.apachelounge.com/download/
- Configure manually (advanced)

---

## 🧪 Test Database Connection

Run this command to test your database connection:

```bash
cd C:\Users\ajaya\Documents\Scholarship\php-backend
php test-db-connection.php
```

---

## ✅ After Setup

1. **Start Apache** (from XAMPP Control Panel or your Apache installation)
2. **Your MySQL is already running** (system service)
3. **Test the portal:** http://localhost/Scholarship/test-backend.html
4. **Open portal:** http://localhost/Scholarship/index.html

---

## 🔧 Troubleshooting

### "Can't connect to MySQL server"
- Check MySQL service is running:
```bash
Get-Service MySQL* | Select-Object Name, Status
```
- Start if stopped:
```bash
Start-Service MySQL80  # or your MySQL service name
```

### "Access denied for user 'root'"
- Password might be different
- Update in `php-backend/config/database.php`:
```php
define('DB_PASS', 'your_actual_password');
```

### "Unknown database 'pmsss_scholarship'"
- Database not created yet
- Follow "Create Database" steps above

---

## 📊 View Your Data

### Command Line:
```bash
mysql -u root -p
# Enter password: Ajay
USE pmsss_scholarship;
SHOW TABLES;
SELECT * FROM users;
SELECT * FROM applications;
```

### phpMyAdmin:
http://localhost/phpmyadmin (if installed)

### MySQL Workbench:
Open Workbench → Connect → Browse `pmsss_scholarship`

---

## 🎉 You're All Set!

Your setup:
- ✅ MySQL: Your system installation
- ✅ PHP/Apache: XAMPP (or separate)
- ✅ Database: pmsss_scholarship
- ✅ Backend: PHP files ready

**Next:** Create the database and test!
