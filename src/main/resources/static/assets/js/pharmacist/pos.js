// DOM Elements
const searchInput = document.querySelector('.search-input');
const searchButton = document.querySelector('.search-button');
const clearButtons = document.querySelectorAll('.clear-button');
const paymentButton = document.querySelector('.payment-button');
const customerNameInput = document.querySelector('.customer-info .form-input');
const phoneInput = document.querySelector('.customer-info .form-group:nth-child(2) .form-input');
const paymentAmountInput = document.querySelector('.payment-details .form-input');
const paymentMethodSelect = document.querySelector('.form-select');
const notesTextarea = document.querySelector('.form-textarea');

const resultContainer = document.querySelector('#medicine-list');

let debounceTimer;

searchInput.addEventListener('input', () => {
  clearTimeout(debounceTimer);

  debounceTimer = setTimeout(() => {
    const searchTerm = searchInput.value.trim();

    if (searchTerm.length === 0) {
      resultContainer.innerHTML = "";
      return;
    }

    fetch(`/pharmacist/pos/api/search?keyword=${encodeURIComponent(searchTerm)}`)
      .then(res => res.json())
      .then(data => {
        renderResults(data);
      });
  }, 300); // delay 300ms
});

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
                                            style="margin-bottom: 10px; padding: 5px; background-color: #f9f9f9; border-radius: 4px; cursor: pointer;">
                                                <strong>Số lô: ${inv.batchNumber || 'N/A'}</strong><br>
                                                HSD: ${expiryDate}<br>
                                                Tồn kho: <strong>${inv.quantity}</strong> ${variant.baseUnitName || ''}<br>
                                                Giá bán: <strong style="color: #c0392b;">${inv.salePrice}</strong><br>
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
                        }
                        detailsContainer.innerHTML = detailsHtml;
                        detailsContainer.style.display = 'block';

                        // Add event listeners to inventory items
                        addInventoryItemClickListeners();
                    })
                    .catch(error => {
                        console.error('Error fetching variant details:', error);
                        detailsContainer.innerHTML = '<p style="color: red;">Không thể tải chi tiết thuốc.</p>';
                        detailsContainer.style.display = 'block';
                    });
            } else {
                detailsContainer.style.display = 'none';
            }
        });
    });
}

// State for the prescription
let prescriptionItems = [];

