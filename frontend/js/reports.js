// Reports & Analytics JavaScript

// Check authentication
function checkAuth() {
    const token = localStorage.getItem('adminToken');
    if (!token) {
        window.location.href = 'admin-login.html';
        return false;
    }
    return token;
}

// Logout function
function logout() {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminData');
    window.location.href = 'admin-login.html';
}

// Add logout to link
document.getElementById('logoutLink')?.addEventListener('click', (e) => {
    e.preventDefault();
    logout();
});

// Global variables
let allApplications = [];
let filteredApplications = [];
let charts = {};

// Transform application data to match expected format
function transformApplicationData(app) {
    // Concatenate name fields
    const nameParts = [app.first_name, app.middle_name, app.last_name].filter(part => part && part.trim());
    const full_name = nameParts.join(' ');
    
    // Parse academic_info if it's a JSON string
    let academic_info = app.academic_info;
    if (typeof academic_info === 'string') {
        try {
            academic_info = JSON.parse(academic_info);
        } catch (e) {
            academic_info = {};
        }
    }
    
    return {
        ...app,
        full_name: full_name,
        phone: app.mobile || app.phone,
        aadhar_number: app.aadhar || app.aadhar_number,
        dob: app.date_of_birth || app.dob,
        aadhar: app.aadhar_doc || app.aadhar,
        income: app.income_cert || app.income,
        course_name: academic_info?.course?.name || 'N/A',
        institution_name: academic_info?.course?.collegeName || 'N/A'
    };
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', async () => {
    const token = checkAuth();
    if (!token) return;

    // Set admin name
    const adminData = JSON.parse(localStorage.getItem('adminData') || '{}');
    document.getElementById('adminName').textContent = adminData.username || 'Admin';

    // Load data
    await loadAllData();
});

// Load all data
async function loadAllData() {
    try {
        const token = localStorage.getItem('adminToken');
        
        console.log('Loading reports data...');
        
        // Fetch all applications
        const response = await fetch(`${API_CONFIG.BASE_URL}/admin/applications.php?limit=1000`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch applications');
        }

        const data = await response.json();
        console.log('Received data:', data);
        
        allApplications = (data.applications || []).map(transformApplicationData);
        filteredApplications = [...allApplications];
        
        console.log('Transformed applications:', allApplications.length);

        // Populate state filter
        populateStateFilter();

        // Update all displays
        updateStatistics();
        updateCharts();
        updateTable();

    } catch (error) {
        console.error('Error loading data:', error);
        document.getElementById('reportTableBody').innerHTML = `
            <tr>
                <td colspan="9" style="text-align: center; padding: 40px; color: #e74c3c;">
                    <h3>Error Loading Data</h3>
                    <p>${error.message}</p>
                    <p>Please check your connection and try again.</p>
                </td>
            </tr>
        `;
    }
}

// Populate state filter dropdown
function populateStateFilter() {
    const stateFilter = document.getElementById('stateFilter');
    const states = [...new Set(allApplications.map(app => app.state).filter(Boolean))].sort();
    
    states.forEach(state => {
        const option = document.createElement('option');
        option.value = state;
        option.textContent = state;
        stateFilter.appendChild(option);
    });
}

// Apply filters
function applyFilters() {
    const dateRange = document.getElementById('dateRange').value;
    const status = document.getElementById('statusFilter').value;
    const category = document.getElementById('categoryFilter').value;
    const state = document.getElementById('stateFilter').value;

    filteredApplications = allApplications.filter(app => {
        // Date filter
        if (dateRange !== 'all') {
            const appDate = new Date(app.created_at);
            const now = new Date();
            
            switch(dateRange) {
                case 'today':
                    if (appDate.toDateString() !== now.toDateString()) return false;
                    break;
                case 'week':
                    const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
                    if (appDate < weekAgo) return false;
                    break;
                case 'month':
                    const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
                    if (appDate < monthAgo) return false;
                    break;
                case 'year':
                    const yearAgo = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
                    if (appDate < yearAgo) return false;
                    break;
            }
        }

        // Status filter
        if (status && app.status !== status) return false;

        // Category filter
        if (category && app.category !== category) return false;

        // State filter
        if (state && app.state !== state) return false;

        return true;
    });

    // Update displays
    updateStatistics();
    updateCharts();
    updateTable();
}

// Reset filters
function resetFilters() {
    document.getElementById('dateRange').value = 'month';
    document.getElementById('statusFilter').value = '';
    document.getElementById('categoryFilter').value = '';
    document.getElementById('stateFilter').value = '';
    
    filteredApplications = [...allApplications];
    updateStatistics();
    updateCharts();
    updateTable();
}

