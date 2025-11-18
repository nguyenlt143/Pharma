// Revenue Report JavaScript
let invoiceTable;

// Wait for jQuery and DOM to be ready
if (typeof $ !== 'undefined') {
    $(document).ready(function() {
        loadShifts();
        loadEmployees();
        initDataTable();
        // Don't auto-load report, wait for user to click button or after filters are loaded
        setTimeout(() => {
            loadReport();
        }, 500);
    });
} else {
    // Fallback if jQuery not loaded yet
    document.addEventListener('DOMContentLoaded', function() {
        // Wait a bit for jQuery to load
        const checkJQuery = setInterval(function() {
            if (typeof $ !== 'undefined') {
                clearInterval(checkJQuery);
                loadShifts();
                loadEmployees();
                initDataTable();
                setTimeout(() => {
                    loadReport();
                }, 500);
            }
        }, 100);
    });
}

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

function initDataTable() {
    // Check if jQuery and DataTable are available
    if (typeof $ === 'undefined') {
        console.error('jQuery chưa được load');
        return;
    }
    
    if (typeof $.fn.DataTable === 'undefined') {
        console.error('DataTables chưa được load');
        return;
    }

    // Destroy existing DataTable if it exists
    if (invoiceTable) {
        invoiceTable.destroy();
    }

    // Initialize DataTable
    const tableElement = document.getElementById('invoiceTable');
    if (!tableElement) {
        console.error('Không tìm thấy bảng invoiceTable');
        return;
    }

    invoiceTable = $('#invoiceTable').DataTable({
        processing: true,
        serverSide: false,
        paging: true,
        searching: false,
        ordering: true,
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/vi.json'
        }
    });
}

function loadReport() {
    // Check if invoiceTable is initialized
    if (!invoiceTable) {
        console.warn('DataTable chưa được khởi tạo, đang khởi tạo...');
        initDataTable();
    }

    const fromDate = document.getElementById('fromDate').value;
    const toDate = document.getElementById('toDate').value;
    const shift = document.getElementById('shiftSelect').value;
    const employeeId = document.getElementById('employeeSelect').value;

    const params = new URLSearchParams({
        fromDate: fromDate,
        toDate: toDate
    });
    if (shift) params.append('shift', shift);
    if (employeeId) params.append('employeeId', employeeId);

    fetch(`/api/owner/report/revenue?${params}`)
        .then(res => res.json())
        .then(data => {
            // Update stats
            document.getElementById('totalRevenue').textContent = formatCurrency(data.totalRevenue || 0);
            document.getElementById('totalProfit').textContent = formatCurrency(data.totalProfit || 0);
            document.getElementById('totalOrders').textContent = data.totalOrders || 0;

            // Update table - ensure invoiceTable is initialized
            if (!invoiceTable) {
                console.error('DataTable vẫn chưa được khởi tạo');
                return;
            }

            invoiceTable.clear();
            if (data.invoices && data.invoices.length > 0) {
                data.invoices.forEach(inv => {
                    invoiceTable.row.add([
                        inv.id || '',
                        inv.code || '',
                        inv.fullName || '',
                        inv.shiftName || '',
                        inv.createdAt ? new Date(inv.createdAt).toLocaleString('vi-VN') : '',
                        formatCurrency(inv.totalAmount || 0),
                        formatCurrency(inv.profit || 0)
                    ]);
                });
            }
            invoiceTable.draw();
        })
        .catch(err => {
            console.error('Error loading report:', err);
            alert('Có lỗi xảy ra khi tải báo cáo');
        });
}

function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(value);
}

