// PharmaSys Revenue Report
document.addEventListener('DOMContentLoaded', () => {

    // -------------------
    // Pagination State
    // -------------------
    let currentPage = 0;
    let totalPages = 1;

    // -------------------
    // Utils
    // -------------------
    const formatCurrency = (amount) =>
        (parseInt(amount) || 0).toLocaleString('vi-VN') + 'đ';

    const todayStr = () => {
        const d = new Date();
        const mm = String(d.getMonth() + 1).padStart(2, '0');
        const dd = String(d.getDate()).padStart(2, '0');
        return `${d.getFullYear()}-${mm}-${dd}`;
    };

    const currentWeekStr = () => {
        const d = new Date();
        // set to Thursday of this week to ensure correct ISO week year
        const target = new Date(d.getTime());
        target.setDate(d.getDate() + 3 - ((d.getDay() + 6) % 7));
        const week1 = new Date(target.getFullYear(), 0, 4);
        const weekNo = 1 + Math.round(((target.getTime() - week1.getTime()) / 86400000 - 3 + ((week1.getDay() + 6) % 7)) / 7);
        const wk = String(weekNo).padStart(2, '0');
        return `${target.getFullYear()}-W${wk}`;
    };

    const currentMonthStr = () => {
        const d = new Date();
        const mm = String(d.getMonth() + 1).padStart(2, '0');
        return `${d.getFullYear()}-${mm}`;
    };

    function setTimeMode(mode) {
        const dateInput = document.getElementById('date-picker');
        const weekInput = document.getElementById('week-picker');
        const monthInput = document.getElementById('month-picker');

        if (!dateInput || !weekInput || !monthInput) return;

        // Hide all inputs first
        dateInput.style.display = 'none';
        weekInput.style.display = 'none';
        monthInput.style.display = 'none';

        // Show and set value for the selected mode
        if (mode === 'week') {
            weekInput.style.display = 'block';
            weekInput.value = currentWeekStr();
        } else if (mode === 'month') {
            monthInput.style.display = 'block';
            monthInput.value = currentMonthStr();
        } else {
            dateInput.style.display = 'block';
            dateInput.value = todayStr();
        }
    }

    // -------------------
    // Hover effect cho bảng
    // -------------------
    function addTableHoverEffect() {
        document.querySelectorAll('.invoice-table tbody tr').forEach(row => {
            row.addEventListener('mouseenter', () => row.style.backgroundColor = '#f8f9fa');
            row.addEventListener('mouseleave', () => row.style.backgroundColor = '');
        });
    }

    // -------------------
    // Cập nhật KPI cards
    // -------------------
    function updateKPI(data) {
        document.getElementById('kpi-totalInvoices').textContent = data.totalInvoices ?? '-';
        document.getElementById('kpi-totalRevenue').textContent = formatCurrency(data.totalRevenue ?? 0);
        document.getElementById('kpi-totalProfit').textContent = formatCurrency(data.totalProfit ?? 0);
    }

    // -------------------
    // Update pagination controls
    // -------------------
    function updatePaginationControls() {
        const recordsPerPageSelect = document.getElementById('recordsPerPage');
        const pageSize = recordsPerPageSelect ? parseInt(recordsPerPageSelect.value, 10) : 25;
        const totalRecords = totalPages * pageSize; // Approximate
        const showing = (currentPage * pageSize) + 1;

        // Update info display
        document.getElementById('totalItems').textContent = totalRecords;
        document.getElementById('showingFrom').textContent = totalRecords > 0 ? showing : 0;
        document.getElementById('showingTo').textContent = Math.min((currentPage + 1) * pageSize, totalRecords);

        // Render pagination buttons
        const paginationButtons = document.getElementById('paginationButtons');
        if (!paginationButtons) return;
        paginationButtons.innerHTML = '';

        if (totalPages <= 1) return;

        const currentPageDisplay = currentPage + 1;

        // First button
        const firstBtn = document.createElement('button');
        firstBtn.innerHTML = '&laquo;&laquo;';
        firstBtn.className = 'pagination-btn' + (currentPage === 0 ? ' disabled' : '');
        firstBtn.disabled = currentPage === 0;
        firstBtn.title = 'Trang đầu';
        firstBtn.onclick = () => { if (currentPage > 0) { currentPage = 0; loadData(); } };
        paginationButtons.appendChild(firstBtn);

        // Previous button
        const prevBtn = document.createElement('button');
        prevBtn.innerHTML = '&laquo;';
        prevBtn.className = 'pagination-btn' + (currentPage === 0 ? ' disabled' : '');
        prevBtn.disabled = currentPage === 0;
        prevBtn.title = 'Trang trước';
        prevBtn.onclick = () => { if (currentPage > 0) { currentPage--; loadData(); } };
        paginationButtons.appendChild(prevBtn);

        // Page number buttons (max 5 visible)
        const maxButtons = 5;
        let startPage = Math.max(1, currentPageDisplay - Math.floor(maxButtons / 2));
        let endPage = Math.min(totalPages, startPage + maxButtons - 1);

        if (endPage - startPage < maxButtons - 1) {
            startPage = Math.max(1, endPage - maxButtons + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            const pageBtn = document.createElement('button');
            pageBtn.textContent = i;
            pageBtn.className = 'pagination-btn' + (i === currentPageDisplay ? ' active' : '');
            pageBtn.onclick = () => { currentPage = i - 1; loadData(); };
            paginationButtons.appendChild(pageBtn);
        }

        // Next button
        const nextBtn = document.createElement('button');
        nextBtn.innerHTML = '&raquo;';
        nextBtn.className = 'pagination-btn' + (currentPage >= totalPages - 1 ? ' disabled' : '');
        nextBtn.disabled = currentPage >= totalPages - 1;
        nextBtn.title = 'Trang sau';
        nextBtn.onclick = () => { if (currentPage < totalPages - 1) { currentPage++; loadData(); } };
        paginationButtons.appendChild(nextBtn);

        // Last button
        const lastBtn = document.createElement('button');
        lastBtn.innerHTML = '&raquo;&raquo;';
        lastBtn.className = 'pagination-btn' + (currentPage >= totalPages - 1 ? ' disabled' : '');
        lastBtn.disabled = currentPage >= totalPages - 1;
        lastBtn.title = 'Trang cuối';
        lastBtn.onclick = () => { if (currentPage < totalPages - 1) { currentPage = totalPages - 1; loadData(); } };
        paginationButtons.appendChild(lastBtn);
    }

    // -------------------
    // Load dữ liệu từ API
    // -------------------
    async function loadData() {
        const mode = document.getElementById('time-mode')?.value || 'day';

        // Get the correct input value based on mode
        let period = '';
        if (mode === 'week') {
            period = document.getElementById('week-picker')?.value || '';
        } else if (mode === 'month') {
            period = document.getElementById('month-picker')?.value || '';
        } else {
            period = document.getElementById('date-picker')?.value || '';
        }

        const shift = document.getElementById('shift-select')?.value || '';
        const employeeId = document.getElementById('employee-select')?.value || '';

        // Get page size from selector
        const recordsPerPageSelect = document.getElementById('recordsPerPage');
        const pageSize = recordsPerPageSelect ? parseInt(recordsPerPageSelect.value, 10) : 25;

        try {
            const params = new URLSearchParams();
            params.set('mode', mode);
            if (mode === 'day') {
                params.set('date', period);
            } else {
                params.set('period', period);
            }
            if (shift) params.set('shift', shift);
            if (employeeId) params.set('employeeId', employeeId);
            params.set('page', currentPage.toString());
            params.set('size', pageSize.toString());

            const url = `/api/manager/report/revenue?${params.toString()}`;
            const res = await fetch(encodeURI(url));
            const data = await res.json();

            // Cập nhật KPI
            updateKPI(data);

            // Xử lý dữ liệu phân trang từ invoices
            const invoicesData = data.invoices || {};
            const invoicesList = invoicesData.data || [];
            totalPages = Math.max(1, invoicesData.totalPages || 1);

            // Cập nhật bảng hóa đơn
            const tbody = document.getElementById('invoice-tbody');
            tbody.innerHTML = '';

            if (invoicesList.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 20px; color: #6B7280;">Không có dữ liệu</td></tr>';
            } else {
                invoicesList.forEach(inv => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${inv.time ?? ''}</td>
                        <td>${inv.code ?? ''}</td>
                        <td>${inv.customer ?? ''}</td>
                        <td>${inv.paymentLabel ?? ''}</td>
                        <td>${formatCurrency(inv.amount)}</td>
                        <td>${formatCurrency(inv.profit)}</td>
                    `;
                    tbody.appendChild(tr);
                });
            }
            addTableHoverEffect();

            // Cập nhật phân trang
            updatePaginationControls();

            // Render product stats list (like dashboard)
            const list = document.getElementById('productStatsList');
            if (list) {
                const stats = Array.isArray(data.productStats) ? data.productStats : [];
                list.innerHTML = '';
                if (stats.length === 0) {
                    list.innerHTML = '<p style="color:#6B7280; text-align:center; padding: 16px;">Không có dữ liệu</p>';
                } else {
                    const html = stats.map(item => `
                        <div class="product-item">
                            <div style="flex:1;">
                                <div class="product-name">${item.name || ''}</div>
                                <div class="product-bar">
                                    <div class="product-bar-fill" style="width:${item.percent || 0}%; background-color:${item.color || '#4CAF50'}"></div>
                                </div>
                            </div>
                            <div class="product-percent">${item.percent || 0}%</div>
                        </div>
                    `).join('');
                    list.innerHTML = html;
                }
            }

            // Tổng sản phẩm
            const totalProductsEl = document.getElementById('total-products');
            if (totalProductsEl) totalProductsEl.textContent = data.totalProducts ?? 0;

        } catch (err) {
            console.error('Lỗi tải dữ liệu:', err);
        }
    }

    // -------------------
    // Load danh sách ca làm việc & nhân viên
    // -------------------
    async function initFilters() {
        // set default mode/date
        const modeSelect = document.getElementById('time-mode');
        if (modeSelect) {
            setTimeMode(modeSelect.value || 'day');
        } else {
            const dateInput = document.getElementById('date-picker');
            if (dateInput && !dateInput.value) {
                dateInput.value = todayStr();
            }
        }

        // load shifts
        try {
            const res = await fetch('/api/manager/shifts');
            const shifts = await res.json();
            const shiftSelect = document.getElementById('shift-select');
            if (Array.isArray(shifts) && shiftSelect) {
                shiftSelect.innerHTML = '<option value="">Tất cả</option>' +
                    shifts.map(s => `<option value="${s.id}">${s.name}</option>`).join('');
            }
        } catch (e) {
            console.warn('Không thể tải danh sách ca làm việc', e);
        }

        // load pharmacists (employees)
        const empSelect = document.getElementById('employee-select');
        if (empSelect) {
            try {
                const res = await fetch('/api/manager/staffs/pharmacists');
                const emps = await res.json();
                empSelect.innerHTML = '<option value="">Tất cả</option>' +
                    (Array.isArray(emps) ? emps.map(e => `<option value="${e.id}">${e.fullName || e.userName || ('User#' + e.id)}</option>`).join('') : '');
            } catch (e) {
                console.warn('Không thể tải danh sách Dược sĩ', e);
                if (!empSelect.querySelector('option')) {
                    empSelect.innerHTML = '<option value="">Tất cả</option>';
                }
            }
        }
    }

    // -------------------
    // Event listeners
    // -------------------
    const modeSel = document.getElementById('time-mode');
    if (modeSel) {
        modeSel.addEventListener('change', () => {
            setTimeMode(modeSel.value || 'day');
            currentPage = 0; // Reset to first page
            loadData();
        });
    }

    // Add event listeners to all three date inputs
    const dateInput = document.getElementById('date-picker');
    const weekInput = document.getElementById('week-picker');
    const monthInput = document.getElementById('month-picker');

    if (dateInput) {
        dateInput.addEventListener('change', () => {
            currentPage = 0; // Reset to first page
            loadData();
        });
    }

    if (weekInput) {
        weekInput.addEventListener('change', () => {
            currentPage = 0; // Reset to first page
            loadData();
        });
    }

    if (monthInput) {
        monthInput.addEventListener('change', () => {
            currentPage = 0; // Reset to first page
            loadData();
        });
    }

    const shiftSelect = document.getElementById('shift-select');
    if (shiftSelect) {
        shiftSelect.addEventListener('change', () => {
            currentPage = 0; // Reset to first page
            loadData();
        });
    }

    const employeeSelect = document.getElementById('employee-select');
    if (employeeSelect) {
        employeeSelect.addEventListener('change', () => {
            currentPage = 0; // Reset to first page
            loadData();
        });
    }

    // Page length selector
    const recordsPerPageSelect = document.getElementById('recordsPerPage');
    if (recordsPerPageSelect) {
        recordsPerPageSelect.addEventListener('change', () => {
            currentPage = 0; // Reset to first page
            loadData();
        });
    }

    // Pagination now handled by dynamic buttons in updatePaginationControls()

    // Init
    initFilters();
    loadData();
});
