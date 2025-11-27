// Manager Import/Export Report JavaScript
let inOutChart;
let categoryDonutChart;
let chartType = 'bar';
let allActivities = [];
let currentActivityFilter = 'all';

document.addEventListener('DOMContentLoaded', () => {
    loadInventorySummary();
    loadInventoryMovements();
    loadCategoryDistribution();
    loadRecentActivities();

    // Chart type toggle
    const chartBarBtn = document.getElementById('chartBarBtn');
    const chartLineBtn = document.getElementById('chartLineBtn');

    if (chartBarBtn) {
        chartBarBtn.addEventListener('click', () => {
            chartType = 'bar';
            chartBarBtn.classList.add('active');
            chartLineBtn?.classList.remove('active');
            loadInventoryMovements();
        });
        chartBarBtn.classList.add('active');
    }

    if (chartLineBtn) {
        chartLineBtn.addEventListener('click', () => {
            chartType = 'line';
            chartLineBtn.classList.add('active');
            chartBarBtn?.classList.remove('active');
            loadInventoryMovements();
        });
    }

    // Range change handler - auto reload
    const rangeSelect = document.getElementById('rangeSelect');
    if (rangeSelect) {
        rangeSelect.addEventListener('change', () => {
            loadInventoryMovements();
        });
    }
});

function loadInventorySummary() {
    fetch('/api/manager/import-export/summary')
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            const totalValueEl = document.getElementById('totalValue');
            const valueDeltaLabelEl = document.getElementById('valueDeltaLabel');
            const lowStockCountEl = document.getElementById('lowStockCount');
            const pendingInboundEl = document.getElementById('pendingInbound');
            const pendingOutboundEl = document.getElementById('pendingOutbound');

            if (totalValueEl) totalValueEl.textContent = data.totalValue || '-';
            if (valueDeltaLabelEl) valueDeltaLabelEl.textContent = data.valueDeltaLabel || '';
            if (lowStockCountEl) lowStockCountEl.textContent = data.lowStockCount || 0;
            if (pendingInboundEl) pendingInboundEl.textContent = data.pendingInbound || 0;
            if (pendingOutboundEl) pendingOutboundEl.textContent = data.pendingOutbound || 0;
        })
        .catch(err => {
            console.error('Failed to load inventory summary', err);
            showError('Không thể tải tổng quan kho');
        });
}

function loadInventoryMovements() {
    const range = document.getElementById('rangeSelect')?.value || 'week';
    const params = new URLSearchParams({ range });

    fetch(`/api/manager/import-export/movements?${params}`)
        .then(res => {
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            return res.json();
        })
        .then(data => {
            renderInOutChart(data);
        })
        .catch(err => {
            console.error('Failed to load inventory movements', err);
            showError('Không thể tải dữ liệu biến động nhập/xuất');
        });
}

function renderInOutChart(data) {
    const ctx = document.getElementById('inOutChart');
    if (!ctx) return;

    if (inOutChart) {
        inOutChart.destroy();
    }

    const chartConfig = {
        type: chartType,
        data: {
            labels: data.labels || [],
            datasets: [
                {
                    label: 'Nhập kho',
                    data: data.imports || [],
                    backgroundColor: chartType === 'bar' ? 'rgba(59, 130, 246, 0.8)' : 'rgba(59, 130, 246, 0.2)',
                    borderColor: 'rgba(59, 130, 246, 1)',
                    borderWidth: 2,
                    tension: 0.4,
                    fill: chartType === 'line'
                },
                {
                    label: 'Xuất kho',
                    data: data.exports || [],
                    backgroundColor: chartType === 'bar' ? 'rgba(239, 68, 68, 0.8)' : 'rgba(239, 68, 68, 0.2)',
                    borderColor: 'rgba(239, 68, 68, 1)',
                    borderWidth: 2,
                    tension: 0.4,
                    fill: chartType === 'line'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            aspectRatio: 2.5,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        boxWidth: 12,
                        padding: 15,
                        font: { size: 13 }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            label += formatCurrency(context.parsed.y);
                            return label;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return formatCurrency(value);
                        }
                    }
                }
            }
        }
    };

    inOutChart = new Chart(ctx, chartConfig);
}

function loadCategoryDistribution() {
    fetch('/api/manager/import-export/categories')
        .then(res => {
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            return res.json();
        })
        .then(data => {
            renderCategoryDonut(data);
        })
        .catch(err => {
            console.error('Failed to load category distribution', err);
        });
}

function renderCategoryDonut(data) {
    const ctx = document.getElementById('categoryDonut');
    if (!ctx) return;

    if (categoryDonutChart) {
        categoryDonutChart.destroy();
    }

    const colors = [
        '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
        '#EC4899', '#14B8A6', '#F97316', '#6366F1', '#84CC16'
    ];

    categoryDonutChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: data.map(d => d.name),
            datasets: [{
                data: data.map(d => d.value),
                backgroundColor: colors.slice(0, data.length),
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            aspectRatio: 1.5,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = formatCurrency(context.parsed);
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((context.parsed / total) * 100).toFixed(1);
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });

    // Render custom legend
    const legendEl = document.getElementById('donutLegend');
    if (legendEl && data.length > 0) {
        const total = data.reduce((sum, d) => sum + d.value, 0);
        legendEl.innerHTML = data.map((d, i) => {
            const percentage = ((d.value / total) * 100).toFixed(1);
            return `
                <li>
                    <span style="background: ${colors[i]}"></span>
                    <span>${d.name}: ${formatCurrency(d.value)} (${percentage}%)</span>
                </li>
            `;
        }).join('');
    }
}

