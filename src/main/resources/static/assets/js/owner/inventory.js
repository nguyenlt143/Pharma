// Owner Inventory Dashboard JavaScript
let inOutChart;
let categoryChart;
let categoryDonutChart;
let chartType = 'bar';

document.addEventListener('DOMContentLoaded', () => {
    loadBranches();
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
        // Set initial active state
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
    
    // Filter change handlers
    const branchSelect = document.getElementById('branchSelect');
    const rangeSelect = document.getElementById('rangeSelect');
    const categorySelect = document.getElementById('categorySelect');
    
    if (branchSelect) {
        branchSelect.addEventListener('change', loadInventoryData);
    }
    if (rangeSelect) {
        rangeSelect.addEventListener('change', loadInventoryData);
    }
    if (categorySelect) {
        categorySelect.addEventListener('change', loadInventoryData);
    }
    
});

function loadBranches() {
    fetch('/api/owner/branches')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('branchSelect');
            if (select && Array.isArray(data)) {
                // Keep existing selected option
                const currentValue = select.value;
                const options = '<option value="">Tất cả</option>' +
                    data.map(b => {
                        const selected = b.id.toString() === currentValue ? ' selected' : '';
                        return `<option value="${b.id}"${selected}>${b.name || 'Chi nhánh #' + b.id}</option>`;
                    }).join('');
                select.innerHTML = options;
            }
        })
        .catch(err => {
            console.warn('Không thể tải danh sách chi nhánh:', err);
        });
}

function loadInventorySummary() {
    const branchId = document.getElementById('branchSelect')?.value || '';
    const params = branchId ? `?branchId=${branchId}` : '';
    
    fetch(`/api/owner/inventory/summary${params}`)
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
            // Show error to user
            const totalValueEl = document.getElementById('totalValue');
            if (totalValueEl) totalValueEl.textContent = 'Lỗi tải dữ liệu';
        });
}

function loadInventoryMovements() {
    const branchId = document.getElementById('branchSelect')?.value || '';
    const range = document.getElementById('rangeSelect')?.value || 'week';
    const params = new URLSearchParams();
    if (branchId) params.append('branchId', branchId);
    params.append('range', range);
    
    fetch(`/api/owner/inventory/movements?${params}`)
        .then(res => res.json())
        .then(data => {
            updateInOutChart(data, chartType);
        })
        .catch(err => {
            console.error('Failed to load inventory movements', err);
        });
}

function loadCategoryDistribution() {
    const branchId = document.getElementById('branchSelect')?.value || '';
    const categoryId = document.getElementById('categorySelect')?.value || '';

    const params = new URLSearchParams();
    if (branchId) params.append('branchId', branchId);
    if (categoryId) params.append('categoryId', categoryId);

    const query = params.toString() ? `?${params.toString()}` : '';

    fetch(`/api/owner/inventory/categories${query}`)
        .then(res => res.json())
        .then(data => {
            updateCategoryChart(data);
            updateCategoryDonut(data);
        })
        .catch(err => {
            console.error('Failed to load category distribution', err);
        });
}

function loadRecentActivities() {
    const branchId = document.getElementById('branchSelect')?.value || '';
    const params = branchId ? `?branchId=${branchId}&limit=5` : '?limit=5';
    
    fetch(`/api/owner/inventory/activities${params}`)
        .then(res => res.json())
        .then(data => {
            updateRecentActivities(data);
        })
        .catch(err => {
            console.error('Failed to load recent activities', err);
        });
}

function loadInventoryData() {
    loadInventorySummary();
    loadInventoryMovements();
    loadCategoryDistribution();
    loadRecentActivities();
}

