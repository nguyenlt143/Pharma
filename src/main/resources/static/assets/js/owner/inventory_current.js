// Owner - Current Inventory (Kho hiện tại)
document.addEventListener('DOMContentLoaded', () => {

    const API_BASE = '/api/owner/inventory/current';

    let allInventoryData = [];
    let filteredInventoryData = [];
    let currentPage = 1;
    let recordsPerPage = 25;

    const formatCurrency = (amount) => {
        if (!amount) return '0đ';
        return new Intl.NumberFormat('vi-VN').format(Math.round(amount)) + 'đ';
    };

    const formatNumber = (num) => {
        if (!num) return '0';
        return new Intl.NumberFormat('vi-VN').format(num);
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '-';
        try {
            const date = new Date(dateStr);
            return date.toLocaleDateString('vi-VN');
        } catch (e) {
            return dateStr;
        }
    };

    const getSelectedBranchId = () => {
        return document.getElementById('branchSelect')?.value || '';
    };

    // ===== API calls =====
    const fetchBranches = async () => {
        try {
            const res = await fetch('/api/owner/branches');
            if (!res.ok) throw new Error('Không thể tải danh sách chi nhánh');
            return await res.json();
        } catch (e) {
            console.error(e);
            return [];
        }
    };

    const fetchSummary = async (branchId) => {
        if (!branchId) return null;
        try {
            const res = await fetch(`${API_BASE}?branchId=${branchId}`);
            if (!res.ok) throw new Error('Không thể tải KPI tồn kho');
            return await res.json();
        } catch (e) {
            console.error(e);
            return null;
        }
    };

    const fetchInventoryDetails = async (branchId) => {
        if (!branchId) return [];
        try {
            const res = await fetch(`${API_BASE}/details?branchId=${branchId}`);
            if (!res.ok) throw new Error('Không thể tải chi tiết tồn kho');
            return await res.json();
        } catch (e) {
            console.error(e);
            return [];
        }
    };

    const fetchCategories = async (branchId) => {
        if (!branchId) return [];
        try {
            const res = await fetch(`${API_BASE}/categories?branchId=${branchId}`);
            if (!res.ok) throw new Error('Không thể tải danh mục');
            return await res.json();
        } catch (e) {
            console.error(e);
            return [];
        }
    };

    const fetchStatistics = async (branchId) => {
        if (!branchId) return [];
        try {
            const res = await fetch(`${API_BASE}/statistics?branchId=${branchId}`);
            if (!res.ok) throw new Error('Không thể tải thống kê');
            return await res.json();
        } catch (e) {
            console.error(e);
            return [];
        }
    };

    // ===== KPIs & table =====
    const updateKPIs = (data) => {
        const totalItemsEl = document.getElementById('totalItems');
        const lowStockEl = document.getElementById('lowStock');
        const totalValueEl = document.getElementById('totalValue');
        const nearExpiryEl = document.getElementById('nearExpiry');
        const expiredEl = document.getElementById('expired');

        if (!data) {
            if (totalItemsEl) totalItemsEl.textContent = '-';
            if (lowStockEl) lowStockEl.textContent = '-';
            if (totalValueEl) totalValueEl.textContent = '-';
            if (nearExpiryEl) nearExpiryEl.textContent = '-';
            if (expiredEl) expiredEl.textContent = '-';
            return;
        }

        if (totalItemsEl) totalItemsEl.textContent = formatNumber(data.totalItems || 0);
        if (lowStockEl) lowStockEl.textContent = formatNumber(data.lowStock || 0);
        if (totalValueEl) totalValueEl.textContent = formatCurrency(data.totalValue || 0);
        if (nearExpiryEl) nearExpiryEl.textContent = formatNumber(data.nearExpiry || 0);
        if (expiredEl) expiredEl.textContent = formatNumber(data.expired || 0);
    };

    const determineStatus = (item) => {
        if (!item.quantity || item.quantity <= 0) {
            return 'Hết hàng';
        }

        if (item.expiryDate) {
            try {
                const expiry = new Date(item.expiryDate);
                const now = new Date();
                const thirtyDaysFromNow = new Date();
                thirtyDaysFromNow.setDate(now.getDate() + 30);

                if (expiry < now) {
                    return 'Đã hết hạn';
                } else if (expiry < thirtyDaysFromNow) {
                    return 'Sắp hết hạn';
                }
            } catch (e) {
                console.error('Error parsing expiry date:', e);
            }
        }

        const minStockThreshold = item.minStock || 1000;
        if (item.quantity < minStockThreshold) {
            return 'Sắp hết hàng';
        }

        return 'Hoạt động';
    };

    const getStatusClass = (status) => {
        switch (status) {
            case 'Đã hết hạn':
            case 'Hết hàng':
                return 'badge-danger';
            case 'Sắp hết hạn':
            case 'Sắp hết hàng':
                return 'badge-warning';
            case 'Hoạt động':
                return 'badge-success';
            default:
                return 'badge-gray';
        }
    };

    const updatePaginationControls = () => {
        const pageInfo = document.getElementById('page-info');
        const prevPageBtn = document.getElementById('prev-page');
        const nextPageBtn = document.getElementById('next-page');
        const recordsPerPageSelect = document.getElementById('records-per-page');

        recordsPerPage = parseInt(recordsPerPageSelect.value, 10);
        const totalRecords = filteredInventoryData.length;
        const totalPages = Math.ceil(totalRecords / recordsPerPage) || 1;

        if (currentPage > totalPages) currentPage = totalPages;

        if (pageInfo) pageInfo.textContent = `Trang ${currentPage} / ${totalPages}`;
        if (prevPageBtn) prevPageBtn.disabled = currentPage === 1;
        if (nextPageBtn) nextPageBtn.disabled = currentPage === totalPages;
    };

    const renderTablePage = () => {
        const tbody = document.getElementById('inventoryTableBody');
        if (!tbody) return;

        updatePaginationControls();

        if (filteredInventoryData.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; padding: 20px; color: #6B7280;">Không có dữ liệu tồn kho</td></tr>';
            return;
        }

        const startIndex = (currentPage - 1) * recordsPerPage;
        const endIndex = startIndex + recordsPerPage;
        const pageData = filteredInventoryData.slice(startIndex, endIndex);

        tbody.innerHTML = pageData.map(item => {
            const status = determineStatus(item);
            const statusClass = getStatusClass(status);

            return `
                <tr>
                    <td>${item.medicineName || '-'}</td>
                    <td>${item.activeIngredient || '-'}</td>
                    <td>${item.strength || '-'}</td>
                    <td>${item.dosageForm || '-'}</td>
                    <td class="mono">${item.batchCode || '-'}</td>
                    <td>${formatNumber(item.quantity || 0)}</td>
                    <td>${item.unit || '-'}</td>
                    <td>${formatDate(item.expiryDate)}</td>
                    <td><span class="badge ${statusClass}">${status}</span></td>
                </tr>
            `;
        }).join('');
    };

    const applyFiltersAndRender = () => {
        const query = (document.getElementById('searchInventory')?.value || '').toLowerCase();
        const categoryId = document.getElementById('categoryFilter')?.value || '';
        const statusFilter = document.getElementById('statusFilter')?.value || '';

        filteredInventoryData = allInventoryData.filter(item => {
            const matchesQuery = !query ||
                (item.medicineName && item.medicineName.toLowerCase().includes(query)) ||
                (item.activeIngredient && item.activeIngredient.toLowerCase().includes(query)) ||
                (item.strength && item.strength.toLowerCase().includes(query));

            const matchesCategory = !categoryId || (item.categoryId && item.categoryId.toString() === categoryId);

            const itemStatus = determineStatus(item);
            const matchesStatus = !statusFilter ||
                (statusFilter === 'active' && itemStatus === 'Hoạt động') ||
                (statusFilter === 'near-expiry' && itemStatus === 'Sắp hết hạn') ||
                (statusFilter === 'expired' && itemStatus === 'Đã hết hạn') ||
                (statusFilter === 'low-stock' && itemStatus === 'Sắp hết hàng') ||
                (statusFilter === 'out-of-stock' && itemStatus === 'Hết hàng');

            return matchesQuery && matchesCategory && matchesStatus;
        });

        currentPage = 1;
        renderTablePage();
    };

    // ===== Charts =====
    let categoryChart = null;
    let donutChart = null;

    const initCharts = () => {
        if (typeof Chart === 'undefined') {
            console.warn('Chart.js is not loaded');
            return;
        }

        const categoryCanvas = document.getElementById('categoryChart');
        if (categoryCanvas) {
            const ctx = categoryCanvas.getContext('2d');
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
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return 'Số lượng: ' + formatNumber(context.parsed.y);
                                }
                            }
                        }
                    },
                    scales: {
                        y: { beginAtZero: true }
                    }
                }
            });
        }

        const donutCanvas = document.getElementById('categoryDonut');
        if (donutCanvas) {
            const ctx = donutCanvas.getContext('2d');
            donutChart = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: [],
                    datasets: [{
                        data: [],
                        backgroundColor: [
                            '#2563EB', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
                            '#EC4899', '#14B8A6', '#F97316', '#06B6D4', '#84CC16'
                        ],
                        borderWidth: 2,
                        borderColor: '#FFFFFF'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const value = context.parsed;
                                    const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                    const percent = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                                    return context.label + ': ' + formatCurrency(value) + ' (' + percent + '%)';
                                }
                            }
                        }
                    }
                }
            });
        }
    };

    const updateCharts = async (branchId) => {
        const emptyChart = document.getElementById('categoryChartEmpty');
        const emptyDonut = document.getElementById('donutChartEmpty');

        if (!branchId) {
            if (emptyChart) emptyChart.classList.remove('hidden');
            if (emptyDonut) emptyDonut.classList.remove('hidden');
            return;
        }

        try {
            const stats = await fetchStatistics(branchId);
            if (!stats || stats.length === 0) {
                if (emptyChart) emptyChart.classList.remove('hidden');
                if (emptyDonut) emptyDonut.classList.remove('hidden');
                return;
            }

            if (categoryChart) {
                categoryChart.data.labels = stats.map(s => s.categoryName);
                categoryChart.data.datasets[0].data = stats.map(s => s.itemCount);
                categoryChart.update();
                if (emptyChart) emptyChart.classList.add('hidden');
            }

            if (donutChart) {
                donutChart.data.labels = stats.map(s => s.categoryName);
                donutChart.data.datasets[0].data = stats.map(s => s.totalValue);
                donutChart.update();

                const legend = document.getElementById('donutLegend');
                if (legend) {
                    const total = stats.reduce((sum, s) => sum + (s.totalValue || 0), 0);
                    const colors = [
                        '#2563EB', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
                        '#EC4899', '#14B8A6', '#F97316', '#06B6D4', '#84CC16'
                    ];
                    legend.innerHTML = stats.map((s, i) => {
                        const percent = total > 0 ? ((s.totalValue / total) * 100).toFixed(1) : 0;
                        const color = colors[i % colors.length];
                        return `
                            <li>
                                <button class="legend-btn">
                                    <span class="swatch" style="background-color: ${color};"></span>
                                    <span>${s.categoryName}: ${formatCurrency(s.totalValue || 0)} (${percent}%)</span>
                                </button>
                            </li>
                        `;
                    }).join('');
                }

                if (emptyDonut) emptyDonut.classList.add('hidden');
            }

        } catch (e) {
            console.error(e);
            if (emptyChart) emptyChart.classList.remove('hidden');
            if (emptyDonut) emptyDonut.classList.remove('hidden');
        }
    };

    // ===== Load & Events =====
    const loadCategoriesForBranch = async (branchId) => {
        const categorySelect = document.getElementById('categoryFilter');
        if (!categorySelect) return;

        categorySelect.innerHTML = '<option value="">Tất cả</option>';
        if (!branchId) return;

        const categories = await fetchCategories(branchId);
        if (categories && categories.length > 0) {
            const options = categories.map(cat =>
                `<option value="${cat.id}">${cat.name}</option>`
            ).join('');
            categorySelect.innerHTML = '<option value="">Tất cả</option>' + options;
        }
    };

    const loadDataForBranch = async () => {
        const branchId = getSelectedBranchId();
        console.log('Loading data for branch:', branchId);

        if (!branchId) {
            updateKPIs(null);
            allInventoryData = [];
            filteredInventoryData = [];
            renderTablePage();
            await updateCharts(null);
            return;
        }

        const [summary, details] = await Promise.all([
            fetchSummary(branchId),
            fetchInventoryDetails(branchId)
        ]);

        console.log('Fetched summary:', summary);
        console.log('Fetched details:', details);

        updateKPIs(summary);
        allInventoryData = details || [];
        applyFiltersAndRender();
        await loadCategoriesForBranch(branchId);
        await updateCharts(branchId);
    };

    const loadBranchesToSelect = async () => {
        const select = document.getElementById('branchSelect');
        if (!select) return;

        const branches = await fetchBranches();
        console.log('Loaded branches:', branches);

        select.innerHTML = '<option value="">Chọn chi nhánh</option>' +
            branches.map(b => `<option value="${b.id}">${b.name || ('Chi nhánh #' + b.id)}</option>`).join('');

        // Auto-select first branch if available
        if (branches && branches.length > 0) {
            select.value = branches[0].id;
            // Trigger change event to load data
            setTimeout(() => {
                loadDataForBranch();
            }, 100);
        }
    };

    const setupEventListeners = () => {
        const branchSelect = document.getElementById('branchSelect');
        if (branchSelect) {
            branchSelect.addEventListener('change', () => {
                loadDataForBranch();
            });
        }

        const searchBtn = document.getElementById('searchBtn');
        if (searchBtn) {
            searchBtn.addEventListener('click', () => applyFiltersAndRender());
        }

        const searchInput = document.getElementById('searchInventory');
        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    applyFiltersAndRender();
                }
            });
        }

        const categoryFilter = document.getElementById('categoryFilter');
        if (categoryFilter) {
            categoryFilter.addEventListener('change', () => applyFiltersAndRender());
        }

        const statusFilter = document.getElementById('statusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', () => applyFiltersAndRender());
        }

        const prevPageBtn = document.getElementById('prev-page');
        if (prevPageBtn) {
            prevPageBtn.addEventListener('click', () => {
                if (currentPage > 1) {
                    currentPage--;
                    renderTablePage();
                }
            });
        }

        const nextPageBtn = document.getElementById('next-page');
        if (nextPageBtn) {
            nextPageBtn.addEventListener('click', () => {
                const totalRecords = filteredInventoryData.length;
                const totalPages = Math.ceil(totalRecords / recordsPerPage);
                if (currentPage < totalPages) {
                    currentPage++;
                    renderTablePage();
                }
            });
        }

        const recordsPerPageSelect = document.getElementById('records-per-page');
        if (recordsPerPageSelect) {
            recordsPerPageSelect.addEventListener('change', () => {
                currentPage = 1;
                renderTablePage();
            });
        }
    };

    const init = async () => {
        initCharts();
        await loadBranchesToSelect();
        setupEventListeners();
    };

    init();
});


