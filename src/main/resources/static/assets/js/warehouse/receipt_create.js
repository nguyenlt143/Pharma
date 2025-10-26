/ Warehouse Management System JavaScript

class WarehouseManager {
    constructor() {
        this.products = [
            {
                id: 1,
                name: 'Kaceni',
                unit: 'Viên',
                dosage: '500mg',
                batchNumber: '123',
                manufacturingDate: '10/10/2025',
                expiryDate: '10/10/2026',
                quantity: '1,000',
                price: '100,000'
            },
            {
                id: 2,
                name: 'Panadol',
                unit: 'Viên',
                dosage: '500mg',
                batchNumber: '69696',
                manufacturingDate: '10/10/2025',
                expiryDate: '10/10/2025',
                quantity: '1,000',
                price: '100,000'
            }
        ];

        this.init();
    }

    init() {
        this.bindEvents();
        this.setCurrentDateTime();
    }

    bindEvents() {
        // Search functionality
        const searchInput = document.querySelector('.search-input');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => this.handleSearch(e.target.value));
        }

        // Add product button
        const addButton = document.querySelector('.add-button');
        if (addButton) {
            addButton.addEventListener('click', () => this.handleAddProduct());
        }

        // Delete buttons
        document.addEventListener('click', (e) => {
            if (e.target.closest('.delete-button')) {
                this.handleDeleteProduct(e.target.closest('tr'));
            }
        });

        // Form inputs
        const quantityInputs = document.querySelectorAll('.table-input:not(.disabled)');
        quantityInputs.forEach(input => {
            input.addEventListener('input', (e) => this.formatNumber(e.target));
            input.addEventListener('blur', (e) => this.validateInput(e.target));
        });

        // Action buttons
        const saveButton = document.querySelector('.btn-secondary');
        const completeButton = document.querySelector('.btn-primary');

        if (saveButton) {
            saveButton.addEventListener('click', () => this.handleSaveDraft());
        }

        if (completeButton) {
            completeButton.addEventListener('click', () => this.handleComplete());
        }

        // Keyboard shortcuts
        document.addEventListener('keydown', (e) => this.handleKeyboardShortcuts(e));
    }

    setCurrentDateTime() {
        const now = new Date();
        const dateInput = document.getElementById('import-date');
        const timeInput = document.getElementById('import-time');

        if (dateInput && !dateInput.value) {
            const day = String(now.getDate()).padStart(2, '0');
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const year = now.getFullYear();
            dateInput.value = `${day}/${month}/${year}`;
        }

        if (timeInput && !timeInput.value) {
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');
            timeInput.value = `${hours}:${minutes}`;
        }
    }

    handleSearch(query) {
        console.log('Searching for:', query);
        // Implement search functionality here
        // This would typically filter products or make an API call
    }

    handleAddProduct() {
        const searchValue = document.querySelector('.search-input').value;
        if (!searchValue.trim()) {
            alert('Vui lòng nhập tên thuốc để tìm kiếm');
            return;
        }

        console.log('Adding product:', searchValue);
        // Implement add product functionality here
        // This would typically open a modal or add a new row to the table
    }

    handleDeleteProduct(row) {
        if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
            row.remove();
            this.updateRowNumbers();
            console.log('Product deleted');
        }
    }

    updateRowNumbers() {
        const rows = document.querySelectorAll('.product-table tbody tr');
        rows.forEach((row, index) => {
            const numberCell = row.querySelector('td:first-child');
            if (numberCell) {
                numberCell.textContent = index + 1;
            }
        });
    }

    formatNumber(input) {
        let value = input.value.replace(/[^\d]/g, '');
        if (value) {
            value = parseInt(value).toLocaleString('vi-VN');
            input.value = value;
        }
    }

    validateInput(input) {
        if (!input.value.trim()) {
            input.style.borderColor = '#EF4444';
            return false;
        }
        input.style.borderColor = '#E5E7EB';
        return true;
    }

    handleSaveDraft() {
        if (this.validateForm()) {
            console.log('Saving draft...');
            alert('Đã lưu nháp thành công!');
            // Implement save draft functionality here
        }
    }

    handleComplete() {
        if (this.validateForm()) {
            console.log('Completing import...');
            alert('Hoàn thành nhập kho thành công!');
            // Implement complete functionality here
        }
    }

    validateForm() {
        const requiredFields = [
            document.getElementById('supplier'),
            document.getElementById('import-date'),
            document.getElementById('import-time')
        ];

        let isValid = true;
        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                field.style.borderColor = '#EF4444';
                isValid = false;
            } else {
                field.style.borderColor = '#E5E7EB';
            }
        });

        if (!isValid) {
            alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
        }

        return isValid;
    }

    handleKeyboardShortcuts(e) {
        // F4 - Focus supplier input
        if (e.key === 'F4') {
            e.preventDefault();
            document.getElementById('supplier').focus();
        }

        // F7 - Add product
        if (e.key === 'F7') {
            e.preventDefault();
            this.handleAddProduct();
        }

        // F8 - Save draft
        if (e.key === 'F8') {
            e.preventDefault();
            this.handleSaveDraft();
        }

        // F9 - Complete
        if (e.key === 'F9') {
            e.preventDefault();
            this.handleComplete();
        }
    }
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new WarehouseManager();
});

// Utility functions
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
        color: '#FFFFFF',
        fontWeight: '500',
        zIndex: '1000',
        transform: 'translateX(100%)',
        transition: 'transform 0.3s ease'
    });

    // Set background color based on type
    const colors = {
        info: '#4361EE',
        success: '#10B981',
        warning: '#F59E0B',
        error: '#EF4444'
    };
    notification.style.backgroundColor = colors[type] || colors.info;

    // Add to DOM and animate in
    document.body.appendChild(notification);
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);

    // Remove after 3 seconds
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// Export for potential module usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = WarehouseManager;
}
