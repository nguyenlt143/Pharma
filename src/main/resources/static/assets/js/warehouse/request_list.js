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
            // For example, you could show a dropdown menu or open a modal
        });
    });

    // Detail link functionality
    const detailLinks = document.querySelectorAll('.detail-link');

    detailLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            // Get the row data
            const row = this.closest('.table-row');
            const branch = row.querySelector('.data-cell:first-child').textContent;
            const date = row.querySelector('.date-cell').textContent;
            const type = row.querySelector('.type-cell').textContent;
            const status = row.querySelector('.status-button').textContent;

            console.log('Detail view for:', {
                branch,
                date,
                type,
                status
            });

            // Add your detail view logic here
            // For example, you could navigate to a detail page or open a modal
        });
    });

    // Search functionality (if needed)
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
