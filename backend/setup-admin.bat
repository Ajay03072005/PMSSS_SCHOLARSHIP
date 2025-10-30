@echo off
title PMSSS Admin Module Setup
color 0E
echo.
echo ========================================
echo   PMSSS Admin Module Setup
echo ========================================
echo.
echo This will create admin tables and default admin user
echo.
echo Database Settings:
echo   Host: localhost
echo   Port: 3307
echo   Database: pmsss_scholarship
echo   User: root
echo.
echo Default Admin Credentials:
echo   Username: admin
echo   Password: admin123
echo.
echo ⚠️ IMPORTANT: Change the default password after first login!
echo.
pause

echo.
echo Installing admin tables...
echo.

"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 pmsss_scholarship < admin-tables.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   ✓ ADMIN MODULE SETUP COMPLETE!
    echo ========================================
    echo.
    echo Admin tables created successfully!
    echo.
    echo Default Admin Login:
    echo   URL: http://localhost:3000/admin-login.html
    echo   Username: admin
    echo   Password: admin123
    echo.
    echo ⚠️ SECURITY WARNING:
    echo   Please change the default password immediately!
    echo.
    echo ========================================
) else (
    echo.
    echo ========================================
    echo   ✗ SETUP FAILED
    echo ========================================
    echo.
    echo Please check:
    echo   1. MySQL is running on port 3307
    echo   2. Database 'pmsss_scholarship' exists
    echo   3. MySQL credentials are correct
    echo.
    echo ========================================
)

echo.
pause
