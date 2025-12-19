// Manager Adjustments & Expired Returns JavaScript
let adjustmentChart;
let chartType = 'bar';
let allActivities = [];
let filteredActivities = [];
let currentActivityFilter = 'all';
let currentPage = 1;

document.addEventListener('DOMContentLoaded', () => {
    loadAdjustmentSummary();
    loadAdjustmentMovements();
    loadRecentActivities();

    // Chart type toggle
    const chartBarBtn = document.getElementById('chartBarBtn');
    const chartLineBtn = document.getElementById('chartLineBtn');

    if (chartBarBtn) {
        chartBarBtn.addEventListener('click', () => {
            chartType = 'bar';
            chartBarBtn.classList.add('active');
            chartLineBtn?.classList.remove('active');
            loadAdjustmentMovements();
        });
        chartBarBtn.classList.add('active');
    }

    if (chartLineBtn) {
        chartLineBtn.addEventListener('click', () => {
            chartType = 'line';
            chartLineBtn.classList.add('active');
            chartBarBtn?.classList.remove('active');
            loadAdjustmentMovements();
        });
    }

    // Range change handler - auto reload
    const rangeSelect = document.getElementById('rangeSelect');
    if (rangeSelect) {
        rangeSelect.addEventListener('change', () => {
            loadAdjustmentMovements();
        });
    }

    // Type filter change handler
    const typeFilter = document.getElementById('typeFilter');
    if (typeFilter) {
        typeFilter.addEventListener('change', () => {
            currentActivityFilter = typeFilter.value;
            loadRecentActivities();
        });
    }

    // Modal close
    const modalClose = document.getElementById('modalClose');
    const detailModal = document.getElementById('detailModal');
    if (modalClose && detailModal) {
        modalClose.addEventListener('click', () => {
            detailModal.classList.add('hidden');
        });
        detailModal.addEventListener('click', (e) => {
            if (e.target === detailModal) {
                detailModal.classList.add('hidden');
            }
        });
    }

    // Page length selector
    const recordsPerPageSelect = document.getElementById('recordsPerPage');
    if (recordsPerPageSelect) {
        recordsPerPageSelect.addEventListener('change', () => {
            currentPage = 1; // Reset to first page
            renderActivitiesPage();
        });
    }

    // Pagination now handled by dynamic buttons in updatePaginationControls()
});

function loadAdjustmentSummary() {
    fetch('/api/manager/adjustments/summary')
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            console.log('Summary data received:', data);
            // Calculate metrics according to requirements
            // Total Loss = Expired Returns + Shortage (all positive)
            const totalLoss = Math.abs(data.totalExpiredValue || 0) + Math.abs(data.totalShortageValue || 0);

            // Total Offset = Surplus (shown as negative/green)
            const totalOffset = Math.abs(data.totalSurplusValue || 0);

            // Net Loss = Total Loss - Total Offset
            const netLoss = totalLoss - totalOffset;

            const totalLossEl = document.getElementById('totalLoss');
            const totalOffsetEl = document.getElementById('totalOffset');
            const netLossEl = document.getElementById('netLoss');

            if (totalLossEl) {
                totalLossEl.textContent = formatCurrency(totalLoss);
            }
            if (totalOffsetEl) {
                totalOffsetEl.textContent = formatCurrency(totalOffset);
            }
            if (netLossEl) {
                netLossEl.textContent = formatCurrency(netLoss);
            }

            console.log('Summary updated - Total Loss:', totalLoss, 'Total Offset:', totalOffset, 'Net Loss:', netLoss);
        })
        .catch(err => {
            console.error('Failed to load adjustment summary', err);
            showError('Không thể tải tổng quan');
        });
}

function loadAdjustmentMovements() {
    const range = document.getElementById('rangeSelect')?.value || 'week';
    const type = document.getElementById('typeFilter')?.value || 'all';
    const params = new URLSearchParams({ range, type });

    fetch(`/api/manager/adjustments/movements?${params}`)
        .then(res => {
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            return res.json();
        })
        .then(data => {
            renderAdjustmentChart(data);
        })
        .catch(err => {
            console.error('Failed to load adjustment movements', err);
            showError('Không thể tải dữ liệu biến động');
        });
}

