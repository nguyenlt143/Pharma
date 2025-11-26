document.addEventListener('DOMContentLoaded', function() {
    const supplierInput = document.getElementById('supplier');
    const importDateInput = document.getElementById('import-date');
    const productTableBody = document.getElementById('productTableBody');
    const totalAmountElement = document.getElementById('totalAmount');
    const btnComplete = document.getElementById('btnComplete');
    const btnCancel = document.getElementById('btnCancel');
    const btnAddFromCatalog = document.getElementById('btnAddFromCatalog');
    const searchInput = document.querySelector('.search-input');

    // Set ngày hiện tại nếu chưa có
    if (!importDateInput.value) {
        importDateInput.value = new Date().toISOString().split('T')[0];
    }

    // Tính tổng tiền
    function calculateTotal() {
        let total = 0;
        document.querySelectorAll('.table-row').forEach(row => {
            const quantity = parseFloat(row.querySelector('.quantity-input').value) || 0;
            const price = parseFloat(row.querySelector('.price-input').value) || 0;
            total += quantity * price;
        });
        totalAmountElement.textContent = total.toLocaleString('vi-VN');
    }

    // Cập nhật STT
    function updateRowNumbers() {
        document.querySelectorAll('.table-row').forEach((row, index) => {
            row.querySelector('.col-stt').textContent = index + 1;
        });
    }

    // Thêm dòng sản phẩm mới
    function addProductRow(product) {
        const rowCount = productTableBody.querySelectorAll('.table-row').length;
        const row = document.createElement('tr');
        row.className = 'table-row';
        row.dataset.variantId = product.variantId;

        row.innerHTML = `
            <td class="col-stt">${rowCount + 1}</td>
            <td class="col-medicine">${product.medicineName}</td>
            <td class="col-unit">${product.unit}</td>
            <td class="col-concentration">${product.concentration}</td>
            <td class="col-batch">
                <input type="text" class="batch-input" placeholder="Nhập số lô" required>
            </td>
            <td class="col-manufacture-date">
                <input type="date" class="manufacture-date-input" required>
            </td>
            <td class="col-expiry-date">
                <input type="date" class="expiry-date-input" required>
            </td>
            <td class="col-quantity">
                <input type="number" class="quantity-input" value="1" min="1" required>
            </td>
            <td class="col-price">
                <input type="number" class="price-input" value="0" min="0" step="1000" placeholder="Giá nhập" required>
            </td>
            <td class="col-actions">
                <button type="button" class="btn-delete" title="Xóa">
                    <span class="material-icons">delete</span>
                </button>
            </td>
        `;

        productTableBody.appendChild(row);
        calculateTotal();
    }

    // Lắng nghe thay đổi số lượng và giá
    productTableBody.addEventListener('input', function(e) {
        if (e.target.classList.contains('quantity-input') ||
            e.target.classList.contains('price-input')) {
            calculateTotal();
        }
    });

    // Xóa dòng
    productTableBody.addEventListener('click', function(e) {
        if (e.target.closest('.btn-delete')) {
            if (confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
                e.target.closest('.table-row').remove();
                updateRowNumbers();
                calculateTotal();
            }
        }
    });

    // Tìm kiếm nhà cung cấp (F4)
    supplierInput.addEventListener('click', function() {
        openSupplierModal();
    });

    document.addEventListener('keydown', function(e) {
        if (e.key === 'F4') {
            e.preventDefault();
            openSupplierModal();
        }
        if (e.key === 'F7') {
            e.preventDefault();
            openProductCatalog();
        }
    });

    // Thêm sản phẩm từ danh mục
    btnAddFromCatalog.addEventListener('click', openProductCatalog);

    // Validate form
    function validateForm() {
        if (!supplierInput.dataset.supplierId) {
            alert('Vui lòng chọn nhà cung cấp');
            supplierInput.focus();
            return false;
        }

        if (!importDateInput.value) {
            alert('Vui lòng chọn ngày nhập');
            importDateInput.focus();
            return false;
        }

        const rows = productTableBody.querySelectorAll('.table-row');
        if (rows.length === 0) {
            alert('Vui lòng thêm ít nhất 1 sản phẩm');
            return false;
        }

        for (let row of rows) {
            const batchInput = row.querySelector('.batch-input');
            const mfgInput = row.querySelector('.manufacture-date-input');
            const expInput = row.querySelector('.expiry-date-input');
            const qtyInput = row.querySelector('.quantity-input');
            const priceInput = row.querySelector('.price-input');

            if (!batchInput.value.trim()) {
                alert('Vui lòng nhập số lô cho tất cả sản phẩm');
                batchInput.focus();
                return false;
            }

            if (!mfgInput.value) {
                alert('Vui lòng nhập ngày sản xuất cho tất cả sản phẩm');
                mfgInput.focus();
                return false;
            }

            if (!expInput.value) {
                alert('Vui lòng nhập hạn sử dụng cho tất cả sản phẩm');
                expInput.focus();
                return false;
            }

            const mfgDate = new Date(mfgInput.value);
            const expDate = new Date(expInput.value);

            if (expDate <= mfgDate) {
                alert('Hạn sử dụng phải sau ngày sản xuất');
                expInput.focus();
                return false;
            }

            if (!qtyInput.value || parseInt(qtyInput.value) < 1) {
                alert('Số lượng phải lớn hơn 0');
                qtyInput.focus();
                return false;
            }

            if (!priceInput.value || parseFloat(priceInput.value) < 0) {
                alert('Vui lòng nhập giá nhập hợp lệ');
                priceInput.focus();
                return false;
            }
        }

        return true;
    }

    // Thu thập dữ liệu form
    function collectFormData() {
        const details = [];

        document.querySelectorAll('.table-row').forEach(row => {
            const price = parseFloat(row.querySelector('.price-input').value);

            details.push({
                variantId: row.dataset.variantId,
                batchCode: row.querySelector('.batch-input').value.trim(),
                manufactureDate: row.querySelector('.manufacture-date-input').value,
                expiryDate: row.querySelector('.expiry-date-input').value,
                quantity: parseInt(row.querySelector('.quantity-input').value),
                price: price,           // Giá nhập kho
                snapCost: price         // Theo flow: price = snap_cost khi nhập từ supplier
            });
        });

        return {
            movementType: 'SUP_TO_WARE',
            supplierId: supplierInput.dataset.supplierId,
            movementDate: importDateInput.value,
            status: 'COMPLETED',
            details: details
        };
    }

    // Gửi phiếu nhập
    function submitReceipt(data) {
        fetch('/api/warehouse/receipts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Lỗi khi tạo phiếu nhập');
                }
                return response.json();
            })
            .then(result => {
                alert('Tạo phiếu nhập thành công!');
                window.location.href = '/warehouse/receipt/list';
            })
            .catch(error => {
                alert('Có lỗi xảy ra: ' + error.message);
                console.error(error);
            });
    }

    // Hoàn thành
    btnComplete.addEventListener('click', function() {
        if (!validateForm()) return;

        if (confirm('Xác nhận hoàn thành phiếu nhập?')) {
            const data = collectFormData();
            submitReceipt(data);
        }
    });

    // Hủy
    btnCancel.addEventListener('click', function() {
        if (confirm('Bạn có chắc muốn hủy? Mọi thay đổi sẽ không được lưu.')) {
            window.location.href = '/warehouse/receipt/list';
        }
    });

    // Modal chọn nhà cung cấp
    function openSupplierModal() {
        const modal = createModal('Chọn nhà cung cấp', `
            <div class="modal-search">
                <input type="text" id="supplierSearchInput" class="modal-search-input" placeholder="Tìm kiếm nhà cung cấp...">
            </div>
            <div id="supplierList" class="modal-list"></div>
        `);

        document.body.appendChild(modal);

        const searchInput = modal.querySelector('#supplierSearchInput');
        const supplierList = modal.querySelector('#supplierList');

        // Load danh sách nhà cung cấp
        function loadSuppliers(query = '') {
            fetch(`/api/warehouse/suppliers/search?q=${encodeURIComponent(query)}`)
                .then(response => response.json())
                .then(suppliers => {
                    supplierList.innerHTML = suppliers.map(supplier => `
                        <div class="modal-list-item" data-id="${supplier.id}">
                            <div class="item-name">${supplier.name}</div>
                            <div class="item-info">${supplier.phone || ''} - ${supplier.address || ''}</div>
                        </div>
                    `).join('');

                    // Xử lý click chọn supplier
                    supplierList.querySelectorAll('.modal-list-item').forEach(item => {
                        item.addEventListener('click', function() {
                            const id = this.dataset.id;
                            const name = this.querySelector('.item-name').textContent;

                            supplierInput.value = name;
                            supplierInput.dataset.supplierId = id;

                            modal.remove();
                        });
                    });
                })
                .catch(error => {
                    console.error('Error loading suppliers:', error);
                    supplierList.innerHTML = '<div class="error">Không thể tải danh sách nhà cung cấp</div>';
                });
        }

        searchInput.addEventListener('input', function() {
            loadSuppliers(this.value);
        });

        loadSuppliers();
    }

    // Modal chọn sản phẩm từ danh mục
    function openProductCatalog() {
        const modal = createModal('Chọn thuốc từ danh mục', `
            <div class="modal-search">
                <input type="text" id="medicineSearchInput" class="modal-search-input" placeholder="Tìm kiếm thuốc...">
            </div>
            <div id="medicineList" class="modal-list"></div>
        `);

        document.body.appendChild(modal);

        const searchInput = modal.querySelector('#medicineSearchInput');
        const medicineList = modal.querySelector('#medicineList');

        // Load danh sách thuốc
        function loadMedicines(query = '') {
            fetch(`/api/warehouse/medicines/search?q=${encodeURIComponent(query)}`)
                .then(response => response.json())
                .then(medicines => {
                    medicineList.innerHTML = medicines.map(medicine => `
                        <div class="modal-list-item" data-id="${medicine.id}" 
                             data-name="${medicine.medicineName}"
                             data-unit="${medicine.unit}"
                             data-concentration="${medicine.concentration || ''}">
                            <div class="item-name">${medicine.medicineName}</div>
                            <div class="item-info">ĐVT: ${medicine.unit} - Hàm lượng: ${medicine.concentration || 'N/A'}</div>
                        </div>
                    `).join('');

                    // Xử lý click chọn medicine
                    medicineList.querySelectorAll('.modal-list-item').forEach(item => {
                        item.addEventListener('click', function() {
                            const product = {
                                variantId: this.dataset.id,
                                medicineName: this.dataset.name,
                                unit: this.dataset.unit,
                                concentration: this.dataset.concentration
                            };

                            addProductRow(product);
                            modal.remove();
                        });
                    });
                })
                .catch(error => {
                    console.error('Error loading medicines:', error);
                    medicineList.innerHTML = '<div class="error">Không thể tải danh sách thuốc</div>';
                });
        }

        searchInput.addEventListener('input', function() {
            loadMedicines(this.value);
        });

        loadMedicines();
    }

    // Hàm tạo modal
    function createModal(title, content) {
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <h3>${title}</h3>
                    <button class="modal-close">&times;</button>
                </div>
                <div class="modal-body">
                    ${content}
                </div>
            </div>
        `;

        // Xử lý đóng modal
        modal.querySelector('.modal-close').addEventListener('click', () => modal.remove());
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                modal.remove();
            }
        });

        return modal;
    }

    // Tính tổng ban đầu
    calculateTotal();
});
