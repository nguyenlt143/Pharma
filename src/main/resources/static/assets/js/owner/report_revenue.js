// Revenue Report JavaScript
let invoiceTable;

// Wait for jQuery and DOM to be ready
if (typeof $ !== 'undefined') {
    $(document).ready(function() {
        loadBranches();
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
                loadBranches();
                initDataTable();
                setTimeout(() => {
                    loadReport();
                }, 500);
            }
        }, 100);
    });
}

function loadBranches() {
    fetch('/api/owner/branches')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('branchSelect');
            if (select && Array.isArray(data)) {
                select.innerHTML = '<option value="">Tất cả chi nhánh</option>' +
                    data.map(b => `<option value="${b.id}">${b.name || 'Chi nhánh #' + b.id}</option>`).join('');
            }
        })
        .catch(err => {
            console.warn('Không thể tải danh sách chi nhánh:', err);
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

    const period = document.getElementById('periodInput').value;
    const branchId = document.getElementById('branchSelect').value;

    const params = new URLSearchParams({
        period: period
    });
    if (branchId) params.append('branchId', branchId);

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

            // data.categories: [{ categoryName, revenue }]
            if (data.categories && data.categories.length > 0) {
                data.categories.forEach((cat, index) => {
                    invoiceTable.row.add([
                        index + 1,
                        cat.categoryName || '',
                        formatCurrency(cat.revenue || 0)
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

