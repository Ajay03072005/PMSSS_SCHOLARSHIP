# 👨‍💼 Admin Module Documentation

## Overview
Complete admin panel for managing PMSSS scholarship applications with dashboard, statistics, and application management features.

## 🎯 Features

### Dashboard
- 📊 **Real-time Statistics**
  - Total applications
  - Pending reviews
  - Approved applications
  - Rejected applications
  - Monthly trends

### Application Management
- 📄 **View All Applications** - Complete list with filtering
- 🔍 **Advanced Search** - Search by ID, name, email, phone
- 📝 **Status Management** - Update application status
- 🗑️ **Delete Applications** - Remove applications with file cleanup
- 📱 **Responsive Table** - Pagination and sorting

### Security
- 🔐 **JWT Authentication** - Secure token-based auth
- 📝 **Activity Logging** - Track all admin actions
- 🔒 **Login Attempt Tracking** - Monitor failed logins
- ⚡ **Token Verification** - Automatic session validation

## 📁 File Structure

```
frontend/
├── admin-login.html          # Admin login page
├── admin-dashboard.html      # Main dashboard
└── js/
    ├── admin-login.js        # Login handler
    └── admin-dashboard.js    # Dashboard logic

backend/
├── api/admin/
│   ├── auth.php             # Authentication API
│   ├── applications.php     # Application management
│   └── statistics.php       # Statistics API
├── admin-tables.sql         # Database schema
└── setup-admin.bat          # Setup script
```

## 🚀 Setup Instructions

### Step 1: Run Database Setup
```bash
cd backend
setup-admin.bat
```

This will:
- Create admin tables
- Create default admin user
- Setup activity logging

### Step 2: Access Admin Panel
- **URL:** `http://localhost:3000/admin-login.html`
- **Default Username:** `admin`
- **Default Password:** `admin123`

### Step 3: Security
⚠️ **IMPORTANT:** Change the default password immediately!

## 📊 Database Tables

### `admins`
Stores admin user accounts
```sql
- id (Primary Key)
- username (Unique)
- password_hash
- name
- email
- role (super_admin, admin, moderator)
- is_active
- last_login
- created_at, updated_at
```

### `admin_login_logs`
Tracks all login attempts
```sql
- id
- username
- ip_address
- user_agent
- success (boolean)
- created_at
```

### `admin_activity_logs`
Logs all admin actions
```sql
- id
- admin_id
- action
- details (JSON)
- created_at
```

### `application_status_history`
Tracks status changes
```sql
- id
- application_id
- old_status
- new_status
- changed_by (admin_id)
- remarks
- created_at
```

## 🔐 API Endpoints

### Authentication
```
POST /api/admin/auth.php?action=login
  Body: { username, password }
  Returns: { token, admin }

GET /api/admin/auth.php?action=verify
  Headers: Authorization: Bearer {token}
  Returns: { valid, admin }
```

### Applications
```
GET /api/admin/applications.php
  Headers: Authorization: Bearer {token}
  Query: page, limit, status, search
  Returns: { applications[], total, totalPages }

PUT /api/admin/applications.php
  Headers: Authorization: Bearer {token}
  Body: { application_id, status, remarks }
  Returns: { success }

DELETE /api/admin/applications.php
  Headers: Authorization: Bearer {token}
  Body: { application_id }
  Returns: { success }
```

### Statistics
```
GET /api/admin/statistics.php
  Headers: Authorization: Bearer {token}
  Returns: { stats }
```

## 🎨 Dashboard Features

### Stats Cards
- **Total Applications** - All submitted applications
- **Pending Review** - Awaiting review
- **Approved** - Approved applications
- **Rejected** - Rejected applications

### Application Table
- **Columns:** ID, Name, Email, Phone, Status, Date, Actions
- **Actions:** View, Update Status, Delete
- **Filters:** All, Pending, Under Review, Approved, Rejected
- **Search:** Real-time search across all fields
- **Pagination:** 10 items per page

### Sidebar Menu
- 📊 Dashboard
- 📄 Applications
- 👥 Users (Coming soon)
- 📈 Reports (Coming soon)
- ⚙️ Settings (Coming soon)

## 💡 Usage Guide

### Login
1. Go to `admin-login.html`
2. Enter username and password
3. Click "Login to Admin Panel"
4. Redirected to dashboard

### View Applications
1. Click "Applications" in sidebar
2. Use filters to narrow down
3. Search by ID, name, or email
4. Navigate using pagination

### Update Status
1. Find application in table
2. Click "Update" button
3. Enter new status: `pending`, `under_review`, `approved`, or `rejected`
4. Status updated with history log

### Delete Application
1. Find application in table
2. Click "Delete" button
3. Confirm deletion
4. Application and files removed

## 🔒 Security Best Practices

1. **Change Default Password**
   ```sql
   UPDATE admins 
   SET password_hash = '$2y$10$your_new_hash' 
   WHERE username = 'admin';
   ```

2. **Create New Admin Users**
   ```sql
   INSERT INTO admins (username, password_hash, name, email, role)
   VALUES ('newadmin', '$2y$10$hash', 'Name', 'email@domain.com', 'admin');
   ```

3. **Review Login Logs**
   ```sql
   SELECT * FROM admin_login_logs 
   WHERE success = 0 
   ORDER BY created_at DESC;
   ```

4. **Monitor Activity**
   ```sql
   SELECT * FROM admin_activity_logs 
   ORDER BY created_at DESC 
   LIMIT 50;
   ```

## 🎯 Status Flow

```
pending → under_review → approved/rejected
```

### Status Types
- `pending` - New application, not reviewed
- `under_review` - Being processed
- `approved` - Application approved
- `rejected` - Application rejected

## 📱 Responsive Design

- **Desktop:** Full sidebar and table layout
- **Tablet:** Collapsible sidebar
- **Mobile:** Hamburger menu (to be implemented)

## 🔧 Configuration

### Change Port
Edit `frontend/js/config.js`:
```javascript
BASE_URL: 'http://localhost:8000/api'
```

### Adjust Pagination
Edit `frontend/js/admin-dashboard.js`:
```javascript
const itemsPerPage = 10; // Change to desired number
```

## 🐛 Troubleshooting

### Cannot Login
- Check database connection
- Verify admin user exists
- Check password hash
- Review `admin_login_logs`

### 404 Error
- Ensure backend server is running
- Check API URLs in config
- Verify admin API files exist

### Unauthorized Error
- Token expired (login again)
- Invalid token
- User not an admin

## 📈 Future Enhancements

- [ ] Bulk status updates
- [ ] Export applications (CSV, PDF)
- [ ] Advanced filtering
- [ ] User management
- [ ] Detailed reports
- [ ] Email notifications
- [ ] Role-based permissions
- [ ] Two-factor authentication

## 🤝 Support

For issues or questions:
1. Check troubleshooting guide
2. Review API logs
3. Check browser console
4. Contact system administrator

---

**Admin Module v1.0** - Built for PMSSS Scholarship Portal
