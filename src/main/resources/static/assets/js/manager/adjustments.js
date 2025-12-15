document.addEventListener('DOMContentLoaded', function () {
    const dateFromInput = document.getElementById('dateFrom');
    const dateToInput = document.getElementById('dateTo');
    const typeFilter = document.getElementById('typeFilter');
    const filterBtn = document.getElementById('filterBtn');
    const reportTableBody = document.getElementById('reportTableBody');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageInfo = document.getElementById('page-info');

    const adjTotalEl = document.getElementById('adjustment-total');
    const expTotalEl = document.getElementById('expired-total');
    const grandTotalEl = document.getElementById('grand-total');

    let currentPage = 1;
    const pageSize = 10;

    function formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
    }

    function fetchTotals(from, to, type) {
        const url = new URL('/api/manager/adjustments/totals', window.location.origin);
        url.searchParams.append('from', from);
        url.searchParams.append('to', to);
        if (type && type !== 'all') url.searchParams.append('type', type);

        return fetch(url)
            .then(resp => resp.ok ? resp.json() : Promise.reject('Failed to load totals'))
            .catch(err => {
                console.error('Error fetching totals:', err);
                return { adjustmentTotal: 0, expiredReturnTotal: 0, grandTotal: 0 };
            });
    }

    function fetchAdjustments(page = 1) {
        const from = dateFromInput.value;
        const to = dateToInput.value;
        let type = typeFilter.value;

        if (!from || !to) {
            alert('Vui lòng chọn cả ngày bắt đầu và ngày kết thúc.');
            return;
        }

        // Map UI type to backend enum names if needed
        if (type === 'adjustment') type = 'ADJUSTMENT';
        if (type === 'expired_return') type = 'EXPIRED_RETURN';
        if (type === 'all') type = null;

        const url = new URL('/api/manager/adjustments', window.location.origin);
        url.searchParams.append('from', from);
        url.searchParams.append('to', to);
        url.searchParams.append('page', page - 1); // Spring Page is 0-indexed
        url.searchParams.append('size', pageSize);
        if (type) {
            url.searchParams.append('type', type);
        }

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(data => {
                renderTable(data.content);
                renderPagination(data);
                currentPage = data.number + 1;

                // fetch and render totals
                fetchTotals(from, to, type)
                    .then(t => {
                        adjTotalEl.textContent = t.adjustmentTotal ? t.adjustmentTotal.toLocaleString(undefined, {minimumFractionDigits:2, maximumFractionDigits:2}) : '0';
                        expTotalEl.textContent = t.expiredReturnTotal ? t.expiredReturnTotal.toLocaleString(undefined, {minimumFractionDigits:2, maximumFractionDigits:2}) : '0';
                        grandTotalEl.textContent = t.grandTotal ? t.grandTotal.toLocaleString(undefined, {minimumFractionDigits:2, maximumFractionDigits:2}) : '0';
                    });
            })
            .catch(error => console.error('Error fetching adjustments:', error));
    }

    function renderTable(adjustments) {
        reportTableBody.innerHTML = '';
        if (!adjustments || adjustments.length === 0) {
            reportTableBody.innerHTML = '<tr><td colspan="7" class="text-center">Không có dữ liệu</td></tr>';
            return;
        }

        adjustments.forEach(adj => {
            const typeLabel = adj.type === 'ADJUSTMENT' ? 'Điều chỉnh' : 'Trả hàng hết hạn';
            const row = `
                <tr>
                    <td>${formatDate(adj.createdAt)}</td>
                    <td>${typeLabel}</td>
                    <td>Sản phẩm ID: ${adj.variantId || ''}</td>
                    <td>${adj.batchId || ''}</td>
                    <td>${(adj.reason || '').trim()}</td>
                    <td>${adj.differenceQuantity || 0}</td>
                    <td>${adj.createdBy || ''}</td>
                </tr>
            `;
            reportTableBody.innerHTML += row;
        });
    }

    function renderPagination(pageData) {
        pageInfo.textContent = `Trang ${pageData.number + 1} / ${pageData.totalPages}`;
        prevPageBtn.disabled = pageData.first;
        nextPageBtn.disabled = pageData.last;
    }

    filterBtn.addEventListener('click', () => fetchAdjustments(1));

    prevPageBtn.addEventListener('click', () => {
        if (currentPage > 1) {
            fetchAdjustments(currentPage - 1);
        }
    });

    nextPageBtn.addEventListener('click', () => {
        const totalPages = parseInt(pageInfo.textContent.split('/')[1].trim());
        if (currentPage < totalPages) {
            fetchAdjustments(currentPage + 1);
        }
    });

    // Set default dates
    const today = new Date();
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    dateFromInput.value = firstDayOfMonth.toISOString().split('T')[0];
    dateToInput.value = today.toISOString().split('T')[0];

    // Initial fetch
    fetchAdjustments();
});