function loadRecentActivities() {
    fetch('/api/manager/import-export/activities?limit=50')
        .then(res => {
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            return res.json();
        })
        .then(data => {
            allActivities = data;
            filterActivities(currentActivityFilter);
        })
        .catch(err => {
            console.error('Failed to load recent activities', err);
        });
}

function filterActivities(type) {
    currentActivityFilter = type;

    // Update active button
    document.querySelectorAll('.filter-btn').forEach(btn => {
        if (btn.getAttribute('data-type') === type) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });

    // Filter activities
    let filtered = allActivities;
    if (type === 'import') {
        filtered = allActivities.filter(act => act.typeClass === 'import');
    } else if (type === 'export') {
        filtered = allActivities.filter(act => act.typeClass === 'export');
    }

    renderActivities(filtered);
}

function renderActivities(activities) {
    const tbody = document.getElementById('recentActivities');
    if (!tbody) return;

    if (activities.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="muted">Không có hoạt động nào</td></tr>';
        return;
    }

    tbody.innerHTML = activities.map(act => `
        <tr>
            <td><a href="javascript:void(0)" onclick="viewRequestDetail(${act.id})" class="mono link">${act.code}</a></td>
            <td><span class="badge badge-${act.typeClass}">${act.type}</span></td>
            <td>${act.creator || '-'}</td>
            <td>${act.totalQty || 0}</td>
            <td><span class="status status-${act.statusClass}">${act.statusLabel}</span></td>
            <td>${act.timeAgo}</td>
            <td>
                <button class="btn btn-small" onclick="viewRequestDetail(${act.id})">Xem</button>
            </td>
        </tr>
    `).join('');
}


function viewRequestDetail(requestId) {
    if (!requestId) return;

    const modal = document.getElementById('requestDetailModal');
    const content = document.getElementById('requestDetailContent');

    if (!modal || !content) return;

    // Show modal
    modal.classList.add('active');

    // Show loading
    content.innerHTML = `
        <div class="loading-spinner">
            <div class="spinner"></div>
            <p>Đang tải chi tiết...</p>
        </div>
    `;

    // Fetch details
    fetch(`/api/manager/import-export/request/${requestId}`)
        .then(res => {
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            return res.json();
        })
        .then(data => {
            renderRequestDetail(data);
        })
        .catch(err => {
            console.error('Failed to load request detail', err);
            content.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #EF4444;">
                    <p>Không thể tải chi tiết yêu cầu</p>
                    <button class="btn btn-ghost" onclick="closeRequestModal()">Đóng</button>
                </div>
            `;
        });
}

function renderRequestDetail(data) {
    const content = document.getElementById('requestDetailContent');
    if (!content) return;

    content.innerHTML = `
        <div class="detail-grid">
            <div class="detail-item">
                <div class="detail-label">Mã đơn</div>
                <div class="detail-value mono">${data.code}</div>
            </div>
            <div class="detail-item">
                <div class="detail-label">Loại</div>
                <div class="detail-value">${data.type}</div>
            </div>
            <div class="detail-item">
                <div class="detail-label">Chi nhánh</div>
                <div class="detail-value">${data.branchName}</div>
            </div>
            <div class="detail-item">
                <div class="detail-label">Trạng thái</div>
                <div class="detail-value">${data.status}</div>
            </div>
            <div class="detail-item">
                <div class="detail-label">Tổng số lượng</div>
                <div class="detail-value">${data.totalQty || 0} sản phẩm</div>
            </div>
            <div class="detail-item">
                <div class="detail-label">Ngày tạo</div>
                <div class="detail-value">${data.createdAt}</div>
            </div>
        </div>
        
        ${data.note ? `
            <div class="detail-item" style="margin-bottom: 16px;">
                <div class="detail-label">Ghi chú</div>
                <div class="detail-value">${data.note}</div>
            </div>
        ` : ''}
        
        <h4 style="margin: 24px 0 12px; font-size: 16px; font-weight: 600;">Chi tiết sản phẩm</h4>
        <table class="detail-table">
            <thead>
                <tr>
                    <th>Tên thuốc</th>
                    <th>Dạng bào chế</th>
                    <th>Số lượng</th>
                    <th>Đơn vị</th>
                </tr>
            </thead>
            <tbody>
                ${data.details && data.details.length > 0 ? data.details.map(d => `
                    <tr>
                        <td>${d.medicineName || '-'}</td>
                        <td>${d.variantName || '-'}</td>
                        <td>${d.quantity || 0}</td>
                        <td>${d.unit || '-'}</td>
                    </tr>
                `).join('') : '<tr><td colspan="4" style="text-align: center; color: #9CA3AF;">Không có sản phẩm</td></tr>'}
            </tbody>
        </table>
    `;
}

function closeRequestModal() {
    const modal = document.getElementById('requestDetailModal');
    if (modal) {
        modal.classList.remove('active');
    }
}

// Close modal when clicking outside
document.addEventListener('click', (e) => {
    const modal = document.getElementById('requestDetailModal');
    if (modal && e.target === modal) {
        closeRequestModal();
    }
});

// Helper function to format currency
function formatCurrency(value) {
    if (!value || value === 0) return '0 đ';

    const num = typeof value === 'string' ? parseFloat(value) : value;

    if (num >= 1000000000) {
        return (num / 1000000000).toFixed(1) + ' Tỷ';
    } else if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + ' Triệu';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(0) + ' K';
    }

    return new Intl.NumberFormat('vi-VN').format(num) + ' đ';
}

function showError(message) {
    // Simple error notification
    console.error(message);
    // You can implement a toast notification here
}