function updateInOutChart(data, type) {
    const ctx = document.getElementById('inOutChart');
    if (!ctx) return;
    
    if (inOutChart) {
        inOutChart.destroy();
    }
    
    const labels = data.labels || [];
    const imports = data.imports || [];
    const exports = data.exports || [];
    
    inOutChart = new Chart(ctx, {
        type: type,
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Nhập kho',
                    data: imports,
                    backgroundColor: 'rgba(37, 99, 235, 0.5)',
                    borderColor: '#2563EB',
                    borderWidth: 2
                },
                {
                    label: 'Xuất kho',
                    data: exports,
                    backgroundColor: 'rgba(251, 146, 60, 0.5)',
                    borderColor: '#FB923C',
                    borderWidth: 2
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function updateCategoryChart(data) {
    const ctx = document.getElementById('categoryChart');
    
    if (!ctx) return;
    
    if (categoryChart) {
        categoryChart.destroy();
    }
    
    if (!data || data.length === 0) {
        // Create empty chart with no data
        categoryChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: [],
                datasets: [{
                    label: 'Số lượng sản phẩm',
                    data: [],
                    backgroundColor: '#2563EB',
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
        return;
    }
    
    const labels = data.map(item => item.name || '');
    const itemCounts = data.map(item => item.itemCount || 0);
    
    categoryChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Số lượng sản phẩm',
                data: itemCounts,
                backgroundColor: '#2563EB',
                borderRadius: 6
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
                    callbacks: {
                        label: function(context) {
                            return 'Số lượng: ' + new Intl.NumberFormat('vi-VN').format(context.parsed.y);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return new Intl.NumberFormat('vi-VN').format(value);
                        }
                    }
                }
            }
        }
    });
}

function updateCategoryDonut(data) {
    const ctx = document.getElementById('categoryDonut');
    
    if (!ctx) return;
    
    if (categoryDonutChart) {
        categoryDonutChart.destroy();
    }
    
    if (!data || data.length === 0) {
        // Create empty chart with no data
        categoryDonutChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: [],
                datasets: [{
                    data: [],
                    backgroundColor: []
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
        // Clear legend
        const legendEl = document.getElementById('donutLegend');
        if (legendEl) {
            legendEl.innerHTML = '';
        }
        return;
    }
    
    const labels = data.map(item => item.name || '');
    const values = data.map(item => item.value || 0);
    const colors = ['#4CAF50', '#2196F3', '#FF9800', '#9C27B0', '#E91E63', '#009688', '#2563EB', '#10B981', '#F59E0B', '#EF4444'];
    
    categoryDonutChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: colors.slice(0, labels.length)
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
                    callbacks: {
                        label: function(context) {
                            const value = context.parsed;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percent = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                            return context.label + ': ' + new Intl.NumberFormat('vi-VN').format(value) + ' đ (' + percent + '%)';
                        }
                    }
                }
            }
        }
    });
    
    // Update legend
    const legendEl = document.getElementById('donutLegend');
    if (legendEl) {
        legendEl.innerHTML = data.map((item, index) => {
            const total = values.reduce((a, b) => a + b, 0);
            const percent = total > 0 ? ((item.value / total) * 100).toFixed(1) : 0;
            return `
            <li>
                <button class="legend-btn">
                    <span class="swatch" style="background-color: ${colors[index % colors.length]}"></span>
                    <span>${item.name || ''}</span>
                    <span style="margin-left: auto; color: #6B7280; font-size: 12px;">${percent}%</span>
                </button>
            </li>
        `;
        }).join('');
    }
}

function updateRecentActivities(activities) {
    const tbody = document.getElementById('recentActivities');
    if (!tbody) return;
    
    if (!activities || activities.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="muted">Không có hoạt động gần đây</td></tr>';
        return;
    }
    
    tbody.innerHTML = activities.map(act => {
        const url = act.detailUrl || '';
        const idMatch = url.match(/\/(\d+)$/);
        const id = idMatch ? idMatch[1] : null;
        let onClick = '';

        if (id) {
            if (url.includes('/owner/request/')) {
                onClick = `onclick="viewRequestDetail(${id}); return false;"`;
            } else if (url.includes('/owner/movement/')) {
                onClick = `onclick="viewMovementDetail(${id}); return false;"`;
            }
        }

        return `
        <tr>
            <td><span class="mono">${act.code || ''}</span></td>
            <td><span class="badge ${(act.type || '').toLowerCase().replace(/\s+/g, '')}">${act.type || ''}</span></td>
            <td>${act.branch || '-'}</td>
            <td>${act.creator || '-'}</td>
            <td>${act.totalQty || 0}</td>
            <td><span class="status ${(act.status || '').toLowerCase()}">${act.status || ''}</span></td>
            <td>${act.timeAgo || ''}</td>
            <td>
                <button class="btn small" ${onClick}>Xem</button>
            </td>
        </tr>
    `;
    }).join('');
}

