// Request List JavaScript - Simplified (No Filters)
document.addEventListener('DOMContentLoaded', function() {
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

    // Detail link functionality
    const detailLinks = document.querySelectorAll('.detail-link');

    detailLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Allow normal navigation
            const row = this.closest('tr');
            if (row) {
                console.log('Navigating to request detail');
            }
        });
    });
});

