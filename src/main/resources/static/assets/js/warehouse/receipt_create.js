// Warehouse Management System JavaScript
class WarehouseManager {
    constructor() {
        this.init();
    }

    init() {
        this.bindEvents();
        this.setCurrentDateTime();
        this.renderTableFromBackend();
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

        // Delete buttons (event delegation)
        document.addEventListener('click', (e) => {
            if (e.target.closest('.delete-button')) {
                this.handleDeleteProduct(e.target.closest('tr'));
            }
        });

        // Action buttons
        const saveButton = document.querySelector('.btn-secondary');
        const completeButton = document.querySelector('.btn-primary');

        if (saveButton) saveButton.addEventListener('click', () => this.handleSaveDraft());
        if (completeButton) completeButton.addEventListener('click', () => this.handleComplete());

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
            dateInput.value = `${year}-${month}-${day}`; // format YYYY-MM-DD
        }

        if (timeInput && !timeInput.value) {
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');
            timeInput.value = `${hours}:${minutes}`;
        }
    }

    renderTableFromBackend() {
        // Lấy dữ liệu từ JTE serialize sang JSON
        const inventoryDetails = window.inventoryDetails || [];
        const tbody = document.getElementById('productTableBody');
        tbody.innerHTML = '';

        if (inventoryDetails.length > 0) {
            inventoryDetails.forEach((d, index) => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${index + 1}</td>
                    <td>${d.medicineName || ""}</td>
                    <td>${d.unit || ""}</td>
                    <td>${d.strength || ""}</td>
                    <td><input type="text" class="table-input disabled" value="${d.batchCode || ""}" disabled></td>
                    <td><input type="text" class="table-input disabled" value="${d.mfgDate || ""}" disabled></td>
                    <td><input type="text" class="table-input disabled" value="${d.expiryDate || ""}" disabled></td>
                    <td><input type="number" class="table-input" value="${d.quantity || 0}"></td>
                    <td><input type="number" class="table-input" value="${d.price || 0}"></td>
                    <td><button class="delete-button"><span class="material-icons">delete</span></button></td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            const tr = document.createElement('tr');
            tr.innerHTML = '<td colspan="10" style="text-align:center; color:#999;">Chưa có thuốc nào trong phiếu nhập</td>';
            tbody.appendChild(tr);
        }
    }

    handleSearch(query) {
        console.log('Searching for:', query);
        // TODO: filter table rows based on search query
    }

    handleAddProduct() {
        const searchValue = document.querySelector('.search-input').value.trim();
        if (!searchValue) {
            alert('Vui lòng nhập tên thuốc để tìm kiếm');
            return;
        }

        console.log('Adding product:', searchValue);
        // TODO: add new row dynamically or open modal to select product
    }

    handleDeleteProduct(row) {
        if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
            row.remove();
            this.updateRowNumbers();
        }
    }

    updateRowNumbers() {
        const rows = document.querySelectorAll('.product-table tbody tr');
        rows.forEach((row, index) => {
            const numberCell = row.querySelector('td:first-child');
            if (numberCell) numberCell.textContent = index + 1;
        });
    }

    handleSaveDraft() {
        if (this.validateForm()) {
            console.log('Saving draft...');
            showNotification('Đã lưu nháp thành công!', 'success');
            // TODO: call backend API to save draft
        }
    }

    handleComplete() {
        if (this.validateForm()) {
            console.log('Completing import...');
            showNotification('Hoàn thành nhập kho thành công!', 'success');
            // TODO: call backend API to complete
        }
    }

    validateForm() {
        const requiredFields = [
            document.getElementById('supplier'),
            document.getElementById('import-date')
            // Add time input if exists
        ];

        let isValid = true;
        requiredFields.forEach(field => {
            if (!field || !field.value.trim()) {
                field.style.borderColor = '#EF4444';
                isValid = false;
            } else {
                field.style.borderColor = '#E5E7EB';
            }
        });

        if (!isValid) {
            showNotification('Vui lòng điền đầy đủ thông tin bắt buộc!', 'error');
        }

        return isValid;
    }

    handleKeyboardShortcuts(e) {
        switch (e.key) {
            case 'F4':
                e.preventDefault();
                document.getElementById('supplier').focus();
                break;
            case 'F7':
                e.preventDefault();
                this.handleAddProduct();
                break;
            case 'F8':
                e.preventDefault();
                this.handleSaveDraft();
                break;
            case 'F9':
                e.preventDefault();
                this.handleComplete();
                break;
        }
    }
}

// Initialize when DOM loaded
document.addEventListener('DOMContentLoaded', () => {
    new WarehouseManager();
});

// Utility: notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
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

    const colors = {
        info: '#4361EE',
        success: '#10B981',
        warning: '#F59E0B',
        error: '#EF4444'
    };
    notification.style.backgroundColor = colors[type] || colors.info;

    document.body.appendChild(notification);
    setTimeout(() => notification.style.transform = 'translateX(0)', 100);
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => document.body.removeChild(notification), 300);
    }, 3000);
}
