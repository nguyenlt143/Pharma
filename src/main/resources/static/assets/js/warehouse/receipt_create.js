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
        row.dataset.quantityPerPackage = product.quantityPerPackage || '';

        const qtyPerPackage = product.quantityPerPackage ? Math.round(product.quantityPerPackage) : 'N/A';

        row.innerHTML = `
            <td class="col-stt">${rowCount + 1}</td>
            <td class="col-medicine">${product.medicineName}</td>
            <td class="col-unit">${product.unit}</td>
            <td class="col-qty-per-package">${qtyPerPackage}</td>
            <td class="col-concentration">${product.concentration}</td>
            <td class="col-batch">
                <input type="text" class="batch-input" placeholder="Nhập số lô" required>
                <div class="invalid-feedback">Số lô đã tồn tại</div>
            </td>
            <td class="col-manufacture-date">
                <input type="date" class="manufacture-date-input" required>
                <div class="invalid-feedback">NSX không hợp lệ</div>
            </td>
            <td class="col-expiry-date">
                <input type="date" class="expiry-date-input" required>
                <div class="invalid-feedback">HSD không hợp lệ</div>
            </td>
            <td class="col-quantity">
                <input type="number" class="quantity-input" value="1" min="1" required>
                <div class="invalid-feedback">Số lượng phải chia hết cho số viên trong hộp</div>
            </td>
            <td class="col-price">
                <input type="number" class="price-input" value="" min="1" placeholder="Giá nhập" required>
                <div class="invalid-feedback">Giá nhập phải lớn hơn 0</div>
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
        if (e.target.classList.contains('quantity-input')) {
            const row = e.target.closest('.table-row');
            const quantityPerPackage = parseFloat(row.dataset.quantityPerPackage);
            const quantity = parseFloat(e.target.value);

            // Remove previous validation state
            e.target.classList.remove('is-invalid');

            // Validate quantity divisibility
            if (quantityPerPackage && quantity > 0) {
                if (quantity % quantityPerPackage !== 0) {
                    e.target.classList.add('is-invalid');
                    e.target.nextElementSibling.textContent = `Số lượng phải chia hết cho ${Math.round(quantityPerPackage)} viên/hộp`;
                }
            }
            calculateTotal();
        }

        if (e.target.classList.contains('price-input')) {
            const price = parseFloat(e.target.value);

            // Validate price > 0
            if (!e.target.value || price <= 0) {
                e.target.classList.add('is-invalid');
            } else {
                e.target.classList.remove('is-invalid');
            }

            calculateTotal();
        }

        // Validate batch code
        if (e.target.classList.contains('batch-input')) {
            const currentRow = e.target.closest('.table-row');
            let currentBatchCode = e.target.value;
            const currentVariantId = currentRow.dataset.variantId;

            // Remove previous validation state
            e.target.classList.remove('is-invalid');

            // Validate character set: only alphanumeric and hyphen allowed
            const validBatchPattern = /^[A-Za-z0-9\-]*$/;
            if (currentBatchCode && !validBatchPattern.test(currentBatchCode)) {
                e.target.classList.add('is-invalid');
                e.target.nextElementSibling.textContent = 'Số lô chỉ được chứa chữ cái, số và dấu gạch ngang (-)';
                // Remove invalid characters
                e.target.value = currentBatchCode.replace(/[^A-Za-z0-9\-]/g, '');
                return;
            }

            currentBatchCode = currentBatchCode.trim();

            if (currentBatchCode) {
                // Check for duplicate batch code with same variant
                const allRows = productTableBody.querySelectorAll('.table-row');
                let isDuplicate = false;

                allRows.forEach(row => {
                    if (row !== currentRow) {
                        const batchInput = row.querySelector('.batch-input');
                        const variantId = row.dataset.variantId;

                        if (variantId === currentVariantId &&
                            batchInput.value.trim() === currentBatchCode) {
                            isDuplicate = true;
                        }
                    }
                });

                if (isDuplicate) {
                    e.target.classList.add('is-invalid');
                    e.target.nextElementSibling.textContent = 'Số lô đã tồn tại';
                }
            }
        }

        // Validate manufacture date
        if (e.target.classList.contains('manufacture-date-input')) {
            const mfgDate = new Date(e.target.value);
            const currentDate = new Date();
            currentDate.setHours(0, 0, 0, 0);

            // Remove previous validation state
            e.target.classList.remove('is-invalid');

            if (e.target.value) {
                // Allow NSX = current date, only reject if NSX > current date
                if (mfgDate > currentDate) {
                    e.target.classList.add('is-invalid');
                    e.target.nextElementSibling.textContent = 'NSX không được lớn hơn ngày hiện tại';
                }
            }
        }

        // Validate expiry date
        if (e.target.classList.contains('expiry-date-input')) {
            const row = e.target.closest('.table-row');
            const mfgInput = row.querySelector('.manufacture-date-input');
            const expDate = new Date(e.target.value);
            const mfgDate = new Date(mfgInput.value);

            // Remove previous validation state
            e.target.classList.remove('is-invalid');

            if (e.target.value && mfgInput.value) {
                // Check if HSD > NSX
                if (expDate <= mfgDate) {
                    e.target.classList.add('is-invalid');
                    e.target.nextElementSibling.textContent = 'HSD phải sau NSX';
                    return;
                }

                // Check if HSD <= NSX + 20 years
                const maxExpiryDate = new Date(mfgDate);
                maxExpiryDate.setFullYear(maxExpiryDate.getFullYear() + 20);

                if (expDate > maxExpiryDate) {
                    e.target.classList.add('is-invalid');
                    e.target.nextElementSibling.textContent = 'HSD không được quá 20 năm từ NSX';
                }
            }
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

        // Check for any validation errors
        let hasErrors = false;
        const batchCodes = new Map(); // Map to track batch codes by variant

        for (let row of rows) {
            const batchInput = row.querySelector('.batch-input');
            const mfgInput = row.querySelector('.manufacture-date-input');
            const expInput = row.querySelector('.expiry-date-input');
            const qtyInput = row.querySelector('.quantity-input');
            const priceInput = row.querySelector('.price-input');
            const variantId = row.dataset.variantId;

            // Clear all validation states
            batchInput.classList.remove('is-invalid');
            mfgInput.classList.remove('is-invalid');
            expInput.classList.remove('is-invalid');
            qtyInput.classList.remove('is-invalid');
            priceInput.classList.remove('is-invalid');

            // Validate batch code
            if (!batchInput.value.trim()) {
                batchInput.classList.add('is-invalid');
                batchInput.nextElementSibling.textContent = 'Vui lòng nhập số lô';
                if (!hasErrors) {
                    batchInput.focus();
                    hasErrors = true;
                }
                continue;
            }

            // Validate batch code characters
            const validBatchPattern = /^[A-Za-z0-9\-]+$/;
            if (!validBatchPattern.test(batchInput.value.trim())) {
                batchInput.classList.add('is-invalid');
                batchInput.nextElementSibling.textContent = 'Số lô chỉ được chứa chữ cái, số và dấu gạch ngang (-)';
                if (!hasErrors) {
                    batchInput.focus();
                    hasErrors = true;
                }
                continue;
            }

            // Check for duplicate batch codes
            const batchKey = `${variantId}_${batchInput.value.trim()}`;
            if (batchCodes.has(batchKey)) {
                batchInput.classList.add('is-invalid');
                batchInput.nextElementSibling.textContent = 'Số lô đã tồn tại';
                if (!hasErrors) {
                    batchInput.focus();
                    hasErrors = true;
                }
                continue;
            }
            batchCodes.set(batchKey, true);

            // Validate manufacture date
            if (!mfgInput.value) {
                mfgInput.classList.add('is-invalid');
                mfgInput.nextElementSibling.textContent = 'Vui lòng nhập NSX';
                if (!hasErrors) {
                    mfgInput.focus();
                    hasErrors = true;
                }
                continue;
            }

            const mfgDate = new Date(mfgInput.value);
            const currentDate = new Date();
            currentDate.setHours(0, 0, 0, 0);

            // Allow NSX = currentDate, only reject if NSX > currentDate
            if (mfgDate > currentDate) {
                mfgInput.classList.add('is-invalid');
                mfgInput.nextElementSibling.textContent = 'NSX không được lớn hơn ngày hiện tại';
                if (!hasErrors) {
                    mfgInput.focus();
                    hasErrors = true;
                }
                continue;
            }

            // Validate expiry date
            if (!expInput.value) {
                expInput.classList.add('is-invalid');
                expInput.nextElementSibling.textContent = 'Vui lòng nhập HSD';
                if (!hasErrors) {
                    expInput.focus();
                    hasErrors = true;
                }
                continue;
            }

            const expDate = new Date(expInput.value);

            // HSD must be after NSX
            if (expDate <= mfgDate) {
                expInput.classList.add('is-invalid');
                expInput.nextElementSibling.textContent = 'HSD phải sau NSX';
                if (!hasErrors) {
                    expInput.focus();
                    hasErrors = true;
                }
                continue;
            }

            // HSD max 20 years from NSX
            const maxExpiryDate = new Date(mfgDate);
            maxExpiryDate.setFullYear(maxExpiryDate.getFullYear() + 20);

            if (expDate > maxExpiryDate) {
                expInput.classList.add('is-invalid');
                expInput.nextElementSibling.textContent = 'HSD không được quá 20 năm từ NSX';
                if (!hasErrors) {
                    expInput.focus();
                    hasErrors = true;
                }
                continue;
            }

            // Validate quantity
            if (!qtyInput.value || parseInt(qtyInput.value) < 1) {
                qtyInput.classList.add('is-invalid');
                qtyInput.nextElementSibling.textContent = 'Số lượng phải lớn hơn 0';
                if (!hasErrors) {
                    qtyInput.focus();
                    hasErrors = true;
                }
                continue;
            }

            // Validate quantity divisibility
            const quantityPerPackage = parseFloat(row.dataset.quantityPerPackage);
            const quantity = parseInt(qtyInput.value);
            if (quantityPerPackage && quantity > 0) {
                if (quantity % quantityPerPackage !== 0) {
                    qtyInput.classList.add('is-invalid');
                    qtyInput.nextElementSibling.textContent = `Số lượng phải chia hết cho ${Math.round(quantityPerPackage)} viên/hộp`;
                    if (!hasErrors) {
                        qtyInput.focus();
                        hasErrors = true;
                    }
                    continue;
                }
            }

            // Validate price
            if (!priceInput.value || parseFloat(priceInput.value) <= 0) {
                priceInput.classList.add('is-invalid');
                if (!hasErrors) {
                    priceInput.focus();
                    hasErrors = true;
                }
                continue;
            }
        }

        return !hasErrors;
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
                    return response.json().then(err => {
                        throw new Error(err.message || 'Lỗi khi tạo phiếu nhập');
                    });
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
                             data-concentration="${medicine.concentration || ''}"
                             data-quantity-per-package="${medicine.quantityPerPackage || ''}">
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
                                concentration: this.dataset.concentration,
                                quantityPerPackage: this.dataset.quantityPerPackage
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
