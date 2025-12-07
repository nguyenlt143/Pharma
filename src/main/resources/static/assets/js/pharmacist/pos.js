// DOM Elements
const searchInput = document.querySelector('.search-input');
const searchButton = document.querySelector('.search-button');
const clearButtons = document.querySelectorAll('.clear-button');
const paymentButton = document.querySelector('.payment-button');
const customerNameInput = document.querySelector('.customer-info .form-input');
const phoneInput = document.querySelector('.customer-info .form-group:nth-child(2) .form-input');
const paymentAmountInput = document.querySelector('.payment-details .form-input');
const paymentMethodSelect = document.querySelector('#paymentMethod');
const notesTextarea = document.querySelector('.form-textarea');
const qrCodeSection = document.querySelector('#qrCodeSection');

const resultContainer = document.querySelector('#medicine-list');

// Check critical elements
if (!searchInput) {
    console.error('Search input element not found!');
}
if (!resultContainer) {
    console.error('Result container element not found!');
}

// Payment method change event listener
if (paymentMethodSelect && qrCodeSection) {
    paymentMethodSelect.addEventListener('change', function() {
        const selectedMethod = this.value;

        if (selectedMethod === 'transfer') {
            qrCodeSection.style.display = 'block';
            // Generate QR code with payment amount
            updateQRCode();
        } else {
            qrCodeSection.style.display = 'none';
        }
    });
}

let debounceTimer;

// Only add event listener if searchInput exists
if (searchInput) {
  searchInput.addEventListener('input', () => {
  try {
    console.log('Search input triggered');
    clearTimeout(debounceTimer);

    debounceTimer = setTimeout(() => {
      const searchTerm = searchInput.value.trim();
      console.log('Search term:', searchTerm);

      if (searchTerm.length === 0) {
        resultContainer.innerHTML = "";
        return;
      }

      console.log('Fetching search results...');
      fetch(`/pharmacist/pos/api/search?keyword=${encodeURIComponent(searchTerm)}`)
        .then(res => {
          console.log('Search API response status:', res.status);
          return res.json();
        })
        .then(data => {
          console.log('Search results:', data);
          renderResults(data);
        })
        .catch(error => {
          console.error('Search API error:', error);
          resultContainer.innerHTML = '<div style="color: red; padding: 10px;">Lỗi tìm kiếm: ' + error.message + '</div>';
        });
    }, 300); // delay 300ms
  } catch (error) {
    console.error('Error in search input handler:', error);
  }
  });
} else {
  console.error('Cannot add search event listener - searchInput element not found');
}

function renderResults(medicines) {
    let html = "";
    medicines.forEach(medicine => {
        html += `
      <div class="medicine-card"
      data-medicine-id="${medicine.id}"
      data-medicine-name="${medicine.name}">
        <h3 class="medicine-name">${medicine.name}</h3>
        <p class="medicine-ingredient">Hoạt chất: ${medicine.activeIngredient}</p>
        <div class="variant-details" style="display: none;"></div>
      </div>
    `;
    });
    resultContainer.innerHTML = html;
    addEventListenersToMedicineCards();
}

