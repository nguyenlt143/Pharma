document.addEventListener('DOMContentLoaded', function() {
    // Initialize form functionality
    initializeForm();
    initializeTable();
});

function initializeForm() {
    // Set current date if not set
    const dateInput = document.querySelector('.date-field');
    if (dateInput && !dateInput.value) {
        const today = new Date();
        const formattedDate = today.toISOString().split('T')[0];
        dateInput.value = formattedDate;
    }

    // Clear validation error when branch is selected
    const branchSelect = document.getElementById('branchId');
    if (branchSelect) {
        branchSelect.addEventListener('change', function() {
            this.classList.remove('is-invalid');
            const errorDiv = this.parentElement.querySelector('.invalid-feedback');
            if (errorDiv) errorDiv.remove();
        });
    }
}

function initializeTable() {
    // Handle quantity input changes
    const qtyInputs = document.querySelectorAll('.qty-input');

    qtyInputs.forEach(input => {
        input.addEventListener('input', function() {
            validateQuantity(this);
            calculateTotals();
        });

        input.addEventListener('blur', function() {
            formatQuantity(this);
        });
    });

    // Initial calculation
    calculateTotals();
}

function validateQuantity(input) {
    const value = parseInt(input.value) || 0;
    const max = parseInt(input.getAttribute('data-available'));
    const feedbackDiv = input.nextElementSibling;

    // Remove previous validation state
    input.classList.remove('is-invalid');

    // Validate: empty or null
    if (input.value === '' || input.value === null) {
        return true; // Allow empty for optional entry
    }

    // Validate: value must be >= 0 (allow 0 for not sending)
    if (value < 0) {
        input.classList.add('is-invalid');
        feedbackDiv.textContent = 'Số lượng không được âm';
        return false;
    }

    // Validate: value must not exceed available quantity
    if (value > max) {
        input.classList.add('is-invalid');
        feedbackDiv.textContent = `Vượt quá tồn kho (${max})`;
        return false;
    }

    return true;
}

function formatQuantity(input) {
    const value = parseInt(input.value);
    if (isNaN(value) || value < 0) {
        input.value = 0;
    } else {
        input.value = value.toString();
    }
}

function calculateTotals() {
    const qtyInputs = document.querySelectorAll('.qty-input');
    let totalAmount = 0;

    qtyInputs.forEach(input => {
        const quantity = parseInt(input.value) || 0;
        const price = parseFloat(input.getAttribute('data-price')) || 0;
        totalAmount += quantity * price;
    });

    // Update total display
    const totalAmountElement = document.getElementById('totalAmount');
    if (totalAmountElement) {
        totalAmountElement.textContent = formatCurrency(totalAmount);
    }
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'decimal',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(amount);
}

function showToast(message, type = 'info') {
    // Simple toast notification
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'error' ? '#EF4444' : type === 'warning' ? '#F59E0B' : '#10B981'};
        color: white;
        border-radius: 8px;
        z-index: 10000;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    `;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}

function saveDraft() {
    showToast('Chức năng lưu nháp đang được phát triển', 'info');
}

function createExport() {
    // Collect form data
    const branchId = document.getElementById('branchId').value;
    const requestId = document.getElementById('requestId').value;
    const createdDate = document.getElementById('createdDate').value;
    const note = document.getElementById('note').value;

    // Clear previous validation errors
    const branchSelect = document.getElementById('branchId');
    branchSelect.classList.remove('is-invalid');
    const existingError = branchSelect.parentElement.querySelector('.invalid-feedback');
    if (existingError) existingError.remove();

    if (!branchId) {
        branchSelect.classList.add('is-invalid');
        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.style.display = 'block';
        errorDiv.style.color = '#dc3545';
        errorDiv.style.fontSize = '0.875rem';
        errorDiv.style.marginTop = '0.25rem';
        errorDiv.textContent = 'Vui lòng chọn chi nhánh nhận';
        branchSelect.parentElement.appendChild(errorDiv);
        showToast('Vui lòng chọn chi nhánh nhận', 'error');
        branchSelect.focus();
        return;
    }

    // Collect batch data
    const qtyInputs = document.querySelectorAll('.qty-input');
    const details = [];
    let hasQuantity = false;

    qtyInputs.forEach(input => {
        const quantity = parseInt(input.value) || 0;
        if (quantity > 0) {
            hasQuantity = true;
            details.push({
                inventoryId: parseInt(input.getAttribute('data-inventory-id')),
                batchId: parseInt(input.getAttribute('data-batch-id')),
                variantId: parseInt(input.getAttribute('data-variant-id')),
                quantity: quantity,
                price: parseFloat(input.getAttribute('data-price'))
            });
        }
    });

    if (!hasQuantity) {
        showToast('Vui lòng nhập số lượng xuất cho ít nhất một lô hàng', 'error');
        return;
    }

    // Validate all quantities before submission
    let hasValidationErrors = false;
    qtyInputs.forEach(input => {
        const quantity = parseInt(input.value) || 0;
        if (quantity > 0) {
            if (!validateQuantity(input)) {
                hasValidationErrors = true;
            }
        }
    });

    if (hasValidationErrors) {
        showToast('Vui lòng kiểm tra lại số lượng nhập', 'error');
        return;
    }

    // Validate requested quantities
    const validationResult = validateRequestedQuantities();
    if (!validationResult.valid) {
        showToast(validationResult.message, 'error');
        return;
    }

    const exportData = {
        requestId: requestId ? parseInt(requestId) : null,
        branchId: parseInt(branchId),
        createdDate: createdDate,
        note: note,
        details: details
    };

    console.log('Export data:', exportData);

    // Disable button to prevent double submission
    const createButton = document.querySelector('.btn-primary');
    const originalText = createButton.textContent;
    createButton.disabled = true;
    createButton.textContent = 'Đang tạo...';

    // Send to backend
    fetch('/warehouse/export/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(exportData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('Tạo phiếu xuất thành công! Trạng thái: Đang giao', 'success');
            console.log('Movement ID:', data.movementId);

            // Redirect to receipt list after 1.5 seconds
            setTimeout(() => {
                window.location.href = '/warehouse/receipt-list';
            }, 1500);
        } else {
            showToast('Lỗi: ' + data.message, 'error');
            createButton.disabled = false;
            createButton.textContent = originalText;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Có lỗi xảy ra khi tạo phiếu xuất', 'error');
        createButton.disabled = false;
        createButton.textContent = originalText;
    });
}

function validateRequestedQuantities() {
    // Group quantities by variant
    const qtyInputs = document.querySelectorAll('.qty-input');
    const variantQuantities = {};

    qtyInputs.forEach(input => {
        const variantId = input.getAttribute('data-variant-id');
        const quantity = parseInt(input.value) || 0;

        if (!variantQuantities[variantId]) {
            variantQuantities[variantId] = 0;
        }
        variantQuantities[variantId] += quantity;
    });

    // Check against requested quantities
    for (const variantId in variantQuantities) {
        const medicineRow = document.querySelector(`.medicine-row[data-variant-id="${variantId}"]`);
        if (medicineRow) {
            const requestedQty = parseInt(medicineRow.querySelector('.requested-qty').textContent);
            const totalQty = variantQuantities[variantId];

            if (totalQty > requestedQty) {
                const medicineName = medicineRow.querySelector('.medicine-name').textContent;
                return {
                    valid: false,
                    message: `Tổng số lượng xuất của "${medicineName}" (${totalQty}) vượt quá số lượng yêu cầu (${requestedQty})`
                };
            }
        }
    }

    return { valid: true };
}
