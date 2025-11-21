// Danh sách thuốc đã chọn
let selectedMedicines = [];

// Debounce function để tránh search quá nhiều
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Tìm kiếm thuốc
const searchMedicines = debounce(function(query) {
    if (query.length < 2) {
        $('#searchResults').addClass('hidden').empty();
        return;
    }

    $.ajax({
        url: '/inventory/api/medicines/search',
        method: 'GET',
        data: { query: query },
        success: function(medicines) {
            displaySearchResults(medicines);
        },
        error: function(xhr) {
            console.error('Error searching medicines:', xhr);
            $('#searchResults').addClass('hidden');
        }
    });
}, 300);

// Hiển thị kết quả tìm kiếm
function displaySearchResults(medicines) {
    const $results = $('#searchResults');
    $results.empty();

    if (medicines.length === 0) {
        $results.append(`
            <div class="p-4 text-center text-gray-500">
                Không tìm thấy thuốc nào
            </div>
        `);
    } else {
        medicines.forEach(medicine => {
            const expiryWarning = checkExpiryWarning(medicine.expiryDate);
            const $item = $(`
                <div class="p-4 hover:bg-gray-50 cursor-pointer border-b border-gray-100 search-result-item" 
                     data-medicine='${JSON.stringify(medicine)}'>
                    <div class="flex justify-between items-start">
                        <div class="flex-1">
                            <p class="font-semibold text-gray-900">${medicine.medicineName}</p>
                            <p class="text-sm text-gray-600">
                                ${medicine.activeIngredient ? medicine.activeIngredient + ' - ' : ''}
                                ${medicine.strength || ''} ${medicine.dosageForm || ''}
                            </p>
                            <div class="flex gap-4 mt-1 text-xs text-gray-500">
                                <span>Lô: <strong>${medicine.batchCode}</strong></span>
                                <span class="${expiryWarning ? 'text-red-600 font-semibold' : ''}">
                                    HSD: ${medicine.expiryDate}
                                </span>
                                <span>Tồn: <strong class="text-blue-600">${medicine.currentStock} ${medicine.unit}</strong></span>
                            </div>
                        </div>
                        <div class="ml-4">
                            <span class="material-icons-outlined text-blue-600">add_circle_outline</span>
                        </div>
                    </div>
                </div>
            `);

            $item.on('click', function() {
                const medicineData = JSON.parse($(this).attr('data-medicine'));
                addMedicine(medicineData);
            });

            $results.append($item);
        });
    }

    $results.removeClass('hidden');
}

// Kiểm tra cảnh báo hạn sử dụng (< 6 tháng)
function checkExpiryWarning(expiryDateStr) {
    if (!expiryDateStr) return false;

    const parts = expiryDateStr.split('/');
    if (parts.length !== 3) return false;

    const expiryDate = new Date(parts[2], parts[1] - 1, parts[0]);
    const today = new Date();
    const sixMonthsLater = new Date();
    sixMonthsLater.setMonth(sixMonthsLater.getMonth() + 6);

    return expiryDate < sixMonthsLater;
}

// Thêm thuốc vào danh sách
function addMedicine(medicine) {
    // Kiểm tra đã tồn tại chưa (cùng variant và batch)
    const exists = selectedMedicines.find(m =>
        m.variantId === medicine.variantId && m.batchId === medicine.batchId
    );

    if (exists) {
        alert('Thuốc này đã được thêm vào danh sách!');
        return;
    }

    // Thêm vào danh sách với số lượng mặc định = 0
    selectedMedicines.push({
        ...medicine,
        requestQuantity: 1
    });

    renderSelectedMedicines();

    // Clear search
    $('#medicineSearch').val('');
    $('#searchResults').addClass('hidden').empty();
}

