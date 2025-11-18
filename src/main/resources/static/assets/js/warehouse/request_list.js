document.addEventListener('DOMContentLoaded', function() {
    // Tab functionality
    const tabs = document.querySelectorAll('.tab');

    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            // Remove active class from all tabs
            tabs.forEach(t => t.classList.remove('active'));

            // Add active class to clicked tab
            this.classList.add('active');

            // Update tab styles based on active state
            updateTabStyles();
        });
    });

    function updateTabStyles() {
        tabs.forEach(tab => {
            const tabText = tab.querySelector('.tab-text');
            if (tab.classList.contains('active')) {
                tab.style.backgroundColor = '#F8F9FC';
                tab.style.boxShadow = '0px 0px 4px rgba(0, 0, 0, 0.1)';
                tabText.style.color = '#0D131C';
            } else {
                tab.style.backgroundColor = 'transparent';
                tab.style.boxShadow = 'none';
                tabText.style.color = '#49699C';
            }
        });
    }

    // Filter button functionality
    const filterButtons = document.querySelectorAll('.filter-button');

    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            const filterText = this.querySelector('.filter-text').textContent;
            console.log(`Filter clicked: ${filterText}`);

            // Add your filter logic here
        });
    });

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

    // Initialize tab styles
    updateTabStyles();
});
