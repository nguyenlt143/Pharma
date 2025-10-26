document.addEventListener('DOMContentLoaded', function() {
    // Time filter functionality
    const filterButtons = document.querySelectorAll('.filter-btn');

    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active class from all buttons
            filterButtons.forEach(btn => btn.classList.remove('active'));

            // Add active class to clicked button
            this.classList.add('active');

            // Here you could add functionality to update the data based on the selected time period
            console.log('Selected time period:', this.textContent);
        });
    });

    // Navigation functionality
    const navLinks = document.querySelectorAll('.nav-link');

    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            // Remove active class from all nav links
            navLinks.forEach(navLink => navLink.classList.remove('active'));

            // Add active class to clicked link
            this.classList.add('active');

            console.log('Navigated to:', this.textContent);
        });
    });

    // Add hover effects to stat cards
    const statCards = document.querySelectorAll('.stat-card');

    statCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0px 4px 6px -1px rgba(0, 0, 0, 0.1), 0px 2px 4px rgba(0, 0, 0, 0.1)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0px 1px 2px -1px rgba(0, 0, 0, 0.1), 0px 1px 3px rgba(0, 0, 0, 0.1)';
        });
    });

    // Add hover effects to chart cards
    const chartCards = document.querySelectorAll('.chart-card');

    chartCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-1px)';
            this.style.boxShadow = '0px 4px 6px -1px rgba(0, 0, 0, 0.1), 0px 2px 4px rgba(0, 0, 0, 0.1)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0px 1px 2px -1px rgba(0, 0, 0, 0.1), 0px 1px 3px rgba(0, 0, 0, 0.1)';
        });
    });

    // Simulate data updates (optional)
    function updateStats() {
        const statValues = document.querySelectorAll('.stat-value');
        const changeTexts = document.querySelectorAll('.change-text');

        // This is just for demonstration - in a real app, you'd fetch data from an API
        const mockData = {
            revenue: ['đ59.5M', 'đ62.1M', 'đ58.3M'],
            profit: ['đ19.7M', 'đ20.5M', 'đ18.9M'],
            orders: ['45', '52', '41'],
            changes: ['+12%', '+8%', '+5%']
        };

        // You could implement actual data updates here
        console.log('Stats could be updated with new data');
    }

    // Optional: Update stats every 30 seconds (commented out for demo)
    // setInterval(updateStats, 30000);
});
