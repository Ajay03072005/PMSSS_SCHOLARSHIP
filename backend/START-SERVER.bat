@echo off
title PMSSS PHP Server - Port 8000
color 0A
echo.
echo ========================================
echo   PMSSS Scholarship Portal
echo   PHP Development Server
echo ========================================
echo.
echo Starting PHP server on http://localhost:8000
echo.
echo Database: MySQL (localhost:3307)
echo Database Name: pmsss_scholarship
echo.
echo Server root: backend/
echo API endpoints: http://localhost:8000/api/
echo.
echo Keep this window open while using the portal.
echo Press Ctrl+C to stop the server.
echo.
echo ========================================
echo.

cd /d "%~dp0"
"C:\xampp\php\php.exe" -S localhost:8000 -t .

pause