function viewRequestDetail(id) {
    const modal = document.getElementById('requestDetailModal');
    const content = document.getElementById('requestDetailContent');
    
    if (!modal || !content) return;
    
    // Show modal with loading
    content.innerHTML = '<div style="text-align: center; color: #6B7280; padding: 40px;">Đang tải...</div>';
    modal.style.display = 'flex';
    
    // Fetch request detail
    fetch(`/api/owner/inventory/request/${id}`)
        .then(res => {
            if (!res.ok) throw new Error('Không thể tải chi tiết');
            return res.json();
        })
        .then(data => {
            const statusClass = (data.status || '').toLowerCase();
            const typeClass = (data.type || '').toLowerCase().replace(/\s+/g, '');
            
            content.innerHTML = `
                <div style="display: grid; gap: 20px;">
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Mã yêu cầu</div>
                            <div style="font-size: 16px; font-weight: 600; color: #111827;">${data.code || '-'}</div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Loại</div>
                            <div><span class="badge ${typeClass}">${data.type || '-'}</span></div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Chi nhánh</div>
                            <div style="font-size: 16px; font-weight: 500; color: #111827;">${data.branchName || '-'}</div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Trạng thái</div>
                            <div><span class="status ${statusClass}">${data.status || '-'}</span></div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Ngày tạo</div>
                            <div style="font-size: 14px; color: #111827;">${data.createdAt || '-'}</div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Tổng số lượng</div>
                            <div style="font-size: 16px; font-weight: 600; color: #111827;">${data.totalQty || 0}</div>
                        </div>
                    </div>
                    ${data.note ? `
                    <div>
                        <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Ghi chú</div>
                        <div style="font-size: 14px; color: #111827; padding: 12px; background: #F9FAFB; border-radius: 8px;">${data.note}</div>
                    </div>
                    ` : ''}
                    <div>
                        <div style="font-size: 14px; font-weight: 600; color: #111827; margin-bottom: 12px;">Danh sách thuốc</div>
                        <table style="width: 100%; border-collapse: collapse;">
                            <thead>
                                <tr style="background: #F9FAFB; border-bottom: 1px solid #E5E7EB;">
                                    <th style="padding: 12px; text-align: left; font-size: 12px; font-weight: 600; color: #6B7280;">Tên thuốc</th>
                                    <th style="padding: 12px; text-align: left; font-size: 12px; font-weight: 600; color: #6B7280;">Biến thể</th>
                                    <th style="padding: 12px; text-align: right; font-size: 12px; font-weight: 600; color: #6B7280;">Số lượng</th>
                                    <th style="padding: 12px; text-align: left; font-size: 12px; font-weight: 600; color: #6B7280;">Đơn vị</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${(data.details || []).map(d => `
                                    <tr style="border-bottom: 1px solid #F3F4F6;">
                                        <td style="padding: 12px; font-size: 14px; color: #111827;">${d.medicineName || '-'}</td>
                                        <td style="padding: 12px; font-size: 14px; color: #6B7280;">${d.variantName || '-'}</td>
                                        <td style="padding: 12px; text-align: right; font-size: 14px; font-weight: 500; color: #111827;">${d.quantity || 0}</td>
                                        <td style="padding: 12px; font-size: 14px; color: #6B7280;">${d.unit || '-'}</td>
                                    </tr>
                                `).join('')}
                                ${(!data.details || data.details.length === 0) ? `
                                    <tr>
                                        <td colspan="4" style="padding: 24px; text-align: center; color: #9CA3AF;">Không có dữ liệu</td>
                                    </tr>
                                ` : ''}
                            </tbody>
                        </table>
                    </div>
                </div>
            `;
        })
        .catch(err => {
            content.innerHTML = `<div style="text-align: center; color: #EF4444; padding: 40px;">Lỗi: ${err.message}</div>`;
        });
}