function renderAdjustmentChart(data) {
    console.log('Rendering chart with data:', data);
    const ctx = document.getElementById('adjustmentChart');
    if (!ctx) {
        console.error('Chart canvas not found!');
        return;
    }

    if (adjustmentChart) {
        adjustmentChart.destroy();
    }

    console.log('Chart.js loaded:', typeof Chart !== 'undefined');
    console.log('Chart type:', chartType);
    console.log('Labels:', data.labels);
    console.log('Adjustments:', data.adjustments);
    console.log('Expired Returns:', data.expiredReturns);

    const chartConfig = {
        type: chartType,
        data: {
            labels: data.labels || [],
            datasets: [
                {
                    label: 'Kiểm kho',
                    data: data.adjustments || [],
                    backgroundColor: chartType === 'bar' ? 'rgba(59, 130, 246, 0.8)' : 'rgba(59, 130, 246, 0.2)',
                    borderColor: 'rgba(59, 130, 246, 1)',
                    borderWidth: 2,
                    tension: 0.4,
                    fill: chartType === 'line'
                },
                {
                    label: 'Trả hàng hết hạn',
                    data: data.expiredReturns || [],
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
            maintainAspectRatio: false,
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

    adjustmentChart = new Chart(ctx, chartConfig);
}

function loadRecentActivities() {
    const type = currentActivityFilter;
    const params = new URLSearchParams({ limit: 100, type });

    fetch(`/api/manager/adjustments/activities?${params}`)
        .then(res => {
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            return res.json();
        })
        .then(data => {
            allActivities = data;
            filteredActivities = data;
            currentPage = 1;
            renderActivitiesPage();
        })
        .catch(err => {
            console.error('Failed to load activities', err);
            showError('Không thể tải hoạt động gần đây');
        });
}

function renderActivitiesPage() {
    const recordsPerPageSelect = document.getElementById('recordsPerPage');
    const recordsPerPage = recordsPerPageSelect ? parseInt(recordsPerPageSelect.value, 10) : 10;
    const tbody = document.getElementById('activitiesTableBody');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (!filteredActivities || filteredActivities.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Không có dữ liệu</td></tr>';
        updatePaginationControls();
        return;
    }

    const startIndex = (currentPage - 1) * recordsPerPage;
    const endIndex = startIndex + recordsPerPage;
    const pageData = filteredActivities.slice(startIndex, endIndex);

    pageData.forEach(activity => {
        const tr = document.createElement('tr');
        const typeDisplay = getTypeDisplayName(activity.type);
        const badgeClass = getTypeBadgeClass(activity.type);

        // Determine status based on activity data
        let statusHtml = '';
        let valueHtml = '';
        const value = activity.totalValue || 0;

        if (activity.adjustmentType === 'SURPLUS') {
            // Hàng thừa - màu xanh lá, mũi tên lên, hiển thị âm
            statusHtml = '<span class="status-badge status-surplus">↑ Thừa</span>';
            valueHtml = `<span class="value-surplus">-${formatCurrency(Math.abs(value))}</span>`;
        } else if (activity.adjustmentType === 'SHORTAGE' || activity.type === 'BR_TO_WARE2') {
            // Hàng thiếu hoặc hết hạn - màu đỏ, mũi tên xuống, hiển thị dương
            const label = activity.type === 'BR_TO_WARE2' ? 'Hết hạn' : 'Thiếu';
            statusHtml = `<span class="status-badge status-shortage">↓ ${label}</span>`;
            valueHtml = `<span class="value-shortage">+${formatCurrency(Math.abs(value))}</span>`;
        } else if (activity.adjustmentType === 'MIXED') {
            // Mixed adjustment - both surplus and shortage in same movement
            statusHtml = '<span class="status-badge status-mixed">⇅ Điều chỉnh</span>';
            valueHtml = escapeHtml(activity.totalValueFormatted || formatCurrency(Math.abs(value)));
        } else {
            // Default case
            statusHtml = '<span class="status-badge">-</span>';
            valueHtml = escapeHtml(activity.totalValueFormatted || '-');
        }

        tr.innerHTML = `
            <td>${escapeHtml(activity.code)}</td>
            <td><span class="badge ${badgeClass}">${escapeHtml(typeDisplay)}</span></td>
            <td>${statusHtml}</td>
            <td>${valueHtml}</td>
            <td>${escapeHtml(activity.timeAgo || '-')}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-primary" onclick="viewDetail(${activity.id})">Xem</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    updatePaginationControls();
}

function getTypeDisplayName(type) {
    const typeMap = {
        'INVENTORY_ADJUSTMENT': 'Kiểm kho',
        'BR_TO_WARE2': 'Trả hàng hết hạn'
    };
    return typeMap[type] || type;
}

function getTypeBadgeClass(type) {
    const classMap = {
        'INVENTORY_ADJUSTMENT': 'adjustment',
        'BR_TO_WARE2': 'expired-return'
    };
    return classMap[type] || '';
}

function updatePaginationControls() {
    const recordsPerPageSelect = document.getElementById('recordsPerPage');
    const recordsPerPage = recordsPerPageSelect ? parseInt(recordsPerPageSelect.value, 10) : 10;
    const totalRecords = filteredActivities.length;
    const totalPages = Math.ceil(totalRecords / recordsPerPage) || 1;

    if (currentPage > totalPages) {
        currentPage = totalPages > 0 ? totalPages : 1;
    }

    // Update info display
    document.getElementById('totalItems').textContent = totalRecords;
    document.getElementById('showingFrom').textContent = totalRecords > 0 ? (currentPage - 1) * recordsPerPage + 1 : 0;
    document.getElementById('showingTo').textContent = Math.min(currentPage * recordsPerPage, totalRecords);

    // Render pagination buttons
    const paginationButtons = document.getElementById('paginationButtons');
    if (!paginationButtons) return;

    paginationButtons.innerHTML = '';

    if (totalPages <= 1) return;

    // First button
    const firstBtn = document.createElement('button');
    firstBtn.innerHTML = '&laquo;&laquo;';
    firstBtn.className = 'pagination-btn' + (currentPage === 1 ? ' disabled' : '');
    firstBtn.disabled = currentPage === 1;
    firstBtn.title = 'Trang đầu';
    firstBtn.onclick = () => { if (currentPage > 1) { currentPage = 1; renderActivitiesPage(); } };
    paginationButtons.appendChild(firstBtn);

    // Previous button
    const prevBtn = document.createElement('button');
    prevBtn.innerHTML = '&laquo;';
    prevBtn.className = 'pagination-btn' + (currentPage === 1 ? ' disabled' : '');
    prevBtn.disabled = currentPage === 1;
    prevBtn.title = 'Trang trước';
    prevBtn.onclick = () => { if (currentPage > 1) { currentPage--; renderActivitiesPage(); } };
    paginationButtons.appendChild(prevBtn);

    // Page number buttons (max 5 visible)
    const maxButtons = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxButtons / 2));
    let endPage = Math.min(totalPages, startPage + maxButtons - 1);

    if (endPage - startPage < maxButtons - 1) {
        startPage = Math.max(1, endPage - maxButtons + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.textContent = i;
        pageBtn.className = 'pagination-btn' + (i === currentPage ? ' active' : '');
        pageBtn.onclick = () => { currentPage = i; renderActivitiesPage(); };
        paginationButtons.appendChild(pageBtn);
    }

    // Next button
    const nextBtn = document.createElement('button');
    nextBtn.innerHTML = '&raquo;';
    nextBtn.className = 'pagination-btn' + (currentPage === totalPages ? ' disabled' : '');
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.title = 'Trang sau';
    nextBtn.onclick = () => { if (currentPage < totalPages) { currentPage++; renderActivitiesPage(); } };
    paginationButtons.appendChild(nextBtn);

    // Last button
    const lastBtn = document.createElement('button');
    lastBtn.innerHTML = '&raquo;&raquo;';
    lastBtn.className = 'pagination-btn' + (currentPage === totalPages ? ' disabled' : '');
    lastBtn.disabled = currentPage === totalPages;
    lastBtn.title = 'Trang cuối';
    lastBtn.onclick = () => { if (currentPage < totalPages) { currentPage = totalPages; renderActivitiesPage(); } };
    paginationButtons.appendChild(lastBtn);
}

window.viewDetail = function(id) {
    fetch(`/api/manager/adjustments/detail/${id}`)
        .then(res => {
            if (!res.ok) throw new Error('Không thể tải chi tiết');
            return res.json();
        })
        .then(data => {
            showDetailModal(data);
        })
        .catch(err => {
            console.error('Failed to load detail', err);
            showError('Không thể tải chi tiết');
        });
};

function showDetailModal(data) {
    const modal = document.getElementById('detailModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalBody = document.getElementById('modalBody');

    if (!modal || !modalBody) return;

    modalTitle.textContent = `Chi tiết ${data.code || ''}`;

    const typeDisplay = getTypeDisplayName(data.type);
    const badgeClass = getTypeBadgeClass(data.type);

    let detailsHtml = '<div class="detail-info">';
    detailsHtml += `<div class="detail-row"><strong>Mã:</strong> ${escapeHtml(data.code || '-')}</div>`;
    detailsHtml += `<div class="detail-row"><strong>Loại:</strong> <span class="badge ${badgeClass}">${escapeHtml(typeDisplay)}</span></div>`;
    detailsHtml += `<div class="detail-row"><strong>Chi nhánh:</strong> ${escapeHtml(data.branchName || '-')}</div>`;
    detailsHtml += `<div class="detail-row"><strong>Tổng giá trị:</strong> ${escapeHtml(data.totalValueFormatted || '-')}</div>`;
    detailsHtml += `<div class="detail-row"><strong>Tổng số lượng:</strong> ${data.totalQty || 0}</div>`;
    detailsHtml += '</div>';

    if (data.details && data.details.length > 0) {
        detailsHtml += '<h4 style="margin-top: 20px;">Chi tiết sản phẩm</h4>';
        detailsHtml += '<table class="table" style="margin-top: 10px;">';
        detailsHtml += '<thead><tr><th>Thuốc</th><th>Biến thể</th><th>Lô</th><th>SL</th><th>Đơn giá</th><th>Thành tiền</th></tr></thead>';
        detailsHtml += '<tbody>';
        data.details.forEach(item => {
            detailsHtml += '<tr>';
            detailsHtml += `<td>${escapeHtml(item.medicineName || '-')}</td>`;
            detailsHtml += `<td>${escapeHtml(item.variantName || '-')}</td>`;
            detailsHtml += `<td>${escapeHtml(item.batchCode || '-')}</td>`;
            detailsHtml += `<td>${item.quantity || 0}</td>`;
            detailsHtml += `<td>${formatCurrency(item.price || 0)}</td>`;
            detailsHtml += `<td>${formatCurrency(item.subtotal || 0)}</td>`;
            detailsHtml += '</tr>';
        });
        detailsHtml += '</tbody></table>';
    }

    modalBody.innerHTML = detailsHtml;
    modal.classList.remove('hidden');
}

function formatCurrency(value) {
    if (value == null || isNaN(value)) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0
    }).format(value);
}

function escapeHtml(s) {
    if (!s) return '';
    return String(s)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

function showError(message) {
    const toast = document.getElementById('toast');
    if (toast) {
        toast.textContent = message;
        toast.classList.remove('hidden');
        toast.classList.add('error');
        setTimeout(() => {
            toast.classList.add('hidden');
            toast.classList.remove('error');
        }, 3000);
    } else {
        // Use global toast system as fallback
        if (window.showToast) {
            window.showToast(message, 'error');
        } else {
            console.error('Toast not available:', message);
        }
    }
}

