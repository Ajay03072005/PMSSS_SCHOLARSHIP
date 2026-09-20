// Admin Dashboard Handler
let currentPage = 1;
let currentStatus = 'all';
let applicationsData = [];
let usersData = [];
let currentUserPage = 1;
const itemsPerPage = 10;

document.addEventListener('DOMContentLoaded', () => {
    // Check if admin is logged in
    const adminToken = localStorage.getItem('adminToken');
    if (!adminToken) {
        window.location.href = 'admin-login.html';
        return;
    }

    // Load admin info
    loadAdminInfo();

    // Load dashboard data
    loadDashboard();

    // Setup event listeners
    setupEventListeners();
});

// Helper function to transform application data from database format
function transformApplicationData(app) {
    console.log('Transforming app:', app);
    
    // Parse academic info if it's a JSON string
    let academicInfo = {};
    if (app.academic_info) {
        try {
            academicInfo = typeof app.academic_info === 'string' 
                ? JSON.parse(app.academic_info) 
                : app.academic_info;
        } catch (e) {
            console.error('Error parsing academic_info:', e);
        }
    }
    
    // Transform to expected format
    const transformed = {
        ...app,
        // Combine name fields (handle empty middle name)
        full_name: [app.first_name, app.middle_name, app.last_name]
            .filter(n => n && n.trim())
            .join(' '),
        // Map column names
        dob: app.date_of_birth,
        phone: app.mobile,
        aadhar_number: app.aadhar,
        aadhar: app.aadhar_doc, // Document path
        income: app.income_cert, // Document path
        // Extract course and institution from academic_info
        course_name: academicInfo.course?.name || 'N/A',
        institution_name: academicInfo.course?.collegeName || 'N/A',
        // Keep academic info for detailed view
        academicInfo: academicInfo
    };
    
    console.log('Transformed result:', transformed);
    return transformed;
}

// Load admin info
function loadAdminInfo() {
    const adminUser = JSON.parse(localStorage.getItem('adminUser') || '{}');
    const adminName = document.getElementById('adminName');
    const adminAvatar = document.getElementById('adminAvatar');

    if (adminUser.name) {
        adminName.textContent = adminUser.name;
        adminAvatar.textContent = adminUser.name.charAt(0).toUpperCase();
    }
}

// Setup event listeners
function setupEventListeners() {
    // Logout button
    document.getElementById('logoutBtn').addEventListener('click', logout);

    // Menu items
    document.querySelectorAll('.menu-item').forEach(item => {
        item.addEventListener('click', (e) => {
            const section = item.getAttribute('data-section');
            
            // Only prevent default for internal navigation (items with data-section)
            // Let external links (like reports.html) work normally
            if (section) {
                e.preventDefault();
                showSection(section);
            }
            // If no data-section, let the browser follow the href normally
        });
    });

    // Filter buttons
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentStatus = btn.getAttribute('data-status');
            currentPage = 1;
            loadApplications();
        });
    });

    // Search input
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(() => {
            currentPage = 1;
            loadApplications();
        }, 500));
    }

    // User search input
    const userSearchInput = document.getElementById('userSearchInput');
    if (userSearchInput) {
        userSearchInput.addEventListener('input', debounce(() => {
            currentUserPage = 1;
            loadUsers();
        }, 500));
    }

    // Pagination
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    
    if (prevBtn) prevBtn.addEventListener('click', () => changePage(-1));
    if (nextBtn) nextBtn.addEventListener('click', () => changePage(1));

    // User pagination
    const userPrevBtn = document.getElementById('userPrevBtn');
    const userNextBtn = document.getElementById('userNextBtn');
    
    if (userPrevBtn) userPrevBtn.addEventListener('click', () => changeUserPage(-1));
    if (userNextBtn) userNextBtn.addEventListener('click', () => changeUserPage(1));
}

