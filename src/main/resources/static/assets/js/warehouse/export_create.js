document.addEventListener('DOMContentLoaded', function() {
    // Initialize form functionality
    initializeForm();
    initializeTable();
    initializeSearch();
});

function initializeForm() {
    // Auto-generate form ID if needed
    const formIdInput = document.querySelector('input[value="PXK20240521001"]');

    // Set current date
    const dateInput = document.querySelector('.date-field');
    if (dateInput && !dateInput.value) {
        const today = new Date();
        const formattedDate = `${String(today.getMonth() + 1).padStart(2, '0')}/${String(today.getDate()).padStart(2, '0')}/${today.getFullYear()}`;
        dateInput.value = formattedDate;
    }

    // Handle form submission
    const saveButton = document.querySelector('.btn-secondary');
    const createButton = document.querySelector('.btn-primary');

    saveButton.addEventListener('click', function() {
        handleSaveDraft();
    });

    createButton.addEventListener('click', function() {
        handleCreateExport();
    });
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
}

function initializeSearch() {
    const searchInput = document.querySelector('.search-input input');

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        filterMedicines(searchTerm);
    });
}

function validateQuantity(input) {
    const value = parseInt(input.value);
    const row = input.closest('tr');
    const requestedQty = parseInt(row.querySelector('.requested-qty').textContent);

    if (isNaN(value) || value < 0) {
        input.style.borderColor = '#EF4444';
        return false;
    }

    if (value > requestedQty) {
        input.style.borderColor = '#F59E0B';
        showWarning(`Số lượng gửi không được vượt quá số lượng yêu cầu (${requestedQty})`);
    } else {
        input.style.borderColor = '#E5E7EB';
    }

    return true;
}

function formatQuantity(input) {
    const value = parseInt(input.value);
    if (!isNaN(value)) {
        input.value = value.toString();
    }
}

function calculateTotals() {
    const qtyInputs = document.querySelectorAll('.qty-input');
    let totalSent = 0;

    qtyInputs.forEach(input => {
        const value = parseInt(input.value) || 0;
        totalSent += value;
    });

    // Update totals display if needed
    console.log('Total medicines to be sent:', totalSent);
}

function filterMedicines(searchTerm) {
    const tableRows = document.querySelectorAll('.medicine-table tbody tr');

    tableRows.forEach(row => {
        const medicineName = row.querySelector('.medicine-name').textContent.toLowerCase();
        const unit = row.querySelector('.unit').textContent.toLowerCase();
        const dosage = row.querySelector('.dosage').textContent.toLowerCase();

        const matches = medicineName.includes(searchTerm) ||
            unit.includes(searchTerm) ||
            dosage.includes(searchTerm);

        row.style.display = matches ? '' : 'none';
    });
}

function handleSaveDraft() {
    const formData = collectFormData();

    // Simulate saving draft
    showNotification('Đã lưu nháp thành công', 'success');

    // In a real application, you would send this data to the server
    console.log('Saving draft:', formData);
}

function handleCreateExport() {
    const formData = collectFormData();

    // Validate form
    if (!validateForm(formData)) {
        return;
    }

    // Simulate creating export
    showNotification('Đã tạo phiếu xuất thành công', 'success');

    // In a real application, you would send this data to the server
    console.log('Creating export:', formData);
}

function collectFormData() {
    const formCode = document.querySelector('input[value="PXK20240521001"]').value;
    const createDate = document.querySelector('.date-field').value;
    const branch = document.querySelector('.form-select').value;
    const notes = document.querySelector('.form-textarea').value;

    const medicines = [];
    const tableRows = document.querySelectorAll('.medicine-table tbody tr');

    tableRows.forEach((row, index) => {
        const medicine = {
            stt: index + 1,
            name: row.querySelector('.medicine-name').textContent,
            unit: row.querySelector('.unit').textContent,
            dosage: row.querySelector('.dosage').textContent,
            requestedQty: parseInt(row.querySelector('.requested-qty').textContent),
            sentQty: parseInt(row.querySelector('.qty-input').value) || 0
        };
        medicines.push(medicine);
    });

    return {
        formCode,
        createDate,
        branch,
        notes,
        medicines
    };
}

function validateForm(formData) {
    // Check if any medicines have quantities to send
    const hasQuantities = formData.medicines.some(medicine => medicine.sentQty > 0);

    if (!hasQuantities) {
        showNotification('Vui lòng nhập số lượng thuốc cần xuất', 'error');
        return false;
    }

    // Check if branch is selected
    if (!formData.branch) {
        showNotification('Vui lòng chọn chi nhánh nhận', 'error');
        return false;
    }

    return true;
}

function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;

    // Style the notification
    Object.assign(notification.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        padding: '12px 24px',
        borderRadius: '8px',
        color: 'white',
        fontWeight: '500',
        zIndex: '1000',
        opacity: '0',
        transform: 'translateY(-20px)',
        transition: 'all 0.3s ease'
    });

    // Set background color based on type
    const colors = {
        success: '#17CF17',
        error: '#EF4444',
        warning: '#F59E0B',
        info: '#3B82F6'
    };
    notification.style.backgroundColor = colors[type] || colors.info;

    // Add to page
    document.body.appendChild(notification);

    // Animate in
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateY(0)';
    }, 100);

    // Remove after 3 seconds
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateY(-20px)';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

function showWarning(message) {
    showNotification(message, 'warning');
}

// Utility functions for number formatting
function formatNumber(num) {
    return new Intl.NumberFormat('vi-VN').format(num);
}

function parseNumber(str) {
    return parseInt(str.replace(/[^\d]/g, '')) || 0;
}

// Export functions for potential external use
window.PharmacyExportForm = {
    collectFormData,
    validateForm,
    showNotification
};