// Update statistics
function updateStatistics() {
    console.log('updateStatistics called');
    console.log('filteredApplications:', filteredApplications.length);
    
    const total = filteredApplications.length;
    const approved = filteredApplications.filter(app => app.status === 'approved').length;
    const pending = filteredApplications.filter(app => app.status === 'pending' || app.status === 'under_review').length;
    const rejected = filteredApplications.filter(app => app.status === 'rejected').length;

    console.log('Stats:', { total, approved, pending, rejected });

    // Calculate success rate
    const successRate = total > 0 ? ((approved / total) * 100).toFixed(1) : 0;

    // Calculate average processing time (mock data for now)
    const avgTime = calculateAverageProcessingTime();

    // Update DOM
    const totalEl = document.getElementById('totalApps');
    const approvedEl = document.getElementById('approvedApps');
    const pendingEl = document.getElementById('pendingApps');
    const rejectedEl = document.getElementById('rejectedApps');
    const successRateEl = document.getElementById('successRate');
    const avgTimeEl = document.getElementById('avgTime');

    console.log('DOM elements found:', {
        totalEl: !!totalEl,
        approvedEl: !!approvedEl,
        pendingEl: !!pendingEl,
        rejectedEl: !!rejectedEl,
        successRateEl: !!successRateEl,
        avgTimeEl: !!avgTimeEl
    });

    if (totalEl) totalEl.textContent = total;
    if (approvedEl) approvedEl.textContent = approved;
    if (pendingEl) pendingEl.textContent = pending;
    if (rejectedEl) rejectedEl.textContent = rejected;
    if (successRateEl) successRateEl.textContent = successRate + '%';
    if (avgTimeEl) avgTimeEl.textContent = avgTime;

    // Calculate changes (comparing to previous period - simplified)
    updateChangeIndicators();
}

// Calculate average processing time
function calculateAverageProcessingTime() {
    const processedApps = filteredApplications.filter(app => 
        app.status === 'approved' || app.status === 'rejected'
    );

    if (processedApps.length === 0) return '0d';

    let totalDays = 0;
    processedApps.forEach(app => {
        const created = new Date(app.created_at);
        const updated = new Date(app.updated_at || app.created_at);
        const days = Math.floor((updated - created) / (1000 * 60 * 60 * 24));
        totalDays += days;
    });

    const avgDays = Math.floor(totalDays / processedApps.length);
    return avgDays + 'd';
}

// Update change indicators (simplified - comparing to all data)
function updateChangeIndicators() {
    const totalChange = allApplications.length > 0 
        ? ((filteredApplications.length / allApplications.length) * 100 - 100).toFixed(1)
        : 0;
    
    // This is simplified - in a real app, you'd compare to previous period
    document.querySelectorAll('.change').forEach(el => {
        el.textContent = `${totalChange >= 0 ? '+' : ''}${totalChange}% from all data`;
    });
}

// Update charts
function updateCharts() {
    updateTrendChart();
    updateStatusChart();
    updateCategoryChart();
    updateStateChart();
}

// Trend Chart
function updateTrendChart() {
    const ctx = document.getElementById('trendChart');
    if (!ctx) return;

    // Group applications by date
    const dateCounts = {};
    filteredApplications.forEach(app => {
        const date = new Date(app.created_at).toLocaleDateString();
        dateCounts[date] = (dateCounts[date] || 0) + 1;
    });

    const sortedDates = Object.keys(dateCounts).sort((a, b) => new Date(a) - new Date(b));
    const counts = sortedDates.map(date => dateCounts[date]);

    if (charts.trend) charts.trend.destroy();

    charts.trend = new Chart(ctx, {
        type: 'line',
        data: {
            labels: sortedDates,
            datasets: [{
                label: 'Applications',
                data: counts,
                borderColor: '#3498db',
                backgroundColor: 'rgba(52, 152, 219, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    labels: { color: '#000' }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { color: '#000' }
                },
                x: {
                    ticks: { color: '#000' }
                }
            }
        }
    });
}