function addEventListenersToMedicineCards() {
    const medicineCards = document.querySelectorAll('.medicine-card');
    medicineCards.forEach(card => {
        const medicineName = card.dataset.medicineName;
        card.addEventListener('click', () => {
            const medicineId = card.dataset.medicineId;
            const detailsContainer = card.querySelector('.variant-details');
            const isDisplayed = detailsContainer.style.display === 'block';

            if (!isDisplayed) {
                // Fetch variants and inventory for the clicked medicine
                fetch(`/pharmacist/pos/api/medicine/${medicineId}/variants`)
                    .then(res => res.json())
                    .then(variants => {
                        let detailsHtml = '<h4>Các loại thuốc có sẵn:</h4>';
                        if (variants.length === 0) {
                            detailsHtml += '<p>Không có loại nào trong kho.</p>';
                        } else {
                            detailsHtml += '<table class="variant-table" style="width:100%; border-collapse: collapse; margin-top: 10px;">';
                            detailsHtml += '<thead><tr style="background-color: #f0f0f0;"><th style="border: 1px solid #ddd; padding: 8px;">Thông tin biến thể</th><th style="border: 1px solid #ddd; padding: 8px;">Chi tiết kho</th></tr></thead><tbody>';

                            variants.forEach(variant => {
                                // Variant information column
                                let variantInfoHtml = `<td style="border: 1px solid #ddd; padding: 8px; vertical-align: top;">
                                    <strong>Dạng: ${variant.dosageForm || 'N/A'}</strong><br>
                                    <strong>Nồng độ: ${variant.strength || 'N/A'}</strong><br>
                                    Liều lượng: ${variant.dosage || 'N/A'}<br>
                                    Đóng gói: ${variant.quantityPerPackage || 'N/A'} ${variant.baseUnitName || ''} / ${variant.packageUnitName || ''}<br>
                                    Điều kiện bảo quản: ${variant.storageConditions || 'N/A'}<br>
                                    Chỉ định: ${variant.indications || 'N/A'}<br>
                                    Chống chỉ định: ${variant.contraindications || 'N/A'}<br>
                                    Tác dụng phụ: ${variant.sideEffects || 'N/A'}<br>
                                    Hướng dẫn: ${variant.instructions || 'N/A'}<br>
                                    Cần đơn thuốc: ${variant.prescriptionRequire ? 'Có' : 'Không'}<br>
                                    Công dụng: ${variant.uses || 'N/A'}<br>
                                    Quốc gia: ${variant.country || 'N/A'}<br>
                                </td>`;

                                // Inventory details column
                                let inventoryInfoHtml = `<td style="border: 1px solid #ddd; padding: 8px; vertical-align: top;">`;
                                if (variant.inventories && variant.inventories.length > 0) {
                                    variant.inventories.forEach(inv => {
                                        const expiryDate = inv.expiryDate ? new Date(inv.expiryDate).toLocaleDateString('vi-VN') : 'N/A';
                                        const salePrice = inv.salePrice ? inv.salePrice.toLocaleString('vi-VN') + ' VNĐ' : 'Chưa có giá';
                                        inventoryInfoHtml += `
                                            <div class="inventory-wrapper" style="margin-bottom: 10px; padding: 8px; background-color: #f9f9f9; border-radius: 4px;">
                                                <div class="inventory-item"
                                                data-inventory-id="${inv.id}"
                                                data-medicine-name="${medicineName}"
                                                data-units='${JSON.stringify(variant.unitConversion)}'
                                                data-strength="${variant.strength}"
                                                data-base-unit-name="${variant.baseUnitName}"
                                                data-variant-id="${variant.variantId}"
                                                data-batch-number="${inv.batchNumber}"
                                                data-expiry-date="${inv.expiryDate}"
                                                data-sale-price="${inv.salePrice}"
                                                data-max-quantity="${inv.quantity}"
                                                style="cursor: pointer; padding: 4px; border-radius: 2px;"
                                                title="Click để thêm vào đơn thuốc">
                                                    <strong>Số lô: ${inv.batchNumber || 'N/A'}</strong><br>
                                                    HSD: ${expiryDate}<br>
                                                    Tồn kho: <strong>${inv.quantity}</strong> ${variant.baseUnitName || ''}<br>
                                                    Giá bán: <strong style="color: #c0392b;">${salePrice}</strong>
                                                </div>
                                                <div style="margin-top: 8px;">
                                                    <button class="add-to-cart-btn"
                                                            data-inventory-id="${inv.id}"
                                                            data-medicine-name="${medicineName}"
                                                            data-units='${JSON.stringify(variant.unitConversion)}'
                                                            data-strength="${variant.strength}"
                                                            data-base-unit-name="${variant.baseUnitName}"
                                                            data-variant-id="${variant.variantId}"
                                                            data-batch-number="${inv.batchNumber}"
                                                            data-expiry-date="${inv.expiryDate}"
                                                            data-sale-price="${inv.salePrice}"
                                                            data-max-quantity="${inv.quantity}"
                                                            style="background: #28a745; color: white; border: none; padding: 6px 12px; border-radius: 3px; font-size: 12px; cursor: pointer; width: 100%;"
                                                            ${inv.quantity <= 0 ? 'disabled' : ''}>
                                                        ${inv.quantity <= 0 ? 'Hết hàng' : 'Thêm vào đơn'}
                                                    </button>
                                                </div>
                                            </div>
                                        `;
                                    });
                                } else {
                                    inventoryInfoHtml += '<span style="color: red;">Hết hàng</span>';
                                }
                                inventoryInfoHtml += `</td>`;

                                detailsHtml += `<tr>${variantInfoHtml}${inventoryInfoHtml}</tr>`;
                            });
                            detailsHtml += '</tbody></table>';
                            detailsHtml += '</div>'; // Close variant-table-wrapper
                        }
                        detailsContainer.innerHTML = detailsHtml;
                        detailsContainer.style.display = 'block';

                        // Auto-scroll to ensure the card is visible
                        // Use longer timeout to ensure DOM is fully rendered with correct heights
                        setTimeout(() => {
                            ensureCardVisible(card);
                        }, 300); // Increased to 300ms for better rendering

                        // Add event listeners to inventory items
                        addInventoryItemClickListeners();
                    })
                    .catch(error => {
                        console.error('Error fetching variant details:', error);
                        detailsContainer.innerHTML = '<p style="color: red;">Không thể tải chi tiết thuốc.</p>';
                        detailsContainer.style.display = 'block';

                        // Auto-scroll even on error
                        setTimeout(() => {
                            ensureCardVisible(card);
                        }, 300);
                    });
            } else {
                detailsContainer.style.display = 'none';
            }
        });
    });
}

// Function to ensure expanded card is fully visible in the viewport
function ensureCardVisible(card) {
    const medicineList = document.querySelector('.medicine-list');
    if (!medicineList || !card) {
        console.warn('ensureCardVisible: medicineList or card not found');
        return;
    }

    // Use double requestAnimationFrame to ensure DOM is fully updated
    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            // Force layout recalculation
            const cardTop = card.offsetTop;
            const cardHeight = card.offsetHeight;
            const listScrollTop = medicineList.scrollTop;
            const listScrollHeight = medicineList.scrollHeight;

            console.log('Scroll Debug:', {
                cardTop,
                cardHeight,
                listScrollTop,
                listScrollHeight,
                cardElement: card.querySelector('.medicine-name')?.textContent
            });

            // Always scroll card to near top of viewport
            // This ensures user can see the card header and scroll down to see all content
            const targetScrollTop = Math.max(0, cardTop - 30); // 30px from top

            console.log('Scrolling to:', targetScrollTop, 'from:', listScrollTop);

            // Scroll to target position
            medicineList.scrollTo({
                top: targetScrollTop,
                behavior: 'smooth'
            });
        });
    });
}

