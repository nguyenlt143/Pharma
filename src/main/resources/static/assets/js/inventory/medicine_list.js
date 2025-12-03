document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const tableBody = document.getElementById('medicineTableBody');

    // Populate category dropdown
    const categories = new Set();
    const rows = tableBody.querySelectorAll('tr[data-category]');
    rows.forEach(row => {
        const category = row.dataset.category;
        if (category && category !== 'null') {
            categories.add(category);
        }
    });

    categories.forEach(cat => {
        const option = document.createElement('option');
        option.value = cat;
        option.textContent = cat;
        categoryFilter.appendChild(option);
    });

    function applyFilters() {
        const searchTerm = (searchInput?.value || '').trim().toLowerCase();
        const categoryTerm = (categoryFilter?.value || '').trim();

        const allRows = tableBody.querySelectorAll('tr[data-medicine-name]');
        let visibleIndex = 0;

        allRows.forEach(row => {
            const medicineName = (row.dataset.medicineName || '').toLowerCase();
            const activeIngredient = (row.dataset.activeIngredient || '').toLowerCase();
            const batchCode = (row.dataset.batchCode || '').toLowerCase();
            const category = row.dataset.category || '';

            const matchSearch = !searchTerm ||
                medicineName.includes(searchTerm) ||
                activeIngredient.includes(searchTerm) ||
                batchCode.includes(searchTerm);

            const matchCategory = !categoryTerm || category === categoryTerm;

            if (matchSearch && matchCategory) {
                row.style.display = '';
                // Update row index (first td)
                const indexCell = row.querySelector('td');
                if (indexCell) indexCell.textContent = (++visibleIndex).toString();
            } else {
                row.style.display = 'none';
            }
        });
    }

    if (searchInput) {
        searchInput.addEventListener('input', applyFilters);
    }
    if (categoryFilter) {
        categoryFilter.addEventListener('change', applyFilters);
    }
});