// Show section
function showSection(section) {
    // Update menu active state
    document.querySelectorAll('.menu-item').forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('data-section') === section) {
            item.classList.add('active');
        }
    });

    // Hide all sections
    document.querySelectorAll('.section-content').forEach(sec => {
        sec.style.display = 'none';
    });

    // Show selected section
    const sectionMap = {
        'dashboard': 'dashboardSection',
        'applications': 'applicationsSection',
        'users': 'usersSection',
        'reports': 'reportsSection',
        'settings': 'settingsSection'
    };

    const sectionId = sectionMap[section];
    if (sectionId) {
        document.getElementById(sectionId).style.display = 'block';
        
        // Update page title
        const titles = {
            'dashboard': 'Dashboard',
            'applications': 'All Applications',
            'users': 'User Management',
            'reports': 'Reports & Analytics',
            'settings': 'System Settings'
        };
        document.getElementById('pageTitle').textContent = titles[section];

        // Load section data
        if (section === 'applications') {
            loadApplications();
        } else if (section === 'users') {
            loadUsers();
        }
    }
}

// Load dashboard
async function loadDashboard() {
    try {
        const token = localStorage.getItem('adminToken');
        
        if (!token) {
            console.error('No admin token found');
            return;
        }

        console.log('Fetching statistics from:', `${API_CONFIG.BASE_URL}/admin/statistics.php`);
        
        const response = await fetch(`${API_CONFIG.BASE_URL}/admin/statistics.php`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        console.log('Statistics response status:', response.status);
        
        const data = await response.json();
        console.log('Statistics data:', data);

        if (data.success && data.stats) {
            // Update stats
            document.getElementById('totalApps').textContent = data.stats.total || 0;
            document.getElementById('pendingApps').textContent = data.stats.pending || 0;
            document.getElementById('approvedApps').textContent = data.stats.approved || 0;
            document.getElementById('totalUsersCard').textContent = data.stats.total_users || 0;

            // Load recent applications
            loadRecentApplications();
        } else {
            console.error('Failed to load statistics:', data.message);
            // Show error but don't crash
            document.getElementById('totalApps').textContent = '0';
            document.getElementById('pendingApps').textContent = '0';
            document.getElementById('approvedApps').textContent = '0';
            document.getElementById('totalUsersCard').textContent = '0';
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
        // Show error but don't crash
        document.getElementById('totalApps').textContent = '0';
        document.getElementById('pendingApps').textContent = '0';
        document.getElementById('approvedApps').textContent = '0';
        document.getElementById('totalUsersCard').textContent = '0';
    }
}

// Load users
async function loadUsers() {
    try {
        const token = localStorage.getItem('adminToken');
        const searchTerm = document.getElementById('userSearchInput')?.value.trim() || '';
        
        let url = `${API_CONFIG.BASE_URL}/admin/users.php?page=${currentUserPage}&limit=${itemsPerPage}`;
        
        if (searchTerm) {
            url += `&search=${encodeURIComponent(searchTerm)}`;
        }

        console.log('Fetching users from:', url);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        console.log('Users response status:', response.status);
        
        const data = await response.json();
        console.log('Users data:', data);

        if (data.success && data.users) {
            usersData = data.users;
            displayUsers(data.users);
            updateUserPagination(data.total, data.page, data.totalPages);
            
            // Update total users count in section header
            const totalUsersEl = document.getElementById('totalUsers');
            if (totalUsersEl) {
                totalUsersEl.textContent = data.total;
            }
        } else {
            console.error('Failed to load users:', data.message);
            document.getElementById('usersTable').innerHTML = 
                `<p style="text-align: center; color: #f44336; padding: 20px;">Error: ${data.message || 'Failed to load users'}</p>`;
        }
    } catch (error) {
        console.error('Error loading users:', error);
        document.getElementById('usersTable').innerHTML = 
            `<p style="text-align: center; color: #f44336; padding: 20px;">Error loading users: ${error.message}</p>`;
    }
}

// Display users
function displayUsers(users) {
    const container = document.getElementById('usersTable');
    
    if (!users || users.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No users found</p>
            </div>
        `;
        return;
    }

    const html = `
        <table class="applications-table">
            <thead>
                <tr>
                    <th>User ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Applications</th>
                    <th>Last Application</th>
                    <th>Registered</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${users.map(user => `
                    <tr>
                        <td><strong>#${user.id}</strong></td>
                        <td>${user.name || 'N/A'}</td>
                        <td>${user.email || 'N/A'}</td>
                        <td>${user.phone || 'N/A'}</td>
                        <td>
                            <span style="background: #e3f2fd; color: #1976d2; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600;">
                                ${user.application_count} application${user.application_count != 1 ? 's' : ''}
                            </span>
                        </td>
                        <td>${user.last_application_date ? formatDate(user.last_application_date) : 'None'}</td>
                        <td>${formatDate(user.created_at)}</td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-view" onclick="viewUserDetails(${user.id})">View</button>
                                <button class="btn-edit" onclick="editUser(${user.id})">Edit</button>
                                ${user.application_count == 0 ? `<button class="btn-delete" onclick="deleteUser(${user.id})">Delete</button>` : ''}
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

// Update user pagination
function updateUserPagination(total, page, totalPages) {
    const pagination = document.getElementById('userPagination');
    const pageInfo = document.getElementById('userPageInfo');
    const prevBtn = document.getElementById('userPrevBtn');
    const nextBtn = document.getElementById('userNextBtn');

    if (totalPages > 1) {
        pagination.style.display = 'flex';
        pageInfo.textContent = `Page ${page} of ${totalPages} (${total} total)`;
        prevBtn.disabled = page <= 1;
        nextBtn.disabled = page >= totalPages;
    } else {
        pagination.style.display = 'none';
    }
}

// Change user page
function changeUserPage(direction) {
    currentUserPage += direction;
    loadUsers();
}

// View user details
async function viewUserDetails(userId) {
    try {
        const token = localStorage.getItem('adminToken');
        
        // Fetch user details
        const userResponse = await fetch(`${API_CONFIG.BASE_URL}/admin/users.php?search=${userId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        const userData = await userResponse.json();
        
        if (userData.success && userData.users && userData.users.length > 0) {
            const user = userData.users[0];
            
            // Fetch user's applications
            const appResponse = await fetch(`${API_CONFIG.BASE_URL}/applications.php?user_id=${userId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            let applications = [];
            try {
                const appData = await appResponse.json();
                if (appData.success && appData.applications) {
                    applications = appData.applications;
                }
            } catch (e) {
                console.log('No applications found for user');
            }
            
            // Display user details in modal or new window
            showUserModal(user, applications);
        } else {
            alert('User not found');
        }
    } catch (error) {
        console.error('Error fetching user details:', error);
        alert('Failed to fetch user details');
    }
}

// Show user modal
function showUserModal(user, applications) {
    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10000;
    `;
    
    modal.innerHTML = `
        <div style="background: white; padding: 30px; border-radius: 10px; max-width: 800px; max-height: 90vh; overflow-y: auto; width: 90%;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 2px solid #d32f2f; padding-bottom: 15px;">
                <h2 style="color: #333; margin: 0;">User Details</h2>
                <button onclick="this.closest('div').parentElement.remove()" style="background: #d32f2f; color: white; border: none; padding: 8px 16px; border-radius: 5px; cursor: pointer; font-size: 14px;">✕ Close</button>
            </div>
            
            <div style="margin-bottom: 25px;">
                <h3 style="color: #d32f2f; margin-bottom: 15px;">Personal Information</h3>
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px;">
                    <div>
                        <strong style="color: #666;">User ID:</strong><br>
                        <span style="color: #333;">#${user.id}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Name:</strong><br>
                        <span style="color: #333;">${user.name}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Email:</strong><br>
                        <span style="color: #333;">${user.email}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Phone:</strong><br>
                        <span style="color: #333;">${user.phone || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Registered:</strong><br>
                        <span style="color: #333;">${formatDate(user.created_at)}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Total Applications:</strong><br>
                        <span style="color: #333;">${user.application_count}</span>
                    </div>
                </div>
            </div>
            
            ${applications.length > 0 ? `
                <div>
                    <h3 style="color: #d32f2f; margin-bottom: 15px;">Application History</h3>
                    <table style="width: 100%; border-collapse: collapse;">
                        <thead>
                            <tr style="background: #f5f5f5;">
                                <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">Application ID</th>
                                <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">Status</th>
                                <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">Submitted</th>
                                <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${applications.map(app => `
                                <tr>
                                    <td style="padding: 10px; border: 1px solid #ddd;">${app.application_id}</td>
                                    <td style="padding: 10px; border: 1px solid #ddd;"><span class="status-badge status-${app.status}">${formatStatus(app.status)}</span></td>
                                    <td style="padding: 10px; border: 1px solid #ddd;">${formatDate(app.created_at)}</td>
                                    <td style="padding: 10px; border: 1px solid #ddd;">
                                        <button onclick="viewApplication('${app.application_id}')" style="background: #2196f3; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;">View</button>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            ` : '<p style="text-align: center; color: #999; padding: 20px; background: #f5f5f5; border-radius: 5px;">No applications submitted yet</p>'}
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Close on outside click
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.remove();
        }
    });
}

// Edit user
async function editUser(userId) {
    const user = usersData.find(u => u.id === userId);
    if (!user) {
        alert('User not found');
        return;
    }
    
    const newName = prompt('Enter new name:', user.name);
    if (newName === null) return; // Cancelled
    
    const newEmail = prompt('Enter new email:', user.email);
    if (newEmail === null) return; // Cancelled
    
    const newPhone = prompt('Enter new phone:', user.phone);
    if (newPhone === null) return; // Cancelled
    
    try {
        const token = localStorage.getItem('adminToken');
        const response = await fetch(`${API_CONFIG.BASE_URL}/admin/users.php`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                user_id: userId,
                name: newName,
                email: newEmail,
                phone: newPhone
            })
        });

        const data = await response.json();

        if (data.success) {
            alert('User updated successfully!');
            loadUsers();
        } else {
            alert(data.message || 'Failed to update user');
        }
    } catch (error) {
        console.error('Error updating user:', error);
        alert('Failed to update user. Please try again.');
    }
}

// Delete user
async function deleteUser(userId) {
    if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
        return;
    }

    try {
        const token = localStorage.getItem('adminToken');
        const response = await fetch(`${API_CONFIG.BASE_URL}/admin/users.php`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                user_id: userId
            })
        });

        const data = await response.json();

        if (data.success) {
            alert('User deleted successfully!');
            loadUsers();
            loadDashboard(); // Refresh stats
        } else {
            alert(data.message || 'Failed to delete user');
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        alert('Failed to delete user. Please try again.');
    }
}

