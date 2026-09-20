@echo off
title PMSSS 2.0 Spring Boot Server - Port 8000
color 0B
echo.
echo ========================================================
echo   PMSSS 2.0 - AI-Powered Scholarship Management System
echo   Spring Boot 3 Backend Server
echo ========================================================
echo.
echo Server running on: http://localhost:8000
echo Swagger UI:        http://localhost:8000/swagger-ui.html
echo OpenAPI Docs:      http://localhost:8000/v3/api-docs
echo.
echo Features:
echo   - Spring Security with JWT (Student, SAG Officer, Finance, Admin)
echo   - AI Document Intelligence and OCR Text Extraction
echo   - Application Consistency Matching and Duplicate Detection
echo   - AI Eligibility and Student Chatbot Assistant
echo   - Direct Benefit Transfer (DBT) Payment Workflow
echo   - Audit Logging and Safe Natural Language Analytics
echo.
echo Keep this window open while using the portal.
echo Press Ctrl+C to stop the server.
echo ========================================================
echo.

cd /d "%~dp0pmsss-backend"
mvn spring-boot:run

pause