// State for the prescription
let prescriptionItems = [];

function addInventoryItemClickListeners() {
    console.log('Setting up inventory item click listeners');

    // Remove existing event listeners to prevent duplicates
    document.removeEventListener('click', handleInventoryClicks);

    // Add event delegation for inventory items and buttons
    document.addEventListener('click', handleInventoryClicks);
}

function handleInventoryClicks(e) {
    // Handle add-to-cart button clicks
    if (e.target.classList.contains('add-to-cart-btn')) {
        e.stopPropagation();
        e.preventDefault();

        console.log('Add to cart button clicked');
        const button = e.target;

        // Extract data from button's data attributes
        const inventoryData = {
            inventoryId: button.dataset.inventoryId,
            medicineName: button.dataset.medicineName,
            unitConversions: JSON.parse(button.dataset.units || "[]"),
            maxQuantity: parseInt(button.dataset.maxQuantity, 10),
            salePrice: parseFloat(button.dataset.salePrice),
            strength: button.dataset.strength,
            batchNumber: button.dataset.batchNumber,
            expiryDate: button.dataset.expiryDate,
            baseUnitName: button.dataset.baseUnitName
        };

        addItemToPrescription(inventoryData, button);
        return;
    }

    // Handle inventory-item div clicks (alternative way to add)
    if (e.target.closest('.inventory-item')) {
        const item = e.target.closest('.inventory-item');

        // Don't trigger if click was on a button
        if (e.target.tagName === 'BUTTON') return;

        console.log('Inventory item clicked');

        // Extract data from inventory item's data attributes
        const inventoryData = {
            inventoryId: item.dataset.inventoryId,
            medicineName: item.dataset.medicineName,
            unitConversions: JSON.parse(item.dataset.units || "[]"),
            maxQuantity: parseInt(item.dataset.maxQuantity, 10),
            salePrice: parseFloat(item.dataset.salePrice),
            strength: item.dataset.strength,
            batchNumber: item.dataset.batchNumber,
            expiryDate: item.dataset.expiryDate,
            baseUnitName: item.dataset.baseUnitName
        };

        addItemToPrescription(inventoryData, null);
    }
}

function addItemToPrescription(inventoryData, button) {
    try {
        console.log('Adding item to prescription:', inventoryData);

        // Validate inventory data
        if (!inventoryData.inventoryId) {
            alert('Lỗi: Không tìm thấy thông tin inventory.');
            return;
        }

        if (isNaN(inventoryData.maxQuantity) || inventoryData.maxQuantity <= 0) {
            alert('Sản phẩm đã hết hàng.');
            return;
        }

        if (isNaN(inventoryData.salePrice) || inventoryData.salePrice <= 0) {
            alert('Sản phẩm chưa có giá bán. Vui lòng cập nhật giá trước khi bán.');
            return;
        }

        const existingItem = prescriptionItems.find(p => p.inventoryId === inventoryData.inventoryId);

        if (existingItem) {
            // Validate quantity before increasing
            if (existingItem.quantity < inventoryData.maxQuantity) {
                existingItem.quantity++;

                // Visual feedback for button
                if (button) {
                    const originalText = button.textContent;
                    button.textContent = 'Đã thêm!';
                    button.style.background = '#17a2b8';
                    setTimeout(() => {
                        button.textContent = originalText;
                        button.style.background = '#28a745';
                    }, 1000);
                }
            } else {
                alert('Số lượng đã đạt tối đa tồn kho (' + inventoryData.maxQuantity + ').');
                return;
            }
        } else {
            // Create new prescription item
            const newItem = {
                inventoryId: inventoryData.inventoryId,
                medicineName: inventoryData.medicineName || 'N/A',
                strength: inventoryData.strength || '',
                dosageForm: 'N/A',
                baseUnitName: inventoryData.baseUnitName || 'Đơn vị',
                packageUnitName: '',
                batchNumber: inventoryData.batchNumber || 'N/A',
                expiryDate: inventoryData.expiryDate || 'N/A',
                salePrice: inventoryData.salePrice,
                currentPrice: inventoryData.salePrice,
                unitPrice: inventoryData.salePrice,
                quantity: 1,
                maxQuantity: inventoryData.maxQuantity,
                baseStock: inventoryData.maxQuantity,
                selectedMultiplier: 1,
                units: inventoryData.unitConversions
            };

            console.log('Adding new item to prescription:', newItem);
            prescriptionItems.push(newItem);

            // Visual feedback for button
            if (button) {
                const originalText = button.textContent;
                button.textContent = 'Đã thêm!';
                button.style.background = '#17a2b8';
                setTimeout(() => {
                    button.textContent = originalText;
                    button.style.background = '#28a745';
                }, 1000);
            }
        }

        renderPrescription();
    } catch (error) {
        console.error('Error adding item to prescription:', error);
        alert('Có lỗi xảy ra khi thêm vào đơn: ' + error.message);
    }
}



function getTotalAmount() {
    return prescriptionItems.reduce((sum, item) => {
        return sum + (item.quantity * item.currentPrice);
    }, 0);
}

