// Receipt List JavaScript
document.addEventListener('DOMContentLoaded', function() {
    let currentFilters = {
        type: '',
        branchId: '',
        status: ''
    };

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
                dateFilter.querySelector('.filter-text').textContent = selectedDate;
                filterByDate(selectedDate);
            }
            this.style.display = 'none';
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

        fetch(`/warehouse/receipt-list/filter?${params.toString()}`)
            .then(response => response.json())
            .then(data => {
                updateTable(data);
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

    // Filter by date (client-side for now)
    function filterByDate(selectedDate) {
        const rows = document.querySelectorAll('#receipt-tbody .table-row');
        rows.forEach(row => {
            const dateCell = row.querySelector('.date-cell');
            if (dateCell) {
                const rowDate = dateCell.textContent.trim();
                if (rowDate === selectedDate || selectedDate === '') {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        });
    }
});