// Status Chart
function updateStatusChart() {
    const ctx = document.getElementById('statusChart');
    if (!ctx) return;

    const statusCounts = {
        pending: 0,
        under_review: 0,
        approved: 0,
        rejected: 0
    };

    filteredApplications.forEach(app => {
        if (statusCounts.hasOwnProperty(app.status)) {
            statusCounts[app.status]++;
        }
    });

    if (charts.status) charts.status.destroy();

    charts.status = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Pending', 'Under Review', 'Approved', 'Rejected'],
            datasets: [{
                data: [
                    statusCounts.pending,
                    statusCounts.under_review,
                    statusCounts.approved,
                    statusCounts.rejected
                ],
                backgroundColor: [
                    '#f39c12',
                    '#3498db',
                    '#27ae60',
                    '#e74c3c'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { color: '#000' }
                }
            }
        }
    });
}

// Category Chart
function updateCategoryChart() {
    const ctx = document.getElementById('categoryChart');
    if (!ctx) return;

    const categoryCounts = {};
    filteredApplications.forEach(app => {
        const category = app.category || 'General';
        categoryCounts[category] = (categoryCounts[category] || 0) + 1;
    });

    const categories = Object.keys(categoryCounts);
    const counts = Object.values(categoryCounts);

    if (charts.category) charts.category.destroy();

    charts.category = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: categories,
            datasets: [{
                label: 'Applications',
                data: counts,
                backgroundColor: '#9b59b6'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { color: '#000' }
                },
                x: {
                    ticks: { color: '#000' }
                }
            }
        }
    });
}

// State Chart
function updateStateChart() {
    const ctx = document.getElementById('stateChart');
    if (!ctx) return;

    const stateCounts = {};
    filteredApplications.forEach(app => {
        const state = app.state || 'Unknown';
        stateCounts[state] = (stateCounts[state] || 0) + 1;
    });

    // Get top 10 states
    const sortedStates = Object.entries(stateCounts)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 10);

    const states = sortedStates.map(item => item[0]);
    const counts = sortedStates.map(item => item[1]);

    if (charts.state) charts.state.destroy();

    charts.state = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: states,
            datasets: [{
                label: 'Applications',
                data: counts,
                backgroundColor: '#1abc9c'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            indexAxis: 'y',
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                x: {
                    beginAtZero: true,
                    ticks: { color: '#000' }
                },
                y: {
                    ticks: { color: '#000' }
                }
            }
        }
    });
}

// Update table
function updateTable() {
    console.log('updateTable called');
    const tbody = document.getElementById('reportTableBody');
    console.log('Table body element:', !!tbody);
    console.log('Filtered applications count:', filteredApplications.length);
    
    if (!tbody) {
        console.error('reportTableBody element not found!');
        return;
    }
    
    if (filteredApplications.length === 0) {
        console.log('No filtered applications, showing empty state');
        tbody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-state" style="text-align: center; padding: 40px;">
                    <h3 style="color: #666;">No data found</h3>
                    <p style="color: #999;">Try adjusting your filters</p>
                </td>
            </tr>
        `;
        return;
    }

    console.log('Rendering', filteredApplications.length, 'applications');
    tbody.innerHTML = filteredApplications.map(app => `
        <tr>
            <td>${app.id}</td>
            <td>${app.full_name || 'N/A'}</td>
            <td>${app.email || 'N/A'}</td>
            <td>${app.phone || 'N/A'}</td>
            <td>${app.state || 'N/A'}</td>
            <td>${app.category || 'General'}</td>
            <td>
                <span class="status-badge status-${app.status}">
                    ${formatStatus(app.status)}
                </span>
            </td>
            <td>${formatDate(app.created_at)}</td>
            <td>${formatDate(app.updated_at)}</td>
        </tr>
    `).join('');
    
    console.log('Table updated successfully');
}

// Format status
function formatStatus(status) {
    const statusMap = {
        'pending': 'Pending',
        'under_review': 'Under Review',
        'approved': 'Approved',
        'rejected': 'Rejected'
    };
    return statusMap[status] || status;
}

// Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Export functions
function exportReport() {
    exportToExcel();
}

function exportToExcel() {
    // Create CSV content
    let csv = 'ID,Name,Email,Phone,State,Category,Status,Applied On,Last Updated\n';
    
    filteredApplications.forEach(app => {
        csv += `${app.id},"${app.full_name || 'N/A'}","${app.email || 'N/A'}","${app.phone || 'N/A'}","${app.state || 'N/A'}","${app.category || 'General'}","${formatStatus(app.status)}","${formatDate(app.created_at)}","${formatDate(app.updated_at)}"\n`;
    });

    // Download
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `PMSSS_Report_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
}

function exportToPDF() {
    alert('PDF export will be available soon! For now, use Excel export or print this page.');
    // In a real implementation, you'd use a library like jsPDF
}