function viewMovementDetail(id) {
    const modal = document.getElementById('requestDetailModal');
    const content = document.getElementById('requestDetailContent');

    if (!modal || !content) return;

    // Show modal with loading
    content.innerHTML = '<div style="text-align: center; color: #6B7280; padding: 40px;">Đang tải...</div>';
    modal.style.display = 'flex';

    fetch(`/api/owner/inventory/movement/${id}`)
        .then(res => {
            if (!res.ok) throw new Error('Không thể tải chi tiết');
            return res.json();
        })
        .then(data => {
            const statusClass = (data.status || '').toLowerCase();
            const typeClass = (data.type || '').toLowerCase().replace(/\s+/g, '');

            content.innerHTML = `
                <div style="display: grid; gap: 20px;">
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Mã phiếu</div>
                            <div style="font-size: 16px; font-weight: 600; color: #111827;">${data.code || '-'}</div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Loại</div>
                            <div><span class="badge ${typeClass}">${data.type || '-'}</span></div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Chi nhánh</div>
                            <div style="font-size: 16px; font-weight: 500; color: #111827;">${data.branchName || '-'}</div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Trạng thái</div>
                            <div><span class="status ${statusClass}">${data.status || '-'}</span></div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Ngày tạo</div>
                            <div style="font-size: 14px; color: #111827;">${data.createdAt || '-'}</div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: #6B7280; margin-bottom: 4px;">Tổng số lượng</div>
                            <div style="font-size: 16px; font-weight: 600; color: #111827;">${data.totalQty || 0}</div>
                        </div>
                    </div>
                    <div>
                        <div style="font-size: 14px; font-weight: 600; color: #111827; margin-bottom: 12px;">Danh sách thuốc</div>
                        <table style="width: 100%; border-collapse: collapse;">
                            <thead>
                                <tr style="background: #F9FAFB; border-bottom: 1px solid #E5E7EB;">
                                    <th style="padding: 12px; text-align: left; font-size: 12px; font-weight: 600; color: #6B7280;">Tên thuốc</th>
                                    <th style="padding: 12px; text-align: left; font-size: 12px; font-weight: 600; color: #6B7280;">Biến thể</th>
                                    <th style="padding: 12px; text-align: right; font-size: 12px; font-weight: 600; color: #6B7280;">Số lượng</th>
                                    <th style="padding: 12px; text-align: left; font-size: 12px; font-weight: 600; color: #6B7280;">Đơn vị</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${(data.details || []).map(d => `
                                    <tr style="border-bottom: 1px solid #F3F4F6;">
                                        <td style="padding: 12px; font-size: 14px; color: #111827;">${d.medicineName || '-'}</td>
                                        <td style="padding: 12px; font-size: 14px; color: #6B7280;">${d.variantName || '-'}</td>
                                        <td style="padding: 12px; text-align: right; font-size: 14px; font-weight: 500; color: #111827;">${d.quantity || 0}</td>
                                        <td style="padding: 12px; font-size: 14px; color: #6B7280;">${d.unit || '-'}</td>
                                    </tr>
                                `).join('')}
                                ${(!data.details || data.details.length === 0) ? `
                                    <tr>
                                        <td colspan="4" style="padding: 24px; text-align: center; color: #9CA3AF;">Không có dữ liệu</td>
                                    </tr>
                                ` : ''}
                            </tbody>
                        </table>
                    </div>
                </div>
            `;
        })
        .catch(err => {
            content.innerHTML = `<div style="text-align: center; color: #EF4444; padding: 40px;">Lỗi: ${err.message}</div>`;
        });
}

function closeRequestModal() {
    const modal = document.getElementById('requestDetailModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Close modal when clicking outside
document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('requestDetailModal');
    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeRequestModal();
            }
        });
    }
});

function resetFilters() {
    const form = document.getElementById('filterForm');
    if (!form) return;
    form.reset();
    loadInventoryData();
}

