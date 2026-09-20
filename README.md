# PMSSS 2.0 – Smart Scholarship Portal

An AI-powered, full-stack scholarship management system for Prime Minister's Special Scholarship Scheme (PMSSS).

## 🎯 Project Overview

This system features a decoupled, enterprise-grade architecture:

```
PMSSS_SCHOLARSHIP/
├── pmsss-frontend/     # Vite + React 18 Single Page Application
├── pmsss-backend/      # Spring Boot 3 Java REST API Microservice
└── README.md           # Documentation
```

---

## ✨ System Architecture & Tech Stack

### Frontend (`pmsss-frontend`)
- **Framework**: React 18 with Vite
- **Routing**: React Router 6 with Role-Based Guard Protection (`RoleBasedRoute.jsx`)
- **State & UI**: Vanilla CSS & Tailwind Utility Classes
- **Port**: `5173`

### Backend (`pmsss-backend`)
- **Framework**: Spring Boot 3 (Java 17) & Hibernate JPA
- **Security**: Spring Security with JWT Bearer Authentication
- **Database**: MySQL (`pmsss_db` on port 3306)
- **Document Intelligence**: OCR Text Extraction & Classification Engine
- **Cloudinary Integration**: Cloud Asset Management with local storage fallback
- **Port**: `8081` (`/api/v1`)

---

## 🚀 Quick Start

### Prerequisites
- JDK 17+
- Maven 3.8+
- Node.js v18+
- MySQL 8.0+

### 1. Start Backend Server
```bash
cd pmsss-backend
mvn spring-boot:run
```
Backend will start on: **http://localhost:8081**  
Swagger OpenAPI Docs: **http://localhost:8081/swagger-ui.html**

### 2. Start Frontend App
```bash
cd pmsss-frontend
npm install
npm run dev
```
Frontend will run on: **http://localhost:5173**

---

## 🔐 API Architecture (`/api/v1`)

- `POST /api/v1/auth/register` - Register user account
- `POST /api/v1/auth/login` - Authenticate & obtain JWT
- `POST /api/v1/student/profile` - Manage student profile
- `POST /api/v1/applications` - Create/update application draft
- `POST /api/v1/documents/upload` - Secure document upload with Cloudinary tracking
- `POST /api/v1/sag/applications/{id}/verify` - SAG Officer verification
- `POST /api/v1/payment/process` - Direct Benefit Transfer (DBT) payment processing

---

## 🧪 Testing & Validation

Run full automated E2E regression suite:
```bash
node scratch/run_full_regression_testing.js
```
Purge synthetic test data:
```sql
SOURCE scratch/cleanup_test_data.sql;
```

---

## 👨‍💻 Developer
**Ajay**
- Repository: [PMSSS_SCHOLARSHIP](https://github.com/Ajay03072005/PMSSS_SCHOLARSHIP)
