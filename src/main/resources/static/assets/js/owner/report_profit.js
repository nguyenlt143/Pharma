// Profit Report JavaScript
let profitTable;

$(document).ready(function() {
    initDataTable();
    loadReport();
});

function initDataTable() {
    profitTable = $('#profitTable').DataTable({
        processing: true,
        serverSide: false,
        paging: true,
        searching: false,
        ordering: true,
        language: {
            url: '/assets/datatable_vi.json'
        }
    });
}

function loadReport() {
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

    fetch(`/api/owner/report/profit?${params}`)
        .then(res => res.json())
        .then(data => {
            // Update stats
            document.getElementById('totalProfit').textContent = formatCurrency(data.totalProfit || 0);
            document.getElementById('totalRevenue').textContent = formatCurrency(data.totalRevenue || 0);
            document.getElementById('totalOrders').textContent = data.totalOrders || 0;

            // Update table
            profitTable.clear();
            if (data.profitDetails && data.profitDetails.length > 0) {
                data.profitDetails.forEach(item => {
                    profitTable.row.add([
                        item.invoiceId || '',
                        item.invoiceCode || '',
                        item.date ? new Date(item.date).toLocaleString('vi-VN') : '',
                        formatCurrency(item.revenue || 0),
                        formatCurrency(item.profit || 0)
                    ]);
                });
            }
            profitTable.draw();
        })
        .catch(err => {
            console.error('Error loading report:', err);
            showToast('Có lỗi xảy ra khi tải báo cáo', 'error');
        });
}

function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(value);
}

