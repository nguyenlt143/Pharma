document.addEventListener('DOMContentLoaded', function() {
    let currentFilters = {
        type: '',
        branchId: '',
        status: '',
        date: ''
    };

    let currentPage = 0;
    let pageSize = 10;

    // FIX: Detect request type from URL and initialize filter
    function detectRequestTypeFromURL() {
        const path = window.location.pathname;

        if (path.includes('/import')) {
            return 'IMPORT';
        } else if (path.includes('/return')) {
            return 'RETURN';
        }
        return ''; // Empty string for "all requests"
    }

    // Initialize type filter based on current URL
    currentFilters.type = detectRequestTypeFromURL();
    console.log('Request List: Type filter initialized from URL:', currentFilters.type || 'ALL');

    // Highlight active tab based on current URL
    function highlightActiveTabFromURL() {
        const path = window.location.pathname;
        const tabs = document.querySelectorAll('.tab');

        tabs.forEach(tab => {
            const link = tab.querySelector('.tab-link');
            if (link) {
                const href = link.getAttribute('href');
                // Check if current path matches this tab's href
                if (href === path) {
                    tab.classList.add('active');
                } else {
                    tab.classList.remove('active');
                }
            }
        });
    }

    // Highlight the correct tab on page load
    highlightActiveTabFromURL();

    // Initialize dropdown default selections
    function initializeDropdowns() {
        // Set first item (Tất cả) as selected by default for branch filter
        const branchItems = document.querySelectorAll('#branch-menu .dropdown-item');
        if (branchItems.length > 0) {
            branchItems[0].classList.add('selected');
        }

        // Set first item (Tất cả) as selected by default for status filter
        const statusItems = document.querySelectorAll('#status-menu .dropdown-item');
        if (statusItems.length > 0) {
            statusItems[0].classList.add('selected');
        }
    }

    // Initialize on page load
    initializeDropdowns();

    // Function to clear all filters
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

        // Reset selected states
        document.querySelectorAll('.dropdown-item').forEach(item => {
            item.classList.remove('selected');
        });

        // Set default selections
        initializeDropdowns();

        // Reset to page 1
        currentPage = 0;

        // Refresh data
        fetchRequests();
    }

    // Clear filters button event
    const clearFiltersButton = document.getElementById('clear-filters');
    if (clearFiltersButton) {
        clearFiltersButton.addEventListener('click', function(e) {
            e.stopPropagation();
            clearAllFilters();
        });
    }

    // Add keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Escape key to close dropdowns
        if (e.key === 'Escape') {
            closeAllDropdowns();
        }
        // Ctrl+R to clear filters
        if (e.ctrlKey && e.key === 'r') {
            e.preventDefault();
            clearAllFilters();
        }
    });

    // Branch filter dropdown
    const branchFilter = document.getElementById('branch-filter');
    const branchMenu = document.getElementById('branch-menu');

    if (branchFilter && branchMenu) {
        branchFilter.addEventListener('click', function(e) {
            e.stopPropagation();
            const isOpen = branchMenu.classList.contains('show');
            closeAllDropdowns();

            if (!isOpen) {
                branchMenu.classList.add('show');
                branchFilter.classList.add('open');
            }
        });

        const branchItems = branchMenu.querySelectorAll('.dropdown-item');
        branchItems.forEach(item => {
            item.addEventListener('click', function(e) {
                e.stopPropagation();

                // Remove selected class from all items
                branchItems.forEach(i => i.classList.remove('selected'));
                // Add selected class to clicked item
                this.classList.add('selected');

                const value = this.getAttribute('data-value');
                const text = this.textContent;
                branchFilter.querySelector('.filter-text').textContent = text;
                currentFilters.branchId = value;

                branchMenu.classList.remove('show');
                branchFilter.classList.remove('open');
                currentPage = 0; // Reset to page 1 when filtering
                fetchRequests();
            });
        });
    }

    // Status filter dropdown
    const statusFilter = document.getElementById('status-filter');
    const statusMenu = document.getElementById('status-menu');

    if (statusFilter && statusMenu) {
        statusFilter.addEventListener('click', function(e) {
            e.stopPropagation();
            const isOpen = statusMenu.classList.contains('show');
            closeAllDropdowns();

            if (!isOpen) {
                statusMenu.classList.add('show');
                statusFilter.classList.add('open');
            }
        });

        const statusItems = statusMenu.querySelectorAll('.dropdown-item');
        statusItems.forEach(item => {
            item.addEventListener('click', function(e) {
                e.stopPropagation();

                // Remove selected class from all items
                statusItems.forEach(i => i.classList.remove('selected'));
                // Add selected class to clicked item
                this.classList.add('selected');

                const value = this.getAttribute('data-value');
                const text = this.textContent;
                statusFilter.querySelector('.filter-text').textContent = text;
                currentFilters.status = value;

                statusMenu.classList.remove('show');
                statusFilter.classList.remove('open');
                currentPage = 0; // Reset to page 1 when filtering
                fetchRequests();
            });
        });
    }

    // Date filter
    const dateFilter = document.getElementById('date-filter');
    const dateInput = document.getElementById('date-input');

    if (dateFilter && dateInput) {
        dateFilter.addEventListener('click', function(e) {
            e.stopPropagation();
            closeAllDropdowns();
            dateInput.style.opacity = '1';
            dateInput.style.pointerEvents = 'auto';
            dateInput.focus();
            dateInput.click();
        });

        dateInput.addEventListener('change', function() {
            const selectedDate = this.value;
            if (selectedDate) {
                // Format date for display (optional)
                const formattedDate = new Date(selectedDate).toLocaleDateString('vi-VN');
                dateFilter.querySelector('.filter-text').textContent = formattedDate;
                currentFilters.date = selectedDate;
                currentPage = 0; // Reset to page 1 when filtering
                fetchRequests();
            }
            this.style.opacity = '0';
            this.style.pointerEvents = 'none';
        });

        dateInput.addEventListener('blur', function() {
            this.style.opacity = '0';
            this.style.pointerEvents = 'none';
        });
    }

    // Close dropdowns when clicking outside
    document.addEventListener('click', function() {
        closeAllDropdowns();
    });

    // Function to close all dropdowns
    function closeAllDropdowns() {
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            menu.classList.remove('show');
        });
        document.querySelectorAll('.filter-button').forEach(button => {
            button.classList.remove('open');
        });

        // Also hide date input
        if (dateInput) {
            dateInput.style.opacity = '0';
            dateInput.style.pointerEvents = 'none';
        }
    }

    // Fetch requests from server
    function fetchRequests() {
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

        fetch(`/warehouse/request/list/filter?${params.toString()}`)
            .then(response => response.json())
            .then(data => {
                updateTable(data.content);
                updatePagination(data);
            })
            .catch(error => {
                console.error('Error fetching requests:', error);
            });
    }

    // Update table with new data
    function updateTable(requests) {
        const tbody = document.getElementById('request-tbody');

        if (requests.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" style="text-align: center;">Không có dữ liệu</td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = requests.map(request => {
            const requestType = request.requestType === 'IMPORT' ? 'Nhập hàng' :
                              request.requestType === 'RETURN' ? 'Trả hàng' : 'Không xác định';

            let statusHtml = '';
            if (request.requestStatus === 'REQUESTED') {
                statusHtml = '<div class="status-button pending">Đang duyệt</div>';
            } else if (request.requestStatus === 'CONFIRMED') {
                statusHtml = '<div class="status-button approved">Chấp nhận</div>';
            } else if (request.requestStatus === 'CANCELLED') {
                statusHtml = '<div class="status-button rejected">Từ chối</div>';
            } else {
                statusHtml = '<div class="status-button undefined">Không xác định</div>';
            }

            return `
                <tr>
                    <td>${request.branchName}</td>
                    <td>${request.createdAt}</td>
                    <td>${requestType}</td>
                    <td>${statusHtml}</td>
                    <td>
                        <a href="/warehouse/request/detail?id=${request.id}" class="detail-link">Xem Chi Tiết</a>
                    </td>
                </tr>
            `;
        }).join('');
    }

    // Filter by date (client-side filtering for simplicity)
    function filterByDate(selectedDate) {
        const rows = document.querySelectorAll('#request-tbody tr');
        rows.forEach(row => {
            const dateCell = row.cells[1];
            if (dateCell) {
                const cellDate = dateCell.textContent.trim();
                if (cellDate === selectedDate) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        });
    }

    // Detail link functionality
    const detailLinks = document.querySelectorAll('.detail-link');

    detailLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Chỉ log thông tin, không preventDefault
            const row = this.closest('.table-row');
            if (row) {
                const branchCell = row.querySelector('.data-cell:first-child');
                const dateCell = row.querySelector('.date-cell');
                const typeCell = row.querySelector('.type-cell');
                const statusCell = row.querySelector('.status-button');

                const branch = branchCell ? branchCell.textContent : '';
                const date = dateCell ? dateCell.textContent : '';
                const type = typeCell ? typeCell.textContent : '';
                const status = statusCell ? statusCell.textContent : '';

                console.log('Detail view for:', { branch, date, type, status });
            }
            // Không preventDefault → click trái tự điều hướng
        });
    });

    // Search functionality
    const searchContainer = document.querySelector('.search-container');

    if (searchContainer) {
        searchContainer.addEventListener('click', function() {
            console.log('Search clicked');
            // Add your search logic here
        });
    }

    // Pagination event handlers
    document.addEventListener('click', function(e) {
        if (e.target.closest('.pagination-btn') && !e.target.closest('.pagination-btn').disabled) {
            const btn = e.target.closest('.pagination-btn');
            const page = parseInt(btn.getAttribute('data-page'));
            if (!isNaN(page)) {
                currentPage = page;
                fetchRequests();
            }
        }
    });

    // Page size change handler
    const pageSizeSelect = document.getElementById('page-size-select');
    if (pageSizeSelect) {
        pageSizeSelect.addEventListener('change', function() {
            pageSize = parseInt(this.value);
            currentPage = 0; // Reset to first page
            fetchRequests();
        });
    }

    // FIX: Update pagination controls with proper active state highlighting
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
