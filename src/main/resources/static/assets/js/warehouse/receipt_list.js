// Receipt List JavaScript - Simplified (No Filters)
document.addEventListener('DOMContentLoaded', function() {
    let currentPage = 0;
    let pageSize = 10;
    let currentType = ''; // For tab filtering

    // Tab switching functionality
    const tabs = document.querySelectorAll('.tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            // Remove active class from all tabs
            tabs.forEach(t => t.classList.remove('active'));
            // Add active class to clicked tab
            this.classList.add('active');

            // Update current type and fetch data
            currentType = this.getAttribute('data-type') || '';
            currentPage = 0;
            fetchReceipts();
        });
    });

    // Fetch receipts from server
    function fetchReceipts() {
        const params = new URLSearchParams();

        if (currentType) {
            params.append('type', currentType);
        }

        // Add pagination parameters
        params.append('page', currentPage);
        params.append('size', pageSize);

        fetch(`/warehouse/receipt-list/filter?${params.toString()}`)
            .then(response => response.json())
            .then(data => {
                updateTable(data.content);
                updatePagination(data);
            })
            .catch(error => {
                console.error('Error fetching receipts:', error);
            });
    }

    // Update table with new data
    function updateTable(receipts) {
        const tbody = document.getElementById('receipt-tbody');

        if (receipts.length === 0) {
            tbody.innerHTML = `
                <tr class="table-row">
                    <td class="table-cell data-cell" colspan="6" style="text-align: center;">Không có dữ liệu</td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = receipts.map((receipt, index) => `
            <tr class="table-row">
                <td class="table-cell data-cell" style="text-align: center;">${index + 1}</td>
                <td class="table-cell data-cell">${receipt.branchName}</td>
                <td class="table-cell data-cell date-cell">${receipt.createdDate}</td>
                <td class="table-cell data-cell type-cell">${receipt.requestType}</td>
                <td class="table-cell data-cell">
                    <div class="status-button ${receipt.statusClass}">${receipt.status}</div>
                </td>
                <td class="table-cell data-cell">
                    <a href="/warehouse/receipt-detail/${receipt.id}" class="detail-link">Xem Chi Tiết</a>
                </td>
            </tr>
        `).join('');
    }

    // Update pagination controls
    function updatePagination(paginationData) {
        const container = document.querySelector('.pagination-container');
        if (!container) return;

        // Update info
        const infoEl = container.querySelector('.pagination-info');
        if (infoEl) {
            const start = paginationData.currentPage * paginationData.pageSize + 1;
            const end = Math.min((paginationData.currentPage + 1) * paginationData.pageSize, paginationData.totalElements);
            infoEl.textContent = `Hiển thị ${start} - ${end} / ${paginationData.totalElements} bản ghi`;
        }

        // Update pagination buttons
        const paginationBtns = container.querySelectorAll('.pagination-btn');
        paginationBtns.forEach(btn => {
            const btnPage = parseInt(btn.getAttribute('data-page'));
            const btnText = btn.textContent.trim();

            btn.classList.remove('active');

            if (!isNaN(btnPage) && btnPage === paginationData.currentPage && !['‹', '›'].includes(btnText)) {
                btn.classList.add('active');
            }
        });

        // Show/hide pagination
        if (paginationData.totalElements <= paginationData.pageSize) {
            container.style.display = 'none';
        } else {
            container.style.display = 'flex';
        }
    }

    // Pagination event handlers
    document.addEventListener('click', function(e) {
        if (e.target.closest('.pagination-btn') && !e.target.closest('.pagination-btn').disabled) {
            const btn = e.target.closest('.pagination-btn');
            const page = parseInt(btn.getAttribute('data-page'));
            if (!isNaN(page)) {
                currentPage = page;
                fetchReceipts();
            }
        }
    });

    // Page size change handler
    const pageSizeSelect = document.getElementById('page-size-select');
    if (pageSizeSelect) {
        pageSizeSelect.addEventListener('change', function() {
            pageSize = parseInt(this.value);
            currentPage = 0;
            fetchReceipts();
        });
    }
});