function renderPrescription() {
    const prescriptionBody = document.getElementById('prescription-items');
    const totalAmountEl = document.querySelector('.total-amount');
    const paymentValues = document.querySelectorAll('.payment-details .payment-value');
    let totalAmount = 0;

    if (!prescriptionBody) return;

    prescriptionBody.innerHTML = ''; // Clear existing items

    prescriptionItems.forEach((item, index) => {
        const itemTotal = item.quantity * item.currentPrice;
        totalAmount += itemTotal;

        const row = document.createElement('tr');

        const medicineDisplayName = item.strength ? `${item.medicineName} - ${item.strength}` : item.medicineName;

        row.innerHTML = `
            <td>${index + 1}</td>
            <td>
                <div class="medicine-info">
                    <div class="medicine-title">${medicineDisplayName}</div>
                    <div class="medicine-detail">Lô: ${item.batchNumber} - HSD: ${item.expiryDate}</div>
                </div>
            </td>
            <td>
                <select class="unit-select"
                        data-inventory-id="${item.inventoryId}"
                        title="Chọn đơn vị bán hàng">
                    ${item.units.map(u => `
                    <option value="${u.multiplier}"
                            data-unit="${u.unitName}"
                            ${item.selectedMultiplier === u.multiplier ? "selected" : ""}>
                        ${u.unitName}
                    </option>
                    `).join('')}
                </select>
            </td>
            <td>
                <input type="number"
                       class="quantity-input"
                       value="${item.quantity}"
                       min="1"
                       max="${item.maxQuantity}"
                       data-inventory-id="${item.inventoryId}"
                       title="Tồn kho: ${item.maxQuantity}"
                       placeholder="SL">
            </td>
            <td class="text-right">${item.currentPrice.toLocaleString('vi-VN')}</td>
            <td class="text-right">${itemTotal.toLocaleString('vi-VN')}</td>
            <td class="text-center">
                    <button class="delete-item-btn"
                            data-index="${index}"
                            title="Xóa sản phẩm">🗑</button>
            </td>
        `;
        prescriptionBody.appendChild(row);
    });

    if (totalAmountEl) {
        totalAmountEl.textContent = totalAmount.toLocaleString('vi-VN');
    }

    // Update payment section with total
    if (paymentValues.length >= 2) {
        paymentValues[0].textContent = totalAmount.toLocaleString('vi-VN');
        paymentValues[1].textContent = totalAmount.toLocaleString('vi-VN');
    }

    // Add event listeners for new elements
    addPrescriptionActionListeners();

    // Update payment totals and QR code
    updatePaymentTotals();

    // Update Clear All button state
    updateClearAllButtonState();
}

// Function to update Clear All button state
function updateClearAllButtonState() {
    const clearAllBtn = document.getElementById('clearAllBtn');
    if (clearAllBtn) {
        if (prescriptionItems.length === 0) {
            clearAllBtn.disabled = true;
            clearAllBtn.style.opacity = '0.5';
            clearAllBtn.style.cursor = 'not-allowed';
        } else {
            clearAllBtn.disabled = false;
            clearAllBtn.style.opacity = '1';
            clearAllBtn.style.cursor = 'pointer';
        }
    }
}

function addPrescriptionActionListeners() {

    document.querySelectorAll('.unit-select').forEach(select => {
        select.addEventListener('change', (e) => {
            const index = parseInt(e.target.closest('tr').rowIndex - 1, 10);
            const multiplier = parseInt(e.target.value, 10);

            const item = prescriptionItems[index];
            item.selectedMultiplier = multiplier;

            // Cập nhật giá theo đơn vị
            item.currentPrice = item.salePrice * multiplier;

            // Cập nhật tồn kho tối đa theo đơn vị mới
            // baseStock là tổng tồn tính theo đơn vị nhỏ nhất
            item.maxQuantity = Math.floor(item.baseStock / multiplier);

            // Reset quantity nếu vượt mức
            if (item.quantity > item.maxQuantity) {
                item.quantity = item.maxQuantity;
            }

            renderPrescription();
        });
    });

    // Quantity change with validation
    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('change', (e) => {
            const inventoryId = e.target.dataset.inventoryId;
            let newQuantity = parseInt(e.target.value, 10);
            const item = prescriptionItems.find(p => p.inventoryId === inventoryId);

            if (item) {
                // Remove any previous error/success classes
                e.target.classList.remove('error', 'success');

                // Validate input is a valid number
                if (isNaN(newQuantity)) {
                    e.target.classList.add('error');
                    alert('Vui lòng nhập số lượng hợp lệ.');
                    e.target.value = item.quantity;
                    setTimeout(() => e.target.classList.remove('error'), 2000);
                    return;
                }

                // Validate quantity does not exceed max stock
                if (newQuantity > item.maxQuantity) {
                    e.target.classList.add('error');
                    alert(`Số lượng vượt quá tồn kho. Tồn kho hiện tại: ${item.maxQuantity}`);
                    newQuantity = item.maxQuantity;
                    e.target.value = newQuantity;
                    setTimeout(() => e.target.classList.remove('error'), 2000);
                }

                // Validate quantity is at least 1
                if (newQuantity < 1) {
                    e.target.classList.add('error');
                    alert('Số lượng phải lớn hơn 0.');
                    newQuantity = 1;
                    e.target.value = newQuantity;
                    setTimeout(() => e.target.classList.remove('error'), 2000);
                }

                // If validation passed, show success feedback
                if (newQuantity >= 1 && newQuantity <= item.maxQuantity) {
                    e.target.classList.add('success');
                    setTimeout(() => e.target.classList.remove('success'), 1000);
                }

                item.quantity = newQuantity;
                renderPrescription();
            }
        });

        // Prevent entering invalid characters
        input.addEventListener('keypress', (e) => {
            // Only allow numbers
            if (e.key && !/[0-9]/.test(e.key) && e.key !== 'Enter' && e.key !== 'Backspace') {
                e.preventDefault();
            }
        });

        // Add input event for real-time validation feedback
        input.addEventListener('input', (e) => {
            const value = e.target.value;
            const inventoryId = e.target.dataset.inventoryId;
            const item = prescriptionItems.find(p => p.inventoryId === inventoryId);

            if (item) {
                e.target.classList.remove('error', 'success');

                const numValue = parseInt(value, 10);
                if (value && (isNaN(numValue) || numValue < 1 || numValue > item.maxQuantity)) {
                    e.target.classList.add('error');
                }
            }
        });
    });

    // Delete item buttons
    document.querySelectorAll('.delete-item-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const index = parseInt(e.target.dataset.index);

            // Remove item without confirmation
            prescriptionItems.splice(index, 1);
            renderPrescription();
        });
    });
}

