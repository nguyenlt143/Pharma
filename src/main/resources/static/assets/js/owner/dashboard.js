// Dashboard JavaScript
let revenueChart;
let currentView = 'profit';

document.addEventListener('DOMContentLoaded', function() {
    console.log('Owner dashboard JS loaded');

    loadBranches();

    // Set default month and year to current month/year
    setCurrentMonthYear();

    // Auto-load dashboard after a short delay to ensure branches are loaded
    setTimeout(() => {
        loadDashboard('profit');
    }, 300);

    // Auto-load on filter change
    const monthSelect = document.getElementById('monthSelect');
    const yearSelect = document.getElementById('yearSelect');
    const branchSelect = document.getElementById('branchSelect');

    if (monthSelect) {
        monthSelect.addEventListener('change', () => loadDashboard(currentView));
    }

    if (yearSelect) {
        yearSelect.addEventListener('change', () => loadDashboard(currentView));
    }

    if (branchSelect) {
        branchSelect.addEventListener('change', () => loadDashboard(currentView));
    }
});

function setCurrentMonthYear() {
    const now = new Date();
    const currentMonth = String(now.getMonth() + 1).padStart(2, '0');
    const currentYear = now.getFullYear();

    const monthSelect = document.getElementById('monthSelect');
    const yearSelect = document.getElementById('yearSelect');

    if (monthSelect) {
        monthSelect.value = currentMonth;
    }

    if (yearSelect) {
        yearSelect.value = String(currentYear);
    }
}

function loadBranches() {
    console.log('Loading branches...');
    fetch('/api/owner/branches')
        .then(res => res.json())
        .then(data => {
            console.log('Branches loaded:', data);
            const select = document.getElementById('branchSelect');
            if (select && Array.isArray(data)) {
                select.innerHTML = '<option value="">Tất cả chi nhánh</option>' +
                    data.map(b => `<option value="${b.id}">${b.name || 'Chi nhánh #' + b.id}</option>`).join('');
            }
        })
        .catch(err => {
            console.error('Không thể tải danh sách chi nhánh:', err);
        });
}

function loadDashboard(view) {
    currentView = view;
    const month = document.getElementById('monthSelect').value;
    const year = document.getElementById('yearSelect').value;
    const period = `${year}-${month}`;
    const branchId = document.getElementById('branchSelect').value;

    console.log(`Loading dashboard - view: ${view}, period: ${period}, branchId: ${branchId || 'all'}`);

    const url = view === 'revenue' ? '/api/owner/dashboard/revenue' : '/api/owner/dashboard/profit';
    
    const params = new URLSearchParams({
        period: period
    });
    if (branchId) params.append('branchId', branchId);

    console.log(`Fetching: ${url}?${params}`);

    fetch(`${url}?${params}`)
        .then(res => {
            console.log('Dashboard response status:', res.status);
            if (!res.ok) {
                throw new Error(`HTTP ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            console.log('Dashboard data received:', data);
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
            showToast('Có lỗi xảy ra khi tải dashboard', 'error');
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
                    display: false
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

function showToast(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);
    // Optional: Add visual toast notification here
    alert(message);
}
