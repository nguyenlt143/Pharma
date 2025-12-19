// Manager Inventory Report JavaScript
document.addEventListener('DOMContentLoaded', () => {

    // =========================
    // Constants & Utils
    // =========================
    const API_BASE = '/api/manager/report/inventory';

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

    const showLoading = (elementId) => {
        const el = document.getElementById(elementId);
        if (el) {
            el.innerHTML = '<div class="spinner"></div>';
        }
    };

    const showError = (elementId, message = 'Lỗi') => {
        const el = document.getElementById(elementId);
        if (el) {
            el.innerHTML = `<span style="color: #EF4444;">${message}</span>`;
        }
    };

    // =========================
    // Pagination State
    // =========================
    let allInventoryData = [];
    let filteredInventoryData = [];
    let currentPage = 1;


    // =========================
    // API Calls
    // =========================
    const fetchSummary = async () => {
        try {
            console.log('Fetching summary from:', API_BASE);
            const response = await fetch(API_BASE);
            if (!response.ok) {
                console.error(`Failed to fetch summary: ${response.status} ${response.statusText}`);
                throw new Error(`Failed to fetch summary: ${response.status} ${response.statusText}`);
            }
            const data = await response.json();
            console.log('Summary data received:', data);

            // Validate data structure
            if (!data || typeof data !== 'object') {
                console.error('Invalid summary data received:', data);
                return {
                    totalItems: 0,
                    lowStock: 0,
                    totalValue: 0.0,
                    nearExpiry: 0,
                    expired: 0,
                    lastUpdated: new Date().toISOString()
                };
            }

            // Ensure all required fields exist with default values
            return {
                totalItems: data.totalItems || 0,
                lowStock: data.lowStock || 0,
                totalValue: data.totalValue || 0.0,
                nearExpiry: data.nearExpiry || 0,
                expired: data.expired || 0,
                lastUpdated: data.lastUpdated || new Date().toISOString()
            };
        } catch (error) {
            console.error('Error fetching summary:', error);
            // Return default values instead of throwing
            return {
                totalItems: 0,
                lowStock: 0,
                totalValue: 0.0,
                nearExpiry: 0,
                expired: 0,
                lastUpdated: new Date().toISOString()
            };
        }
    };

    const fetchInventoryDetails = async () => {
        try {
            console.log('Fetching inventory details from:', `${API_BASE}/details`);
            const response = await fetch(`${API_BASE}/details`);
            if (!response.ok) {
                throw new Error(`Failed to fetch inventory details: ${response.status}`);
            }
            const data = await response.json();
            console.log('Inventory details received:', data.length, 'items');
            return data;
        } catch (error) {
            console.error('Error fetching inventory details:', error);
            return [];
        }
    };

    const fetchCategories = async () => {
        try {
            const response = await fetch(`${API_BASE}/categories`);
            if (!response.ok) {
                throw new Error('Failed to fetch categories');
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching categories:', error);
            return [];
        }
    };

    const searchInventory = async (query, categoryId, status) => {
        try {
            const params = new URLSearchParams();
            if (query) params.set('query', query);
            if (categoryId) params.set('categoryId', categoryId);
            if (status) params.set('status', status);

            const response = await fetch(`${API_BASE}/search?${params.toString()}`);
            if (!response.ok) {
                throw new Error('Failed to search inventory');
            }
            return await response.json();
        } catch (error) {
            console.error('Error searching inventory:', error);
            return [];
        }
    };

    // =========================
    // Pagination & Table Rendering
    // =========================
    const updatePaginationControls = () => {
        const recordsPerPageSelect = document.getElementById('recordsPerPage');
        const recordsPerPage = recordsPerPageSelect ? parseInt(recordsPerPageSelect.value, 10) : 25;
        const totalRecords = filteredInventoryData.length;
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
        firstBtn.onclick = () => { if (currentPage > 1) { currentPage = 1; renderTablePage(); } };
        paginationButtons.appendChild(firstBtn);

        // Previous button
        const prevBtn = document.createElement('button');
        prevBtn.innerHTML = '&laquo;';
        prevBtn.className = 'pagination-btn' + (currentPage === 1 ? ' disabled' : '');
        prevBtn.disabled = currentPage === 1;
        prevBtn.title = 'Trang trước';
        prevBtn.onclick = () => { if (currentPage > 1) { currentPage--; renderTablePage(); } };
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
            pageBtn.onclick = () => { currentPage = i; renderTablePage(); };
            paginationButtons.appendChild(pageBtn);
        }

        // Next button
        const nextBtn = document.createElement('button');
        nextBtn.innerHTML = '&raquo;';
        nextBtn.className = 'pagination-btn' + (currentPage === totalPages ? ' disabled' : '');
        nextBtn.disabled = currentPage === totalPages;
        nextBtn.title = 'Trang sau';
        nextBtn.onclick = () => { if (currentPage < totalPages) { currentPage++; renderTablePage(); } };
        paginationButtons.appendChild(nextBtn);

        // Last button
        const lastBtn = document.createElement('button');
        lastBtn.innerHTML = '&raquo;&raquo;';
        lastBtn.className = 'pagination-btn' + (currentPage === totalPages ? ' disabled' : '');
        lastBtn.disabled = currentPage === totalPages;
        lastBtn.title = 'Trang cuối';
        lastBtn.onclick = () => { if (currentPage < totalPages) { currentPage = totalPages; renderTablePage(); } };
        paginationButtons.appendChild(lastBtn);
    };

    const renderTablePage = () => {
        const recordsPerPageSelect = document.getElementById('recordsPerPage');
        const recordsPerPage = recordsPerPageSelect ? parseInt(recordsPerPageSelect.value, 10) : 10;
        const tbody = document.getElementById('inventoryTableBody');
        if (!tbody) return;

        if (filteredInventoryData.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; padding: 20px; color: #6B7280;">Không có dữ liệu tồn kho</td></tr>';
            updatePaginationControls();
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

        updatePaginationControls();
    };


    // =========================
    // Update UI
    // =========================
    const updateKPIs = (data) => {
        console.log('Updating KPIs with data:', data);

        if (!data || typeof data !== 'object') {
            console.error('Invalid data passed to updateKPIs:', data);
            data = {
                totalItems: 0,
                lowStock: 0,
                totalValue: 0.0,
                nearExpiry: 0,
                expired: 0
            };
        }

        // Total Items
        const totalItemsEl = document.getElementById('totalItems');
        if (totalItemsEl) {
            const value = data.totalItems !== undefined && data.totalItems !== null ? data.totalItems : 0;
            totalItemsEl.textContent = formatNumber(value);
            console.log('Updated totalItems:', value);
        }

        // Low Stock (now includes both low stock and out of stock)
        const lowStockEl = document.getElementById('lowStock');
        if (lowStockEl) {
            const value = data.lowStock !== undefined && data.lowStock !== null ? data.lowStock : 0;
            lowStockEl.textContent = formatNumber(value);
            console.log('Updated lowStock:', value);
        }

        // Total Value
        const totalValueEl = document.getElementById('totalValue');
        if (totalValueEl) {
            const value = data.totalValue !== undefined && data.totalValue !== null ? data.totalValue : 0;
            totalValueEl.textContent = formatCurrency(value);
            console.log('Updated totalValue:', value);
        }

        // Near Expiry
        const nearExpiryEl = document.getElementById('nearExpiry');
        if (nearExpiryEl) {
            const value = data.nearExpiry !== undefined && data.nearExpiry !== null ? data.nearExpiry : 0;
            nearExpiryEl.textContent = formatNumber(value);
            console.log('Updated nearExpiry:', value);
        }

        // Expired
        const expiredEl = document.getElementById('expired');
        if (expiredEl) {
            const value = data.expired !== undefined && data.expired !== null ? data.expired : 0;
            expiredEl.textContent = formatNumber(value);
            console.log('Updated expired:', value);
        }

        // Update Alerts (no-op when alert widgets are removed)
        updateAlerts(data);
    };

    // Define no-op updateAlerts to keep compatibility when alert UI is removed
    const updateAlerts = (_data) => { /* intentionally left blank */ };

    // Charts instances
    let categoryChart = null;
    let donutChart = null;

    const initCharts = () => {
        // Check if Chart.js is available
        if (typeof Chart === 'undefined') {
            console.warn('Chart.js is not loaded. Charts will not be displayed.');
            return false;
        }

        // Category Chart
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

        // Donut Chart
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
                                    const percent = ((value / total) * 100).toFixed(1);
                                    return context.label + ': ' + formatCurrency(value) + ' (' + percent + '%)';
                                }
                            }
                        }
                    }
                }
            });
        }

        return true;
    };

    const updateCharts = async () => {
        try {
            console.log('Fetching statistics from:', `${API_BASE}/statistics`);
            const response = await fetch(`${API_BASE}/statistics`);
            if (!response.ok) throw new Error(`Failed to fetch statistics: ${response.status}`);

            const stats = await response.json();
            console.log('Statistics received:', stats);

            if (stats && stats.length > 0) {
                console.log('Updating charts with', stats.length, 'categories');

                // Update Category Chart (Bar - by item count)
                if (categoryChart) {
                    categoryChart.data.labels = stats.map(s => s.categoryName);
                    categoryChart.data.datasets[0].data = stats.map(s => s.itemCount);
                    categoryChart.update();
                    console.log('Category chart updated');

                    const emptyChart = document.getElementById('categoryChartEmpty');
                    if (emptyChart) emptyChart.classList.add('hidden');
                } else {
                    console.warn('Category chart not initialized');
                }

                // Update Donut Chart (by total value)
                if (donutChart) {
                    donutChart.data.labels = stats.map(s => s.categoryName);
                    donutChart.data.datasets[0].data = stats.map(s => s.totalValue);
                    donutChart.update();
                    console.log('Donut chart updated');

                    // Update legend
                    updateDonutLegend(stats);

                    const emptyDonut = document.getElementById('donutChartEmpty');
                    if (emptyDonut) emptyDonut.classList.add('hidden');
                } else {
                    console.warn('Donut chart not initialized');
                }
            } else {
                console.warn('No statistics data received');
                // Show empty states
                const emptyChart = document.getElementById('categoryChartEmpty');
                const emptyDonut = document.getElementById('donutChartEmpty');
                if (emptyChart) emptyChart.classList.remove('hidden');
                if (emptyDonut) emptyDonut.classList.remove('hidden');
            }
        } catch (error) {
            console.error('Error updating charts:', error);
            // Show empty states on error
            const emptyChart = document.getElementById('categoryChartEmpty');
            const emptyDonut = document.getElementById('donutChartEmpty');
            if (emptyChart) emptyChart.classList.remove('hidden');
            if (emptyDonut) emptyDonut.classList.remove('hidden');
        }
    };

    const updateDonutLegend = (stats) => {
        const legend = document.getElementById('donutLegend');
        if (!legend) return;

        const colors = [
            '#2563EB', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
            '#EC4899', '#14B8A6', '#F97316', '#06B6D4', '#84CC16'
        ];

        const total = stats.reduce((sum, s) => sum + s.totalValue, 0);

        legend.innerHTML = stats.map((s, i) => {
            const percent = ((s.totalValue / total) * 100).toFixed(1);
            const color = colors[i % colors.length];
            return `
                <li>
                    <button class="legend-btn">
                        <span class="swatch" style="background-color: ${color};"></span>
                        <span>${s.categoryName}: ${formatCurrency(s.totalValue)} (${percent}%)</span>
                    </button>
                </li>
            `;
        }).join('');
    };

    const updateInventoryTable = (data) => {
        allInventoryData = data;
        applyFiltersAndRender();
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

    const determineStatus = (item) => {
        // Check if out of stock
        if (!item.quantity || item.quantity <= 0) {
            return 'Hết hàng';
        }

        // Check expiry date first (higher priority)
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

        // Check if low stock (quantity < minStock or < 1000 if minStock is not set)
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

    // =========================
    // Load Data
    // =========================
    const loadCategories = async () => {
        try {
            console.log('Fetching categories...');
            const categories = await fetchCategories();
            console.log('Categories received:', categories);

            const categorySelect = document.getElementById('categoryFilter');
            if (!categorySelect) {
                console.error('Category select element not found');
                return;
            }

            if (categories && categories.length > 0) {
                const options = categories.map(cat =>
                    `<option value="${cat.id}">${cat.name}</option>`
                ).join('');
                categorySelect.innerHTML = '<option value="">Tất cả</option>' + options;
                console.log('Categories populated:', categories.length, 'options');
            } else {
                console.warn('No categories found');
                categorySelect.innerHTML = '<option value="">Tất cả</option>';
            }
        } catch (error) {
            console.error('Error loading categories:', error);
        }
    };

    const loadData = async () => {
        try {
            // Load summary (KPIs) - priority 1
            console.log('Loading KPIs...');
            const summary = await fetchSummary();
            updateKPIs(summary);
            console.log('KPIs updated successfully');

            // Load categories for filter - priority 2
            console.log('Loading categories...');
            await loadCategories();
            console.log('Categories loaded successfully');

            // Load inventory details - priority 3
            console.log('Loading inventory details...');
            const inventoryDetails = await fetchInventoryDetails();
            updateInventoryTable(inventoryDetails);
            console.log('Inventory table updated with', inventoryDetails.length, 'items');

            // Load and update charts - priority 4
            console.log('Loading charts...');
            await updateCharts();
            console.log('Charts updated successfully');

        } catch (error) {
            console.error('Error loading data:', error);

            // Show error in KPIs
            ['totalItems', 'lowStock', 'totalValue', 'nearExpiry', 'expired'].forEach(id => {
                showError(id, '-');
            });
        }
    };

    const handleSearch = () => {
        applyFiltersAndRender();
    };

    // =========================
    // Event Handlers
    // =========================
    const setupEventListeners = () => {
        const searchBtn = document.getElementById('searchBtn');
        if (searchBtn) {
            searchBtn.addEventListener('click', handleSearch);
        }

        const searchInput = document.getElementById('searchInventory');
        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    handleSearch();
                }
            });
        }

        const categoryFilter = document.getElementById('categoryFilter');
        if (categoryFilter) {
            categoryFilter.addEventListener('change', handleSearch);
        }

        const statusFilter = document.getElementById('statusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', handleSearch);
        }

        // Page length selector
        const recordsPerPageSelect = document.getElementById('recordsPerPage');
        if (recordsPerPageSelect) {
            recordsPerPageSelect.addEventListener('change', () => {
                currentPage = 1; // Reset to first page
                renderTablePage();
            });
        }

        // Pagination now handled by dynamic buttons in updatePaginationControls()
    };


    // =========================
    // Initialization
    // =========================
    const init = () => {
        // Show initial loading state for table
        const tbody = document.getElementById('inventoryTableBody');
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="9" style="text-align: center; padding: 40px;">
                        <div class="spinner"></div>
                        <p style="margin-top: 12px; color: #6B7280;">Đang tải dữ liệu...</p>
                    </td>
                </tr>
            `;
        }

        initCharts();
        loadData();
        setupEventListeners();
    };

    init();
});