function addInventoryItemClickListeners() {
    const inventoryItems = document.querySelectorAll('.inventory-item');
    inventoryItems.forEach(item => {
        const medicineName = item.dataset.medicineName;
        item.addEventListener('click', (e) => {
            e.stopPropagation();

            // Extract all data from data attributes
            const medicineName = item.dataset.medicineName;
            const unitConversions = JSON.parse(item.dataset.units || "[]");
            const inventoryId = item.dataset.inventoryId;
            const maxQuantity = parseInt(item.dataset.maxQuantity, 10);
            const salePrice = parseFloat(item.dataset.salePrice);

            // Debug log to check what data we're getting
            console.log('Clicked inventory item data:', {
                inventoryId,
                medicineName: item.dataset.medicineName,
                strength: item.dataset.strength,
                batchNumber: item.dataset.batchNumber,
                expiryDate: item.dataset.expiryDate,
                salePrice,
                maxQuantity
            });

            // Validate inventory data
            if (!inventoryId) {
                alert('Lỗi: Không tìm thấy thông tin inventory.');
                console.error('Missing inventoryId in dataset:', item.dataset);
                return;
            }

            if (isNaN(maxQuantity) || maxQuantity <= 0) {
                alert('Sản phẩm đã hết hàng.');
                return;
            }

            if (isNaN(salePrice) || salePrice <= 0) {
                alert('Sản phẩm chưa có giá bán. Vui lòng cập nhật giá trước khi bán.');
                return;
            }

            const existingItem = prescriptionItems.find(p => p.inventoryId === inventoryId);

            if (existingItem) {
                // Validate quantity before increasing
                if (existingItem.quantity < maxQuantity) {
                    existingItem.quantity++;
                } else {
                    alert('Số lượng đã đạt tối đa tồn kho (' + maxQuantity + ').');
                    return;
                }
            } else {
                // Create new prescription item with all required data
                const newItem = {
                    inventoryId: inventoryId,
                    medicineName: item.dataset.medicineName || 'N/A',
                    strength: item.dataset.strength || '',
                    dosageForm: item.dataset.dosageForm || 'N/A',
                    baseUnitName: item.dataset.baseUnitName || 'Đơn vị',
                    packageUnitName: item.dataset.packageUnitName || '',
                    batchNumber: item.dataset.batchNumber || 'N/A',
                    expiryDate: item.dataset.expiryDate || 'N/A',


                    salePrice: salePrice,
                    currentPrice: salePrice,
                    quantity: 1,

                    maxQuantity: maxQuantity,
                    baseStock: maxQuantity,

                    selectedMultiplier: 1,
                    units: unitConversions
                };

                console.log('Adding new item to prescription:', newItem);
                prescriptionItems.push(newItem);
            }
            renderPrescription();
        });
    });
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
        // Medicine name with strength (concentration)
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
                <select class="unit-select" data-inventory-id="${item.inventoryId}">
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
                <input type="number" class="quantity-input" value="${item.quantity}" min="1" max="${item.maxQuantity}" data-inventory-id="${item.inventoryId}" style="width: 60px; padding: 4px;">
            </td>
            <td class="text-right">${item.currentPrice.toLocaleString('vi-VN')}</td>
            <td class="text-right">${itemTotal.toLocaleString('vi-VN')}</td>
            <td>
                    <button class="delete-item-btn" data-index="${index}" style="color:red;">🗑</button>
            </td>
        `;
        prescriptionBody.appendChild(row);
    });

    totalAmountEl.textContent = totalAmount.toLocaleString('vi-VN');

    // Update payment section with total
    if (paymentValues.length >= 2) {
        paymentValues[0].textContent = totalAmount.toLocaleString('vi-VN');
        paymentValues[1].textContent = totalAmount.toLocaleString('vi-VN');
    }

    // Add event listeners for new elements
    addPrescriptionActionListeners();
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
                // Validate input is a valid number
                if (isNaN(newQuantity)) {
                    alert('Vui lòng nhập số lượng hợp lệ.');
                    e.target.value = item.quantity;
                    return;
                }

                // Validate quantity does not exceed max stock
                if (newQuantity > item.maxQuantity) {
                    alert(`Số lượng vượt quá tồn kho. Tồn kho hiện tại: ${item.maxQuantity}`);
                    newQuantity = item.maxQuantity;
                    e.target.value = newQuantity;
                }

                // Validate quantity is at least 1
                if (newQuantity < 1) {
                    alert('Số lượng phải lớn hơn 0.');
                    newQuantity = 1;
                    e.target.value = newQuantity;
                }

                item.quantity = newQuantity;
                renderPrescription();
            }
        });

        document.querySelectorAll('.delete-item-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const index = parseInt(e.target.dataset.index);

                prescriptionItems.splice(index, 1);
                renderPrescription();
            });
        });

        // Prevent entering invalid characters
        input.addEventListener('keypress', (e) => {
            // Only allow numbers
            if (e.key && !/[0-9]/.test(e.key) && e.key !== 'Enter' && e.key !== 'Backspace') {
                e.preventDefault();
            }
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

// Payment functionality
if (paymentButton) {
  paymentButton.addEventListener('click', () => {
    const customerName = customerNameInput.value.trim();
    const phoneNumber = phoneInput.value.trim();
    const paymentAmount = parseFloat(paymentAmountInput.value.trim()) || 0;
    const paymentMethod = paymentMethodSelect.value;
    const notes = notesTextarea.value.trim();

        const totalAmount = getTotalAmount();

        if (prescriptionItems.length === 0) {
            alert("Chưa có sản phẩm nào trong đơn!");
            return;
        }

        if (paymentAmount < totalAmount) {
            alert(`Khách trả thiếu tiền! Cần ${totalAmount.toLocaleString('vi-VN')} VNĐ`);
            return;
        }

    const paymentData = {
      customerName,
      phoneNumber,
      paymentAmount,
      totalAmount,
      change: paymentAmount - totalAmount,
      paymentMethod,
      notes,
      items: prescriptionItems
    };

    processPayment(paymentData);
  });
}

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
  .then(res => {
    if (!res.ok) throw new Error("Lỗi tạo hóa đơn");
    return res.json();
  })
  .then(result => {
    alert(`Thanh toán thành công! Mã hóa đơn: ${result.id}`);
    clearPaymentForm();
    prescriptionItems = [];
    renderPrescription();
  })
  .catch(err => {
    console.error("Payment error", err);
    alert("Thanh toán thất bại!");
  });
}

function clearPaymentForm() {
  customerNameInput.value = '';
  phoneInput.value = '';
  paymentAmountInput.value = '';
  notesTextarea.value = '';
  paymentMethodSelect.selectedIndex = 0;
  document.querySelector('.payment-row:last-of-type .payment-value').textContent = '0';
}

function updatePaymentTotals() {
  const totalAmountElements = document.querySelectorAll('.total-amount, .payment-value');
  totalAmountElements.forEach(element => {
    if (element.textContent === '') {
      element.textContent = '0.00';
    }
  });
}

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

// Initialize POS page
document.addEventListener('DOMContentLoaded', () => {
  updatePaymentTotals();
  if (searchInput) searchInput.focus();
});