// Load recent applications
async function loadRecentApplications() {
    try {
        const token = localStorage.getItem('adminToken');
        
        console.log('Fetching applications from:', `${API_CONFIG.BASE_URL}/admin/applications.php?limit=5`);
        
        const response = await fetch(`${API_CONFIG.BASE_URL}/admin/applications.php?limit=5`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        console.log('Applications response status:', response.status);
        
        const data = await response.json();
        console.log('Applications data:', data);

        if (data.success && data.applications) {
            displayRecentApplications(data.applications);
        } else {
            console.error('Failed to load applications:', data.message);
            document.getElementById('recentApplications').innerHTML = 
                `<p style="text-align: center; color: #f44336; padding: 20px;">Error: ${data.message || 'Failed to load applications'}</p>`;
        }
    } catch (error) {
        console.error('Error loading recent applications:', error);
        document.getElementById('recentApplications').innerHTML = 
            `<p style="text-align: center; color: #f44336; padding: 20px;">Error loading applications: ${error.message}</p>`;
    }
}

// Display recent applications
function displayRecentApplications(applications) {
    const container = document.getElementById('recentApplications');
    
    console.log('displayRecentApplications called with:', applications);
    
    if (!applications || applications.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>No recent applications</p></div>';
        return;
    }

    // Transform applications data
    const transformedApps = applications.map(app => transformApplicationData(app));
    
    console.log('Transformed apps:', transformedApps);

    const html = `
        <table class="applications-table">
            <thead>
                <tr>
                    <th>Application ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${transformedApps.map(app => `
                    <tr>
                        <td><strong>${app.application_id}</strong></td>
                        <td>${app.full_name || 'N/A'}</td>
                        <td>${app.email || 'N/A'}</td>
                        <td><span class="status-badge status-${app.status}">${formatStatus(app.status)}</span></td>
                        <td>${formatDate(app.created_at)}</td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-view" onclick="event.preventDefault(); viewApplicationDetails('${app.application_id}'); return false;">View</button>
                                <button class="btn-edit" onclick="event.preventDefault(); updateStatus('${app.application_id}'); return false;">Update</button>
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

// Load all applications
async function loadApplications() {
    try {
        const token = localStorage.getItem('adminToken');
        
        if (!token) {
            console.error('No admin token found');
            document.getElementById('applicationsTable').innerHTML = 
                '<p style="text-align: center; color: #f44336; padding: 40px;">Please login first</p>';
            return;
        }
        
        const searchTerm = document.getElementById('searchInput')?.value.trim() || '';
        
        let url = `${API_CONFIG.BASE_URL}/admin/applications.php?page=${currentPage}&limit=${itemsPerPage}`;
        
        if (currentStatus !== 'all') {
            url += `&status=${currentStatus}`;
        }
        
        if (searchTerm) {
            url += `&search=${encodeURIComponent(searchTerm)}`;
        }

        console.log('Fetching applications from:', url);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        console.log('Applications response status:', response.status);
        
        const data = await response.json();
        console.log('Applications data:', data);

        if (data.success && data.applications) {
            applicationsData = data.applications;
            displayApplications(data.applications);
            updatePagination(data.total, data.page, data.totalPages);
        } else {
            console.error('Failed to load applications:', data.message);
            document.getElementById('applicationsTable').innerHTML = 
                `<p style="text-align: center; color: #f44336; padding: 40px;">Error: ${data.message || 'Failed to load applications'}</p>`;
        }
    } catch (error) {
        console.error('Error loading applications:', error);
        document.getElementById('applicationsTable').innerHTML = 
            `<p style="text-align: center; color: #f44336; padding: 40px;">Error loading applications: ${error.message}<br><small>Check browser console (F12) for details</small></p>`;
    }
}

// Display applications
function displayApplications(applications) {
    const container = document.getElementById('applicationsTable');
    
    if (!applications || applications.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No applications found</p>
            </div>
        `;
        return;
    }

    // Transform applications data
    const transformedApps = applications.map(app => transformApplicationData(app));

    const html = `
        <table class="applications-table">
            <thead>
                <tr>
                    <th>Application ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Course/Institution</th>
                    <th>Status</th>
                    <th>Submitted</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${transformedApps.map(app => `
                    <tr>
                        <td><strong>${app.application_id}</strong></td>
                        <td>
                            <div style="font-weight: 600; color: #333;">${app.full_name || 'N/A'}</div>
                            ${app.user_name ? `<div style="font-size: 11px; color: #999;">User: ${app.user_name}</div>` : ''}
                        </td>
                        <td>${app.email || 'N/A'}</td>
                        <td>${app.phone || 'N/A'}</td>
                        <td>
                            <div style="font-size: 12px;">${app.course_name || 'N/A'}</div>
                            <div style="font-size: 11px; color: #666;">${app.institution_name || 'N/A'}</div>
                        </td>
                        <td><span class="status-badge status-${app.status}">${formatStatus(app.status)}</span></td>
                        <td>${formatDate(app.created_at)}</td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-view" onclick="event.preventDefault(); viewApplicationDetails('${app.application_id}'); return false;">View Full Details</button>
                                <button class="btn-edit" onclick="event.preventDefault(); updateStatus('${app.application_id}'); return false;">Update</button>
                                <button class="btn-delete" onclick="event.preventDefault(); deleteApplication('${app.application_id}'); return false;">Delete</button>
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

// Update pagination
function updatePagination(total, page, totalPages) {
    const pagination = document.getElementById('pagination');
    const pageInfo = document.getElementById('pageInfo');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    if (totalPages > 1) {
        pagination.style.display = 'flex';
        pageInfo.textContent = `Page ${page} of ${totalPages} (${total} total)`;
        prevBtn.disabled = page <= 1;
        nextBtn.disabled = page >= totalPages;
    } else {
        pagination.style.display = 'none';
    }
}

// Change page
function changePage(direction) {
    currentPage += direction;
    loadApplications();
}

// View application
function viewApplication(applicationId) {
    window.open(`view-application.html?id=${applicationId}`, '_blank');
}

// View application details in modal
async function viewApplicationDetails(applicationId) {
    try {
        const token = localStorage.getItem('adminToken');
        
        console.log('Fetching details for application:', applicationId);
        
        // Fetch full application details
        const response = await fetch(`${API_CONFIG.BASE_URL}/admin/applications.php?search=${applicationId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        console.log('Response status:', response.status);
        
        const data = await response.json();
        
        console.log('Response data:', data);
        
        if (data.success && data.applications && data.applications.length > 0) {
            const app = transformApplicationData(data.applications[0]);
            console.log('Transformed application:', app);
            showApplicationDetailsModal(app);
        } else {
            console.error('Application not found:', data);
            alert(`Application not found!\n\nSearched for: ${applicationId}\nApplications returned: ${data.applications ? data.applications.length : 0}`);
        }
    } catch (error) {
        console.error('Error fetching application details:', error);
        alert('Failed to fetch application details: ' + error.message);
    }
}

// Show detailed application modal
function showApplicationDetailsModal(app) {
    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10000;
        overflow-y: auto;
        padding: 20px;
    `;
    
    const documentsHtml = `
        <div style="margin-top: 20px;">
            <h3 style="color: #d32f2f; margin-bottom: 15px; border-bottom: 2px solid #d32f2f; padding-bottom: 10px;">📎 Uploaded Documents</h3>
            <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px;">
                ${app.photo ? `
                    <div style="background: #f5f5f5; padding: 12px; border-radius: 8px;">
                        <strong style="color: #666;">📷 Photograph:</strong><br>
                        <a href="${API_CONFIG.BASE_URL}/../${app.photo}" target="_blank" style="color: #2196f3; text-decoration: none;">
                            View Photo →
                        </a>
                    </div>
                ` : ''}
                ${app.aadhar ? `
                    <div style="background: #f5f5f5; padding: 12px; border-radius: 8px;">
                        <strong style="color: #666;">🆔 Aadhar Card:</strong><br>
                        <a href="${API_CONFIG.BASE_URL}/../${app.aadhar}" target="_blank" style="color: #2196f3; text-decoration: none;">
                            View Document →
                        </a>
                    </div>
                ` : ''}
                ${app.income ? `
                    <div style="background: #f5f5f5; padding: 12px; border-radius: 8px;">
                        <strong style="color: #666;">💰 Income Certificate:</strong><br>
                        <a href="${API_CONFIG.BASE_URL}/../${app.income}" target="_blank" style="color: #2196f3; text-decoration: none;">
                            View Document →
                        </a>
                    </div>
                ` : ''}
                ${app.domicile ? `
                    <div style="background: #f5f5f5; padding: 12px; border-radius: 8px;">
                        <strong style="color: #666;">🏠 Domicile Certificate:</strong><br>
                        <a href="${API_CONFIG.BASE_URL}/../${app.domicile}" target="_blank" style="color: #2196f3; text-decoration: none;">
                            View Document →
                        </a>
                    </div>
                ` : ''}
                ${app.tenth_marksheet ? `
                    <div style="background: #f5f5f5; padding: 12px; border-radius: 8px;">
                        <strong style="color: #666;">📄 10th Marksheet:</strong><br>
                        <a href="${API_CONFIG.BASE_URL}/../${app.tenth_marksheet}" target="_blank" style="color: #2196f3; text-decoration: none;">
                            View Document →
                        </a>
                    </div>
                ` : ''}
                ${app.twelfth_marksheet ? `
                    <div style="background: #f5f5f5; padding: 12px; border-radius: 8px;">
                        <strong style="color: #666;">📄 12th Marksheet:</strong><br>
                        <a href="${API_CONFIG.BASE_URL}/../${app.twelfth_marksheet}" target="_blank" style="color: #2196f3; text-decoration: none;">
                            View Document →
                        </a>
                    </div>
                ` : ''}
                ${app.admission_letter ? `
                    <div style="background: #f5f5f5; padding: 12px; border-radius: 8px;">
                        <strong style="color: #666;">📋 Admission Letter:</strong><br>
                        <a href="${API_CONFIG.BASE_URL}/../${app.admission_letter}" target="_blank" style="color: #2196f3; text-decoration: none;">
                            View Document →
                        </a>
                    </div>
                ` : ''}
                ${app.bank_passbook ? `
                    <div style="background: #f5f5f5; padding: 12px; border-radius: 8px;">
                        <strong style="color: #666;">🏦 Bank Passbook:</strong><br>
                        <a href="${API_CONFIG.BASE_URL}/../${app.bank_passbook}" target="_blank" style="color: #2196f3; text-decoration: none;">
                            View Document →
                        </a>
                    </div>
                ` : ''}
            </div>
        </div>
    `;
    
    modal.innerHTML = `
        <div style="background: white; padding: 30px; border-radius: 10px; max-width: 1000px; max-height: 90vh; overflow-y: auto; width: 100%;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 3px solid #d32f2f; padding-bottom: 15px;">
                <h2 style="color: #333; margin: 0;">📋 Complete Application Details</h2>
                <button onclick="this.closest('div').parentElement.remove()" style="background: #d32f2f; color: white; border: none; padding: 8px 16px; border-radius: 5px; cursor: pointer; font-size: 14px; font-weight: bold;">✕ Close</button>
            </div>
            
            <!-- Application Status -->
            <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 25px;">
                <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; text-align: center;">
                    <div>
                        <div style="font-size: 12px; opacity: 0.9;">Application ID</div>
                        <div style="font-size: 24px; font-weight: bold; margin-top: 5px;">${app.application_id}</div>
                    </div>
                    <div>
                        <div style="font-size: 12px; opacity: 0.9;">Status</div>
                        <div style="font-size: 18px; font-weight: bold; margin-top: 5px;">${formatStatus(app.status)}</div>
                    </div>
                    <div>
                        <div style="font-size: 12px; opacity: 0.9;">Submitted On</div>
                        <div style="font-size: 18px; font-weight: bold; margin-top: 5px;">${formatDate(app.created_at)}</div>
                    </div>
                </div>
            </div>

            <!-- Personal Information -->
            <div style="margin-bottom: 25px;">
                <h3 style="color: #d32f2f; margin-bottom: 15px; border-bottom: 2px solid #d32f2f; padding-bottom: 10px;">👤 Personal Information</h3>
                <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px;">
                    <div>
                        <strong style="color: #666;">Full Name:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.full_name || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Father's Name:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.father_name || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Mother's Name:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.mother_name || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Date of Birth:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.dob || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Gender:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.gender || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Category:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.category || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Aadhar Number:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.aadhar_number || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Mobile:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.phone || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Email:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.email || 'N/A'}</span>
                    </div>
                </div>
            </div>

            <!-- Address Information -->
            <div style="margin-bottom: 25px;">
                <h3 style="color: #d32f2f; margin-bottom: 15px; border-bottom: 2px solid #d32f2f; padding-bottom: 10px;">📍 Address Information</h3>
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px;">
                    <div style="grid-column: 1 / -1;">
                        <strong style="color: #666;">Complete Address:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.address || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">District:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.district || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">State:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.state || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Pin Code:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.pincode || 'N/A'}</span>
                    </div>
                </div>
            </div>

            <!-- Academic Information -->
            <div style="margin-bottom: 25px;">
                <h3 style="color: #d32f2f; margin-bottom: 15px; border-bottom: 2px solid #d32f2f; padding-bottom: 10px;">🎓 Academic Information</h3>
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px;">
                    <div>
                        <strong style="color: #666;">10th Percentage:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.tenth_percentage || 'N/A'}%</span>
                    </div>
                    <div>
                        <strong style="color: #666;">10th Board:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.tenth_board || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">12th Percentage:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.twelfth_percentage || 'N/A'}%</span>
                    </div>
                    <div>
                        <strong style="color: #666;">12th Board:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.twelfth_board || 'N/A'}</span>
                    </div>
                </div>
            </div>

            <!-- Course & Institution -->
            <div style="margin-bottom: 25px;">
                <h3 style="color: #d32f2f; margin-bottom: 15px; border-bottom: 2px solid #d32f2f; padding-bottom: 10px;">🏫 Course & Institution Details</h3>
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px;">
                    <div>
                        <strong style="color: #666;">Course Name:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.course_name || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Course Duration:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.course_duration || 'N/A'} years</span>
                    </div>
                    <div style="grid-column: 1 / -1;">
                        <strong style="color: #666;">Institution Name:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.institution_name || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Institution State:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.institution_state || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Year of Admission:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.year_of_admission || 'N/A'}</span>
                    </div>
                </div>
            </div>

            <!-- Family & Income -->
            <div style="margin-bottom: 25px;">
                <h3 style="color: #d32f2f; margin-bottom: 15px; border-bottom: 2px solid #d32f2f; padding-bottom: 10px;">💰 Family & Income Details</h3>
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px;">
                    <div>
                        <strong style="color: #666;">Father's Occupation:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.father_occupation || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Mother's Occupation:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.mother_occupation || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Annual Family Income:</strong><br>
                        <span style="color: #333; font-size: 15px;">₹ ${app.family_income ? parseInt(app.family_income).toLocaleString('en-IN') : 'N/A'}</span>
                    </div>
                </div>
            </div>

            <!-- Bank Details -->
            <div style="margin-bottom: 25px;">
                <h3 style="color: #d32f2f; margin-bottom: 15px; border-bottom: 2px solid #d32f2f; padding-bottom: 10px;">🏦 Bank Account Details</h3>
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px;">
                    <div>
                        <strong style="color: #666;">Bank Name:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.bank_name || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Branch Name:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.branch_name || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">Account Number:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.account_number || 'N/A'}</span>
                    </div>
                    <div>
                        <strong style="color: #666;">IFSC Code:</strong><br>
                        <span style="color: #333; font-size: 15px;">${app.ifsc_code || 'N/A'}</span>
                    </div>
                </div>
            </div>

            ${documentsHtml}

            <!-- Action Buttons -->
            <div style="margin-top: 30px; padding-top: 20px; border-top: 2px solid #eee; display: flex; gap: 10px; justify-content: flex-end;">
                <button onclick="updateStatus('${app.application_id}')" style="background: #ff9800; color: white; border: none; padding: 12px 24px; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 600;">
                    Update Status
                </button>
                <button onclick="window.print()" style="background: #2196f3; color: white; border: none; padding: 12px 24px; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 600;">
                    Print Application
                </button>
                <button onclick="this.closest('div').parentElement.parentElement.remove()" style="background: #666; color: white; border: none; padding: 12px 24px; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 600;">
                    Close
                </button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Close on outside click
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.remove();
        }
    });
}

