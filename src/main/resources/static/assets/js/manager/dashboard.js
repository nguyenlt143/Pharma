document.addEventListener('DOMContentLoaded', function() {
    // Buttons filter
    const filterButtons = document.querySelectorAll('.filter-btn');

    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            filterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');

            const days = this.dataset.range; // 0 / 7 / 30
            loadDashboard(days);
        });
    });

    // Initial load: today (0)
    loadDashboard(0);

    // Hover effects (stat + chart cards)
    addHoverEffect('.stat-card', -2);
    addHoverEffect('.chart-card', -1);

    async function loadDashboard(days) {
        try {
            const response = await fetch(`/api/manager/dashboard?days=${days}`);
            if (!response.ok) throw new Error('API error ' + response.status);

            const data = await response.json();
            updateDashboardUI(data);
        } catch (err) {
            console.error('Failed to load dashboard:', err);
        }
    }

    function updateDashboardUI(data) {
        document.getElementById('revenue').innerText = data.revenue;
        document.getElementById('profit').innerText = data.profit;
        document.getElementById('orders').innerText = data.orderCount;

        // Nếu có change %
        document.getElementById('changeRevenue').innerText = data.changeRevenue || '';
        document.getElementById('changeProfit').innerText = data.changeProfit || '';
        document.getElementById('changeOrders').innerText = data.changeOrders || '';
    }

    function addHoverEffect(selector, translateY) {
        const cards = document.querySelectorAll(selector);
        cards.forEach(card => {
            card.addEventListener('mouseenter', () => {
                card.style.transform = `translateY(${translateY}px)`;
                card.style.boxShadow = '0px 4px 6px -1px rgba(0,0,0,0.1), 0px 2px 4px rgba(0,0,0,0.1)';
            });
            card.addEventListener('mouseleave', () => {
                card.style.transform = 'translateY(0)';
                card.style.boxShadow = '0px 1px 2px -1px rgba(0,0,0,0.1), 0px 1px 3px rgba(0,0,0,0.1)';
            });
        });
    }
});
