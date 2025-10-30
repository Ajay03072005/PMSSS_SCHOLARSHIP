# 🚀 QUICK START GUIDE

## Start the Application (2 Steps)

### Step 1: Start Backend Server
```bash
cd backend
START-SERVER.bat
```
**Keep this window open!** Server runs on: `http://localhost:8000`

### Step 2: Open Frontend
- **Option A (Recommended):** Open `frontend/index.html` with Live Server in VS Code
- **Option B:** Double-click `frontend/index.html` to open in browser
- **Option C:** Run a local server:
  ```bash
  cd frontend
  python -m http.server 3000
  ```

## Test the Setup

### 1. Test Backend
Open in browser: `http://localhost:8000/index.php`

You should see:
```json
{
  "status": "success",
  "message": "PMSSS Scholarship API Server",
  ...
}
```

### 2. Test Frontend
Open: `frontend/index.html` in your browser

You should see the PMSSS homepage with:
- Navigation menu
- Hero section
- Features
- Login/Register buttons

## Common Issues

### ❌ "Not Found" Error
**Problem:** Server isn't running or running from wrong directory

**Fix:**
```bash
# Stop any running PHP servers
taskkill /F /IM php.exe

# Start from backend folder
cd backend
START-SERVER.bat
```

### ❌ API Connection Failed
**Problem:** Frontend can't connect to backend

**Check:**
1. Backend server is running on port 8000
2. Open `http://localhost:8000/index.php` - should show API info
3. Check browser console (F12) for errors
4. Verify `frontend/js/config.js` has: `BASE_URL: 'http://localhost:8000/api'`

### ❌ Database Connection Error
**Problem:** Can't connect to MySQL

**Fix:**
1. Start XAMPP
2. Start MySQL (Port: 3307)
3. Run: `backend/setup-database.bat`
4. Test: `http://localhost:8000/test-connection.php`

## File Structure

```
PMSSS_SCHOLARSHIP/
├── backend/               ← Start server here
│   ├── START-SERVER.bat  ← Double-click to start
│   ├── api/              ← API endpoints
│   ├── config/           ← Configuration
│   └── uploads/          ← Uploaded files
│
└── frontend/             ← Open in browser
    ├── index.html        ← Homepage
    ├── apply.html        ← Application form
    ├── login.html        ← Login page
    └── js/               ← JavaScript files
```

## URLs Reference

| Service | URL | Purpose |
|---------|-----|---------|
| Backend Root | http://localhost:8000 | API server |
| API Info | http://localhost:8000/index.php | Server status |
| Auth API | http://localhost:8000/api/auth.php | Login/Register |
| Applications API | http://localhost:8000/api/applications.php | Submit/View applications |
| Frontend | frontend/index.html | User interface |

## Next Steps

1. ✅ Start backend server
2. ✅ Open frontend in browser
3. 📝 Register a new account
4. 📄 Submit scholarship application
5. 📊 Track application status
6. 💾 Download application PDF

## Need Help?

- 📖 See `backend/BACKEND-README.md` for API documentation
- 📖 See `frontend/FRONTEND-README.md` for frontend setup
- 📖 See `PROJECT-README.md` for complete overview

---

**Quick Test:**
1. Open: `http://localhost:8000/index.php` (should show API info)
2. Open: `frontend/index.html` (should show homepage)
3. Click "Register" and create an account
4. Fill out application form
5. View and download your application

**Everything working?** You're ready to go! 🎉
