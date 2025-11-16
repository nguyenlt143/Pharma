// Dashboard JavaScript
let revenueChart;
let currentView = 'revenue';

document.addEventListener('DOMContentLoaded', function() {
    loadShifts();
    loadEmployees();
    loadDashboard('revenue');
    
    // Auto-load on filter change
    const modeSelect = document.getElementById('modeSelect');
    const periodInput = document.getElementById('periodInput');
    const shiftSelect = document.getElementById('shiftSelect');
    const employeeSelect = document.getElementById('employeeSelect');
    
    if (modeSelect) {
        modeSelect.addEventListener('change', () => loadDashboard(currentView));
    }
    if (periodInput) {
        periodInput.addEventListener('change', () => loadDashboard(currentView));
    }
    if (shiftSelect) {
        shiftSelect.addEventListener('change', () => loadDashboard(currentView));
    }
    if (employeeSelect) {
        employeeSelect.addEventListener('change', () => loadDashboard(currentView));
    }
});

function loadShifts() {
    fetch('/api/owner/shifts')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('shiftSelect');
            if (select && Array.isArray(data)) {
                select.innerHTML = '<option value="">Tất cả</option>' +
                    data.map(s => `<option value="${s.id}">${s.name || 'Ca #' + s.id}</option>`).join('');
            }
        })
        .catch(err => {
            console.warn('Không thể tải danh sách ca làm việc:', err);
        });
}

function loadEmployees() {
    fetch('/api/owner/employees')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('employeeSelect');
            if (select && Array.isArray(data)) {
                select.innerHTML = '<option value="">Tất cả</option>' +
                    data.map(e => `<option value="${e.id}">${e.fullName || e.userName || 'User #' + e.id}</option>`).join('');
            }
        })
        .catch(err => {
            console.warn('Không thể tải danh sách nhân viên:', err);
        });
}

function loadDashboard(view) {
    currentView = view;
    const mode = document.getElementById('modeSelect').value;
    const period = document.getElementById('periodInput').value;
    const shift = document.getElementById('shiftSelect').value;
    const employeeId = document.getElementById('employeeSelect').value;

    const url = view === 'revenue' ? '/api/owner/dashboard/revenue' : '/api/owner/dashboard/profit';
    
    const params = new URLSearchParams({
        mode: mode,
        period: period
    });
    if (shift) params.append('shift', shift);
    if (employeeId) params.append('employeeId', employeeId);

    fetch(`${url}?${params}`)
        .then(res => res.json())
        .then(data => {
            // Update stats
            if (view === 'revenue') {
                document.getElementById('totalRevenue').textContent = formatCurrency(data.totalRevenue || 0);
                document.getElementById('totalProfit').textContent = formatCurrency(data.totalProfit || 0);
                document.getElementById('totalOrders').textContent = data.totalOrders || 0;

                // Update product stats
                updateProductStats(data.productStats || []);
            } else {
                document.getElementById('totalProfit').textContent = formatCurrency(data.totalProfit || 0);
                document.getElementById('totalRevenue').textContent = formatCurrency(data.totalRevenue || 0);
                document.getElementById('totalOrders').textContent = data.totalOrders || 0;
            }

            // Update chart (if needed)
            updateChart(data);
        })
        .catch(err => {
            console.error('Error loading dashboard:', err);
            alert('Có lỗi xảy ra khi tải dashboard');
        });
}

function updateProductStats(stats) {
    const container = document.getElementById('productStatsList');
    if (stats.length === 0) {
        container.innerHTML = '<p style="color: #6B7280; text-align: center; padding: 20px;">Không có dữ liệu</p>';
        return;
    }

    let html = '';
    stats.forEach(item => {
        html += `
            <div class="product-item">
                <div style="flex: 1;">
                    <div class="product-name">${item.name}</div>
                    <div class="product-bar">
                        <div class="product-bar-fill" style="width: ${item.percent}%; background-color: ${item.color};"></div>
                    </div>
                </div>
                <div class="product-percent">${item.percent}%</div>
            </div>
        `;
    });
    container.innerHTML = html;
}

function updateChart(data) {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;
    
    if (revenueChart) {
        revenueChart.destroy();
    }
    
    // Get daily revenue data
    const dailyRevenues = data.dailyRevenues || [];
    
    if (dailyRevenues.length === 0) {
        // Show empty chart message
        ctx.getContext('2d').clearRect(0, 0, ctx.width, ctx.height);
        ctx.getContext('2d').font = '16px Arial';
        ctx.getContext('2d').fillStyle = '#6B7280';
        ctx.getContext('2d').textAlign = 'center';
        ctx.getContext('2d').fillText('Không có dữ liệu', ctx.width / 2, ctx.height / 2);
        return;
    }
    
    const labels = dailyRevenues.map(item => {
        const date = new Date(item.date);
        return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
    });
    const revenues = dailyRevenues.map(item => item.revenue || 0);
    
    revenueChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu',
                data: revenues,
                borderColor: '#2563EB',
                backgroundColor: 'rgba(37, 99, 235, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    callbacks: {
                        label: function(context) {
                            return 'Doanh thu: ' + formatCurrency(context.parsed.y);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return new Intl.NumberFormat('vi-VN').format(value) + ' đ';
                        }
                    }
                }
            }
        }
    });
}

function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(value);
}

