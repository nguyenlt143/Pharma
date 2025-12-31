// Safeguard to prevent double initialization
if (window.receiptCreateInitialized) {
    console.warn('Receipt create already initialized, skipping...');
} else {
    window.receiptCreateInitialized = true;

document.addEventListener('DOMContentLoaded', function() {
    const supplierInput = document.getElementById('supplier');
    const importDateInput = document.getElementById('import-date');
    const productTableBody = document.getElementById('productTableBody');
    const totalAmountElement = document.getElementById('totalAmount');
    const btnComplete = document.getElementById('btnComplete');
    const btnCancel = document.getElementById('btnCancel');
    const btnAddFromCatalog = document.getElementById('btnAddFromCatalog');
    const searchInput = document.querySelector('.search-input');

    // Helper function to show field-level error
    function showFieldError(field, message) {
        // Remove any existing error message
        let errorDiv = field.parentElement.querySelector('.invalid-feedback');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'invalid-feedback';
            errorDiv.style.display = 'block';
            errorDiv.style.color = '#dc3545';
            errorDiv.style.fontSize = '0.875rem';
            errorDiv.style.marginTop = '0.25rem';
            field.parentElement.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }

    // Helper function to clear field error
    function clearFieldError(field) {
        field.classList.remove('is-invalid');
        const errorDiv = field.parentElement.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    }

    // Custom confirmation modal
    function showConfirmModal(title, message, onConfirm) {
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content" style="max-width: 400px;">
                <div class="modal-header">
                    <h3>${title}</h3>
                </div>
                <div class="modal-body">
                    <p style="margin: 20px 0; color: #666;">${message}</p>
                </div>
                <div style="display: flex; gap: 10px; justify-content: flex-end; padding: 15px; border-top: 1px solid #e5e7eb;">
                    <button class="btn-cancel" style="padding: 8px 20px; border: 1px solid #d1d5db; background: white; border-radius: 6px; cursor: pointer;">Hủy</button>
                    <button class="btn-confirm" style="padding: 8px 20px; border: none; background: #2563eb; color: white; border-radius: 6px; cursor: pointer;">Xác nhận</button>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        const btnCancel = modal.querySelector('.btn-cancel');
        const btnConfirm = modal.querySelector('.btn-confirm');

        btnCancel.addEventListener('click', () => modal.remove());
        btnConfirm.addEventListener('click', () => {
            modal.remove();
            onConfirm();
        });

        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                modal.remove();
            }
        });
    }

    // Clear validation errors when user starts typing
    supplierInput.addEventListener('input', () => clearFieldError(supplierInput));
    importDateInput.addEventListener('change', () => clearFieldError(importDateInput));

    // Set ngày hiện tại nếu chưa có
    if (!importDateInput.value) {
        importDateInput.value = new Date().toISOString().split('T')[0];
    }

    // Format date from yyyy-MM-dd to dd/MM/yyyy
    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        const [year, month, day] = dateString.split('-');
        return `${day}/${month}/${year}`;
    }

    // Format number as Vietnamese currency
    function formatCurrency(amount) {
        if (!amount && amount !== 0) return '0';
        return Math.round(amount).toLocaleString('vi-VN');
    }

    // Tính tổng tiền
    function calculateTotal() {
        let total = 0;
        document.querySelectorAll('.table-row').forEach(row => {
            const quantity = parseFloat(row.dataset.quantity) || 0;
            const price = parseFloat(row.dataset.price) || 0;
            const conversionRatio = parseFloat(row.dataset.conversionRatio) || 1;
            total += quantity * price * conversionRatio;
        });
        totalAmountElement.textContent = total.toLocaleString('vi-VN');
    }

    // Cập nhật STT
    function updateRowNumbers() {
        document.querySelectorAll('.table-row').forEach((row, index) => {
            row.querySelector('.col-stt').textContent = index + 1;
        });
    }

    // Hiển thị popup nhập thông tin thuốc
    function showProductDetailModal(product, preFilledDetails = null) {
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';

        // Debug: Log product data to check unitConversions
        console.log('Product data:', product);
        console.log('Unit conversions:', product.unitConversions);

        // Prepare unit conversions table rows
        const unitConversionsHtml = product.unitConversions && product.unitConversions.length > 0
            ? product.unitConversions.map((uc, index) => `
                <tr style="border-bottom: 1px solid #e5e7eb;">
                    <td style="padding: 8px; text-align: center;">${index + 1}</td>
                    <td style="padding: 8px;">${uc.unitName || ''}</td>
                    <td style="padding: 8px; text-align: right;">
                        ${uc.multiplier ? uc.multiplier + ' ' + (product.baseUnit || '') : 'N/A'}
                    </td>
                    <td style="padding: 8px; text-align: center;">
                        <input type="checkbox" ${uc.isSale ? 'checked' : ''} disabled style="width: 16px; height: 16px;">
                    </td>
                </tr>
            `).join('')
            : '<tr><td colspan="4" style="padding: 20px; text-align: center; color: #9ca3af;">Chưa có đơn vị quy đổi</td></tr>';

        modal.innerHTML = `
            <div class="modal-content" style="max-width: 900px; max-height: 90vh; overflow-y: auto;">
                <div class="modal-header">
                    <h3>Nhập thông tin thuốc</h3>
                    <button class="modal-close">&times;</button>
                </div>
                <div class="modal-body" style="padding: 20px;">
                    <!-- Section 1: Thông tin thuốc -->
                    <div class="product-info-grid" style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px; padding: 15px; background: #f9fafb; border-radius: 6px; margin-bottom: 20px;">
                        <div>
                            <div style="font-weight: 600; font-size: 1.1rem; color: #1f2937; margin-bottom: 10px;">
                                ${product.medicineName || 'N/A'}
                            </div>
                            <div style="font-size: 0.875rem; color: #6b7280;">
                                <div><strong>Danh mục:</strong> ${product.categoryName || 'N/A'}</div>
                                <div><strong>Hoạt chất:</strong> ${product.activeIngredient || 'N/A'}</div>
                            </div>
                        </div>
                        <div style="font-size: 0.875rem; color: #6b7280;">
                            <div><strong>Dạng bào chế:</strong> ${product.dosageFormName || 'N/A'}</div>
                            <div><strong>Hàm lượng:</strong> ${product.concentration || 'N/A'}</div>
                            <div><strong>Số đăng ký:</strong> ${product.registrationNumber || 'N/A'}</div>
                        </div>
                        <div style="font-size: 0.875rem; color: #6b7280;">
                            <div><strong>Nhà sản xuất:</strong> ${product.manufacturer || 'N/A'}</div>
                            <div><strong>Quốc gia:</strong> ${product.country || 'N/A'}</div>
                        </div>
                    </div>

                    <!-- Section 2: Đơn vị quy đổi -->
                    <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px; padding: 15px; background: #eff6ff; border-radius: 6px; margin-bottom: 20px;">
                        <div>
                            <div style="font-size: 0.875rem; color: #6b7280;">Đơn vị cơ bản</div>
                            <div style="font-weight: 600; color: #1f2937;">${product.baseUnit || 'N/A'}</div>
                        </div>
                        <div>
                            <div style="font-size: 0.875rem; color: #6b7280;">Đơn vị nhập</div>
                            <div style="font-weight: 600; color: #1f2937;">${product.importUnit || 'N/A'}</div>
                        </div>
                        <div>
                            <div style="font-size: 0.875rem; color: #6b7280;">Tỉ lệ quy đổi</div>
                            <div style="font-weight: 600; color: #1f2937;">
                                ${product.conversionRatio && product.conversionRatio > 1
                                    ? '1 ' + (product.importUnit || '') + ' = ' + product.conversionRatio + ' ' + (product.baseUnit || '')
                                    : 'N/A'}
                            </div>
                        </div>
                        <div style="grid-column: 1 / -1;">
                            <div style="font-size: 0.875rem; color: #6b7280;">Quy cách đóng gói</div>
                            <div style="font-weight: 600; color: #1f2937;">${product.packagingSpec || 'N/A'}</div>
                        </div>
                    </div>

                    <!-- Section 3: Form nhập liệu -->
                    <div style="display: grid; gap: 15px; margin-bottom: 20px;">
                        <div>
                            <label style="display: block; margin-bottom: 5px; font-weight: 500; color: #374151;">Số lô <span style="color: #dc3545;">*</span></label>
                            <input type="text" id="modal-batch" class="modal-input" placeholder="Nhập số lô" value="${preFilledDetails ? preFilledDetails.batchCode : ''}" style="width: 100%; padding: 8px; border: 1px solid #d1d5db; border-radius: 6px;">
                            <div class="invalid-feedback" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">
                            <div>
                                <label style="display: block; margin-bottom: 5px; font-weight: 500; color: #374151;">Ngày sản xuất <span style="color: #dc3545;">*</span></label>
                                <input type="date" id="modal-mfg-date" class="modal-input" value="${preFilledDetails ? preFilledDetails.manufactureDate : ''}" style="width: 100%; padding: 8px; border: 1px solid #d1d5db; border-radius: 6px;">
                                <div class="invalid-feedback" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                            </div>
                            <div>
                                <label style="display: block; margin-bottom: 5px; font-weight: 500; color: #374151;">Hạn sử dụng <span style="color: #dc3545;">*</span></label>
                                <input type="date" id="modal-exp-date" class="modal-input" value="${preFilledDetails ? preFilledDetails.expiryDate : ''}" style="width: 100%; padding: 8px; border: 1px solid #d1d5db; border-radius: 6px;">
                                <div class="invalid-feedback" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                            </div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px;">
                            <div>
                                <label style="display: block; margin-bottom: 5px; font-weight: 500; color: #374151;">
                                    Số lượng nhập (${product.importUnit || 'đơn vị'}) <span style="color: #dc3545;">*</span>
                                </label>
                                <input type="number" id="modal-quantity" class="modal-input" value="${preFilledDetails ? preFilledDetails.quantity : 1}" min="1" style="width: 100%; padding: 8px; border: 1px solid #d1d5db; border-radius: 6px;">
                                <div class="invalid-feedback" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                            </div>
                            <div>
                                <label style="display: block; margin-bottom: 5px; font-weight: 500; color: #374151;">Đơn giá <span style="color: #dc3545;">*</span></label>
                                <input type="number" id="modal-price" class="modal-input" placeholder="0" value="${preFilledDetails ? preFilledDetails.price : ''}" min="1" style="width: 100%; padding: 8px; border: 1px solid #d1d5db; border-radius: 6px;">
                                <div class="invalid-feedback" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                            </div>
                            <div>
                                <label style="display: block; margin-bottom: 5px; font-weight: 500; color: #374151;">Thành tiền</label>
                                <input type="text" id="modal-total" class="modal-input" placeholder="0" readonly style="width: 100%; padding: 8px; border: 1px solid #d1d5db; border-radius: 6px; background: #f9fafb; color: #374151; font-weight: 600;">
                            </div>
                        </div>
                    </div>

                    <!-- Section 4: Bảng danh sách đơn vị quy đổi -->
                    <div style="margin-top: 20px;">
                        <h4 style="font-size: 0.9rem; font-weight: 600; color: #374151; margin-bottom: 10px;">
                            Danh sách đơn vị quy đổi
                        </h4>
                        <div style="overflow-x: auto;">
                            <table style="width: 100%; border-collapse: collapse; font-size: 0.875rem; border: 1px solid #e5e7eb;">
                                <thead>
                                    <tr style="background: #f3f4f6; border-bottom: 2px solid #e5e7eb;">
                                        <th style="padding: 10px 8px; text-align: center; width: 60px; border-right: 1px solid #e5e7eb;">STT</th>
                                        <th style="padding: 10px 8px; text-align: left; border-right: 1px solid #e5e7eb;">Đơn vị</th>
                                        <th style="padding: 10px 8px; text-align: right; border-right: 1px solid #e5e7eb;">Quy đổi</th>
                                        <th style="padding: 10px 8px; text-align: center; width: 120px;">Bán hàng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${unitConversionsHtml}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div style="display: flex; gap: 10px; justify-content: flex-end; padding: 15px; border-top: 1px solid #e5e7eb; background: #f9fafb;">
                    <button class="btn-modal-cancel" style="padding: 10px 24px; border: 1px solid #d1d5db; background: white; border-radius: 6px; cursor: pointer; font-weight: 500;">Hủy</button>
                    <button class="btn-modal-add" style="padding: 10px 24px; border: none; background: #2563eb; color: white; border-radius: 6px; cursor: pointer; font-weight: 500;">Thêm vào danh sách</button>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        const batchInput = modal.querySelector('#modal-batch');
        const mfgInput = modal.querySelector('#modal-mfg-date');
        const expInput = modal.querySelector('#modal-exp-date');
        const qtyInput = modal.querySelector('#modal-quantity');
        const priceInput = modal.querySelector('#modal-price');
        const totalInput = modal.querySelector('#modal-total');
        const btnCancel = modal.querySelector('.btn-modal-cancel');
        const btnAdd = modal.querySelector('.btn-modal-add');
        const closeBtn = modal.querySelector('.modal-close');

        // Function to calculate and update total amount
        function updateTotalAmount() {
            const quantity = parseFloat(qtyInput.value) || 0;
            const price = parseFloat(priceInput.value) || 0;
            const conversionRatio = parseFloat(product.conversionRatio) || 1;
            const total = quantity * price * conversionRatio;
            totalInput.value = total.toLocaleString('vi-VN', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
        }

        // Helper function to show error
        function showModalError(input, message) {
            input.style.borderColor = '#dc3545';
            const errorDiv = input.nextElementSibling;
            if (errorDiv && errorDiv.classList.contains('invalid-feedback')) {
                errorDiv.textContent = message;
                errorDiv.style.display = 'block';
            }
        }

        // Helper function to clear error
        function clearModalError(input) {
            input.style.borderColor = '#d1d5db';
            const errorDiv = input.nextElementSibling;
            if (errorDiv && errorDiv.classList.contains('invalid-feedback')) {
                errorDiv.style.display = 'none';
            }
        }

        // Real-time validation
        batchInput.addEventListener('input', () => clearModalError(batchInput));
        mfgInput.addEventListener('change', () => clearModalError(mfgInput));
        expInput.addEventListener('change', () => clearModalError(expInput));
        qtyInput.addEventListener('input', () => {
            clearModalError(qtyInput);
            updateTotalAmount();
        });
        priceInput.addEventListener('input', () => {
            clearModalError(priceInput);
            updateTotalAmount();
        });

        // Initialize total amount on load
        updateTotalAmount();

        // Validate and add product
        function validateAndAddProduct() {
            let isValid = true;

            // Validate batch code
            if (!batchInput.value.trim()) {
                showModalError(batchInput, 'Vui lòng nhập số lô');
                isValid = false;
            } else {
                const validBatchPattern = /^[A-Za-z0-9\-]+$/;
                if (!validBatchPattern.test(batchInput.value.trim())) {
                    showModalError(batchInput, 'Số lô chỉ được chứa chữ cái, số và dấu gạch ngang (-)');
                    isValid = false;
                } else {
                    // Check duplicate batch code
                    const allRows = productTableBody.querySelectorAll('.table-row');
                    let isDuplicate = false;
                    allRows.forEach(row => {
                        const existingBatch = row.dataset.batchCode;
                        const existingVariantId = row.dataset.variantId;
                        if (existingVariantId === product.variantId && existingBatch === batchInput.value.trim()) {
                            isDuplicate = true;
                        }
                    });
                    if (isDuplicate) {
                        showModalError(batchInput, 'Số lô đã tồn tại');
                        isValid = false;
                    }
                }
            }

            // Validate manufacture date
            if (!mfgInput.value) {
                showModalError(mfgInput, 'Vui lòng nhập ngày sản xuất');
                isValid = false;
            } else {
                const mfgDate = new Date(mfgInput.value);
                const currentDate = new Date();
                currentDate.setHours(0, 0, 0, 0);
                if (mfgDate > currentDate) {
                    showModalError(mfgInput, 'NSX không được lớn hơn ngày hiện tại');
                    isValid = false;
                }
            }

            // Validate expiry date
            if (!expInput.value) {
                showModalError(expInput, 'Vui lòng nhập hạn sử dụng');
                isValid = false;
            } else if (mfgInput.value) {
                const mfgDate = new Date(mfgInput.value);
                const expDate = new Date(expInput.value);
                if (expDate <= mfgDate) {
                    showModalError(expInput, 'HSD phải sau NSX');
                    isValid = false;
                } else {
                    const maxExpiryDate = new Date(mfgDate);
                    maxExpiryDate.setFullYear(maxExpiryDate.getFullYear() + 20);
                    if (expDate > maxExpiryDate) {
                        showModalError(expInput, 'HSD không được quá 20 năm từ NSX');
                        isValid = false;
                    }
                }
            }

            // Validate quantity
            if (!qtyInput.value || parseInt(qtyInput.value) < 1) {
                showModalError(qtyInput, 'Số lượng phải lớn hơn 0');
                isValid = false;
            }

            // Validate price
            if (!priceInput.value || parseFloat(priceInput.value) <= 0) {
                showModalError(priceInput, 'Giá nhập phải lớn hơn 0');
                isValid = false;
            }

            if (!isValid) return;

            // Add product to table
            addProductRow(product, {
                batchCode: batchInput.value.trim(),
                manufactureDate: mfgInput.value,
                expiryDate: expInput.value,
                quantity: parseInt(qtyInput.value),
                price: parseFloat(priceInput.value)
            });

            modal.remove();
        }

        btnAdd.addEventListener('click', validateAndAddProduct);
        btnCancel.addEventListener('click', () => modal.remove());
        closeBtn.addEventListener('click', () => modal.remove());
        modal.addEventListener('click', (e) => {
            if (e.target === modal) modal.remove();
        });

        // Focus on first input
        setTimeout(() => batchInput.focus(), 100);
    }

    // Thêm dòng sản phẩm mới
    function addProductRow(product, details) {
        const rowCount = productTableBody.querySelectorAll('.table-row').length;
        const row = document.createElement('tr');
        row.className = 'table-row';

        // Store all necessary data in dataset (including product info for editing)
        row.dataset.variantId = product.variantId;
        row.dataset.conversionRatio = product.conversionRatio || 1;
        row.dataset.importUnit = product.importUnit || '';
        row.dataset.baseUnit = product.baseUnit || '';
        row.dataset.batchCode = details.batchCode;
        row.dataset.expiryDate = details.expiryDate;
        row.dataset.manufactureDate = details.manufactureDate;
        row.dataset.quantity = details.quantity;
        row.dataset.price = details.price;

        // Store additional product info for editing
        row.dataset.categoryName = product.categoryName || 'N/A';
        row.dataset.activeIngredient = product.activeIngredient || 'N/A';
        row.dataset.dosageFormName = product.dosageFormName || 'N/A';
        row.dataset.concentration = product.concentration || 'N/A';
        row.dataset.registrationNumber = product.registrationNumber || 'N/A';
        row.dataset.manufacturer = product.manufacturer || 'N/A';
        row.dataset.country = product.country || 'N/A';
        row.dataset.packagingSpec = product.packagingSpec || 'N/A';
        row.dataset.unitConversions = JSON.stringify(product.unitConversions || []);

        // Calculate total amount for this row
        const totalAmount = details.quantity * details.price * (product.conversionRatio || 1);

        row.innerHTML = `
            <td class="col-stt">${rowCount + 1}</td>
            <td class="col-medicine">${product.medicineName}</td>
            <td class="col-batch">${details.batchCode}</td>
            <td class="col-expiry-date">${formatDate(details.expiryDate)}</td>
            <td class="col-unit">${product.importUnit || 'N/A'}</td>
            <td class="col-quantity">${details.quantity}</td>
            <td class="col-price">${formatCurrency(details.price)}</td>
            <td class="col-total">${formatCurrency(totalAmount)}</td>
            <td class="col-actions">
                <button type="button" class="btn-edit" title="Sửa">
                    <span class="material-icons">edit</span>
                </button>
                <button type="button" class="btn-delete" title="Xóa">
                    <span class="material-icons">delete</span>
                </button>
            </td>
        `;

        productTableBody.appendChild(row);
        calculateTotal();
    }


    // Xóa và sửa dòng
    productTableBody.addEventListener('click', function(e) {
        // Handle edit button
        if (e.target.closest('.btn-edit')) {
            const row = e.target.closest('.table-row');

            // Reconstruct product object from dataset
            const product = {
                variantId: row.dataset.variantId,
                medicineName: row.querySelector('.col-medicine').textContent,
                conversionRatio: parseFloat(row.dataset.conversionRatio) || 1,
                importUnit: row.dataset.importUnit,
                baseUnit: row.dataset.baseUnit,
                // Get other product info from the original product (will need to fetch or store)
                categoryName: row.dataset.categoryName || 'N/A',
                activeIngredient: row.dataset.activeIngredient || 'N/A',
                dosageFormName: row.dataset.dosageFormName || 'N/A',
                concentration: row.dataset.concentration || 'N/A',
                registrationNumber: row.dataset.registrationNumber || 'N/A',
                manufacturer: row.dataset.manufacturer || 'N/A',
                country: row.dataset.country || 'N/A',
                packagingSpec: row.dataset.packagingSpec || 'N/A',
                unitConversions: JSON.parse(row.dataset.unitConversions || '[]')
            };

            // Reconstruct details from dataset
            const details = {
                batchCode: row.dataset.batchCode,
                manufactureDate: row.dataset.manufactureDate,
                expiryDate: row.dataset.expiryDate,
                quantity: parseInt(row.dataset.quantity),
                price: parseFloat(row.dataset.price)
            };

            // Remove the current row
            row.remove();

            // Show modal with pre-filled data
            showProductDetailModal(product, details);
        }

        // Handle delete button
        if (e.target.closest('.btn-delete')) {
            const rowToDelete = e.target.closest('.table-row');
            showConfirmModal('Xác nhận xóa', 'Bạn có chắc muốn xóa sản phẩm này?', function() {
                rowToDelete.remove();
                updateRowNumbers();
                calculateTotal();
            });
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
    if (btnAddFromCatalog) {
        btnAddFromCatalog.addEventListener('click', function(e) {
            console.log('Add medicine button clicked');
            try {
                e.preventDefault();
                openProductCatalog();
            } catch (error) {
                console.error('Error opening product catalog:', error);
                showToast('Có lỗi xảy ra khi mở danh sách thuốc: ' + error.message, 'error');
            }
        });
    } else {
        console.error('btnAddFromCatalog element not found!');
    }

    // Validate form
    function validateForm() {
        // Clear previous validation states
        supplierInput.classList.remove('is-invalid');
        importDateInput.classList.remove('is-invalid');

        let isValid = true;

        if (!supplierInput.dataset.supplierId) {
            supplierInput.classList.add('is-invalid');
            showFieldError(supplierInput, 'Vui lòng chọn nhà cung cấp');
            supplierInput.focus();
            isValid = false;
        }

        if (!importDateInput.value) {
            importDateInput.classList.add('is-invalid');
            showFieldError(importDateInput, 'Vui lòng chọn ngày nhập');
            if (isValid) importDateInput.focus();
            isValid = false;
        }

        const rows = productTableBody.querySelectorAll('.table-row');
        if (rows.length === 0) {
            showToast('Vui lòng thêm ít nhất 1 sản phẩm', 'warning');
            isValid = false;
        }

        return isValid;
    }

    // Thu thập dữ liệu form
    function collectFormData() {
        const details = [];

        document.querySelectorAll('.table-row').forEach(row => {
            const importQuantity = parseInt(row.dataset.quantity);
            const conversionRatio = parseFloat(row.dataset.conversionRatio) || 1;
            const baseUnitQuantity = importQuantity * conversionRatio;
            const price = parseFloat(row.dataset.price);

            details.push({
                variantId: row.dataset.variantId,
                batchCode: row.dataset.batchCode,
                manufactureDate: row.dataset.manufactureDate,
                expiryDate: row.dataset.expiryDate,
                quantity: baseUnitQuantity,  // Store in base unit
                price: price,                // Giá nhập kho
                snapCost: price,             // Theo flow: price = snap_cost khi nhập từ supplier
                // Additional metadata for audit trail
                importUnit: row.dataset.importUnit,
                importQuantity: importQuantity,
                conversionRatio: conversionRatio,
                baseUnit: row.dataset.baseUnit
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
                showToast('Tạo phiếu nhập thành công!', 'success');
                setTimeout(() => {
                    window.location.href = '/warehouse/receipt/list';
                }, 1500);
            })
            .catch(error => {
                showToast('Có lỗi xảy ra: ' + error.message, 'error');
                console.error(error);
            });
    }

    // Hoàn thành
    btnComplete.addEventListener('click', function() {
        if (!validateForm()) return;

        showConfirmModal('Xác nhận hoàn thành phiếu nhập?', 'Phiếu nhập sẽ được lưu vào hệ thống.', function() {
            const data = collectFormData();
            submitReceipt(data);
        });
    });

    // Hủy
    btnCancel.addEventListener('click', function() {
        showConfirmModal('Bạn có chắc muốn hủy?', 'Mọi thay đổi sẽ không được lưu.', function() {
            window.location.href = '/warehouse/receipt/list';
        });
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
                })
                .catch(error => {
                    console.error('Error loading suppliers:', error);
                    supplierList.innerHTML = '<div class="error">Không thể tải danh sách nhà cung cấp</div>';
                });
        }

        // Use event delegation to prevent duplicate handlers
        supplierList.addEventListener('click', function(e) {
            const item = e.target.closest('.modal-list-item');
            if (!item) return;

            const id = item.dataset.id;
            const name = item.querySelector('.item-name').textContent;

            supplierInput.value = name;
            supplierInput.dataset.supplierId = id;

            // Clear validation error when supplier is selected
            supplierInput.classList.remove('is-invalid');
            clearFieldError(supplierInput);

            modal.remove();
        });

        searchInput.addEventListener('input', function() {
            loadSuppliers(this.value);
        });

        loadSuppliers();
    }

    // Modal chọn sản phẩm từ danh mục
    function openProductCatalog() {
        console.log('Opening product catalog...');

        const modal = createModal('Chọn thuốc từ danh mục', `
            <div class="modal-search">
                <input type="text" id="medicineSearchInput" class="modal-search-input" placeholder="Tìm kiếm thuốc...">
            </div>
            <div id="medicineList" class="modal-list"></div>
        `);

        document.body.appendChild(modal);
        console.log('Modal appended to body');

        const searchInput = modal.querySelector('#medicineSearchInput');
        const medicineList = modal.querySelector('#medicineList');

        // Load danh sách thuốc
        function loadMedicines(query = '') {
            console.log('Loading medicines with query:', query);

            fetch(`/api/warehouse/medicines/search?q=${encodeURIComponent(query)}`)
                .then(response => {
                    console.log('Response status:', response.status);
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(medicines => {
                    console.log('Medicines loaded:', medicines.length, 'items');

                    if (!medicines || medicines.length === 0) {
                        medicineList.innerHTML = '<div class="error" style="padding: 20px; text-align: center; color: #6B7280;">Không tìm thấy thuốc nào</div>';
                        return;
                    }

                    medicineList.innerHTML = medicines.map(medicine => `
                        <div class="modal-list-item"
                             data-medicine='${JSON.stringify(medicine).replace(/'/g, "&apos;")}'>
                            <div class="item-name">${medicine.medicineName || 'N/A'}</div>
                            <div class="item-info" style="font-size: 0.875rem; color: #6b7280;">
                                <span class="badge" style="background: #eff6ff; color: #2563eb; padding: 2px 8px; border-radius: 4px; margin-right: 8px;">
                                    ${medicine.dosageFormName || 'N/A'}
                                </span>
                                <span style="margin-right: 8px;">${medicine.concentration || 'N/A'}</span>
                                <span style="margin-right: 8px;">ĐVT: ${medicine.baseUnit || 'N/A'}</span>
                                <span style="color: #9ca3af;">${medicine.packagingSpec || 'N/A'}</span>
                            </div>
                        </div>
                    `).join('');

                    console.log('Medicine list rendered');
                })
                .catch(error => {
                    console.error('Error loading medicines:', error);
                    medicineList.innerHTML = '<div class="error" style="padding: 20px; text-align: center; color: #dc3545;">Không thể tải danh sách thuốc. Lỗi: ' + error.message + '</div>';
                });
        }

        // Use event delegation to prevent duplicate handlers
        medicineList.addEventListener('click', function(e) {
            const item = e.target.closest('.modal-list-item');
            if (!item) return;

            try {
                const medicineData = JSON.parse(item.dataset.medicine);

                const product = {
                    variantId: medicineData.id,
                    medicineName: medicineData.medicineName,
                    categoryName: medicineData.categoryName,
                    activeIngredient: medicineData.activeIngredient,
                    manufacturer: medicineData.manufacturer,
                    country: medicineData.country,
                    dosageFormName: medicineData.dosageFormName,
                    registrationNumber: medicineData.registrationNumber,
                    concentration: medicineData.concentration,
                    baseUnit: medicineData.baseUnit,
                    importUnit: medicineData.importUnit,
                    conversionRatio: medicineData.conversionRatio,
                    packagingSpec: medicineData.packagingSpec,
                    unitConversions: medicineData.unitConversions || []
                };

                modal.remove();
                // Show detail modal to enter product information
                showProductDetailModal(product);
            } catch (error) {
                console.error('Error parsing medicine data:', error);
                showToast('Lỗi khi tải thông tin thuốc', 'error');
            }
        });

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

} // End safeguard

