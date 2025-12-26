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

    // Biến lưu thông tin sản phẩm tạm thời
    let tempProductData = null;

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
    // Hàm mở modal nhập thông tin thuốc
    function addProductRow(product) {
        // Lưu thông tin sản phẩm tạm thời
        tempProductData = product;

        // Mở modal
        const modal = document.getElementById('productDetailModal');
        const modalTitle = document.getElementById('modalProductName');

        // Điền thông tin vào modal
        modalTitle.textContent = `Nhập thông tin: ${product.medicineName}`;
        document.getElementById('modal-base-unit').value = product.baseUnit || 'N/A';
        document.getElementById('modal-concentration').value = product.concentration || 'N/A';

        // Populate dropdown đơn vị nhập
        const packageUnitSelect = document.getElementById('modal-package-unit');
        packageUnitSelect.innerHTML = '';

        if (product.unitConversions && product.unitConversions.length > 0) {
            // Thêm đơn vị cơ bản (đơn vị nhỏ nhất) đầu tiên - CÓ THỂ CHỌN ĐƯỢC
            const baseUnitOption = document.createElement('option');
            // Giả sử đơn vị đầu tiên trong danh sách backend có multiplier = 1 (đơn vị cơ bản)
            // Nhưng backend chỉ trả về unitConversions không bao gồm đơn vị cơ bản
            // Nên ta cần tạo option cho đơn vị cơ bản dựa vào product.baseUnit
            if (product.baseUnit) {
                baseUnitOption.value = 'base_unit'; // Giá trị đặc biệt để nhận biết đơn vị cơ bản
                baseUnitOption.textContent = product.baseUnit;
                baseUnitOption.dataset.multiplier = '1'; // Đơn vị cơ bản có multiplier = 1
                baseUnitOption.dataset.unitName = product.baseUnit;
                packageUnitSelect.appendChild(baseUnitOption);
            }

            // Thêm tất cả các đơn vị khác vào dropdown
            product.unitConversions.forEach(uc => {
                const option = document.createElement('option');
                option.value = uc.unitId;
                option.textContent = uc.unitName;
                option.dataset.multiplier = uc.multiplier;
                option.dataset.unitName = uc.unitName;
                packageUnitSelect.appendChild(option);
            });

            // Chọn đơn vị lớn nhất làm mặc định (option cuối cùng)
            packageUnitSelect.selectedIndex = packageUnitSelect.options.length - 1;

            // Cập nhật tỉ lệ quy đổi ban đầu
            updateConversionRate();

            // Populate bảng danh sách đơn vị quy đổi
            populateUnitConversionTable(product);
        } else {
            // Nếu không có unit conversions
            const option = document.createElement('option');
            option.value = '';
            option.textContent = 'Không có đơn vị quy đổi';
            option.disabled = true;
            packageUnitSelect.appendChild(option);
            packageUnitSelect.selectedIndex = 0;
            document.getElementById('modal-conversion-rate').value = 'N/A';

            // Clear bảng đơn vị quy đổi
            document.getElementById('modal-unit-conversion-list').innerHTML =
                '<tr><td colspan="3" style="padding: 16px; text-align: center; color: #6b7280;">Không có đơn vị quy đổi</td></tr>';
        }

        // Đăng ký event listener cho dropdown (gỡ bỏ listener cũ trước nếu có)
        packageUnitSelect.removeEventListener('change', updateConversionRate);
        packageUnitSelect.addEventListener('change', updateConversionRate);

        // Reset các trường nhập liệu
        document.getElementById('modal-batch').value = '';
        document.getElementById('modal-manufacture-date').value = '';
        document.getElementById('modal-expiry-date').value = '';
        document.getElementById('modal-quantity').value = '1';
        document.getElementById('modal-price').value = '';

        // Clear validation errors
        modal.querySelectorAll('.invalid-feedback').forEach(el => el.style.display = 'none');
        modal.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));

        // Hiển thị modal
        modal.style.display = 'flex';
    }

    // Hàm populate bảng danh sách đơn vị quy đổi
    function populateUnitConversionTable(product) {
        const tbody = document.getElementById('modal-unit-conversion-list');
        tbody.innerHTML = '';

        if (!product.baseUnit) {
            tbody.innerHTML = '<tr><td colspan="3" style="padding: 16px; text-align: center; color: #6b7280;">Không có thông tin đơn vị</td></tr>';
            return;
        }

        // Thêm đơn vị cơ bản (đầu tiên)
        const baseRow = document.createElement('tr');
        baseRow.style.borderBottom = '1px solid #e5e7eb';
        baseRow.innerHTML = `
            <td style="padding: 10px 12px; font-weight: 500; color: #1e40af;">${product.baseUnit}</td>
            <td style="padding: 10px 12px; font-weight: 600; color: #059669;">1</td>
            <td style="padding: 10px 12px; color: #6b7280; font-style: italic;">Đơn vị cơ bản</td>
        `;
        tbody.appendChild(baseRow);

        // Thêm các đơn vị quy đổi khác
        if (product.unitConversions && product.unitConversions.length > 0) {
            product.unitConversions.forEach((uc, index) => {
                const row = document.createElement('tr');
                row.style.borderBottom = index < product.unitConversions.length - 1 ? '1px solid #e5e7eb' : 'none';
                row.innerHTML = `
                    <td style="padding: 10px 12px; font-weight: 500; color: #1e40af;">${uc.unitName}</td>
                    <td style="padding: 10px 12px; font-weight: 600; color: #059669;">${uc.multiplier}</td>
                    <td style="padding: 10px 12px; color: #6b7280;">${uc.note || '—'}</td>
                `;
                tbody.appendChild(row);
            });
        }
    }

    // Hàm cập nhật tỉ lệ quy đổi khi thay đổi đơn vị nhập
    function updateConversionRate() {
        const packageUnitSelect = document.getElementById('modal-package-unit');
        const selectedOption = packageUnitSelect.options[packageUnitSelect.selectedIndex];

        if (selectedOption && selectedOption.value) {
            const multiplier = selectedOption.dataset.multiplier;
            const unitName = selectedOption.dataset.unitName;
            const baseUnit = document.getElementById('modal-base-unit').value;

            // Nếu chọn đơn vị cơ bản (multiplier = 1)
            if (parseFloat(multiplier) === 1) {
                document.getElementById('modal-conversion-rate').value =
                    `1 ${unitName} = 1 ${baseUnit}`;
            } else {
                document.getElementById('modal-conversion-rate').value =
                    `1 ${unitName} = ${multiplier} ${baseUnit}`;
            }

            // Cập nhật quantityPerPackage trong tempProductData
            if (tempProductData) {
                tempProductData.selectedUnitId = selectedOption.value;
                tempProductData.selectedUnitName = unitName;
                tempProductData.selectedMultiplier = parseFloat(multiplier);
            }
        } else {
            document.getElementById('modal-conversion-rate').value = 'N/A';
        }
    }


    // Hàm đóng modal
    window.closeProductDetailModal = function() {
        const modal = document.getElementById('productDetailModal');
        modal.style.display = 'none';
        tempProductData = null;
    };

    // Hàm validate và thêm sản phẩm vào danh sách
    window.confirmAddProduct = function() {
        if (!tempProductData) return;

        const modal = document.getElementById('productDetailModal');
        const packageUnitSelect = document.getElementById('modal-package-unit');
        const batchInput = document.getElementById('modal-batch');
        const mfgInput = document.getElementById('modal-manufacture-date');
        const expInput = document.getElementById('modal-expiry-date');
        const qtyInput = document.getElementById('modal-quantity');
        const priceInput = document.getElementById('modal-price');

        let hasErrors = false;

        // Clear previous errors
        modal.querySelectorAll('.invalid-feedback').forEach(el => el.style.display = 'none');
        modal.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));

        // Validate đơn vị nhập
        if (!packageUnitSelect.value) {
            packageUnitSelect.classList.add('is-invalid');
            packageUnitSelect.nextElementSibling.textContent = 'Vui lòng chọn đơn vị nhập';
            packageUnitSelect.nextElementSibling.style.display = 'block';
            hasErrors = true;
        }

        // Validate số lô
        const batchCode = batchInput.value.trim();
        if (!batchCode) {
            batchInput.classList.add('is-invalid');
            batchInput.nextElementSibling.textContent = 'Vui lòng nhập số lô';
            batchInput.nextElementSibling.style.display = 'block';
            hasErrors = true;
        } else if (!/^[A-Za-z0-9\-]+$/.test(batchCode)) {
            batchInput.classList.add('is-invalid');
            batchInput.nextElementSibling.textContent = 'Số lô chỉ được chứa chữ, số và dấu gạch ngang';
            batchInput.nextElementSibling.style.display = 'block';
            hasErrors = true;
        } else {
            // Check duplicate batch code
            const allRows = productTableBody.querySelectorAll('.table-row');
            let isDuplicate = false;
            allRows.forEach(row => {
                const existingBatch = row.querySelector('.batch-input').value.trim();
                const existingVariantId = row.dataset.variantId;
                if (existingVariantId === tempProductData.variantId && existingBatch === batchCode) {
                    isDuplicate = true;
                }
            });

            if (isDuplicate) {
                batchInput.classList.add('is-invalid');
                batchInput.nextElementSibling.textContent = 'Số lô đã tồn tại cho thuốc này';
                batchInput.nextElementSibling.style.display = 'block';
                hasErrors = true;
            }
        }

        // Validate NSX
        if (!mfgInput.value) {
            mfgInput.classList.add('is-invalid');
            mfgInput.nextElementSibling.textContent = 'Vui lòng nhập ngày sản xuất';
            mfgInput.nextElementSibling.style.display = 'block';
            hasErrors = true;
        } else {
            const mfgDate = new Date(mfgInput.value);
            const currentDate = new Date();
            currentDate.setHours(0, 0, 0, 0);

            if (mfgDate > currentDate) {
                mfgInput.classList.add('is-invalid');
                mfgInput.nextElementSibling.textContent = 'NSX không được lớn hơn ngày hiện tại';
                mfgInput.nextElementSibling.style.display = 'block';
                hasErrors = true;
            }
        }

        // Validate HSD
        if (!expInput.value) {
            expInput.classList.add('is-invalid');
            expInput.nextElementSibling.textContent = 'Vui lòng nhập hạn sử dụng';
            expInput.nextElementSibling.style.display = 'block';
            hasErrors = true;
        } else if (mfgInput.value) {
            const mfgDate = new Date(mfgInput.value);
            const expDate = new Date(expInput.value);

            if (expDate <= mfgDate) {
                expInput.classList.add('is-invalid');
                expInput.nextElementSibling.textContent = 'HSD phải sau NSX';
                expInput.nextElementSibling.style.display = 'block';
                hasErrors = true;
            } else {
                const maxExpiryDate = new Date(mfgDate);
                maxExpiryDate.setFullYear(maxExpiryDate.getFullYear() + 20);

                if (expDate > maxExpiryDate) {
                    expInput.classList.add('is-invalid');
                    expInput.nextElementSibling.textContent = 'HSD không được quá 20 năm từ NSX';
                    expInput.nextElementSibling.style.display = 'block';
                    hasErrors = true;
                }
            }
        }

        // Validate số lượng
        const quantity = parseInt(qtyInput.value);
        if (!qtyInput.value || quantity < 1) {
            qtyInput.classList.add('is-invalid');
            qtyInput.nextElementSibling.textContent = 'Số lượng phải lớn hơn 0';
            qtyInput.nextElementSibling.style.display = 'block';
            hasErrors = true;
        } else {
            const quantityPerPackage = parseFloat(tempProductData.quantityPerPackage);
            if (quantityPerPackage && quantity > 0) {
                if (quantity % quantityPerPackage !== 0) {
                    qtyInput.classList.add('is-invalid');
                    qtyInput.nextElementSibling.textContent = `Số lượng phải chia hết cho ${Math.round(quantityPerPackage)} viên/hộp`;
                    qtyInput.nextElementSibling.style.display = 'block';
                    hasErrors = true;
                }
            }
        }

        // Validate giá nhập
        const price = parseFloat(priceInput.value);
        if (!priceInput.value || price <= 0) {
            priceInput.classList.add('is-invalid');
            priceInput.nextElementSibling.textContent = 'Giá nhập phải lớn hơn 0';
            priceInput.nextElementSibling.style.display = 'block';
            hasErrors = true;
        }

        // Nếu có lỗi thì dừng lại
        if (hasErrors) return;

        // Tạo row mới và thêm vào bảng
        addProductRowToTable({
            ...tempProductData,
            batch: batchCode,
            manufactureDate: mfgInput.value,
            expiryDate: expInput.value,
            quantity: quantity,
            price: price
        });

        // Đóng modal
        closeProductDetailModal();
    };

    // Hàm thêm sản phẩm vào bảng (logic cũ)
    function addProductRowToTable(product) {
        const rowCount = productTableBody.querySelectorAll('.table-row').length;
        const row = document.createElement('tr');
        row.className = 'table-row';
        row.dataset.variantId = product.variantId;
        row.dataset.quantityPerPackage = product.selectedMultiplier || product.quantityPerPackage || '';

        const displayUnit = product.selectedUnitName || product.packageUnit || product.unit;
        const qtyPerPackage = product.selectedMultiplier
            ? Math.round(product.selectedMultiplier)
            : (product.quantityPerPackage ? Math.round(product.quantityPerPackage) : 'N/A');

        row.innerHTML = `
            <td class="col-stt">${rowCount + 1}</td>
            <td class="col-medicine">${product.medicineName}</td>
            <td class="col-unit">${displayUnit}</td>
            <td class="col-qty-per-package">${qtyPerPackage}</td>
            <td class="col-concentration">${product.concentration}</td>
            <td class="col-batch">
                <input type="text" class="batch-input" value="${product.batch || ''}" placeholder="Nhập số lô" required>
                <div class="invalid-feedback">Số lô đã tồn tại</div>
            </td>
            <td class="col-manufacture-date">
                <input type="date" class="manufacture-date-input" value="${product.manufactureDate || ''}" required>
                <div class="invalid-feedback">NSX không hợp lệ</div>
            </td>
            <td class="col-expiry-date">
                <input type="date" class="expiry-date-input" value="${product.expiryDate || ''}" required>
                <div class="invalid-feedback">HSD không hợp lệ</div>
            </td>
            <td class="col-quantity">
                <input type="number" class="quantity-input" value="${product.quantity || 1}" min="1" required>
                <div class="invalid-feedback">Số lượng phải chia hết cho số viên trong hộp</div>
            </td>
            <td class="col-price">
                <input type="number" class="price-input" value="${product.price || ''}" min="1" placeholder="Giá nhập" required>
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
    btnAddFromCatalog.addEventListener('click', openProductCatalog);

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

        if (!isValid) return false;

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
                             data-quantity-per-package="${medicine.quantityPerPackage || ''}"
                             data-base-unit="${medicine.baseUnit || ''}"
                             data-package-unit="${medicine.packageUnit || ''}"
                             data-unit-conversions='${JSON.stringify(medicine.unitConversions || [])}'>
                            <div class="item-name">${medicine.medicineName}</div>
                            <div class="item-info">
                                ${medicine.baseUnit && medicine.packageUnit
                                    ? `${medicine.packageUnit} → ${medicine.baseUnit} (${medicine.quantityPerPackage || 'N/A'})`
                                    : `ĐVT: ${medicine.unit}`}
                                | Hàm lượng: ${medicine.concentration || 'N/A'}
                            </div>
                        </div>
                    `).join('');
                })
                .catch(error => {
                    console.error('Error loading medicines:', error);
                    medicineList.innerHTML = '<div class="error">Không thể tải danh sách thuốc</div>';
                });
        }

        // Use event delegation to prevent duplicate handlers
        medicineList.addEventListener('click', function(e) {
            const item = e.target.closest('.modal-list-item');
            if (!item) return;

            const product = {
                variantId: item.dataset.id,
                medicineName: item.dataset.name,
                unit: item.dataset.unit,
                concentration: item.dataset.concentration,
                quantityPerPackage: item.dataset.quantityPerPackage,
                baseUnit: item.dataset.baseUnit,
                packageUnit: item.dataset.packageUnit,
                unitConversions: JSON.parse(item.dataset.unitConversions || '[]')
            };

            addProductRow(product);
            modal.remove();
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

