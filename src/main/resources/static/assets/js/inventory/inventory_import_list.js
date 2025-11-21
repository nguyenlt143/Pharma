document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('importSearchInput');
    const dateInput = document.getElementById('dateFilterInput');
    const tableBody = document.getElementById('importTableBody');

    function normalizeDateString(dateStr) {
        // Expect format dd/MM/yyyy HH:mm; extract dd/MM/yyyy
        if (!dateStr) return '';
        const parts = dateStr.split(' ');
        return parts[0];
    }

    function applyFilters() {
        const codeTerm = (searchInput?.value || '').trim().toLowerCase();
        const dateTerm = (dateInput?.value || '').trim(); // yyyy-MM-dd from input

        const rows = tableBody.querySelectorAll('tr[data-import-code]');
        let visibleIndex = 0;
        rows.forEach(row => {
            const importCode = (row.dataset.importCode || '').toLowerCase();
            const createdRaw = row.dataset.createdDate || '';
            const createdDisplayDate = normalizeDateString(createdRaw); // dd/MM/yyyy

            // Convert input date yyyy-MM-dd to dd/MM/yyyy for comparison
            let matchDate = true;
            if (dateTerm) {
                const [y, m, d] = dateTerm.split('-');
                const isoToDisplay = `${d}/${m}/${y}`;
                matchDate = createdDisplayDate === isoToDisplay;
            }

            const matchCode = !codeTerm || importCode.includes(codeTerm);

            if (matchCode && matchDate) {
                row.style.display = '';
                // update index cell (first td)
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
    if (dateInput) {
        dateInput.addEventListener('change', applyFilters);
    }
});