// Clear buttons
clearButtons.forEach(button => {
  button.addEventListener('click', () => {
    const input = button.parentElement.querySelector('.form-input');
    if (input) {
      input.value = '';
      input.focus();
    }
  });
});

// Clear All Button - Xóa tất cả sản phẩm trong đơn thuốc
const clearAllBtn = document.getElementById('clearAllBtn');
if (clearAllBtn) {
    clearAllBtn.addEventListener('click', () => {
        if (prescriptionItems.length === 0) {
            return;
        }

        // Clear all items without confirmation
        prescriptionItems.length = 0;
        renderPrescription();

        // Show notification (optional)
        console.log('Đã xóa tất cả sản phẩm trong đơn thuốc');
    });
}

// OLD PAYMENT FUNCTIONALITY REMOVED - Now handled by form submission with proper validation

// F8 focus payment input
document.addEventListener('keydown', (e) => {
  if (e.key === 'F8') {
    e.preventDefault();
    paymentAmountInput.focus();
  }
});

// Auto-calc change
if (paymentAmountInput) {
  paymentAmountInput.addEventListener('input', () => {
    const paymentAmount = parseFloat(paymentAmountInput.value) || 0;
    const totalAmount = getTotalAmount();
    const change = Math.max(0, paymentAmount - totalAmount);

   const changeElement = document.getElementById('changeAmount');
    if (changeElement) {
     changeElement.textContent = change.toLocaleString('vi-VN');
    }
  });
}

// Utility functions
function searchMedication(searchTerm) {
  console.log('Searching for medication:', searchTerm);
  setTimeout(() => {
    console.log('Search results for:', searchTerm);
  }, 500);
}


function processPayment(paymentData) {
  console.log("Sending invoice:", paymentData);

  fetch('/pharmacist/pos/api/invoices', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(paymentData)
  })
  .then(async res => {
    if (!res.ok) {
      // Đọc lỗi BE trả về để hiển thị
      const errData = await res.json().catch(() => ({}));
      throw new Error(errData.message || "Lỗi tạo hóa đơn");
    }

    return res.json();
  })
  .then(result => {
    alert(`Thanh toán thành công! Mã hóa đơn: ${result.invoiceCode}`);
    clearPaymentForm();
    prescriptionItems = [];
    renderPrescription();
  })
  .catch(err => {
    console.error("Payment error", err);
    alert(err.message || "Thanh toán thất bại!");
  });
}


function clearPaymentForm() {
  customerNameInput.value = '';
  phoneInput.value = '';
  paymentAmountInput.value = '';
  notesTextarea.value = '';
  paymentMethodSelect.selectedIndex = 0;
  const lastPaymentValue = document.querySelector('.payment-row:last-of-type .payment-value');
  if (lastPaymentValue) {
    lastPaymentValue.textContent = '0';
  }
}

// This function is defined later with QR code functionality

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
  if (e.ctrlKey && e.key === 'f') {
    e.preventDefault();
    if (searchInput) {
      searchInput.focus();
      searchInput.select();
    }
  }

  if (e.key === 'Escape' && document.activeElement === searchInput) {
    searchInput.value = '';
  }
});

// ============ VALIDATION FUNCTIONS ============

function showError(fieldId, message) {
    const field = document.getElementById(fieldId);
    const errorDiv = document.getElementById(fieldId + '-error');

    if (field) {
        field.classList.add('is-invalid');
    }
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }
}

function clearError(fieldId) {
    const field = document.getElementById(fieldId);
    const errorDiv = document.getElementById(fieldId + '-error');

    if (field) {
        field.classList.remove('is-invalid');
    }
    if (errorDiv) {
        errorDiv.textContent = '';
        errorDiv.style.display = 'none';
    }
}

// Removed duplicate clearInput function - using the correct one below

function showAlert(type, message) {
    const alertId = type === 'success' ? 'successAlert' : 'errorAlert';
    const messageId = type === 'success' ? 'successMessage' : 'errorMessage';

    const alert = document.getElementById(alertId);
    const messageEl = document.getElementById(messageId);

    if (alert && messageEl) {
        messageEl.textContent = message;
        alert.style.display = 'block';

        // Auto hide after 5 seconds
        setTimeout(() => {
            alert.style.display = 'none';
        }, 5000);
    }
}

