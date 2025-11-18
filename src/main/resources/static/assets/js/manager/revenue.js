// PharmaSys Revenue Report
document.addEventListener('DOMContentLoaded', () => {

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
        const input = document.querySelector('.date-input');
        if (!input) return;
        if (mode === 'week') {
            input.setAttribute('type', 'week');
            input.value = currentWeekStr();
        } else if (mode === 'month') {
            input.setAttribute('type', 'month');
            input.value = currentMonthStr();
        } else {
            input.setAttribute('type', 'date');
            input.value = todayStr();
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
    // Load dữ liệu từ API
    // -------------------
    async function loadData() {
        const mode = document.getElementById('time-mode')?.value || 'day';
        const period = document.querySelector('.date-input').value; // date/week/month format
        const shift = document.getElementById('shift-select').value;
        const employeeId = document.getElementById('employee-select').value;

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
            const url = `/api/manager/report/revenue?${params.toString()}`;
            const res = await fetch(encodeURI(url));
            const data = await res.json();

            // Cập nhật KPI
            updateKPI(data);

            // Cập nhật bảng hóa đơn
            const tbody = document.getElementById('invoice-tbody');
            tbody.innerHTML = '';
            data.invoices?.forEach(inv => {
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
            addTableHoverEffect();

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
            const dateInput = document.querySelector('.date-input');
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
            loadData();
        });
    }
    document.querySelector('.date-input').addEventListener('change', loadData);
    document.getElementById('shift-select').addEventListener('change', loadData);
    document.getElementById('employee-select').addEventListener('change', loadData);

    // -------------------
    // Initial load
    // -------------------
    (async () => {
        await initFilters();
        await loadData();
    })();
});