// Update status
async function updateStatus(applicationId) {
    const newStatus = prompt('Enter new status (pending, under_review, approved, rejected):');
    
    if (!newStatus) return;

    const validStatuses = ['pending', 'under_review', 'approved', 'rejected'];
    if (!validStatuses.includes(newStatus.toLowerCase())) {
        alert('Invalid status. Use: pending, under_review, approved, or rejected');
        return;
    }

    try {
        const token = localStorage.getItem('adminToken');
        const response = await fetch(`${API_CONFIG.BASE_URL}/admin/applications.php`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                application_id: applicationId,
                status: newStatus.toLowerCase()
            })
        });

        const data = await response.json();

        if (data.success) {
            alert('Status updated successfully!');
            loadApplications();
            loadDashboard();
        } else {
            alert(data.message || 'Failed to update status');
        }
    } catch (error) {
        console.error('Error updating status:', error);
        alert('Failed to update status. Please try again.');
    }
}

// Delete application
async function deleteApplication(applicationId) {
    if (!confirm('Are you sure you want to delete this application? This action cannot be undone.')) {
        return;
    }

    try {
        const token = localStorage.getItem('adminToken');
        const response = await fetch(`${API_CONFIG.BASE_URL}/admin/applications.php`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                application_id: applicationId
            })
        });

        const data = await response.json();

        if (data.success) {
            alert('Application deleted successfully!');
            loadApplications();
            loadDashboard();
        } else {
            alert(data.message || 'Failed to delete application');
        }
    } catch (error) {
        console.error('Error deleting application:', error);
        alert('Failed to delete application. Please try again.');
    }
}

// Logout
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        localStorage.removeItem('adminToken');
        localStorage.removeItem('adminUser');
        localStorage.removeItem('adminRemember');
        window.location.href = 'admin-login.html';
    }
}

// Helper functions
function formatStatus(status) {
    const statusMap = {
        'pending': 'Pending',
        'under_review': 'Under Review',
        'approved': 'Approved',
        'rejected': 'Rejected'
    };
    return statusMap[status] || status;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Make showSection global
window.showSection = showSection;