function validatePhoneNumber(fieldId) {
    const field = document.getElementById(fieldId);
    if (!field) return true;

    const value = field.value.trim();
    clearError(fieldId);

    // Nếu để trống thì không validate - sẽ dùng "Không có"
    if (!value) return true;

    // Chỉ validate khi người dùng thực sự nhập số điện thoại
    const phonePattern = /^(0|\+84)[0-9]{9,10}$/;
    if (!phonePattern.test(value)) {
        showError(fieldId, 'Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10-11 chữ số');
        return false;
    }

    return true;
}

function validateField(fieldId, rules) {
    const field = document.getElementById(fieldId);
    if (!field) return true;

    const value = field.value.trim();
    clearError(fieldId);

    // Check required
    if (rules.required && !value) {
        showError(fieldId, rules.requiredMessage || `${fieldId} không được để trống`);
        return false;
    }

    // Skip other validations if field is empty and not required
    if (!value && !rules.required) return true;

    // Check pattern
    if (rules.pattern && !rules.pattern.test(value)) {
        showError(fieldId, rules.patternMessage || `${fieldId} không đúng định dạng`);
        return false;
    }

    // Check min/max length
    if (rules.minLength && value.length < rules.minLength) {
        showError(fieldId, `${fieldId} phải có ít nhất ${rules.minLength} ký tự`);
        return false;
    }

    if (rules.maxLength && value.length > rules.maxLength) {
        showError(fieldId, `${fieldId} không được vượt quá ${rules.maxLength} ký tự`);
        return false;
    }

    // Check numeric values
    if (rules.type === 'number') {
        const numValue = parseFloat(value);
        if (isNaN(numValue)) {
            showError(fieldId, `${fieldId} phải là số hợp lệ`);
            return false;
        }

        if (rules.min !== undefined && numValue < rules.min) {
            showError(fieldId, rules.minMessage || `${fieldId} phải lớn hơn hoặc bằng ${rules.min}`);
            return false;
        }

        if (rules.max !== undefined && numValue > rules.max) {
            showError(fieldId, `${fieldId} phải nhỏ hơn hoặc bằng ${rules.max}`);
            return false;
        }
    }

    return true;
}

function validatePaymentForm() {
    const hasItems = prescriptionItems.length > 0;
    const payButton = document.getElementById('payButton');

    // If no items, just disable button and return - don't validate
    if (!hasItems) {
        if (payButton) {
            payButton.disabled = true;
            payButton.textContent = 'Chưa có sản phẩm';
        }
        return false;
    }

    const totalAmount = getTotalAmount();

    // Only validate when there are items
    const validations = [
        validateField('customerName', {
            required: false,
            maxLength: 100
        }),

        validatePhoneNumber('phoneNumber'),

        validateField('paidAmount', {
            required: true,
            type: 'number',
            min: totalAmount,
            requiredMessage: 'Số tiền khách thanh toán không được để trống',
            minMessage: `Số tiền thanh toán phải ít nhất ${totalAmount.toLocaleString('vi-VN')} VNĐ`
        }),

        validateField('paymentMethod', {
            required: true,
            requiredMessage: 'Phương thức thanh toán không được để trống'
        }),

        validateField('note', {
            required: false,
            maxLength: 500
        })
    ];

    const isFormValid = validations.every(v => v);

    // Update pay button state
    if (payButton) {
        payButton.disabled = !isFormValid || !hasItems;

        if (!hasItems) {
            payButton.textContent = 'Chưa có sản phẩm';
        } else if (!isFormValid) {
            payButton.textContent = 'Vui lòng điền đầy đủ thông tin';
        } else {
            payButton.textContent = 'Thanh toán';
        }
    }

    return isFormValid && hasItems;
}

// ============ UTILITY FUNCTIONS ============

// Clear input function called from HTML
function clearInput(fieldId) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.value = '';
        field.focus();

        // Update placeholder to show what will be used if left empty
        if (fieldId === 'customerName') {
            field.placeholder = 'Để trống sẽ dùng "Khách lẻ"';
        } else if (fieldId === 'phoneNumber') {
            field.placeholder = 'Để trống sẽ dùng "Không có"';
        }

        validatePaymentForm();
    }
}

// Make clearInput available globally
window.clearInput = clearInput;

// Complete form reset function
function resetPaymentFormCompletely() {
    // Reset form fields
    const form = document.getElementById('paymentForm');
    if (form) {
        form.reset();
    }

    // Clear all validation errors
    const errorFields = ['customerName', 'phoneNumber', 'paidAmount', 'paymentMethod', 'note'];
    errorFields.forEach(fieldId => {
        clearError(fieldId);
        const field = document.getElementById(fieldId);
        if (field) {
            field.classList.remove('is-invalid');
        }
    });

    // Reset amount displays
    const changeElement = document.getElementById('changeAmount');
    if (changeElement) {
        changeElement.textContent = '0';
    }

    const subtotalEl = document.getElementById('subtotal');
    const totalAmountEl = document.getElementById('totalAmount');
    const qrDisplayAmount = document.getElementById('qrDisplayAmount');

    if (subtotalEl) subtotalEl.textContent = '0';
    if (totalAmountEl) totalAmountEl.textContent = '0';
    if (qrDisplayAmount) qrDisplayAmount.textContent = '0';

    // Reset placeholders to original state
    const customerNameInput = document.getElementById('customerName');
    const phoneNumberInput = document.getElementById('phoneNumber');

    if (customerNameInput) {
        customerNameInput.placeholder = 'Nhập tên khách hàng (để trống = Khách lẻ)';
    }
    if (phoneNumberInput) {
        phoneNumberInput.placeholder = 'Nhập số điện thoại (để trống = Không có)';
    }

    // Reset payment method
    const paymentMethodSelect = document.getElementById('paymentMethod');
    if (paymentMethodSelect) {
        paymentMethodSelect.selectedIndex = 0;
    }

    // Reset pay button
    const payButton = document.getElementById('payButton');
    if (payButton) {
        payButton.disabled = true;
        payButton.textContent = 'Chưa có sản phẩm';
    }
}

