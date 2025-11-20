// Manager Inventory JS
// Fetch summary data and wire up export/filter interactions

document.addEventListener('DOMContentLoaded', () => {
    const exportBtn = document.getElementById('exportBtn');
    const totalValueEl = document.getElementById('totalValue');
    const lowStockCountEl = document.getElementById('lowStockCount');
    const pendingInboundEl = document.getElementById('pendingInbound');
    const pendingOutboundEl = document.getElementById('pendingOutbound');

    async function loadSummary() {
        try {
            const res = await fetch('/api/manager/report/inventory');
            if (!res.ok) throw new Error('Network error');
            const data = await res.json();
            totalValueEl.textContent = data.totalValue ?? '-';
            lowStockCountEl.textContent = data.lowStockCount ?? 0;
            pendingInboundEl.textContent = data.pendingInbound ?? 0;
            pendingOutboundEl.textContent = data.pendingOutbound ?? 0;
        } catch (err) {
            console.error('Failed to load inventory summary', err);
        }
    }

    if (exportBtn) {
        exportBtn.addEventListener('click', () => {
            // build current query params from filter form
            const form = document.getElementById('filterForm');
            const params = new URLSearchParams(new FormData(form));
            window.location = `/api/manager/report/inventory/export?${params.toString()}`;
        });
    }

    const resetFilters = window.resetFilters = function() {
        const form = document.getElementById('filterForm');
        if (!form) return;
        form.reset();
        form.submit();
    }

    // initial load
    loadSummary();
});