// Render danh sách thuốc đã chọn
function renderSelectedMedicines() {
    const $tbody = $('#selectedMedicinesList');
    $tbody.empty();

    if (selectedMedicines.length === 0) {
        $tbody.append(`
            <tr id="emptyRow">
                <td colspan="9" class="px-4 py-8 text-center text-gray-500">
                    Chưa có thuốc nào được chọn. Vui lòng tìm kiếm và chọn thuốc từ kho tổng.
                </td>
            </tr>
        `);
        return;
    }

    selectedMedicines.forEach((medicine, index) => {
        const expiryWarning = checkExpiryWarning(medicine.expiryDate);
        const $row = $(`
            <tr class="hover:bg-gray-50">
                <td class="px-4 py-3">${index + 1}</td>
                <td class="px-4 py-3">
                    <div class="font-medium text-gray-900">${medicine.medicineName}</div>
                    <div class="text-sm text-gray-500">${medicine.dosageForm || ''}</div>
                </td>
                <td class="px-4 py-3 text-sm">${medicine.activeIngredient || '-'}</td>
                <td class="px-4 py-3 text-sm">${medicine.strength || '-'}</td>
                <td class="px-4 py-3">
                    <span class="font-mono text-sm bg-gray-100 px-2 py-1 rounded">
                        ${medicine.batchCode}
                    </span>
                </td>
                <td class="px-4 py-3">
                    <span class="${expiryWarning ? 'text-red-600 font-semibold' : ''}">
                        ${medicine.expiryDate}
                    </span>
                </td>
                <td class="px-4 py-3 text-center font-semibold text-blue-600">
                    ${medicine.currentStock} ${medicine.unit}
                </td>
                <td class="px-4 py-3">
                    <div class="flex items-center gap-2">
                        <input 
                            type="number" 
                            class="w-24 px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                            min="1"
                            max="${medicine.currentStock}"
                            value="${medicine.requestQuantity}"
                            data-index="${index}">
                        <span class="text-sm text-gray-600">${medicine.unit}</span>
                    </div>
                </td>
                <td class="px-4 py-3 text-center">
                    <button 
                        class="text-red-600 hover:text-red-800 p-1"
                        data-index="${index}"
                        title="Xóa">
                        <span class="material-icons-outlined">delete</span>
                    </button>
                </td>
            </tr>
        `);

        // Handle quantity change
        $row.find('input[type="number"]').on('change', function() {
            const newQuantity = parseInt($(this).val());
            const max = parseInt($(this).attr('max'));

            if (newQuantity < 1) {
                $(this).val(1);
                selectedMedicines[index].requestQuantity = 1;
            } else if (newQuantity > max) {
                $(this).val(max);
                selectedMedicines[index].requestQuantity = max;
                alert(`Số lượng không được vượt quá tồn kho (${max})`);
            } else {
                selectedMedicines[index].requestQuantity = newQuantity;
            }
        });

        // Handle delete
        $row.find('button').on('click', function() {
            const idx = parseInt($(this).attr('data-index'));
            if (confirm('Bạn có chắc muốn xóa thuốc này khỏi danh sách?')) {
                selectedMedicines.splice(idx, 1);
                renderSelectedMedicines();
            }
        });

        $tbody.append($row);
    });
}

// Submit phiếu nhập
function submitImportRequest() {
    if (selectedMedicines.length === 0) {
        alert('Vui lòng chọn ít nhất một thuốc!');
        return;
    }

    // Validate all quantities
    const hasInvalidQuantity = selectedMedicines.some(m => !m.requestQuantity || m.requestQuantity < 1);
    if (hasInvalidQuantity) {
        alert('Vui lòng nhập số lượng hợp lệ cho tất cả thuốc!');
        return;
    }

    const note = $('#importNote').val().trim();

    const requestData = {
        note: note,
        items: selectedMedicines.map(m => ({
            variantId: m.variantId,
            batchId: m.batchId,
            quantity: m.requestQuantity
        }))
    };

    // Disable submit button
    const $submitBtn = $('#submitImport');
    $submitBtn.prop('disabled', true).html('<span class="material-icons-outlined animate-spin">refresh</span> Đang xử lý...');

    $.ajax({
        url: '/inventory/import/submit',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(requestData),
        success: function(response) {
            if (response.success) {
                alert('Tạo phiếu yêu cầu nhập kho thành công!\nMã phiếu: ' + response.code + '\nPhiếu sẽ được gửi đến kho tổng để xác nhận.');
                window.location.href = '/inventory/import/list';
            } else {
                alert('Có lỗi: ' + (response.message || 'Unknown error'));
                $submitBtn.prop('disabled', false).html('<span class="material-icons-outlined">check_circle</span> Hoàn tất và gửi yêu cầu');
            }
        },
        error: function(xhr) {
            console.error('Error submitting import:', xhr);
            const errorMsg = xhr.responseJSON?.message || 'Có lỗi xảy ra khi tạo phiếu nhập. Vui lòng thử lại!';
            alert(errorMsg);
            $submitBtn.prop('disabled', false).html('<span class="material-icons-outlined">check_circle</span> Hoàn tất và gửi yêu cầu');
        }
    });
}

// Event Listeners
$(document).ready(function() {
    // Search input
    $('#medicineSearch').on('input', function() {
        const query = $(this).val().trim();
        searchMedicines(query);
    });

    // Click outside to close search results
    $(document).on('click', function(e) {
        if (!$(e.target).closest('#medicineSearch, #searchResults').length) {
            $('#searchResults').addClass('hidden');
        }
    });

    // Submit button
    $('#submitImport').on('click', submitImportRequest);

    // Initial render
    renderSelectedMedicines();
});