// ============ EVENT LISTENERS ============

// Initialize validation on page load
document.addEventListener('DOMContentLoaded', function() {
    // Form validation
    const paymentForm = document.getElementById('paymentForm');
    if (paymentForm) {
        paymentForm.addEventListener('submit', function(e) {
            e.preventDefault();

            if (!validatePaymentForm()) {
                showAlert('error', 'Vui lòng kiểm tra lại thông tin đã nhập');
                return;
            }

            if (prescriptionItems.length === 0) {
                showAlert('error', 'Chưa có sản phẩm nào trong đơn hàng');
                return;
            }

            // Collect form data with default values only if truly empty
            let customerName = document.getElementById('customerName').value.trim();
            let phoneNumber = document.getElementById('phoneNumber').value.trim();

            // Use default values only if user left fields completely empty
            if (!customerName) customerName = 'Khách lẻ';
            if (!phoneNumber) phoneNumber = 'Không có';

            const formData = {
                customerName: customerName,
                phoneNumber: phoneNumber,
                totalAmount: getTotalAmount(),
                paymentMethod: document.getElementById('paymentMethod').value,
                note: document.getElementById('note').value.trim(),
                items: prescriptionItems.map(item => ({
                    inventoryId: item.inventoryId,
                    quantity: item.quantity,
                    unitPrice: item.currentPrice,
                    selectedMultiplier: item.selectedMultiplier
                }))
            };

            processPaymentWithValidation(formData);
        });
    }

    // Real-time validation on input with default value handling
    ['customerName', 'phoneNumber', 'paidAmount', 'paymentMethod', 'note'].forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (field) {
            field.addEventListener('input', () => {
                clearError(fieldId);
                setTimeout(validatePaymentForm, 100); // Debounce
            });

            field.addEventListener('blur', () => {
                // Just validate, don't auto-fill default values
                validatePaymentForm();
            });
        }
    });

    // Calculate change amount
    const paidAmountField = document.getElementById('paidAmount');
    if (paidAmountField) {
        paidAmountField.addEventListener('input', function() {
            const paidAmount = parseFloat(this.value) || 0;
            const totalAmount = getTotalAmount();
            const change = Math.max(0, paidAmount - totalAmount);

            const changeElement = document.getElementById('changeAmount');
            if (changeElement) {
                changeElement.textContent = change.toLocaleString('vi-VN');
            }
        });
    }

    // Update totals when prescription changes
    const observer = new MutationObserver(() => {
        const totalAmount = getTotalAmount();
        const subtotalEl = document.getElementById('subtotal');
        const totalAmountEl = document.getElementById('totalAmount');

        if (subtotalEl) subtotalEl.textContent = totalAmount.toLocaleString('vi-VN');
        if (totalAmountEl) totalAmountEl.textContent = totalAmount.toLocaleString('vi-VN');

        validatePaymentForm();
    });

    const prescriptionTable = document.getElementById('prescription-items');
    if (prescriptionTable) {
        observer.observe(prescriptionTable, { childList: true, subtree: true });
    }
});

function processPaymentWithValidation(paymentData) {
    const payButton = document.getElementById('payButton');
    if (payButton) {
        payButton.disabled = true;
        payButton.textContent = 'Đang xử lý...';
    }

    // Chuẩn bị dữ liệu InvoiceCreateRequest đầy đủ
    const invoiceData = {
        customerName: paymentData.customerName,
        phoneNumber: paymentData.phoneNumber,
        totalAmount: paymentData.totalAmount,
        paymentMethod: paymentData.paymentMethod,
        note: paymentData.note,
        items: paymentData.items || []
    };

    fetch('/pharmacist/pos/api/invoices', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(invoiceData)
    })
    .then(async res => {
        if (!res.ok) {
            const errorData = await res.json().catch(() => ({}));
            throw new Error(errorData.error || errorData.message || 'Lỗi tạo hóa đơn');
        }
        return res.json();
    })
    .then(result => {
        showAlert('success', `Thanh toán thành công! Mã hóa đơn: ${result.invoiceCode}`);

        // Complete form reset
        resetPaymentFormCompletely();
        prescriptionItems = [];
        renderPrescription();

        // Hide QR code section
        const qrCodeSection = document.querySelector('#qrCodeSection');
        if (qrCodeSection) {
            qrCodeSection.style.display = 'none';
        }
    })
    .catch(error => {
        console.error('Payment error:', error);
        showAlert('error', error.message || 'Thanh toán thất bại. Vui lòng thử lại.');
    })
    .finally(() => {
        if (payButton) {
            payButton.disabled = true; // Keep disabled until user adds items again
            payButton.textContent = 'Chưa có sản phẩm';
        }
    });
}

