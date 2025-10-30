@echo off
echo ====================================
echo  PMSSS Database Setup
echo ====================================
echo.
echo This will create the database for your PMSSS Portal
echo.
echo Your MySQL service is: MySQL80 (Running)
echo Database will be created: pmsss_scholarship
echo.
pause

echo.
echo Attempting to connect to MySQL...
echo.

REM Try common MySQL installation paths
set MYSQL_PATH=""

if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
    echo Found MySQL at: C:\Program Files\MySQL\MySQL Server 8.0\bin\
)

if exist "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set MYSQL_PATH="C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe"
    echo Found MySQL at: C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\
)

if exist "C:\MySQL\bin\mysql.exe" (
    set MYSQL_PATH="C:\MySQL\bin\mysql.exe"
    echo Found MySQL at: C:\MySQL\bin\
)

if %MYSQL_PATH%=="" (
    echo.
    echo ERROR: Could not find MySQL installation!
    echo.
    echo Please run this command manually:
    echo.
    echo mysql -u root -pAjay -e "CREATE DATABASE IF NOT EXISTS pmsss_scholarship CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    echo mysql -u root -pAjay pmsss_scholarship ^< "%~dp0database.sql"
    echo.
    pause
    exit /b 1
)

echo.
echo Creating database...
%MYSQL_PATH% -u root -pAjay -e "CREATE DATABASE IF NOT EXISTS pmsss_scholarship CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Could not create database!
    echo Please check your MySQL password is: Ajay
    echo.
    pause
    exit /b 1
)

echo Database created successfully!
echo.
echo Importing schema and data...
%MYSQL_PATH% -u root -pAjay pmsss_scholarship < "%~dp0database.sql"

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Could not import database schema!
    echo.
    pause
    exit /b 1
)

echo.
echo ====================================
echo  SUCCESS! Database setup complete!
echo ====================================
echo.
echo Database: pmsss_scholarship
echo Tables created: users, applications, application_history, notifications
echo.
echo Next steps:
echo 1. Install XAMPP (for Apache + PHP)
echo 2. Move project to C:\xampp\htdocs\Scholarship
echo 3. Start Apache in XAMPP
echo 4. Open: http://localhost/Scholarship/index.html
echo.
echo View your data:
%MYSQL_PATH% -u root -pAjay -e "USE pmsss_scholarship; SHOW TABLES;"
echo.
pause
