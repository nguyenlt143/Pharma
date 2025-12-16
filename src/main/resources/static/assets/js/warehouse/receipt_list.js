// Receipt List JavaScript
document.addEventListener('DOMContentLoaded', function() {
    let currentFilters = {
        type: '',
        branchId: '',
        status: '',
        date: ''
    };

    let currentPage = 0;
    let pageSize = 10;

    // Tab switching functionality
    const tabs = document.querySelectorAll('.tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            // Remove active class from all tabs
            tabs.forEach(t => t.classList.remove('active'));
            // Add active class to clicked tab
            this.classList.add('active');

            // Update filter and fetch data
            currentFilters.type = this.getAttribute('data-type');
            // FIX 1: Reset to page 1 when changing tabs
            currentPage = 0;
            fetchReceipts();
        });
    });

    // Branch filter dropdown
    const branchFilter = document.getElementById('branch-filter');
    const branchMenu = document.getElementById('branch-menu');

    if (branchFilter && branchMenu) {
        branchFilter.addEventListener('click', function(e) {
            e.stopPropagation();
            branchMenu.classList.toggle('show');
            closeOtherDropdowns(branchMenu);
        });

        const branchItems = branchMenu.querySelectorAll('.dropdown-item');
        branchItems.forEach(item => {
            item.addEventListener('click', function() {
                const value = this.getAttribute('data-value');
                const text = this.textContent;
                branchFilter.querySelector('.filter-text').textContent = text;
                currentFilters.branchId = value;
                branchMenu.classList.remove('show');
                currentPage = 0; // Reset to page 1 when filtering
                fetchReceipts();
            });
        });
    }

    // Status filter dropdown
    const statusFilter = document.getElementById('status-filter');
    const statusMenu = document.getElementById('status-menu');

    if (statusFilter && statusMenu) {
        statusFilter.addEventListener('click', function(e) {
            e.stopPropagation();
            statusMenu.classList.toggle('show');
            closeOtherDropdowns(statusMenu);
        });

        const statusItems = statusMenu.querySelectorAll('.dropdown-item');
        statusItems.forEach(item => {
            item.addEventListener('click', function() {
                const value = this.getAttribute('data-value');
                const text = this.textContent;
                statusFilter.querySelector('.filter-text').textContent = text;
                currentFilters.status = value;
                statusMenu.classList.remove('show');
                currentPage = 0; // Reset to page 1 when filtering
                fetchReceipts();
            });
        });
    }

    // Date filter
    const dateFilter = document.getElementById('date-filter');
    const dateInput = document.getElementById('date-input');

    if (dateFilter && dateInput) {
        dateFilter.addEventListener('click', function(e) {
            e.stopPropagation();
            dateInput.style.display = 'block';
            dateInput.focus();
            dateInput.click();
        });

        dateInput.addEventListener('change', function() {
            const selectedDate = this.value;
            if (selectedDate) {
                // Format date for display
                const formattedDate = new Date(selectedDate).toLocaleDateString('vi-VN');
                dateFilter.querySelector('.filter-text').textContent = formattedDate;
                currentFilters.date = selectedDate;
                currentPage = 0; // Reset to page 1 when filtering
                fetchReceipts();
            }
            this.style.display = 'none';
        });
    }

    // Clear filters button event
    const clearFiltersButton = document.getElementById('clear-filters');
    if (clearFiltersButton) {
        clearFiltersButton.addEventListener('click', function(e) {
            e.stopPropagation();
            clearAllFilters();
        });
    }

    // Close dropdowns when clicking outside
    document.addEventListener('click', function() {
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            menu.classList.remove('show');
        });
    });

    // Function to close other dropdowns
    function closeOtherDropdowns(exceptMenu) {
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            if (menu !== exceptMenu) {
                menu.classList.remove('show');
            }
        });
    }

    // Fetch receipts from server
    function fetchReceipts() {
        const params = new URLSearchParams();

        if (currentFilters.type) {
            params.append('type', currentFilters.type);
        }
        if (currentFilters.branchId) {
            params.append('branchId', currentFilters.branchId);
        }
        if (currentFilters.status) {
            params.append('status', currentFilters.status);
        }
        if (currentFilters.date) {
            params.append('date', currentFilters.date);
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
                    <td class="table-cell data-cell" colspan="5" style="text-align: center;">Không có dữ liệu</td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = receipts.map(receipt => `
            <tr class="table-row">
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

    // Clear all filters function
    function clearAllFilters() {
        currentFilters = {
            type: '',
            branchId: '',
            status: '',
            date: ''
        };

        // Reset dropdown texts
        document.getElementById('branch-filter').querySelector('.filter-text').textContent = 'Chi Nhánh';
        document.getElementById('status-filter').querySelector('.filter-text').textContent = 'Trạng thái';
        document.getElementById('date-filter').querySelector('.filter-text').textContent = 'Ngày';

        // Clear date input
        document.getElementById('date-input').value = '';

        // Reset tab to first one
        tabs.forEach(t => t.classList.remove('active'));
        tabs[0].classList.add('active');

        // Reset to page 1
        currentPage = 0;
        fetchReceipts();
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
            currentPage = 0; // Reset to first page
            fetchReceipts();
        });
    }

    // FIX 2: Update pagination controls with proper active state highlighting
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

        // FIX: Update pagination buttons to highlight current page correctly
        // Only page number buttons should be highlighted, NOT navigation buttons
        const paginationBtns = container.querySelectorAll('.pagination-btn');
        paginationBtns.forEach(btn => {
            const btnPage = parseInt(btn.getAttribute('data-page'));
            const btnText = btn.textContent.trim();

            // Remove active class from all buttons first
            btn.classList.remove('active');

            // Add active class ONLY to page number buttons (not Previous/Next arrows)
            // Check: must have valid page number AND text is not arrow symbols
            if (!isNaN(btnPage) && btnPage === paginationData.currentPage && !['‹', '›'].includes(btnText)) {
                btn.classList.add('active');
            }
        });

        // Show/hide pagination based on total elements
        if (paginationData.totalElements <= paginationData.pageSize) {
            container.style.display = 'none';
        } else {
            container.style.display = 'flex';
        }
    }
});