// Add CSS for validation styles
const validationStyles = `
    <style>
    .required { color: #dc3545; }
    .is-invalid { border-color: #dc3545 !important; }
    .invalid-feedback {
        display: none;
        color: #dc3545;
        font-size: 0.875rem;
        margin-top: 0.25rem;
    }
    .alert {
        padding: 0.75rem 1.25rem;
        margin-bottom: 1rem;
        border: 1px solid transparent;
        border-radius: 0.375rem;
        transition: opacity 0.3s ease-in-out;
    }
    .alert-success {
        color: #155724;
        background-color: #d4edda;
        border-color: #c3e6cb;
    }
    .alert-danger {
        color: #721c24;
        background-color: #f8d7da;
        border-color: #f5c6cb;
    }
    #payButton:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }
    .add-to-cart-btn {
        background: #28a745 !important;
        color: white !important;
        border: none !important;
        padding: 6px 12px !important;
        border-radius: 4px !important;
        font-size: 12px !important;
        font-weight: 500 !important;
        cursor: pointer !important;
        width: 100% !important;
        transition: all 0.2s ease !important;
        margin-top: 5px !important;
    }
    .add-to-cart-btn:hover:not(:disabled) {
        background: #218838 !important;
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .add-to-cart-btn:active {
        transform: translateY(0);
    }
    .add-to-cart-btn:disabled {
        background: #6c757d !important;
        cursor: not-allowed !important;
        opacity: 0.7 !important;
    }
    .inventory-item {
        transition: all 0.2s ease !important;
    }
    .inventory-item:hover {
        box-shadow: 0 2px 8px rgba(0,0,0,0.1) !important;
        transform: translateY(-1px);
    }
    </style>
`;

// Add styles to head
document.head.insertAdjacentHTML('beforeend', validationStyles);

// Event delegation is now used instead of global functions

// Initialize POS page
document.addEventListener('DOMContentLoaded', () => {
  console.log('POS page DOMContentLoaded');

  // Check if all critical elements are available
  const criticalElements = {
    searchInput: document.querySelector('.search-input'),
    resultContainer: document.querySelector('#medicine-list')
  };

  console.log('Critical elements check:', criticalElements);

  // Re-check elements if they weren't found during initial load
  Object.keys(criticalElements).forEach(key => {
    if (!criticalElements[key]) {
      console.warn(`${key} not found on DOMContentLoaded`);
    }
  });

  updatePaymentTotals();

  // Focus search input if available
  if (criticalElements.searchInput) {
    criticalElements.searchInput.focus();
    console.log('Search input focused');
  } else {
    console.warn('Cannot focus search input - element not found');
  }
});

// Function to update QR code with payment amount
function updateQRCode() {
    const totalAmount = getTotalAmount();
    const qrCodeImage = document.getElementById('qrCodeImage');
    const qrDisplayAmount = document.getElementById('qrDisplayAmount');
    const qrAccountNumber = document.getElementById('qrAccountNumber');
    const qrInvoiceCode = document.getElementById('qrInvoiceCode');

    if (totalAmount > 0) {
        // Format amount for display
        const formattedAmount = totalAmount.toLocaleString('vi-VN');

        // Update the amount display in QR section
        if (qrDisplayAmount) {
            qrDisplayAmount.textContent = formattedAmount;
        }

        // Generate invoice code for addInfo
        const invoiceCode = generateInvoiceCode();

        // VietQR configuration - có thể cấu hình từ backend
        const bankCode = 'VCB'; // Vietcombank
        const accountNumber = '0123456789'; // Số tài khoản thật
        const amount = Math.round(totalAmount); // Làm tròn số tiền
        const addInfo = invoiceCode; // Sử dụng mã hóa đơn làm ghi chú

        // Update bank info display
        if (qrAccountNumber) {
            qrAccountNumber.textContent = accountNumber;
        }
        if (qrInvoiceCode) {
            qrInvoiceCode.textContent = invoiceCode;
        }

        // Generate VietQR URL
        const vietQRUrl = `https://img.vietqr.io/${bankCode}/${accountNumber}?amount=${amount}&addInfo=${encodeURIComponent(addInfo)}`;

        // Update QR code image
        if (qrCodeImage) {
            qrCodeImage.src = vietQRUrl;
            qrCodeImage.onerror = function() {
                // Fallback to placeholder if VietQR fails
                console.warn('VietQR API failed, using placeholder');
                this.src = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNNTAgNTBIMTUwVjE1MEg1MFY1MFoiIGZpbGw9ImJsYWNrIi8+CjxyZWN0IHg9IjcwIiB5PSI3MCIgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4=";
            };
        }

        console.log('VietQR URL generated:', vietQRUrl);
        console.log('Bank:', bankCode, 'Account:', accountNumber, 'Amount:', amount, 'Note:', addInfo);
    }
}

// Generate unique invoice code
function generateInvoiceCode() {
    const now = new Date();
    const year = now.getFullYear().toString().slice(-2);
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    return `HD${year}${month}${day}${hours}${minutes}${seconds}`;
}

// Update QR code when total amount changes
function updatePaymentTotals() {
    const totalAmount = getTotalAmount();
    const subtotalEl = document.getElementById('subtotal');
    const totalAmountEl = document.getElementById('totalAmount');

    if (subtotalEl) subtotalEl.textContent = totalAmount.toLocaleString('vi-VN');
    if (totalAmountEl) totalAmountEl.textContent = totalAmount.toLocaleString('vi-VN');

    // Update QR code if transfer method is selected
    const currentPaymentMethod = document.querySelector('#paymentMethod');
    if (currentPaymentMethod && currentPaymentMethod.value === 'transfer') {
        updateQRCode();
    }

    validatePaymentForm();
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    // Initialize Clear All button state
    updateClearAllButtonState();

    // Initialize other components if needed
    console.log('POS system initialized');
});
