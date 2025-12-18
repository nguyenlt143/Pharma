// Warehouse Inventory - Master-Detail Variant Grouping with Pagination and Bulk Actions
document.addEventListener('DOMContentLoaded', function() {
    console.log('Warehouse inventory JS loaded');

    const searchInputEl = document.getElementById('searchInput');
    const categoryFilterEl = document.getElementById('categoryFilter');
    const filterExpiring = document.getElementById('filterExpiring');
    const filterLowStock = document.getElementById('filterLowStock');
    const selectAllCheckbox = document.getElementById('selectAll');
    const bulkBar = document.getElementById('bulkActionBar');
    const selectedCountSpan = document.getElementById('selectedCount');
    const createReceiptBtn = document.getElementById('createReceiptFromList');
    const createExportBtn = document.getElementById('createExportFromList');
    const tableBody = document.getElementById('medicineTableBody');

    if (!tableBody) {
        console.error('medicineTableBody not found!');
        return;
    }

    let currentPage = 1;
    const itemsPerPage = 10;
    let variantData = [];
    let filteredVariantData = [];

    // Group batch rows by variant
    function groupBatchesByVariant() {
        const batchRows = Array.from(tableBody.querySelectorAll('.batch-row'));
        console.log('Found batch rows:', batchRows.length);
        const variantMap = new Map();

        batchRows.forEach(row => {
            const variantId = row.dataset.variantId;
            if (!variantMap.has(variantId)) {
                variantMap.set(variantId, []);
            }
            variantMap.get(variantId).push(row);
        });

        console.log('Grouped into variants:', variantMap.size);
        return variantMap;
    }


    // Build variant rows with expandable batch details
    function buildVariantRows() {
        const variantMap = groupBatchesByVariant();
        variantData = [];

        variantMap.forEach((batches, variantId) => {
            const firstBatch = batches[0];

            // Calculate totals and alerts
            let totalQty = 0;
            let minStock = null;
            let hasExpiring = false;
            let hasExpired = false;

            batches.forEach(batch => {
                totalQty += parseInt(batch.dataset.quantity || 0);
                if (batch.dataset.minStock && batch.dataset.minStock !== '') {
                    minStock = parseInt(batch.dataset.minStock);
                }
                if (batch.dataset.expiryCategory === 'week') hasExpired = true;
                if (batch.dataset.expiryCategory === 'month') hasExpiring = true;
            });

            const isLowStock = minStock !== null && totalQty < minStock;

            variantData.push({
                variantId,
                batches,
                firstBatch,
                totalQty,
                minStock,
                hasExpired,
                hasExpiring,
                isLowStock,
                medicineName: firstBatch.dataset.medicineName,
                activeIngredient: firstBatch.dataset.activeIngredient,
                category: firstBatch.dataset.category,
                unit: firstBatch.dataset.unit
            });
        });

        console.log('Built variant data:', variantData.length);
        applyFilters();
    }

    function renderCurrentPage() {
        // IMPORTANT: Don't clear innerHTML - it will remove batch rows!
        // Only remove previously rendered variant/detail rows
        const existingRows = tableBody.querySelectorAll('.variant-row, .detail-row');
        existingRows.forEach(row => row.remove());

        const startIdx = (currentPage - 1) * itemsPerPage;
        const endIdx = startIdx + itemsPerPage;
        const pageVariants = filteredVariantData.slice(startIdx, endIdx);

        console.log('Rendering page', currentPage, ':', pageVariants.length, 'variants');

        if (pageVariants.length === 0) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = '<td colspan="12" class="px-3 py-6 text-center text-gray-500">Không có thuốc trong kho</td>';
            tableBody.appendChild(emptyRow);
            updatePaginationUI();
            updateBulkBar();
            return;
        }

        pageVariants.forEach((variant, idx) => {
            const globalIdx = startIdx + idx + 1;

            // Create main variant row
            const mainRow = document.createElement('tr');
            mainRow.className = 'variant-row hover:bg-gray-50';
            if (variant.hasExpired) mainRow.classList.add('bg-red-50');
            else if (variant.hasExpiring || variant.isLowStock) mainRow.classList.add('bg-yellow-50');

            mainRow.dataset.variantId = variant.variantId;
            mainRow.dataset.expanded = 'false';
            mainRow.dataset.medicineName = variant.medicineName;
            mainRow.dataset.activeIngredient = variant.activeIngredient;
            mainRow.dataset.category = variant.category;
            mainRow.dataset.hasExpired = variant.hasExpired;
            mainRow.dataset.hasExpiring = variant.hasExpiring;
            mainRow.dataset.isLowStock = variant.isLowStock;

            mainRow.innerHTML = buildMainRowHTML(globalIdx, variant);
            mainRow.querySelector('.expand-cell').addEventListener('click', () => toggleVariantExpansion(variant.variantId));
            tableBody.appendChild(mainRow);

            // Create detail batch rows
            variant.batches.forEach((batch, bIdx) => {
                const detailRow = document.createElement('tr');
                detailRow.className = 'detail-row hidden hover:bg-gray-100 bg-gray-50';
                detailRow.dataset.variantId = variant.variantId;
                detailRow.dataset.batchId = batch.dataset.batchId;
                detailRow.dataset.inventoryId = batch.dataset.inventoryId;
                detailRow.dataset.quantity = batch.dataset.quantity;
                detailRow.dataset.unit = batch.dataset.unit;
                detailRow.dataset.batchCode = batch.dataset.batchCode;
                detailRow.dataset.minStock = batch.dataset.minStock;
                detailRow.innerHTML = buildDetailRowHTML(globalIdx, bIdx, batch);
                tableBody.appendChild(detailRow);
            });
        });

        updatePaginationUI();
        updateBulkBar();
    }


    function buildMainRowHTML(index, variant) {
        const categoryName = variant.category || '';
        const unit = variant.unit || '';

        let html = '';
        html += '<td class="px-3 py-2 text-center cursor-pointer expand-cell">';
        html += '<span class="material-icons-outlined text-base text-gray-600 expand-icon">chevron_right</span>';
        html += '</td>';
        html += '<td class="px-3 py-2 text-center"><input type="checkbox" class="variant-select rounded border-gray-300" data-variant-id="' + variant.variantId + '"></td>';
        html += '<td class="px-3 py-2">' + index + '</td>';
        html += '<td class="px-3 py-2 font-medium text-blue-700">' + escapeHtml(variant.medicineName) + '</td>';
        html += '<td class="px-3 py-2">' + escapeHtml(variant.activeIngredient) + '</td>';
        html += '<td class="px-3 py-2">' + (variant.firstBatch.dataset.strength || '-') + '</td>';
        html += '<td class="px-3 py-2">' + escapeHtml(variant.firstBatch.dataset.dosageForm) + '</td>';
        html += '<td class="px-3 py-2">' + escapeHtml(variant.firstBatch.dataset.manufacturer) + '</td>';
        html += '<td class="px-3 py-2">';
        if (categoryName) {
            html += '<span class="text-xs bg-blue-100 px-2 py-1 rounded">' + escapeHtml(categoryName) + '</span>';
        } else {
            html += '-';
        }
        html += '</td>';
        html += '<td class="px-3 py-2 text-center font-semibold">' + variant.totalQty + ' ' + unit + '</td>';
        html += '<td class="px-3 py-2 text-center">';
        html += '<input type="number" min="0" class="min-stock-input w-24 border border-gray-300 rounded px-2 py-1 text-center" ';
        html += 'value="' + (variant.minStock !== null ? variant.minStock : '') + '" ';
        html += 'data-variant-id="' + variant.variantId + '" ';
        html += 'onclick="event.stopPropagation()">';
        html += '</td>';
        html += '<td class="px-3 py-2 text-center">';
        if (variant.hasExpired) {
            html += '<span class="inline-flex items-center gap-1 text-xs bg-red-100 text-red-800 px-2 py-1 rounded-full" title="Có lô sắp hết hạn (< 1 tuần)"><span class="material-icons-outlined text-xs">warning</span>Hết hạn</span>';
        }
        if (!variant.hasExpired && variant.hasExpiring) {
            html += '<span class="inline-flex items-center gap-1 text-xs bg-orange-100 text-orange-800 px-2 py-1 rounded-full" title="Có lô hết hạn trong 1 tháng"><span class="material-icons-outlined text-xs">error_outline</span>Sắp hết hạn</span>';
        }
        if (variant.isLowStock) {
            html += '<span class="inline-flex items-center gap-1 text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full mt-1" title="Tồn kho thấp"><span class="material-icons-outlined text-xs">inventory_2</span>Sắp hết</span>';
        }
        html += '</td>';
        return html;
    }

    function buildDetailRowHTML(index, bIdx, batch) {
        const expiryDate = batch.dataset.expiryDate;
        let expiryDisplay = '-';
        if (expiryDate) {
            const formatted = formatDate(expiryDate);
            if (batch.dataset.expiryCategory === 'week') {
                expiryDisplay = '<span class="text-red-600 font-semibold">' + formatted + '</span>';
            } else if (batch.dataset.expiryCategory === 'month') {
                expiryDisplay = '<span class="text-orange-600 font-medium">' + formatted + '</span>';
            } else {
                expiryDisplay = formatted;
            }
        }

        let html = '';
        html += '<td class="px-3 py-2"></td>';
        html += '<td class="px-3 py-2 text-center"><input type="checkbox" class="batch-select rounded border-gray-300" data-batch-id="' + batch.dataset.batchId + '"></td>';
        html += '<td class="px-3 py-2 pl-8 text-gray-600">' + index + '.' + (bIdx + 1) + '</td>';
        html += '<td class="px-3 py-2 pl-8 text-gray-700" colspan="5">';
        html += 'Mã lô: <span class="font-mono bg-gray-200 px-2 py-1 rounded">' + (batch.dataset.batchCode || '-') + '</span>';
        html += ' | HSD: ' + expiryDisplay;
        html += '</td>';
        html += '<td class="px-3 py-2"></td>';
        html += '<td class="px-3 py-2 text-center font-medium">' + batch.dataset.quantity + ' ' + batch.dataset.unit + '</td>';
        html += '<td class="px-3 py-2"></td>';
        html += '<td class="px-3 py-2"></td>';
        return html;
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const parts = dateStr.split('-');
        if (parts.length === 3) {
            return parts[2] + '/' + parts[1] + '/' + parts[0];
        }
        return dateStr;
    }

    function escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    }

    function toggleVariantExpansion(variantId) {
        const mainRow = tableBody.querySelector('.variant-row[data-variant-id="' + variantId + '"]');
        const detailRows = tableBody.querySelectorAll('.detail-row[data-variant-id="' + variantId + '"]');
        const icon = mainRow.querySelector('.expand-icon');

        const isExpanded = mainRow.dataset.expanded === 'true';
        mainRow.dataset.expanded = !isExpanded;

        detailRows.forEach(row => {
            row.classList.toggle('hidden', isExpanded);
        });

        icon.textContent = isExpanded ? 'chevron_right' : 'expand_more';
    }


    function applyFilters() {
        const term = (searchInputEl.value || '').toLowerCase().trim();
        const categoryTerm = (categoryFilterEl.value || '').toLowerCase().trim();
        const wantExpiring = filterExpiring.checked;
        const wantLowStock = filterLowStock.checked;

        filteredVariantData = variantData.filter(variant => {
            const name = (variant.medicineName || '').toLowerCase();
            const active = (variant.activeIngredient || '').toLowerCase();
            const category = (variant.category || '').toLowerCase();

            let ok = true;
            if (term) ok = name.includes(term) || active.includes(term);
            if (ok && categoryTerm) ok = category.includes(categoryTerm);
            if (ok && wantExpiring) ok = variant.hasExpired || variant.hasExpiring;
            if (ok && wantLowStock) ok = variant.isLowStock;

            return ok;
        });

        console.log('Filtered variants:', filteredVariantData.length);
        currentPage = 1;
        renderCurrentPage();
    }

    function updatePaginationUI() {
        const totalPages = Math.ceil(filteredVariantData.length / itemsPerPage);

        document.getElementById('totalItems').textContent = filteredVariantData.length;
        document.getElementById('showingFrom').textContent = filteredVariantData.length > 0 ? (currentPage - 1) * itemsPerPage + 1 : 0;
        document.getElementById('showingTo').textContent = Math.min(currentPage * itemsPerPage, filteredVariantData.length);

        const paginationButtons = document.getElementById('paginationButtons');
        paginationButtons.innerHTML = '';

        if (totalPages <= 1) return;

        const prevBtn = document.createElement('button');
        prevBtn.innerHTML = '&laquo; Trước';
        prevBtn.className = 'px-3 py-1 border rounded-md ' + (currentPage === 1 ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-white hover:bg-gray-100');
        prevBtn.disabled = currentPage === 1;
        prevBtn.onclick = () => { if (currentPage > 1) { currentPage--; renderCurrentPage(); } };
        paginationButtons.appendChild(prevBtn);

        const maxButtons = 5;
        let startPage = Math.max(1, currentPage - Math.floor(maxButtons / 2));
        let endPage = Math.min(totalPages, startPage + maxButtons - 1);

        if (endPage - startPage < maxButtons - 1) {
            startPage = Math.max(1, endPage - maxButtons + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            const pageBtn = document.createElement('button');
            pageBtn.textContent = i;
            pageBtn.className = 'px-3 py-1 border rounded-md ' + (i === currentPage ? 'bg-blue-600 text-white' : 'bg-white hover:bg-gray-100');
            pageBtn.onclick = () => { currentPage = i; renderCurrentPage(); };
            paginationButtons.appendChild(pageBtn);
        }

        const nextBtn = document.createElement('button');
        nextBtn.innerHTML = 'Sau &raquo;';
        nextBtn.className = 'px-3 py-1 border rounded-md ' + (currentPage === totalPages ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-white hover:bg-gray-100');
        nextBtn.disabled = currentPage === totalPages;
        nextBtn.onclick = () => { if (currentPage < totalPages) { currentPage++; renderCurrentPage(); } };
        paginationButtons.appendChild(nextBtn);
    }

    function updateBulkBar() {
        let count = 0;
        tableBody.querySelectorAll('.variant-select:checked').forEach(() => count++);
        tableBody.querySelectorAll('.batch-select:checked').forEach(() => count++);

        selectedCountSpan.textContent = count;
        bulkBar.classList.toggle('hidden', count === 0);
    }

    searchInputEl.addEventListener('input', applyFilters);
    categoryFilterEl.addEventListener('change', applyFilters);
    filterExpiring.addEventListener('change', applyFilters);
    filterLowStock.addEventListener('change', applyFilters);

    selectAllCheckbox.addEventListener('change', () => {
        const checked = selectAllCheckbox.checked;
        tableBody.querySelectorAll('.variant-select').forEach(cb => cb.checked = checked);
        updateBulkBar();
    });

    tableBody.addEventListener('change', e => {
        if (e.target.classList.contains('variant-select') || e.target.classList.contains('batch-select')) {
            updateBulkBar();
        }

        // Handle min stock update
        if (e.target && e.target.classList.contains('min-stock-input')) {
            const input = e.target;
            const variantId = input.dataset.variantId;
            const value = input.value === '' ? null : parseInt(input.value);

            // Update min stock for all batches of this variant
            const batchRows = Array.from(document.querySelectorAll('.batch-row[data-variant-id="' + variantId + '"]'));
            const inventoryIds = batchRows.map(row => row.dataset.inventoryId).filter(id => id);

            // Send update to backend for all batches
            inventoryIds.forEach(inventoryId => {
                fetch('/warehouse/api/inventory/' + inventoryId + '/min-stock', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ minStock: value })
                }).then(res => res.json()).then(data => {
                    if (!data || data.success !== true) {
                        console.error('Failed to save min stock for inventory ' + inventoryId);
                    }
                }).catch(err => {
                    console.error('Error saving minStock', err);
                });
            });

            // Update variant data
            const variant = variantData.find(v => v.variantId == variantId);
            if (variant) {
                variant.minStock = value;
                variant.isLowStock = value !== null && variant.totalQty < value;
                applyFilters();
            }
        }
    });

    function collectSelected(forType) {
        const items = [];

        // Collect from variant selections (all batches)
        tableBody.querySelectorAll('.variant-select:checked').forEach(cb => {
            const variantId = cb.dataset.variantId;
            const variant = variantData.find(v => v.variantId == variantId);
            if (variant) {
                variant.batches.forEach(batch => {
                    items.push({
                        variantId: Number(variantId),
                        batchId: Number(batch.dataset.batchId),
                        inventoryId: Number(batch.dataset.inventoryId),
                        medicineName: batch.dataset.medicineName || '',
                        strength: batch.dataset.strength || '',
                        unit: batch.dataset.unit || '',
                        batchCode: batch.dataset.batchCode || '',
                        minStock: batch.dataset.minStock && batch.dataset.minStock !== '' ? Number(batch.dataset.minStock) : null,
                        availableQty: Number(batch.dataset.quantity),
                        quantity: Number(batch.dataset.quantity)
                    });
                });
            }
        });

        // Collect from individual batch selections
        tableBody.querySelectorAll('.batch-select:checked').forEach(cb => {
            const batchId = cb.dataset.batchId;
            const detailRow = cb.closest('.detail-row');
            if (detailRow) {
                const variantId = detailRow.dataset.variantId;
                const variant = variantData.find(v => v.variantId == variantId);
                if (variant) {
                    const batch = variant.batches.find(b => b.dataset.batchId == batchId);
                    if (batch) {
                        items.push({
                            variantId: Number(variantId),
                            batchId: Number(batchId),
                            inventoryId: Number(detailRow.dataset.inventoryId),
                            medicineName: batch.dataset.medicineName || '',
                            strength: batch.dataset.strength || '',
                            unit: detailRow.dataset.unit || '',
                            batchCode: detailRow.dataset.batchCode || '',
                            minStock: detailRow.dataset.minStock && detailRow.dataset.minStock !== '' ? Number(detailRow.dataset.minStock) : null,
                            availableQty: Number(detailRow.dataset.quantity),
                            quantity: Number(detailRow.dataset.quantity)
                        });
                    }
                }
            }
        });

        if (items.length === 0) {
            alert('Chưa chọn thuốc nào.');
            return null;
        }

        const key = forType === 'receipt' ? 'preselectedReceiptItems' : 'preselectedExportItems';
        sessionStorage.setItem(key, JSON.stringify(items));
        window.location.href = forType === 'receipt' ? '/warehouse/receipt/create' : '/warehouse/export/create';
    }

    createReceiptBtn.addEventListener('click', () => collectSelected('receipt'));
    createExportBtn.addEventListener('click', () => collectSelected('export'));

    // Initialize category filter
    function initializeCategoryFilter() {
        const categories = new Set();
        document.querySelectorAll('.batch-row').forEach(row => {
            const cat = row.dataset.category ? row.dataset.category.trim() : '';
            if (cat && cat !== '-' && cat.toLowerCase() !== 'null') categories.add(cat);
        });

        while (categoryFilterEl.options.length > 1) categoryFilterEl.remove(1);
        const sorted = Array.from(categories).sort((a,b) => a.localeCompare(b, 'vi'));
        sorted.forEach(cat => {
            const opt = document.createElement('option');
            opt.value = cat.toLowerCase();
            opt.textContent = cat;
            categoryFilterEl.appendChild(opt);
        });
    }

    // Initialize
    initializeCategoryFilter();
    buildVariantRows();
    applyFilters();
});

